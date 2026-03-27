package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_president_transfer")
public class PresidentTransfer extends BaseEntity {

    private Long clubId;

    private String clubName;

    private Long currentPresidentId;

    private String currentPresidentName;

    private Long newPresidentId;

    private String newPresidentName;

    private Integer status;

    private String reason;

    private String rejectReason;

    private LocalDateTime processTime;
}
