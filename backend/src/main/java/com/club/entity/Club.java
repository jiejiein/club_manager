package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_club")
public class Club extends BaseEntity {

    private String name;

    private Long typeId;

    private String typeName;

    private Long presidentId;

    private String presidentName;

    private String description;

    private String logo;

    private Integer status;

    private Integer memberCount;

    private String rejectReason;
}
