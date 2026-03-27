package com.club.enums;

import lombok.Getter;

@Getter
public enum PresidentTransferStatusEnum {
    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝");

    private final Integer code;
    private final String desc;

    PresidentTransferStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
