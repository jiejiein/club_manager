package com.club.enums;

import lombok.Getter;

@Getter
public enum ClubStatusEnum {
    PENDING(0, "待审核"),
    ACTIVE(1, "正常运营"),
    SUSPENDED(2, "已注销");

    private final Integer code;
    private final String desc;

    ClubStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
