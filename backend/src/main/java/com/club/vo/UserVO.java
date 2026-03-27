package com.club.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer role;
    private String roleName;
    private Integer status;
    private String studentNo;
    private String college;
    private String major;
    private String className;
}
