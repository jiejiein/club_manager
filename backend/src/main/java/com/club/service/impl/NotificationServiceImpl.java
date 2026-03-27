package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.dto.NotificationDTO;
import com.club.entity.Notification;
import com.club.entity.NotificationRead;
import com.club.entity.User;
import com.club.enums.RoleEnum;
import com.club.mapper.NotificationMapper;
import com.club.mapper.NotificationReadMapper;
import com.club.mapper.UserMapper;
import com.club.service.NotificationService;
import com.club.vo.NotificationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private NotificationReadMapper notificationReadMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<NotificationVO> getNotificationPage(Integer current, Integer size, Integer type, Long targetId) {
        Page<Notification> page = new Page<>(current, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Notification::getType, type);
        }
        if (targetId != null) {
            wrapper.eq(Notification::getTargetId, targetId);
        }
        wrapper.orderByDesc(Notification::getCreateTime);
        Page<Notification> notificationPage = notificationMapper.selectPage(page, wrapper);
        Page<NotificationVO> voPage = new Page<>(notificationPage.getCurrent(), notificationPage.getSize(), notificationPage.getTotal());
        voPage.setRecords(notificationPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public NotificationVO getNotificationById(Long id) {
        Notification notification = notificationMapper.selectById(id);
        return convertToVO(notification);
    }

    @Override
    public void createNotification(NotificationDTO dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        // 根据用户角色限制通知类型
        if (user.getRole().equals(RoleEnum.USER.getCode()) || user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            // 社长和学生只能发布社团通知
            dto.setType(2); // 社团通知
        }
        
        // 根据用户角色限制优先级
        if (user.getRole().equals(RoleEnum.USER.getCode())) {
            // 学生只能发布普通优先级的通知
            dto.setPriority(0); // 普通优先级
        } else if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            // 社长只能发布普通和重要优先级的通知
            if (dto.getPriority() == 2) {
                dto.setPriority(1); // 紧急优先级降级为重要
            }
        }
        
        Notification notification = new Notification();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), notification);
        notification.setSenderId(userId);
        notification.setSenderName(user.getNickname());
        notificationMapper.insert(notification);
    }

    @Override
    public void updateNotification(NotificationDTO dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        // 社长只能修改自己发布的通知
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode()) || user.getRole().equals(RoleEnum.USER.getCode())) {
            Notification existing = notificationMapper.selectById(dto.getId());
            if (existing == null) {
                throw new RuntimeException("通知不存在");
            }
            if (!existing.getSenderId().equals(userId)) {
                throw new RuntimeException("只能修改自己发布的通知");
            }
        }
        
        // 根据用户角色限制通知类型
        if (user.getRole().equals(RoleEnum.USER.getCode()) || user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            // 社长和学生只能发布社团通知
            dto.setType(2); // 社团通知
        }
        
        // 根据用户角色限制优先级
        if (user.getRole().equals(RoleEnum.USER.getCode())) {
            // 学生只能发布普通优先级的通知
            dto.setPriority(0); // 普通优先级
        } else if (user.getRole().equals(RoleEnum.PRESIDENT.getCode())) {
            // 社长只能发布普通和重要优先级的通知
            if (dto.getPriority() == 2) {
                dto.setPriority(1); // 紧急优先级降级为重要
            }
        }
        
        Notification notification = new Notification();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), notification);
        notificationMapper.updateById(notification);
    }

    @Override
    public void deleteNotification(Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        
        // 社长和学生只能删除自己发布的通知
        if (user.getRole().equals(RoleEnum.PRESIDENT.getCode()) || user.getRole().equals(RoleEnum.USER.getCode())) {
            Notification existing = notificationMapper.selectById(id);
            if (existing == null) {
                throw new RuntimeException("通知不存在");
            }
            if (!existing.getSenderId().equals(userId)) {
                throw new RuntimeException("只能删除自己发布的通知");
            }
        }
        
        notificationMapper.deleteById(id);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        NotificationRead read = notificationReadMapper.selectOne(
            new LambdaQueryWrapper<NotificationRead>()
                .eq(NotificationRead::getNotificationId, notificationId)
                .eq(NotificationRead::getUserId, userId)
        );
        
        if (read == null) {
            read = new NotificationRead();
            read.setNotificationId(notificationId);
            read.setUserId(userId);
            read.setIsRead(1);
            read.setReadTime(LocalDateTime.now());
            notificationReadMapper.insert(read);
        } else {
            read.setIsRead(1);
            read.setReadTime(LocalDateTime.now());
            notificationReadMapper.updateById(read);
        }
    }

    @Override
    public void markAllAsRead() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Notification> notifications = notificationMapper.selectList(null);
        for (Notification notification : notifications) {
            NotificationRead read = notificationReadMapper.selectOne(
                new LambdaQueryWrapper<NotificationRead>()
                    .eq(NotificationRead::getNotificationId, notification.getId())
                    .eq(NotificationRead::getUserId, userId)
            );
            if (read == null) {
                read = new NotificationRead();
                read.setNotificationId(notification.getId());
                read.setUserId(userId);
                read.setIsRead(1);
                read.setReadTime(LocalDateTime.now());
                notificationReadMapper.insert(read);
            } else if (read.getIsRead() == 0) {
                read.setIsRead(1);
                read.setReadTime(LocalDateTime.now());
                notificationReadMapper.updateById(read);
            }
        }
    }

    @Override
    public List<NotificationVO> getMyNotifications() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        List<Notification> notifications = notificationMapper.selectList(
            new LambdaQueryWrapper<Notification>()
                .eq(Notification::getType, 1)
                .or()
                .eq(Notification::getType, 2)
                .eq(Notification::getTargetId, getClubIdByPresident(userId))
                .orderByDesc(Notification::getCreateTime)
        );
        
        return notifications.stream().map(n -> {
            NotificationVO vo = convertToVO(n);
            NotificationRead read = notificationReadMapper.selectOne(
                new LambdaQueryWrapper<NotificationRead>()
                    .eq(NotificationRead::getNotificationId, n.getId())
                    .eq(NotificationRead::getUserId, userId)
            );
            if (read != null) {
                vo.setIsRead(read.getIsRead());
                vo.setReadTime(read.getReadTime());
            } else {
                vo.setIsRead(0);
            }
            return vo;
        }).toList();
    }

    @Override
    public Long getUnreadCount() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long total = notificationMapper.selectCount(
            new LambdaQueryWrapper<Notification>()
                .eq(Notification::getType, 1)
        );
        Long readCount = notificationReadMapper.selectCount(
            new LambdaQueryWrapper<NotificationRead>()
                .eq(NotificationRead::getUserId, userId)
                .eq(NotificationRead::getIsRead, 1)
        );
        return total - readCount;
    }

    private Long getClubIdByPresident(Long userId) {
        return null;
    }

    private NotificationVO convertToVO(Notification notification) {
        if (notification == null) return null;
        NotificationVO vo = new NotificationVO();
        BeanUtils.copyProperties(notification, vo);
        vo.setTypeName(notification.getType() == 1 ? "系统通知" : "社团通知");
        return vo;
    }
}
