package com.club.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;

    @NotNull(message = "社团ID不能为空")
    private Long clubId;

    @NotBlank(message = "缴费标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "缴费金额不能为空")
    private BigDecimal amount;

    @NotNull(message = "截止日期不能为空")
    private LocalDateTime deadline;
}
