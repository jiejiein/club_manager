package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.dto.PaymentDTO;
import com.club.entity.Club;
import com.club.entity.ClubMember;
import com.club.entity.Payment;
import com.club.entity.PaymentRecord;
import com.club.entity.User;
import com.club.enums.PaymentStatusEnum;
import com.club.enums.RoleEnum;
import com.club.mapper.ClubMemberMapper;
import com.club.mapper.ClubMapper;
import com.club.mapper.PaymentMapper;
import com.club.mapper.PaymentRecordMapper;
import com.club.mapper.UserMapper;
import com.club.service.PaymentService;
import com.club.vo.PaymentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private ClubMapper clubMapper;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<PaymentVO> getPaymentPage(Integer current, Integer size, Long clubId, Integer status) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        Page<Payment> page = new Page<>(current, size);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        
        // 社长只能查看自己社团的费用
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            // 获取社长管理的社团ID
            List<Long> myClubIds = clubMapper.selectList(
                new LambdaQueryWrapper<Club>()
                    .eq(Club::getPresidentId, userId)
                    .select(Club::getId)
            ).stream().map(Club::getId).toList();
            
            if (myClubIds.isEmpty()) {
                return new Page<>(current, size, 0);
            }
            
            if (clubId != null) {
                // 如果指定了clubId，检查是否是自己的社团
                if (!myClubIds.contains(clubId)) {
                    throw new RuntimeException("只能查看自己社团的费用");
                }
                wrapper.eq(Payment::getClubId, clubId);
            } else {
                wrapper.in(Payment::getClubId, myClubIds);
            }
        } else if (clubId != null) {
            wrapper.eq(Payment::getClubId, clubId);
        }
        
        if (status != null) {
            wrapper.eq(Payment::getStatus, status);
        }
        wrapper.orderByDesc(Payment::getCreateTime);
        Page<Payment> paymentPage = paymentMapper.selectPage(page, wrapper);
        Page<PaymentVO> voPage = new Page<>(paymentPage.getCurrent(), paymentPage.getSize(), paymentPage.getTotal());
        voPage.setRecords(paymentPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public PaymentVO getPaymentById(Long id) {
        Payment payment = paymentMapper.selectById(id);
        return convertToVO(payment);
    }

    @Override
    public void createPayment(PaymentDTO dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        Club club = clubMapper.selectById(dto.getClubId());
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        
        // 社长只能为自己社团创建费用
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            if (!club.getPresidentId().equals(userId)) {
                throw new RuntimeException("只能为自己社团创建费用");
            }
        }
        
        Payment payment = new Payment();
        BeanUtils.copyProperties(dto, payment);
        payment.setClubName(club.getName());
        payment.setStatus(PaymentStatusEnum.UNPAID.getCode());
        paymentMapper.insert(payment);
        
        List<ClubMember> members = clubMemberMapper.selectList(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, dto.getClubId())
        );
        
        for (ClubMember member : members) {
            PaymentRecord record = new PaymentRecord();
            record.setPaymentId(payment.getId());
            record.setUserId(member.getUserId());
            record.setUserName(member.getUserName());
            record.setAmount(dto.getAmount());
            record.setStatus(PaymentStatusEnum.UNPAID.getCode());
            paymentRecordMapper.insert(record);
        }
    }

    @Override
    public void updatePayment(PaymentDTO dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        // 社长只能修改自己社团的费用
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            Payment existing = paymentMapper.selectById(dto.getId());
            if (existing == null) {
                throw new RuntimeException("费用项目不存在");
            }
            Club club = clubMapper.selectById(existing.getClubId());
            if (club == null || !club.getPresidentId().equals(userId)) {
                throw new RuntimeException("只能修改自己社团的费用");
            }
        }
        
        Payment payment = new Payment();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), payment);
        paymentMapper.updateById(payment);
    }

    @Override
    public void deletePayment(Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        // 社长只能删除自己社团的费用
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            Payment existing = paymentMapper.selectById(id);
            if (existing == null) {
                throw new RuntimeException("费用项目不存在");
            }
            Club club = clubMapper.selectById(existing.getClubId());
            if (club == null || !club.getPresidentId().equals(userId)) {
                throw new RuntimeException("只能删除自己社团的费用");
            }
        }
        
        paymentMapper.deleteById(id);
    }

    @Override
    public void pay(Long paymentId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        PaymentRecord record = paymentRecordMapper.selectOne(
            new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getPaymentId, paymentId)
                .eq(PaymentRecord::getUserId, userId)
        );
        
        if (record == null) {
            throw new RuntimeException("无缴费记录");
        }
        
        if (record.getStatus().equals(PaymentStatusEnum.PAID.getCode())) {
            throw new RuntimeException("已缴费");
        }
        
        record.setStatus(PaymentStatusEnum.PAID.getCode());
        record.setPayTime(LocalDateTime.now());
        record.setTransactionNo(UUID.randomUUID().toString().replace("-", ""));
        paymentRecordMapper.updateById(record);
        
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
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
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
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return paymentRecordMapper.selectList(
            new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getUserId, userId)
                .orderByDesc(PaymentRecord::getCreateTime)
        );
    }

    @Override
    public void sendReminder(Long paymentId, Long userId) {
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
