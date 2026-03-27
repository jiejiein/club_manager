package com.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.entity.SystemLog;

public interface SystemLogService {

    Page<SystemLog> getLogPage(Integer current, Integer size, String username, Integer status);

    void saveLog(SystemLog log);

    void deleteLog(Long id);
}
