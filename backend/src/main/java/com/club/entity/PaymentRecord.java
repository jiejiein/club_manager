package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_payment_record")
public class PaymentRecord extends BaseEntity {

    private Long paymentId;

    private Long userId;

    private String userName;

    private BigDecimal amount;

    private Integer status;

    private LocalDateTime payTime;

    private String transactionNo;
}
