package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.BaseService;
import com.club.common.BusinessException;
import com.club.common.ErrorCode;
import com.club.entity.Apply;
import com.club.entity.Club;
import com.club.entity.ClubMember;
import com.club.entity.User;
import com.club.enums.ApplyStatusEnum;
import com.club.enums.ClubStatusEnum;
import com.club.enums.RoleEnum;
import com.club.enums.StatusEnum;
import com.club.mapper.ApplyMapper;
import com.club.mapper.ClubMapper;
import com.club.mapper.ClubMemberMapper;
import com.club.service.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 申请服务实现类
 * 优化后的代码结构：
 * 1. 继承 BaseService 获取通用方法
 * 2. 使用 BusinessException 替代 RuntimeException
 * 3. 使用 ErrorCode 统一管理错误信息
 * 4. 提取重复逻辑为私有方法
 */
@Service
public class ApplyServiceImpl extends BaseService implements ApplyService {

    @Autowired
    private ApplyMapper applyMapper;

    @Autowired
    private ClubMapper clubMapper;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    // ==================== 查询方法 ====================

    @Override
    public Page<Apply> getApplyPage(Integer current, Integer size, Long clubId, Integer status) {
        User user = getCurrentUser();
        Page<Apply> page = new Page<>(current, size);
        LambdaQueryWrapper<Apply> wrapper = new LambdaQueryWrapper<>();

        // 社长只能查看自己社团的申请
        if (isRole(RoleEnum.PRESIDENT.getCode())) {
            wrapper = buildPresidentQueryWrapper(user.getId(), clubId);
        } else if (clubId != null) {
            wrapper.eq(Apply::getClubId, clubId);
        }

        if (status != null) {
            wrapper.eq(Apply::getStatus, status);
        }
        wrapper.orderByDesc(Apply::getCreateTime);

        return applyMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Apply> getMyApplies() {
        return applyMapper.selectList(
            new LambdaQueryWrapper<Apply>()
                .eq(Apply::getUserId, getCurrentUserId())
                .orderByDesc(Apply::getCreateTime)
        );
    }

    @Override
    public Page<ClubMember> getClubMembers(Long clubId, Integer current, Integer size) {
        validateClubPermission(clubId);

        Page<ClubMember> page = new Page<>(current, size);
        return clubMemberMapper.selectPage(page,
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .orderByDesc(ClubMember::getJoinTime)
        );
    }

    // ==================== 操作方法 ====================

    @Override
    public void submitApply(Long clubId, String reason) {
        Long userId = getCurrentUserId();
        User user = getCurrentUser();
        Club club = getClubOrThrow(clubId);

        validateClubActive(club);
        validateNoPendingApply(userId, clubId);
        validateNotMember(userId, clubId);

        Apply apply = buildApply(user, club, reason);
        applyMapper.insert(apply);
    }

    @Override
    public void auditApply(Long applyId, Integer status, String rejectReason) {
        User user = getCurrentUser();
        Apply apply = getApplyOrThrow(applyId);

        validateAuditPermission(user, apply);
        validateApplyPending(apply);

        updateApplyStatus(apply, status, rejectReason);

        if (status.equals(ApplyStatusEnum.APPROVED.getCode())) {
            approveApply(apply);
        }
    }

    @Override
    @Transactional
    public void batchAudit(List<Long> applyIds, Integer status, String rejectReason) {
        for (Long applyId : applyIds) {
            auditApply(applyId, status, rejectReason);
        }
    }

    @Override
    @Transactional
    public void removeMember(Long clubId, Long memberUserId) {
        validateClubPermission(clubId);

        clubMemberMapper.delete(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, memberUserId)
        );

        decrementMemberCount(clubId);
    }

    @Override
    @Transactional
    public void batchRemoveMembers(Long clubId, List<Long> userIds) {
        for (Long userId : userIds) {
            removeMember(clubId, userId);
        }
    }

    @Override
    public void cancelApply(Long applyId) {
        Long userId = getCurrentUserId();
        Apply apply = getApplyOrThrow(applyId);

        validateApplyOwnership(apply, userId);
        validateApplyPending(apply);

        applyMapper.deleteById(applyId);
    }

    // ==================== 私有方法：构建对象 ====================

    private Apply buildApply(User user, Club club, String reason) {
        Apply apply = new Apply();
        apply.setClubId(club.getId());
        apply.setClubName(club.getName());
        apply.setUserId(user.getId());
        apply.setUserName(user.getNickname());
        apply.setReason(reason);
        apply.setStatus(ApplyStatusEnum.PENDING.getCode());
        return apply;
    }

