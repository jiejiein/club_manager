package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_apply")
public class Apply extends BaseEntity {

    private Long clubId;

    private String clubName;

    private Long userId;

    private String userName;

    private Integer status;

    private String reason;

    private String rejectReason;

    private LocalDateTime processTime;
}
