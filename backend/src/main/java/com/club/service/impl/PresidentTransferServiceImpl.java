package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.entity.Club;
import com.club.entity.ClubMember;
import com.club.entity.PresidentTransfer;
import com.club.entity.User;
import com.club.enums.PresidentTransferStatusEnum;
import com.club.enums.RoleEnum;
import com.club.mapper.ClubMapper;
import com.club.mapper.ClubMemberMapper;
import com.club.mapper.PresidentTransferMapper;
import com.club.mapper.UserMapper;
import com.club.service.PresidentTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PresidentTransferServiceImpl implements PresidentTransferService {

    @Autowired
    private PresidentTransferMapper transferMapper;

    @Autowired
    private ClubMapper clubMapper;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<PresidentTransfer> getTransferPage(Integer current, Integer size, Integer status) {
        Page<PresidentTransfer> page = new Page<>(current, size);
        LambdaQueryWrapper<PresidentTransfer> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(PresidentTransfer::getStatus, status);
        }
        wrapper.orderByDesc(PresidentTransfer::getCreateTime);
        return transferMapper.selectPage(page, wrapper);
    }

    @Override
    public void createTransfer(Long clubId, Long newPresidentId, String reason) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // 检查当前用户是否是社团的现任社长
        Club club = clubMapper.selectById(clubId);
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        if (!club.getPresidentId().equals(currentUserId)) {
            throw new RuntimeException("只有现任社长可以申请转移社长职位");
        }
        
        // 检查新社长是否是社团成员
        ClubMember existingMember = clubMemberMapper.selectOne(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, newPresidentId)
        );
        if (existingMember == null) {
            throw new RuntimeException("新社长必须是社团成员");
        }
        
        // 检查是否有待审核的转移申请
        PresidentTransfer existingTransfer = transferMapper.selectOne(
            new LambdaQueryWrapper<PresidentTransfer>()
                .eq(PresidentTransfer::getClubId, clubId)
                .eq(PresidentTransfer::getStatus, PresidentTransferStatusEnum.PENDING.getCode())
        );
        if (existingTransfer != null) {
            throw new RuntimeException("该社团已有待审核的转移申请");
        }
        
        // 获取新社长的用户信息
        User newPresident = userMapper.selectById(newPresidentId);
        if (newPresident == null) {
            throw new RuntimeException("新社长用户不存在");
        }
        
        // 获取当前用户信息
        User currentPresident = userMapper.selectById(currentUserId);
        
        // 创建转移申请
        PresidentTransfer transfer = new PresidentTransfer();
        transfer.setClubId(clubId);
        transfer.setClubName(club.getName());
        transfer.setCurrentPresidentId(currentUserId);
        transfer.setCurrentPresidentName(currentPresident.getNickname());
        transfer.setNewPresidentId(newPresidentId);
        transfer.setNewPresidentName(newPresident.getNickname());
        transfer.setStatus(PresidentTransferStatusEnum.PENDING.getCode());
        transfer.setReason(reason);
        transferMapper.insert(transfer);
    }

    @Override
    public void auditTransfer(Long transferId, Integer status, String rejectReason) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // 检查当前用户是否是管理员
        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null || !currentUser.getRole().equals(RoleEnum.ADMIN.getCode())) {
            throw new RuntimeException("只有管理员可以审核转移申请");
        }
        
        // 获取转移申请
        PresidentTransfer transfer = transferMapper.selectById(transferId);
        if (transfer == null) {
            throw new RuntimeException("转移申请不存在");
        }
        
        if (!transfer.getStatus().equals(PresidentTransferStatusEnum.PENDING.getCode())) {
            throw new RuntimeException("该申请已处理");
        }
        
        // 更新申请状态
        transfer.setStatus(status);
        transfer.setRejectReason(rejectReason);
        transfer.setProcessTime(LocalDateTime.now());
        transferMapper.updateById(transfer);
        
        // 如果审核通过，更新社团的社长信息
        if (status.equals(PresidentTransferStatusEnum.APPROVED.getCode())) {
            Club club = clubMapper.selectById(transfer.getClubId());
            if (club != null) {
                club.setPresidentId(transfer.getNewPresidentId());
                club.setPresidentName(transfer.getNewPresidentName());
                clubMapper.updateById(club);
            }
        }
    }

    @Override
    public PresidentTransfer getTransferById(Long id) {
        return transferMapper.selectById(id);
    }
}
