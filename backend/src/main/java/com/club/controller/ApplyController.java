package com.club.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.Result;
import com.club.entity.Apply;
import com.club.entity.ClubMember;
import com.club.service.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apply")
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    @GetMapping("/page")
    public Result<Page<Apply>> getApplyPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) Integer status) {
        return Result.success(applyService.getApplyPage(current, size, clubId, status));
    }

    @PostMapping("/{clubId}")
    public Result<Void> submitApply(@PathVariable Long clubId, @RequestParam(required = false) String reason) {
        applyService.submitApply(clubId, reason);
        return Result.success();
    }

    @PutMapping("/audit/{applyId}")
    public Result<Void> auditApply(@PathVariable Long applyId, @RequestParam Integer status,
                                    @RequestParam(required = false) String rejectReason) {
        applyService.auditApply(applyId, status, rejectReason);
        return Result.success();
    }

    @PutMapping("/batch-audit")
    public Result<Void> batchAudit(@RequestBody List<Long> applyIds, @RequestParam Integer status,
                                    @RequestParam(required = false) String rejectReason) {
        applyService.batchAudit(applyIds, status, rejectReason);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<Apply>> getMyApplies() {
        return Result.success(applyService.getMyApplies());
    }

    @GetMapping("/members/{clubId}")
    public Result<Page<ClubMember>> getClubMembers(
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(applyService.getClubMembers(clubId, current, size));
    }

    @DeleteMapping("/member/{clubId}/{userId}")
    public Result<Void> removeMember(@PathVariable Long clubId, @PathVariable Long userId) {
        applyService.removeMember(clubId, userId);
        return Result.success();
    }

    @DeleteMapping("/members/{clubId}")
    public Result<Void> batchRemoveMembers(@PathVariable Long clubId, @RequestBody List<Long> userIds) {
        applyService.batchRemoveMembers(clubId, userIds);
        return Result.success();
    }

    @DeleteMapping("/{applyId}")
    public Result<Void> cancelApply(@PathVariable Long applyId) {
        applyService.cancelApply(applyId);
        return Result.success();
    }
}
