package com.club.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubVO {
    private Long id;
    private String name;
    private Long typeId;
    private String typeName;
    private Long presidentId;
    private String presidentName;
    private String description;
    private String logo;
    private Integer status;
    private String statusName;
    private Integer memberCount;
    private String rejectReason;
    private LocalDateTime createTime;
}