    // ==================== 私有方法：查询构建 ====================

    private LambdaQueryWrapper<Apply> buildPresidentQueryWrapper(Long presidentId, Long clubId) {
        LambdaQueryWrapper<Apply> wrapper = new LambdaQueryWrapper<>();

        List<Long> myClubIds = clubMapper.selectList(
            new LambdaQueryWrapper<Club>()
                .eq(Club::getPresidentId, presidentId)
                .select(Club::getId)
        ).stream().map(Club::getId).toList();

        if (myClubIds.isEmpty()) {
            return wrapper.eq(Apply::getId, -1L); // 返回空结果
        }

        if (clubId != null) {
            validateClubBelongsToPresident(myClubIds, clubId);
            wrapper.eq(Apply::getClubId, clubId);
        } else {
            wrapper.in(Apply::getClubId, myClubIds);
        }

        return wrapper;
    }

    // ==================== 私有方法：数据获取 ====================

    private Club getClubOrThrow(Long clubId) {
        Club club = clubMapper.selectById(clubId);
        if (club == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return club;
    }

    private Apply getApplyOrThrow(Long applyId) {
        Apply apply = applyMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException(ErrorCode.APPLY_NOT_FOUND);
        }
        return apply;
    }

    // ==================== 私有方法：业务验证 ====================

    private void validateClubActive(Club club) {
        if (!club.getStatus().equals(ClubStatusEnum.ACTIVE.getCode())) {
            throw new BusinessException(ErrorCode.CLUB_NOT_ACTIVE);
        }
    }

    private void validateNoPendingApply(Long userId, Long clubId) {
        Long count = applyMapper.selectCount(
            new LambdaQueryWrapper<Apply>()
                .eq(Apply::getClubId, clubId)
                .eq(Apply::getUserId, userId)
                .eq(Apply::getStatus, ApplyStatusEnum.PENDING.getCode())
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.APPLY_PENDING_EXISTS);
        }
    }

    private void validateNotMember(Long userId, Long clubId) {
        Long count = clubMemberMapper.selectCount(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.ALREADY_MEMBER);
        }
    }

    private void validateApplyPending(Apply apply) {
        if (!apply.getStatus().equals(ApplyStatusEnum.PENDING.getCode())) {
            throw new BusinessException(ErrorCode.APPLY_ALREADY_PROCESSED);
        }
    }

    private void validateAuditPermission(User user, Apply apply) {
        if (isRole(RoleEnum.PRESIDENT.getCode())) {
            Club club = clubMapper.selectById(apply.getClubId());
            if (club == null || !club.getPresidentId().equals(user.getId())) {
                throw new BusinessException(ErrorCode.NOT_YOUR_CLUB);
            }
        }
    }

    private void validateClubPermission(Long clubId) {
        if (!isRole(RoleEnum.PRESIDENT.getCode())) {
            return; // 管理员可以操作所有社团
        }

        User user = getCurrentUser();
        Club club = clubMapper.selectById(clubId);

        if (club == null || !club.getPresidentId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NOT_YOUR_CLUB);
        }
    }

    private void validateClubBelongsToPresident(List<Long> myClubIds, Long clubId) {
        if (!myClubIds.contains(clubId)) {
            throw new BusinessException(ErrorCode.NOT_YOUR_CLUB);
        }
    }

    private void validateApplyOwnership(Apply apply, Long userId) {
        if (!apply.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
    }

    // ==================== 私有方法：业务操作 ====================

    private void updateApplyStatus(Apply apply, Integer status, String rejectReason) {
        apply.setStatus(status);
        apply.setRejectReason(rejectReason);
        apply.setProcessTime(LocalDateTime.now());
        applyMapper.updateById(apply);
    }

    private void approveApply(Apply apply) {
        // 添加成员
        ClubMember member = new ClubMember();
        member.setClubId(apply.getClubId());
        member.setUserId(apply.getUserId());
        member.setUserName(apply.getUserName());
        member.setStatus(StatusEnum.ENABLE.getCode());
        member.setJoinTime(LocalDateTime.now());
        clubMemberMapper.insert(member);

        // 更新社团成员数
        incrementMemberCount(apply.getClubId());
    }

    private void incrementMemberCount(Long clubId) {
        Club club = clubMapper.selectById(clubId);
        club.setMemberCount(club.getMemberCount() + 1);
        clubMapper.updateById(club);
    }

    private void decrementMemberCount(Long clubId) {
        Club club = clubMapper.selectById(clubId);
        club.setMemberCount(Math.max(0, club.getMemberCount() - 1));
        clubMapper.updateById(club);
    }
}
