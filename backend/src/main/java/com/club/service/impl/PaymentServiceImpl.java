package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.BaseService;
import com.club.common.BusinessException;
import com.club.common.ErrorCode;
import com.club.dto.PaymentDTO;
import com.club.entity.Club;
import com.club.entity.ClubMember;
import com.club.entity.Payment;
import com.club.entity.PaymentRecord;
import com.club.entity.User;
import com.club.enums.PaymentStatusEnum;
import com.club.mapper.ClubMemberMapper;
import com.club.mapper.ClubMapper;
import com.club.mapper.PaymentMapper;
import com.club.mapper.PaymentRecordMapper;
import com.club.service.PaymentService;
import com.club.vo.PaymentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PaymentServiceImpl extends BaseService implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private ClubMapper clubMapper;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    // ==================== 查询方法 ====================

    @Override
    public Page<PaymentVO> getPaymentPage(Integer current, Integer size, Long clubId, Integer status) {
        User user = getCurrentUser();
        
        // 社长只能查看自己社团的费用
        if (isPresident()) {
            return getPresidentPaymentPage(current, size, clubId, status, user.getId());
        }
        
        return getAdminPaymentPage(current, size, clubId, status);
    }

    @Override
    public PaymentVO getPaymentById(Long id) {
        Payment payment = paymentMapper.selectById(id);
        return convertToVO(payment);
    }

    @Override
    public Page<PaymentRecord> getPaymentRecords(Long paymentId, Integer current, Integer size) {
        Page<PaymentRecord> page = new Page<>(current, size);
        return paymentRecordMapper.selectPage(page,
            new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getPaymentId, paymentId)
                .orderByDesc(PaymentRecord::getPayTime)
        );
    }

    @Override
    public List<PaymentVO> getMyPayments() {
        Long userId = getCurrentUserId();
        
        List<Long> paymentIds = paymentRecordMapper.selectList(
            new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getUserId, userId)
                .select(PaymentRecord::getPaymentId)
        ).stream().map(PaymentRecord::getPaymentId).distinct().toList();
        
        if (paymentIds.isEmpty()) {
            return List.of();
        }
        
        return paymentMapper.selectList(
            new LambdaQueryWrapper<Payment>()
                .in(Payment::getId, paymentIds)
        ).stream().map(this::convertToVO).toList();
    }

    @Override
    public List<PaymentRecord> getMyPaymentRecords() {
        Long userId = getCurrentUserId();
        return paymentRecordMapper.selectList(
            new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getUserId, userId)
                .orderByDesc(PaymentRecord::getCreateTime)
        );
    }

    // ==================== 创建/更新方法 ====================

    @Override
    public void createPayment(PaymentDTO dto) {
        User user = getCurrentUser();
        Club club = getClubByIdOrThrow(dto.getClubId());
        
        // 社长只能为自己社团创建费用
        validatePresidentClubPermission(club, user);
        
        Payment payment = buildPayment(dto, club);
        paymentMapper.insert(payment);
        
        // 为所有成员创建缴费记录
        createPaymentRecordsForMembers(payment, dto.getAmount());
    }

    @Override
    public void updatePayment(PaymentDTO dto) {
        User user = getCurrentUser();
        
        // 社长只能修改自己社团的费用
        if (isPresident()) {
            validatePaymentBelongsToPresident(dto.getId(), user.getId());
        }
        
        Payment payment = new Payment();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), payment);
        paymentMapper.updateById(payment);
    }

    @Override
    public void deletePayment(Long id) {
        User user = getCurrentUser();
        
        // 社长只能删除自己社团的费用
        if (isPresident()) {
            validatePaymentBelongsToPresident(id, user.getId());
        }
        
        paymentMapper.deleteById(id);
    }

    // ==================== 缴费相关方法 ====================

    @Override
    public void pay(Long paymentId) {
        Long userId = getCurrentUserId();
        
        PaymentRecord record = getPaymentRecordOrThrow(paymentId, userId);
        validateNotAlreadyPaid(record);
        
        updateRecordAsPaid(record);
        updatePaymentStatusIfAllPaid(paymentId);
    }

    @Override
    public void sendReminder(Long paymentId, Long userId) {
        // 开发中
    }

    // ==================== 私有查询方法 ====================

    private Page<PaymentVO> getPresidentPaymentPage(Integer current, Integer size, 
                                                     Long clubId, Integer status, Long userId) {
        List<Long> myClubIds = getMyClubIds(userId);
        
        if (myClubIds.isEmpty()) {
            return new Page<>(current, size, 0);
        }
        
        // 如果指定了clubId，检查是否是自己的社团
        if (clubId != null && !myClubIds.contains(clubId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        
        LambdaQueryWrapper<Payment> wrapper = buildPaymentQueryWrapper(
            clubId != null ? List.of(clubId) : myClubIds, status);
        
        return executePaymentPageQuery(current, size, wrapper);
    }

    private Page<PaymentVO> getAdminPaymentPage(Integer current, Integer size, 
                                                 Long clubId, Integer status) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        
        if (clubId != null) {
            wrapper.eq(Payment::getClubId, clubId);
        }
        if (status != null) {
            wrapper.eq(Payment::getStatus, status);
        }
        wrapper.orderByDesc(Payment::getCreateTime);
        
        return executePaymentPageQuery(current, size, wrapper);
    }

    private List<Long> getMyClubIds(Long userId) {
        return clubMapper.selectList(
            new LambdaQueryWrapper<Club>()
                .eq(Club::getPresidentId, userId)
                .select(Club::getId)
        ).stream().map(Club::getId).toList();
    }

    private LambdaQueryWrapper<Payment> buildPaymentQueryWrapper(List<Long> clubIds, Integer status) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Payment::getClubId, clubIds);
        if (status != null) {
            wrapper.eq(Payment::getStatus, status);
        }
        wrapper.orderByDesc(Payment::getCreateTime);
        return wrapper;
    }

    private Page<PaymentVO> executePaymentPageQuery(Integer current, Integer size, 
                                                     LambdaQueryWrapper<Payment> wrapper) {
        Page<Payment> page = new Page<>(current, size);
        Page<Payment> paymentPage = paymentMapper.selectPage(page, wrapper);
        return convertToVOPage(paymentPage);
    }

    // ==================== 私有验证方法 ====================

    private void validatePresidentClubPermission(Club club, User user) {
        if (isPresident() && !club.getPresidentId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
    }

    private void validatePaymentBelongsToPresident(Long paymentId, Long userId) {
        Payment existing = paymentMapper.selectById(paymentId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        
        Club club = clubMapper.selectById(existing.getClubId());
        if (club == null || !club.getPresidentId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
    }

    private void validateNotAlreadyPaid(PaymentRecord record) {
        if (record.getStatus().equals(PaymentStatusEnum.PAID.getCode())) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_PAID);
        }
    }

    // ==================== 私有工具方法 ====================

    private Club getClubByIdOrThrow(Long clubId) {
        Club club = clubMapper.selectById(clubId);
        if (club == null) {
            throw new BusinessException(ErrorCode.CLUB_NOT_FOUND);
        }
        return club;
    }

    private PaymentRecord getPaymentRecordOrThrow(Long paymentId, Long userId) {
        PaymentRecord record = paymentRecordMapper.selectOne(
            new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getPaymentId, paymentId)
                .eq(PaymentRecord::getUserId, userId)
        );
        if (record == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return record;
    }

    private Payment buildPayment(PaymentDTO dto, Club club) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), payment);
        payment.setClubName(club.getName());
        payment.setStatus(PaymentStatusEnum.UNPAID.getCode());
        return payment;
    }

    private void createPaymentRecordsForMembers(Payment payment, BigDecimal amount) {
        List<ClubMember> members = clubMemberMapper.selectList(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, payment.getClubId())
        );
        
        for (ClubMember member : members) {
            PaymentRecord record = new PaymentRecord();
            record.setPaymentId(payment.getId());
            record.setUserId(member.getUserId());
            record.setUserName(member.getUserName());
            record.setAmount(amount);
            record.setStatus(PaymentStatusEnum.UNPAID.getCode());
            paymentRecordMapper.insert(record);
        }
    }

    private void updateRecordAsPaid(PaymentRecord record) {
        record.setStatus(PaymentStatusEnum.PAID.getCode());
        record.setPayTime(LocalDateTime.now());
        record.setTransactionNo(UUID.randomUUID().toString().replace("-", ""));
        paymentRecordMapper.updateById(record);
    }

    private void updatePaymentStatusIfAllPaid(Long paymentId) {
        Long unpaidCount = paymentRecordMapper.selectCount(
            new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getPaymentId, paymentId)
                .ne(PaymentRecord::getStatus, PaymentStatusEnum.PAID.getCode())
        );
        
        if (unpaidCount == 0) {
            Payment payment = new Payment();
            payment.setId(paymentId);
            payment.setStatus(PaymentStatusEnum.PAID.getCode());
            paymentMapper.updateById(payment);
        }
    }

    private Page<PaymentVO> convertToVOPage(Page<Payment> paymentPage) {
        Page<PaymentVO> voPage = new Page<>(
            paymentPage.getCurrent(), 
            paymentPage.getSize(), 
            paymentPage.getTotal()
        );
        voPage.setRecords(paymentPage.getRecords().stream()
            .map(this::convertToVO)
            .toList());
        return voPage;
    }

    private PaymentVO convertToVO(Payment payment) {
        if (payment == null) return null;
        PaymentVO vo = new PaymentVO();
        BeanUtils.copyProperties(payment, vo);
        vo.setStatusName(getStatusName(payment.getStatus()));
        return vo;
    }

    private String getStatusName(Integer status) {
        for (PaymentStatusEnum e : PaymentStatusEnum.values()) {
            if (e.getCode().equals(status)) {
                return e.getDesc();
            }
        }
        return "";
    }
}
