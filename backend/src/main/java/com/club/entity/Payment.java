package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_payment")
public class Payment extends BaseEntity {

    private Long clubId;

    private String clubName;

    private String title;

    private String description;

    private BigDecimal amount;

    private LocalDateTime deadline;

    private Integer status;
}
