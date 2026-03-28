package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.BaseService;
import com.club.common.BusinessException;
import com.club.common.ErrorCode;
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
import com.club.service.ActivityService;
import com.club.vo.ActivityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ActivityServiceImpl extends BaseService implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivitySignUpMapper signUpMapper;

    @Autowired
    private ClubMapper clubMapper;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    // ==================== 查询方法 ====================

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
        return convertToVOPage(activityPage);
    }

    @Override
    public ActivityVO getActivityById(Long id) {
        Activity activity = activityMapper.selectById(id);
        return convertToVO(activity);
    }

    @Override
    public List<ActivityVO> getMyActivities() {
        Long userId = getCurrentUserId();
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
        return activityMapper.selectList(
            new LambdaQueryWrapper<Activity>()
                .in(Activity::getStatus, 2, 3)
                .orderByDesc(Activity::getCreateTime)
        ).stream().map(this::convertToVO).toList();
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

    // ==================== 创建/更新方法 ====================

    @Override
    public void createActivity(ActivityDTO dto, Long clubId) {
        validateActivityTime(dto);
        
        User user = getCurrentUser();
        Club club = getClubByIdOrThrow(clubId);
        
        validatePresidentPermission(club, user.getId());
        
        Activity activity = buildActivity(dto, club);
        activityMapper.insert(activity);
    }

    @Override
    public void updateActivity(ActivityDTO dto) {
        validateActivityTime(dto);
        
        Activity activity = new Activity();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), activity);
        activityMapper.updateById(activity);
    }

    @Override
    public void deleteActivity(Long id) {
        activityMapper.deleteById(id);
    }

    // ==================== 审核方法 ====================

    @Override
    public void auditActivity(Long id, Integer status, String rejectReason) {
        validateAdminPermission();
        
        Activity activity = new Activity();
        activity.setId(id);
        activity.setStatus(status);
        activity.setRejectReason(rejectReason);
        activityMapper.updateById(activity);
    }

    @Override
    public void auditSignUp(Long signUpId, Integer status) {
        ActivitySignUp signUp = new ActivitySignUp();
        signUp.setId(signUpId);
        signUp.setStatus(status);
        signUpMapper.updateById(signUp);
    }

    // ==================== 报名相关方法 ====================

    @Override
    public void signUpActivity(Long activityId) {
        User user = getCurrentUser();
        Activity activity = getActivityByIdOrThrow(activityId);
        
        validateCanSignUp(activity, user.getId());
        
        createSignUpRecord(activityId, user);
        updateActivityParticipants(activity);
    }

    @Override
    public void cancelSignUp(Long activityId) {
        Long userId = getCurrentUserId();
        Activity activity = getActivityByIdOrThrow(activityId);
        
        signUpMapper.delete(
            new LambdaQueryWrapper<ActivitySignUp>()
                .eq(ActivitySignUp::getActivityId, activityId)
                .eq(ActivitySignUp::getUserId, userId)
        );
        
        activity.setCurrentParticipants(Math.max(0, activity.getCurrentParticipants() - 1));
        activityMapper.updateById(activity);
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
    public void submitRating(Long signUpId, Integer rating, String comment) {
        ActivitySignUp signUp = new ActivitySignUp();
        signUp.setId(signUpId);
        signUp.setRating(rating);
        signUp.setComment(comment);
        signUpMapper.updateById(signUp);
    }

    // ==================== 私有验证方法 ====================

    private void validateActivityTime(ActivityDTO dto) {
        if (dto.getEndTime() != null && dto.getStartTime() != null 
            && dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BusinessException(ErrorCode.ACTIVITY_TIME_INVALID);
        }
        
        if (dto.getSignUpEnd() != null && dto.getSignUpStart() != null 
            && dto.getSignUpEnd().isBefore(dto.getSignUpStart())) {
            throw new BusinessException(ErrorCode.ACTIVITY_SIGN_UP_TIME_INVALID);
        }
    }

    private void validatePresidentPermission(Club club, Long userId) {
        if (!club.getPresidentId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION_CREATE_ACTIVITY);
        }
    }

    private void validateAdminPermission() {
        User user = getCurrentUser();
        if (!user.getRole().equals(RoleEnum.ADMIN.getCode())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION_AUDIT_ACTIVITY);
        }
    }

    private void validateCanSignUp(Activity activity, Long userId) {
        // 检查活动是否已发布
        if (!activity.getStatus().equals(ActivityStatusEnum.PUBLISHED.getCode())) {
            throw new BusinessException(ErrorCode.ACTIVITY_NOT_PUBLISHED);
        }
        
        // 检查报名人数
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new BusinessException(ErrorCode.ACTIVITY_FULL);
        }
        
        // 检查报名时间
        if (LocalDateTime.now().isAfter(activity.getSignUpEnd())) {
            throw new BusinessException(ErrorCode.ACTIVITY_SIGN_UP_ENDED);
        }
        
        // 检查是否是社团成员
        validateIsClubMember(activity.getClubId(), userId);
        
        // 检查是否已报名
        validateNotAlreadySignUp(activity.getId(), userId);
    }

    private void validateIsClubMember(Long clubId, Long userId) {
        Long memberCount = clubMemberMapper.selectCount(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getStatus, 1)
        );
        if (memberCount == 0) {
            throw new BusinessException(ErrorCode.ACTIVITY_NOT_MEMBER);
        }
    }

    private void validateNotAlreadySignUp(Long activityId, Long userId) {
        Long count = signUpMapper.selectCount(
            new LambdaQueryWrapper<ActivitySignUp>()
                .eq(ActivitySignUp::getActivityId, activityId)
                .eq(ActivitySignUp::getUserId, userId)
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.ACTIVITY_ALREADY_SIGN_UP);
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

    private Activity getActivityByIdOrThrow(Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ErrorCode.ACTIVITY_NOT_FOUND);
        }
        return activity;
    }

    private Activity buildActivity(ActivityDTO dto, Club club) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), activity);
        activity.setClubId(club.getId());
        activity.setClubName(club.getName());
        activity.setCurrentParticipants(0);
        activity.setStatus(ActivityStatusEnum.PENDING.getCode());
        return activity;
    }

    private void createSignUpRecord(Long activityId, User user) {
        ActivitySignUp signUp = new ActivitySignUp();
        signUp.setActivityId(activityId);
        signUp.setUserId(user.getId());
        signUp.setUserName(user.getNickname());
        signUp.setStatus(ApplyStatusEnum.PENDING.getCode());
        signUp.setSignUpTime(LocalDateTime.now());
        signUp.setChecked(0);
        signUpMapper.insert(signUp);
    }

    private void updateActivityParticipants(Activity activity) {
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityMapper.updateById(activity);
    }

    private Page<ActivityVO> convertToVOPage(Page<Activity> activityPage) {
        Page<ActivityVO> voPage = new Page<>(
            activityPage.getCurrent(), 
            activityPage.getSize(), 
            activityPage.getTotal()
        );
        voPage.setRecords(activityPage.getRecords().stream()
            .map(this::convertToVO)
            .toList());
        return voPage;
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
