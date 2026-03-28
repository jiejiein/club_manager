package com.club.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    private Long id;

    @NotBlank(message = "活动标题不能为空")
    private String title;

    private String description;

    private String cover;

    @NotBlank(message = "活动地点不能为空")
    private String location;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotNull(message = "报名开始时间不能为空")
    private LocalDateTime signUpStart;

    @NotNull(message = "报名截止时间不能为空")
    private LocalDateTime signUpEnd;

    @NotNull(message = "最大参与人数不能为空")
    private Integer maxParticipants;
}
