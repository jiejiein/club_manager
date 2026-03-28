package com.club.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.Result;
import com.club.dto.NotificationDTO;
import com.club.service.NotificationService;
import com.club.vo.NotificationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/page")
    public Result<Page<NotificationVO>> getNotificationPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Long targetId) {
        return Result.success(notificationService.getNotificationPage(current, size, type, targetId));
    }

    @GetMapping("/{id}")
    public Result<NotificationVO> getNotificationById(@PathVariable Long id) {
        return Result.success(notificationService.getNotificationById(id));
    }

    @PostMapping
    public Result<Void> createNotification(@RequestBody NotificationDTO dto) {
        notificationService.createNotification(dto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateNotification(@RequestBody NotificationDTO dto) {
        notificationService.updateNotification(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return Result.success();
    }

    @PutMapping("/read/{notificationId}")
    public Result<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<NotificationVO>> getMyNotifications() {
        return Result.success(notificationService.getMyNotifications());
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        return Result.success(notificationService.getUnreadCount());
    }
}
