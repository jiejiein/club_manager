package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notification")
public class Notification extends BaseEntity {

    private String title;

    private String content;

    private Integer type;

    private Long targetId;

    private Long senderId;

    private String senderName;

    private Integer priority;
}
