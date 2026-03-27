package com.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_club_member")
public class ClubMember extends BaseEntity {

    private Long clubId;

    private Long userId;

    private String userName;

    private Integer status;

    private LocalDateTime joinTime;
}
