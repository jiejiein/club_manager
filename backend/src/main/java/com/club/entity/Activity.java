package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_activity")
public class Activity extends BaseEntity {

    private Long clubId;

    private String clubName;

    private String title;

    private String description;

    private String cover;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime signUpStart;

    private LocalDateTime signUpEnd;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private Integer status;

    private String rejectReason;
}
