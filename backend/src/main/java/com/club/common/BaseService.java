package com.club.common;

import com.club.entity.User;
import com.club.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 基础服务类
 * 封装通用的业务逻辑
 */
public abstract class BaseService {

    @Autowired
    protected UserMapper userMapper;

    /**
     * 获取当前登录用户ID
     */
    protected Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 获取当前登录用户信息
     */
    protected User getCurrentUser() {
        Long userId = getCurrentUserId();
        return userMapper.selectById(userId);
    }

    /**
     * 检查当前用户是否是指定角色
     */
    protected boolean isRole(Integer roleCode) {
        User user = getCurrentUser();
        return user != null && user.getRole().equals(roleCode);
    }

    /**
     * 检查当前用户是否是管理员
     */
    protected boolean isAdmin() {
        return isRole(1);
    }

    /**
     * 检查当前用户是否是社长
     */
    protected boolean isPresident() {
        return isRole(2);
    }

    /**
     * 检查当前用户是否是普通用户
     */
    protected boolean isUser() {
        return isRole(3);
    }
}
