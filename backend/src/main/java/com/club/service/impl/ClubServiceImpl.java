package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.BaseService;
import com.club.common.BusinessException;
import com.club.common.ErrorCode;
import com.club.dto.ClubDTO;
import com.club.entity.Club;
import com.club.entity.ClubMember;
import com.club.entity.ClubType;
import com.club.entity.User;
import com.club.enums.ClubStatusEnum;
import com.club.mapper.ClubMapper;
import com.club.mapper.ClubMemberMapper;
import com.club.mapper.ClubTypeMapper;
import com.club.mapper.UserMapper;
import com.club.service.ClubService;
import com.club.vo.ClubVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class ClubServiceImpl extends BaseService implements ClubService {

    @Autowired
    private ClubMapper clubMapper;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    @Autowired
    private ClubTypeMapper clubTypeMapper;

    @Autowired
    private UserMapper userMapper;

    // ==================== 查询方法 ====================

    @Override
    public Page<ClubVO> getClubPage(Integer current, Integer size, String keyword, Long typeId, Integer status) {
        Page<Club> page = new Page<>(current, size);
        LambdaQueryWrapper<Club> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Club::getName, keyword);
        }
        if (typeId != null) {
            wrapper.eq(Club::getTypeId, typeId);
        }
        if (status != null) {
            wrapper.eq(Club::getStatus, status);
        }
        wrapper.orderByDesc(Club::getCreateTime);
        
        Page<Club> clubPage = clubMapper.selectPage(page, wrapper);
        return convertToVOPage(clubPage);
    }

    @Override
    public ClubVO getClubById(Long id) {
        Club club = clubMapper.selectById(id);
        return convertToVO(club);
    }

    @Override
    public List<ClubVO> getMyClubs() {
        User user = getCurrentUser();
        LambdaQueryWrapper<Club> wrapper = new LambdaQueryWrapper<>();
        
        if (isPresident()) {
            wrapper.eq(Club::getPresidentId, user.getId());
        } else if (isUser()) {
            wrapper.inSql(Club::getId, 
                "SELECT club_id FROM sys_club_member WHERE user_id = " + user.getId() + " AND deleted = 0");
        }
        wrapper.eq(Club::getStatus, ClubStatusEnum.ACTIVE.getCode());
        
        return clubMapper.selectList(wrapper).stream()
            .map(this::convertToVO)
            .toList();
    }

    @Override
    public List<ClubVO> getActiveClubs() {
        return clubMapper.selectList(
            new LambdaQueryWrapper<Club>()
                .eq(Club::getStatus, ClubStatusEnum.ACTIVE.getCode())
                .orderByDesc(Club::getMemberCount)
        ).stream().map(this::convertToVO).toList();
    }

    // ==================== 创建/更新方法 ====================

    @Override
    public void createClub(ClubDTO dto) {
        User user = getCurrentUser();
        ClubType type = getClubTypeByIdOrThrow(dto.getTypeId());
        
        Club club = buildClub(dto, user, type);
        clubMapper.insert(club);
    }

    @Override
    public void updateClub(ClubDTO dto) {
        User user = getCurrentUser();
        
        // 社长只能修改自己的社团
        if (isPresident()) {
            validateClubPresident(dto.getId(), user.getId());
        }
        
        Club club = new Club();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), club);
        
        if (dto.getTypeId() != null) {
            ClubType type = getClubTypeByIdOrThrow(dto.getTypeId());
            club.setTypeName(type.getName());
        }
        
        clubMapper.updateById(club);
    }

    @Override
    public void deleteClub(Long id) {
        validateAdminPermission();
        clubMapper.deleteById(id);
    }

    // ==================== 审核方法 ====================

    @Override
    public void auditClub(Long id, Integer status, String rejectReason) {
        Club club = new Club();
        club.setId(id);
        club.setStatus(status);
        club.setRejectReason(rejectReason);
        clubMapper.updateById(club);
    }

    // ==================== 社团类型方法 ====================

    @Override
    public List<ClubType> getAllClubTypes() {
        return clubTypeMapper.selectList(
            new LambdaQueryWrapper<ClubType>().orderByAsc(ClubType::getSort)
        );
    }

    @Override
    public void createClubType(String name, String description, Integer sort) {
        ClubType type = new ClubType();
        type.setName(name);
        type.setDescription(description);
        type.setSort(sort);
        clubTypeMapper.insert(type);
    }

    @Override
    public void updateClubType(Long id, String name, String description, Integer sort) {
        ClubType type = new ClubType();
        type.setId(id);
        type.setName(name);
        type.setDescription(description);
        type.setSort(sort);
        clubTypeMapper.updateById(type);
    }

    @Override
    public void deleteClubType(Long id) {
        clubTypeMapper.deleteById(id);
    }

    // ==================== 成员相关方法 ====================

    @Override
    public void quitClub(Long clubId) {
        Long userId = getCurrentUserId();
        
        // 删除成员记录
        clubMemberMapper.delete(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
        );
        
        // 更新社团成员数
        Club club = clubMapper.selectById(clubId);
        if (club != null && club.getMemberCount() > 0) {
            club.setMemberCount(club.getMemberCount() - 1);
            clubMapper.updateById(club);
        }
    }

    @Override
    public void transferPresident(Long clubId, Long newPresidentId) {
        Long currentUserId = getCurrentUserId();
        
        Club club = getClubByIdOrThrow(clubId);
        
        // 检查当前用户是否是社团的现任社长
        validateCurrentPresident(club, currentUserId);
        
        // 检查新社长是否是社团成员
        validateIsClubMember(clubId, newPresidentId);
        
        // 获取新社长的用户信息
        User newPresident = getUserByIdOrThrow(newPresidentId);
        
        // 更新社团的社长信息
        club.setPresidentId(newPresidentId);
        club.setPresidentName(newPresident.getNickname());
        clubMapper.updateById(club);
    }

    // ==================== 私有验证方法 ====================

    private void validateAdminPermission() {
        if (!isAdmin()) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
    }

    private void validateClubPresident(Long clubId, Long userId) {
        Club existingClub = getClubByIdOrThrow(clubId);
        if (!existingClub.getPresidentId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_YOUR_CLUB_MANAGE);
        }
    }

    private void validateCurrentPresident(Club club, Long userId) {
        if (!club.getPresidentId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
    }

    private void validateIsClubMember(Long clubId, Long userId) {
        ClubMember existingMember = clubMemberMapper.selectOne(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
        );
        if (existingMember == null) {
            throw new BusinessException(ErrorCode.ALREADY_MEMBER);
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

    private ClubType getClubTypeByIdOrThrow(Long typeId) {
        ClubType type = clubTypeMapper.selectById(typeId);
        if (type == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        return type;
    }

    private User getUserByIdOrThrow(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    private Club buildClub(ClubDTO dto, User user, ClubType type) {
        Club club = new Club();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), club);
        club.setTypeName(type.getName());
        club.setPresidentId(user.getId());
        club.setPresidentName(user.getNickname());
        club.setStatus(ClubStatusEnum.PENDING.getCode());
        club.setMemberCount(0);
        return club;
    }

    private Page<ClubVO> convertToVOPage(Page<Club> clubPage) {
        Page<ClubVO> voPage = new Page<>(
            clubPage.getCurrent(), 
            clubPage.getSize(), 
            clubPage.getTotal()
        );
        voPage.setRecords(clubPage.getRecords().stream()
            .map(this::convertToVO)
            .toList());
        return voPage;
    }

    private ClubVO convertToVO(Club club) {
        if (club == null) return null;
        ClubVO vo = new ClubVO();
        BeanUtils.copyProperties(club, vo);
        vo.setStatusName(getStatusName(club.getStatus()));
        return vo;
    }

    private String getStatusName(Integer status) {
        for (ClubStatusEnum e : ClubStatusEnum.values()) {
            if (e.getCode().equals(status)) {
                return e.getDesc();
            }
        }
        return "";
    }
}
