package com.club.enums;

import lombok.Getter;

@Getter
public enum PaymentStatusEnum {
    UNPAID(0, "未缴费"),
    PAID(1, "已缴费"),
    OVERDUE(2, "已逾期");

    private final Integer code;
    private final String desc;

    PaymentStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
