package com.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.dto.LoginDTO;
import com.club.dto.RegisterDTO;
import com.club.dto.UserUpdateDTO;
import com.club.vo.LoginVO;
import com.club.vo.UserVO;

public interface UserService {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);

    UserVO getCurrentUser();

    UserVO getUserById(Long id);

    Page<UserVO> getUserPage(Integer current, Integer size, String keyword, Integer role, Integer status);

    void updateUser(UserUpdateDTO dto);

    void updateUserStatus(Long id, Integer status);

    void updateUserRole(Long id, Integer role);

    void resetPassword(Long id, String newPassword);

    void updatePassword(Long id, String oldPassword, String newPassword);
}
