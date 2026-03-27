package com.club.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationVO {
    private Long id;
    private String title;
    private String content;
    private Integer type;
    private String typeName;
    private Long targetId;
    private Long senderId;
    private String senderName;
    private Integer priority;
    private Integer isRead;
    private LocalDateTime createTime;
    private LocalDateTime readTime;
}
