package com.club.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentVO {
    private Long id;
    private Long clubId;
    private String clubName;
    private String title;
    private String description;
    private BigDecimal amount;
    private LocalDateTime deadline;
    private Integer status;
    private String statusName;
    private LocalDateTime createTime;
}
