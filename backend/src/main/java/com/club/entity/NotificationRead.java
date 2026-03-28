package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notification_read")
public class NotificationRead extends BaseEntity {

    private Long notificationId;

    private Long userId;

    private Integer isRead;

    private LocalDateTime readTime;
}
