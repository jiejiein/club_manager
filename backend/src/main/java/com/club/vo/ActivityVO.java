package com.club.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityVO {
    private Long id;
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
    private String statusName;
    private String rejectReason;
    private LocalDateTime createTime;
}
