package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.BaseService;
import com.club.common.BusinessException;
import com.club.common.ErrorCode;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class UserServiceImpl extends BaseService implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // ==================== 认证相关方法 ====================

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = getUserByUsernameOrThrow(dto.getUsername());
        
        validatePassword(dto.getPassword(), user.getPassword());
        validateUserStatus(user);
        
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        return buildLoginVO(user, token);
    }

    @Override
    public void register(RegisterDTO dto) {
        validateUsernameNotExists(dto.getUsername());
        
        User user = buildUser(dto);
        userMapper.insert(user);
    }

    // ==================== 查询方法 ====================

    @Override
    public UserVO getCurrentUserInfo() {
        Long userId = getCurrentUserId();
        User user = getUserByIdOrThrow(userId);
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
        return convertToVOPage(userPage);
    }

    // ==================== 更新方法 ====================

    @Override
    public void updateUser(UserUpdateDTO dto) {
        Long userId = getCurrentUserId();
        
        User user = new User();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), user);
        user.setId(userId);
        userMapper.updateById(user);
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        Long currentUserId = getCurrentUserId();
        
        // 防止禁用自己
        validateNotDisableSelf(id, status, currentUserId);
        
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

    // ==================== 密码相关方法 ====================

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = getUserByIdOrThrow(id);
        
        validateOldPassword(oldPassword, user.getPassword());
        
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(updateUser);
    }

    // ==================== 私有验证方法 ====================

    private void validatePassword(String inputPassword, String storedPassword) {
        if (!passwordEncoder.matches(inputPassword, storedPassword)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
    }

    private void validateUserStatus(User user) {
        if (user.getStatus().equals(StatusEnum.DISABLE.getCode())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateUsernameNotExists(String username) {
        Long count = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
    }

    private void validateNotDisableSelf(Long id, Integer status, Long currentUserId) {
        if (id.equals(currentUserId) && status.equals(StatusEnum.DISABLE.getCode())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
    }

    private void validateOldPassword(String oldPassword, String storedPassword) {
        if (!passwordEncoder.matches(oldPassword, storedPassword)) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_ERROR);
        }
    }

    // ==================== 私有工具方法 ====================

    private User getUserByIdOrThrow(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    private User getUserByUsernameOrThrow(String username) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    private User buildUser(RegisterDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(Objects.requireNonNull(dto), user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(RoleEnum.USER.getCode());
        user.setStatus(StatusEnum.ENABLE.getCode());
        return user;
    }

    private LoginVO buildLoginVO(User user, String token) {
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUser(convertToVO(user));
        return vo;
    }

    private Page<UserVO> convertToVOPage(Page<User> userPage) {
        Page<UserVO> voPage = new Page<>(
            userPage.getCurrent(), 
            userPage.getSize(), 
            userPage.getTotal()
        );
        voPage.setRecords(userPage.getRecords().stream()
            .map(this::convertToVO)
            .toList());
        return voPage;
    }

    private UserVO convertToVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setRoleName(RoleEnum.getByCode(user.getRole()).getDesc());
        return vo;
    }
}
