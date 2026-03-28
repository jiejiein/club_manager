package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.dto.ActivityDTO;
import com.club.entity.Activity;
import com.club.entity.ActivitySignUp;
import com.club.entity.Club;
import com.club.entity.ClubMember;
import com.club.entity.User;
import com.club.enums.ActivityStatusEnum;
import com.club.enums.ApplyStatusEnum;
import com.club.enums.RoleEnum;
import com.club.mapper.ActivityMapper;
import com.club.mapper.ActivitySignUpMapper;
import com.club.mapper.ClubMapper;
import com.club.mapper.ClubMemberMapper;
import com.club.mapper.UserMapper;
import com.club.service.ActivityService;
import com.club.vo.ActivityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivitySignUpMapper signUpMapper;

    @Autowired
    private ClubMapper clubMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    @Override
    public Page<ActivityVO> getActivityPage(Integer current, Integer size, String keyword, Long clubId, Integer status) {
        Page<Activity> page = new Page<>(current, size);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Activity::getTitle, keyword);
        }
        if (clubId != null) {
            wrapper.eq(Activity::getClubId, clubId);
        }
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }
        wrapper.orderByDesc(Activity::getCreateTime);
        Page<Activity> activityPage = activityMapper.selectPage(page, wrapper);
        Page<ActivityVO> voPage = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        voPage.setRecords(activityPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public ActivityVO getActivityById(Long id) {
        Activity activity = activityMapper.selectById(id);
        return convertToVO(activity);
    }

    @Override
    public void createActivity(ActivityDTO dto, Long clubId) {
        // 验证结束时间不得低于开始时间
        if (dto.getEndTime() != null && dto.getStartTime() != null 
            && dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new RuntimeException("结束时间不能早于开始时间");
        }
        
        // 验证报名截止时间不得低于报名开始时间
        if (dto.getSignUpEnd() != null && dto.getSignUpStart() != null 
            && dto.getSignUpEnd().isBefore(dto.getSignUpStart())) {
            throw new RuntimeException("报名截止时间不能早于报名开始时间");
        }
        
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Club club = clubMapper.selectById(clubId);
        
        if (!club.getPresidentId().equals(userId)) {
            throw new RuntimeException("无权限创建活动");
        }
        
        Activity activity = new Activity();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), activity);
        activity.setClubId(clubId);
        activity.setClubName(club.getName());
        activity.setCurrentParticipants(0);
        activity.setStatus(ActivityStatusEnum.PENDING.getCode());
        activityMapper.insert(activity);
    }

    @Override
    public void updateActivity(ActivityDTO dto) {
        // 验证结束时间不得低于开始时间
        if (dto.getEndTime() != null && dto.getStartTime() != null 
            && dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new RuntimeException("结束时间不能早于开始时间");
        }
        
        // 验证报名截止时间不得低于报名开始时间
        if (dto.getSignUpEnd() != null && dto.getSignUpStart() != null 
            && dto.getSignUpEnd().isBefore(dto.getSignUpStart())) {
            throw new RuntimeException("报名截止时间不能早于报名开始时间");
        }
        
        Activity activity = new Activity();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), activity);
        activityMapper.updateById(activity);
    }

    @Override
    public void deleteActivity(Long id) {
        // 直接删除活动记录，不保留状态
        activityMapper.deleteById(id);
    }

    @Override
    public void auditActivity(Long id, Integer status, String rejectReason) {
        // 获取当前登录用户
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        // 只有管理员可以审核活动
        if (!user.getRole().equals(RoleEnum.ADMIN.getCode())) {
            throw new RuntimeException("只有管理员可以审核活动");
        }
        
        Activity activity = new Activity();
        activity.setId(id);
        activity.setStatus(status);
        activity.setRejectReason(rejectReason);
        activityMapper.updateById(activity);
    }

    @Override
    public void signUpActivity(Long activityId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        Activity activity = activityMapper.selectById(activityId);
        
        // 只有已发布的活动才能报名
        if (!activity.getStatus().equals(ActivityStatusEnum.PUBLISHED.getCode())) {
            throw new RuntimeException("活动未开放报名");
        }
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("报名人数已满");
        }
        if (LocalDateTime.now().isAfter(activity.getSignUpEnd())) {
            throw new RuntimeException("报名已截止");
        }
        
        // 检查用户是否是该社团的成员
        Long memberCount = clubMemberMapper.selectCount(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, activity.getClubId())
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getStatus, 1) // 状态为1表示已加入
        );
        if (memberCount == 0) {
            throw new RuntimeException("只有社团成员才能报名该活动");
        }
        
        Long count = signUpMapper.selectCount(
            new LambdaQueryWrapper<ActivitySignUp>()
                .eq(ActivitySignUp::getActivityId, activityId)
                .eq(ActivitySignUp::getUserId, userId)
        );
        if (count > 0) {
            throw new RuntimeException("已报名该活动");
        }
        
        ActivitySignUp signUp = new ActivitySignUp();
        signUp.setActivityId(activityId);
        signUp.setUserId(userId);
        signUp.setUserName(user.getNickname());
        signUp.setStatus(ApplyStatusEnum.PENDING.getCode());
        signUp.setSignUpTime(LocalDateTime.now());
        signUp.setChecked(0);
        signUpMapper.insert(signUp);
        
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityMapper.updateById(activity);
    }

    @Override
    public void cancelSignUp(Long activityId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Activity activity = activityMapper.selectById(activityId);
        
        signUpMapper.delete(
            new LambdaQueryWrapper<ActivitySignUp>()
                .eq(ActivitySignUp::getActivityId, activityId)
                .eq(ActivitySignUp::getUserId, userId)
        );
        
        activity.setCurrentParticipants(Math.max(0, activity.getCurrentParticipants() - 1));
        activityMapper.updateById(activity);
    }

    @Override
    public void auditSignUp(Long signUpId, Integer status) {
        ActivitySignUp signUp = new ActivitySignUp();
        signUp.setId(signUpId);
        signUp.setStatus(status);
        signUpMapper.updateById(signUp);
    }

    @Override
    public void checkIn(Long signUpId) {
        ActivitySignUp signUp = new ActivitySignUp();
        signUp.setId(signUpId);
        signUp.setChecked(1);
        signUp.setCheckInTime(LocalDateTime.now());
        signUpMapper.updateById(signUp);
    }

    @Override
    public Page<ActivitySignUp> getSignUpList(Long activityId, Integer current, Integer size) {
        Page<ActivitySignUp> page = new Page<>(current, size);
        return signUpMapper.selectPage(page,
            new LambdaQueryWrapper<ActivitySignUp>()
                .eq(ActivitySignUp::getActivityId, activityId)
                .orderByDesc(ActivitySignUp::getSignUpTime)
        );
    }

    @Override
    public List<ActivityVO> getMyActivities() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Long> activityIds = signUpMapper.selectList(
            new LambdaQueryWrapper<ActivitySignUp>()
                .eq(ActivitySignUp::getUserId, userId)
                .select(ActivitySignUp::getActivityId)
        ).stream().map(ActivitySignUp::getActivityId).toList();
        
        if (activityIds.isEmpty()) {
            return List.of();
        }
        return activityMapper.selectList(
            new LambdaQueryWrapper<Activity>()
                .in(Activity::getId, activityIds)
        ).stream().map(this::convertToVO).toList();
    }

    @Override
    public List<ActivityVO> getAvailableActivities() {
        // 获取已发布和进行中的活动
        return activityMapper.selectList(
            new LambdaQueryWrapper<Activity>()
                .in(Activity::getStatus, 2, 3)  // 已发布和进行中
                .orderByDesc(Activity::getCreateTime)
        ).stream().map(this::convertToVO).toList();
    }

    @Override
    public void submitRating(Long signUpId, Integer rating, String comment) {
        ActivitySignUp signUp = new ActivitySignUp();
        signUp.setId(signUpId);
        signUp.setRating(rating);
        signUp.setComment(comment);
        signUpMapper.updateById(signUp);
    }

    private ActivityVO convertToVO(Activity activity) {
        if (activity == null) return null;
        ActivityVO vo = new ActivityVO();
        BeanUtils.copyProperties(activity, vo);
        vo.setStatusName(getStatusName(activity.getStatus()));
        return vo;
    }

    private String getStatusName(Integer status) {
        for (ActivityStatusEnum e : ActivityStatusEnum.values()) {
            if (e.getCode().equals(status)) {
                return e.getDesc();
            }
        }
        return "";
    }
}
