package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.club.mapper.UserMapper;
import com.club.service.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplyServiceImpl implements ApplyService {

    @Autowired
    private ApplyMapper applyMapper;

    @Autowired
    private ClubMapper clubMapper;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<Apply> getApplyPage(Integer current, Integer size, Long clubId, Integer status) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        Page<Apply> page = new Page<>(current, size);
        LambdaQueryWrapper<Apply> wrapper = new LambdaQueryWrapper<>();
        
        // 社长只能查看自己社团的申请
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            // 获取社长管理的社团ID
            List<Long> myClubIds = clubMapper.selectList(
                new LambdaQueryWrapper<Club>()
                    .eq(Club::getPresidentId, userId)
                    .select(Club::getId)
            ).stream().map(Club::getId).toList();
            
            if (myClubIds.isEmpty()) {
                return page; // 空结果
            }
            
            if (clubId != null) {
                // 如果指定了clubId，检查是否是自己的社团
                if (!myClubIds.contains(clubId)) {
                    throw new RuntimeException("只能查看自己社团的申请");
                }
                wrapper.eq(Apply::getClubId, clubId);
            } else {
                wrapper.in(Apply::getClubId, myClubIds);
            }
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
    public void submitApply(Long clubId, String reason) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        Club club = clubMapper.selectById(clubId);
        
        if (!club.getStatus().equals(ClubStatusEnum.ACTIVE.getCode())) {
            throw new RuntimeException("社团未正常运营，无法申请加入");
        }
        
        Long count = applyMapper.selectCount(
            new LambdaQueryWrapper<Apply>()
                .eq(Apply::getClubId, clubId)
                .eq(Apply::getUserId, userId)
                .eq(Apply::getStatus, ApplyStatusEnum.PENDING.getCode())
        );
        if (count > 0) {
            throw new RuntimeException("已有待审核的申请");
        }
        
        Long memberCount = clubMemberMapper.selectCount(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
        );
        if (memberCount > 0) {
            throw new RuntimeException("已是该社团成员");
        }
        
        Apply apply = new Apply();
        apply.setClubId(clubId);
        apply.setClubName(club.getName());
        apply.setUserId(userId);
        apply.setUserName(user.getNickname());
        apply.setReason(reason);
        apply.setStatus(ApplyStatusEnum.PENDING.getCode());
        applyMapper.insert(apply);
    }

    @Override
    public void auditApply(Long applyId, Integer status, String rejectReason) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        Apply apply = applyMapper.selectById(applyId);
        if (apply == null) {
            throw new RuntimeException("申请不存在");
        }
        
        // 社长只能审核自己社团的申请
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            Club club = clubMapper.selectById(apply.getClubId());
            if (club == null || !club.getPresidentId().equals(userId)) {
                throw new RuntimeException("只能审核自己社团的申请");
            }
        }
        
        if (!apply.getStatus().equals(ApplyStatusEnum.PENDING.getCode())) {
            throw new RuntimeException("该申请已处理");
        }
        
        apply.setStatus(status);
        apply.setRejectReason(rejectReason);
        apply.setProcessTime(LocalDateTime.now());
        applyMapper.updateById(apply);
        
        if (status.equals(ApplyStatusEnum.APPROVED.getCode())) {
            ClubMember member = new ClubMember();
            member.setClubId(apply.getClubId());
            member.setUserId(apply.getUserId());
            member.setUserName(apply.getUserName());
            member.setStatus(StatusEnum.ENABLE.getCode());
            member.setJoinTime(LocalDateTime.now());
            clubMemberMapper.insert(member);
            
            Club club = clubMapper.selectById(apply.getClubId());
            club.setMemberCount(club.getMemberCount() + 1);
            clubMapper.updateById(club);
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
    public List<Apply> getMyApplies() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return applyMapper.selectList(
            new LambdaQueryWrapper<Apply>()
                .eq(Apply::getUserId, userId)
                .orderByDesc(Apply::getCreateTime)
        );
    }

    @Override
    public Page<ClubMember> getClubMembers(Long clubId, Integer current, Integer size) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        // 社长只能查看自己社团的成员
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            Club club = clubMapper.selectById(clubId);
            if (club == null || !club.getPresidentId().equals(userId)) {
                throw new RuntimeException("只能查看自己社团的成员");
            }
        }
        
        Page<ClubMember> page = new Page<>(current, size);
        return clubMemberMapper.selectPage(page,
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .orderByDesc(ClubMember::getJoinTime)
        );
    }

    @Override
    @Transactional
    public void removeMember(Long clubId, Long memberUserId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        // 社长只能移除自己社团的成员
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            Club club = clubMapper.selectById(clubId);
            if (club == null || !club.getPresidentId().equals(userId)) {
                throw new RuntimeException("只能移除自己社团的成员");
            }
        }
        
        clubMemberMapper.delete(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, memberUserId)
        );
        
        Club club = clubMapper.selectById(clubId);
        club.setMemberCount(Math.max(0, club.getMemberCount() - 1));
        clubMapper.updateById(club);
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
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Apply apply = applyMapper.selectById(applyId);
        if (apply == null) {
            throw new RuntimeException("申请不存在");
        }
        if (!apply.getUserId().equals(userId)) {
            throw new RuntimeException("只能取消自己的申请");
        }
        if (apply.getStatus() != 0) {
            throw new RuntimeException("只能取消待审核的申请");
        }
        applyMapper.deleteById(applyId);
    }
}
