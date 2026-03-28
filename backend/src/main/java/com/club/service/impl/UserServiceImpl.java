package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.dto.LoginDTO;
import com.club.dto.RegisterDTO;
import com.club.dto.UserUpdateDTO;
import com.club.entity.User;
import com.club.enums.RoleEnum;
import com.club.enums.StatusEnum;
import com.club.mapper.UserMapper;
import com.club.security.JwtUtils;
import com.club.service.UserService;
import com.club.vo.LoginVO;
import com.club.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus().equals(StatusEnum.DISABLE.getCode())) {
            throw new RuntimeException("账号已被禁用");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUser(convertToVO(user));
        return vo;
    }

    @Override
    public void register(RegisterDTO dto) {
        Long count = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(RoleEnum.USER.getCode());
        user.setStatus(StatusEnum.ENABLE.getCode());
        userMapper.insert(user);
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectById(userId);
        return convertToVO(user);
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        return convertToVO(user);
    }

    @Override
    public Page<UserVO> getUserPage(Integer current, Integer size, String keyword, Integer role, Integer status) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                   .or().like(User::getNickname, keyword)
                   .or().like(User::getStudentNo, keyword);
        }
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> userPage = userMapper.selectPage(page, wrapper);
        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public void updateUser(UserUpdateDTO dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = new User();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), user);
        user.setId(userId);
        userMapper.updateById(user);
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        // 获取当前登录用户ID
        Long currentUserId = (Long) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        
        // 防止禁用自己
        if (id.equals(currentUserId) && status.equals(StatusEnum.DISABLE.getCode())) {
            throw new RuntimeException("不能禁用当前登录账号");
        }
        
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void updateUserRole(Long id, Integer role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(updateUser);
    }

    private UserVO convertToVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setRoleName(RoleEnum.getByCode(user.getRole()).getDesc());
        return vo;
    }
}
