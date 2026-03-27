package com.club.enums;

import lombok.Getter;

@Getter
public enum ActivityStatusEnum {
    PENDING(1, "待审核"),
    PUBLISHED(2, "已发布"),
    ONGOING(3, "进行中"),
    ENDED(4, "已结束");
    
    // 已取消和已删除的活动直接删除记录，不保留状态

    private final Integer code;
    private final String desc;

    ActivityStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
