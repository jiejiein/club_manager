package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_club_type")
public class ClubType extends BaseEntity {

    private String name;

    private String description;

    private Integer sort;
}
