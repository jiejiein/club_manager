package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_activity_sign_up")
public class ActivitySignUp extends BaseEntity {

    private Long activityId;

    private Long userId;

    private String userName;

    private Integer status;

    private LocalDateTime signUpTime;

    private LocalDateTime checkInTime;

    private Integer checked;

    private Integer rating;

    private String comment;
}
