package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.club.entity.Activity;
import com.club.entity.Club;
import com.club.entity.ClubMember;
import com.club.entity.Payment;
import com.club.entity.PaymentRecord;
import com.club.enums.ActivityStatusEnum;
import com.club.enums.ApplyStatusEnum;
import com.club.enums.ClubStatusEnum;
import com.club.enums.PaymentStatusEnum;
import com.club.mapper.ActivityMapper;
import com.club.mapper.ApplyMapper;
import com.club.mapper.ClubMapper;
import com.club.mapper.ClubMemberMapper;
import com.club.mapper.PaymentMapper;
import com.club.mapper.PaymentRecordMapper;
import com.club.mapper.UserMapper;
import com.club.service.DashboardService;
import com.club.vo.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ClubMapper clubMapper;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ApplyMapper applyMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    public DashboardVO getAdminDashboard() {
        DashboardVO vo = new DashboardVO();
        vo.setTotalClubs(clubMapper.selectCount(
            new LambdaQueryWrapper<Club>().eq(Club::getStatus, ClubStatusEnum.ACTIVE.getCode())
        ));
        vo.setTotalUsers(userMapper.selectCount(null));
        vo.setTotalActivities(activityMapper.selectCount(null));
        vo.setTotalMembers(clubMemberMapper.selectCount(null));
        vo.setPendingApplies(applyMapper.selectCount(
            new LambdaQueryWrapper<com.club.entity.Apply>()
                .eq(com.club.entity.Apply::getStatus, ApplyStatusEnum.PENDING.getCode())
        ));
        vo.setPendingActivities(activityMapper.selectCount(
            new LambdaQueryWrapper<Activity>()
                .eq(Activity::getStatus, ActivityStatusEnum.PENDING.getCode())
        ));
        return vo;
    }

    @Override
    public DashboardVO getPresidentDashboard(Long clubId) {
        DashboardVO vo = new DashboardVO();
        Club club = clubMapper.selectById(clubId);
        vo.setTotalMembers((long) club.getMemberCount());
        vo.setTotalActivities(activityMapper.selectCount(
            new LambdaQueryWrapper<Activity>().eq(Activity::getClubId, clubId)
        ));
        return vo;
    }

    @Override
    public Map<String, Object> getMemberStats(Long clubId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", clubMemberMapper.selectCount(
            new LambdaQueryWrapper<ClubMember>().eq(ClubMember::getClubId, clubId)
        ));
        
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        stats.put("newThisMonth", clubMemberMapper.selectCount(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .ge(ClubMember::getJoinTime, lastMonth)
        ));
        return stats;
    }

    @Override
    public Map<String, Object> getActivityStats(Long clubId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", activityMapper.selectCount(
            new LambdaQueryWrapper<Activity>().eq(Activity::getClubId, clubId)
        ));
        stats.put("ongoing", activityMapper.selectCount(
            new LambdaQueryWrapper<Activity>()
                .eq(Activity::getClubId, clubId)
                .eq(Activity::getStatus, ActivityStatusEnum.ONGOING.getCode())
        ));
        stats.put("ended", activityMapper.selectCount(
            new LambdaQueryWrapper<Activity>()
                .eq(Activity::getClubId, clubId)
                .eq(Activity::getStatus, ActivityStatusEnum.ENDED.getCode())
        ));
        return stats;
    }

    @Override
    public Map<String, Object> getPaymentStats(Long clubId) {
        Map<String, Object> stats = new HashMap<>();
        Long totalPayments = paymentMapper.selectCount(
            new LambdaQueryWrapper<Payment>().eq(Payment::getClubId, clubId)
        );
        stats.put("totalPayments", totalPayments);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        
        java.util.List<Payment> payments = paymentMapper.selectList(
            new LambdaQueryWrapper<Payment>().eq(Payment::getClubId, clubId)
        );
        for (Payment payment : payments) {
            totalAmount = totalAmount.add(payment.getAmount());
        }
        
        java.util.List<PaymentRecord> paidRecords = paymentRecordMapper.selectList(
            new LambdaQueryWrapper<PaymentRecord>()
                .in(PaymentRecord::getPaymentId, 
                    payments.stream().map(Payment::getId).toList())
                .eq(PaymentRecord::getStatus, PaymentStatusEnum.PAID.getCode())
        );
        for (PaymentRecord record : paidRecords) {
            paidAmount = paidAmount.add(record.getAmount());
        }
        
        stats.put("totalAmount", totalAmount);
        stats.put("paidAmount", paidAmount);
        stats.put("completionRate", totalAmount.compareTo(BigDecimal.ZERO) == 0 ? 0 
            : paidAmount.divide(totalAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        return stats;
    }
}
