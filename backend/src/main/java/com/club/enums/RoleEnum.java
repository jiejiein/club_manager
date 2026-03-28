package com.club.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN(1, "管理员"),
    PRESIDENT(2, "社长"),
    USER(3, "普通用户");

    private final Integer code;
    private final String desc;

    RoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoleEnum getByCode(Integer code) {
        for (RoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}
