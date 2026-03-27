package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.dto.ClubDTO;
import com.club.entity.Club;
import com.club.entity.ClubMember;
import com.club.entity.ClubType;
import com.club.entity.User;
import com.club.enums.ClubStatusEnum;
import com.club.enums.RoleEnum;
import com.club.mapper.ClubMapper;
import com.club.mapper.ClubMemberMapper;
import com.club.mapper.ClubTypeMapper;
import com.club.mapper.UserMapper;
import com.club.service.ClubService;
import com.club.vo.ClubVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class ClubServiceImpl implements ClubService {

    @Autowired
    private ClubMapper clubMapper;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    @Autowired
    private ClubTypeMapper clubTypeMapper;

    @Autowired
    private UserMapper userMapper;

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
        Page<ClubVO> voPage = new Page<>(clubPage.getCurrent(), clubPage.getSize(), clubPage.getTotal());
        voPage.setRecords(clubPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public ClubVO getClubById(Long id) {
        Club club = clubMapper.selectById(id);
        return convertToVO(club);
    }

    @Override
    public void createClub(ClubDTO dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        Club club = new Club();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), club);
        ClubType type = clubTypeMapper.selectById(dto.getTypeId());
        club.setTypeName(type.getName());
        club.setPresidentId(userId);
        club.setPresidentName(user.getNickname());
        club.setStatus(ClubStatusEnum.PENDING.getCode());
        club.setMemberCount(0);
        clubMapper.insert(club);
    }

    @Override
    public void updateClub(ClubDTO dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        // 社长只能修改自己的社团
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            Club existingClub = clubMapper.selectById(dto.getId());
            if (existingClub == null) {
                throw new RuntimeException("社团不存在");
            }
            if (!existingClub.getPresidentId().equals(userId)) {
                throw new RuntimeException("只能修改自己管理的社团");
            }
        }
        
        Club club = new Club();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), club);
        if (dto.getTypeId() != null) {
            ClubType type = clubTypeMapper.selectById(dto.getTypeId());
            club.setTypeName(type.getName());
        }
        clubMapper.updateById(club);
    }

    @Override
    public void deleteClub(Long id) {
        // 只有管理员可以删除社团，社长无权删除
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        if (!user.getRole().equals(RoleEnum.ADMIN.getCode())) {
            throw new RuntimeException("只有管理员可以删除社团");
        }
        
        clubMapper.deleteById(id);
    }

    @Override
    public void auditClub(Long id, Integer status, String rejectReason) {
        Club club = new Club();
        club.setId(id);
        club.setStatus(status);
        club.setRejectReason(rejectReason);
        clubMapper.updateById(club);
    }

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

    @Override
    public List<ClubVO> getMyClubs() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        LambdaQueryWrapper<Club> wrapper = new LambdaQueryWrapper<>();
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            wrapper.eq(Club::getPresidentId, userId);
        } else if (user.getRole().equals(RoleEnum.USER.getCode())) {
            wrapper.inSql(Club::getId, 
                "SELECT club_id FROM sys_club_member WHERE user_id = " + userId + " AND deleted = 0");
        }
        wrapper.eq(Club::getStatus, ClubStatusEnum.ACTIVE.getCode());
        return clubMapper.selectList(wrapper).stream().map(this::convertToVO).toList();
    }

    @Override
    public List<ClubVO> getActiveClubs() {
        return clubMapper.selectList(
            new LambdaQueryWrapper<Club>()
                .eq(Club::getStatus, ClubStatusEnum.ACTIVE.getCode())
                .orderByDesc(Club::getMemberCount)
        ).stream().map(this::convertToVO).toList();
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

    @Override
    public void quitClub(Long clubId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // 检查当前用户是否是社团的现任社长
        Club club = clubMapper.selectById(clubId);
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        if (!club.getPresidentId().equals(currentUserId)) {
            throw new RuntimeException("只有现任社长可以转移社长职位");
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
        
        // 获取新社长的用户信息
        User newPresident = userMapper.selectById(newPresidentId);
        if (newPresident == null) {
            throw new RuntimeException("新社长用户不存在");
        }
        
        // 更新社团的社长信息
        club.setPresidentId(newPresidentId);
        club.setPresidentName(newPresident.getNickname());
        clubMapper.updateById(club);
    }
}
