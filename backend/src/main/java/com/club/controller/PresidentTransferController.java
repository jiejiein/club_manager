package com.club.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.Result;
import com.club.entity.PresidentTransfer;
import com.club.service.PresidentTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/president-transfer")
public class PresidentTransferController {

    @Autowired
    private PresidentTransferService transferService;

    @GetMapping("/page")
    public Result<Page<PresidentTransfer>> getTransferPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return Result.success(transferService.getTransferPage(current, size, status));
    }

    @GetMapping("/{id}")
    public Result<PresidentTransfer> getTransferById(@PathVariable Long id) {
        return Result.success(transferService.getTransferById(id));
    }

    @PostMapping
    public Result<Void> createTransfer(@RequestParam Long clubId, 
                                      @RequestParam Long newPresidentId,
                                      @RequestParam(required = false) String reason) {
        transferService.createTransfer(clubId, newPresidentId, reason);
        return Result.success();
    }

    @PutMapping("/audit/{id}")
    public Result<Void> auditTransfer(@PathVariable Long id, 
                                     @RequestParam Integer status,
                                     @RequestParam(required = false) String rejectReason) {
        transferService.auditTransfer(id, status, rejectReason);
        return Result.success();
    }
}
