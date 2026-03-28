package com.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.entity.SystemLog;
import com.club.mapper.SystemLogMapper;
import com.club.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemLogServiceImpl implements SystemLogService {

    @Autowired
    private SystemLogMapper logMapper;

    @Override
    public Page<SystemLog> getLogPage(Integer current, Integer size, String username, Integer status) {
        Page<SystemLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(SystemLog::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(SystemLog::getStatus, status);
        }
        
        wrapper.orderByDesc(SystemLog::getCreateTime);
        return logMapper.selectPage(page, wrapper);
    }

    @Override
    public void saveLog(SystemLog log) {
        logMapper.insert(log);
    }

    @Override
    public void deleteLog(Long id) {
        logMapper.deleteById(id);
    }
}
