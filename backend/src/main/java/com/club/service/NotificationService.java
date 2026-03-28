package com.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.dto.NotificationDTO;
import com.club.vo.NotificationVO;

import java.util.List;

public interface NotificationService {

    Page<NotificationVO> getNotificationPage(Integer current, Integer size, Integer type, Long targetId);

    NotificationVO getNotificationById(Long id);

    void createNotification(NotificationDTO dto);

    void updateNotification(NotificationDTO dto);

    void deleteNotification(Long id);

    void markAsRead(Long notificationId);

    void markAllAsRead();

    List<NotificationVO> getMyNotifications();

    Long getUnreadCount();
}
