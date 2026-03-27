package com.club.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.Result;
import com.club.entity.SystemLog;
import com.club.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system-log")
public class SystemLogController {

    @Autowired
    private SystemLogService logService;

    @GetMapping("/page")
    public Result<Page<SystemLog>> getLogPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        return Result.success(logService.getLogPage(current, size, username, status));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteLog(@PathVariable Long id) {
        logService.deleteLog(id);
        return Result.success();
    }
}
