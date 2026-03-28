package com.club.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.Result;
import com.club.dto.ActivityDTO;
import com.club.entity.ActivitySignUp;
import com.club.service.ActivityService;
import com.club.vo.ActivityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/page")
    public Result<Page<ActivityVO>> getActivityPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) Integer status) {
        return Result.success(activityService.getActivityPage(current, size, keyword, clubId, status));
    }

    @GetMapping("/available")
    public Result<List<ActivityVO>> getAvailableActivities() {
        return Result.success(activityService.getAvailableActivities());
    }

    @GetMapping("/my")
    public Result<List<ActivityVO>> getMyActivities() {
        return Result.success(activityService.getMyActivities());
    }

    @GetMapping("/{id}")
    public Result<ActivityVO> getActivityById(@PathVariable Long id) {
        return Result.success(activityService.getActivityById(id));
    }

    @PostMapping
    public Result<Void> createActivity(@RequestBody ActivityDTO dto, @RequestParam Long clubId) {
        activityService.createActivity(dto, clubId);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateActivity(@RequestBody ActivityDTO dto) {
        activityService.updateActivity(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return Result.success();
    }

    @PutMapping("/audit/{id}")
    public Result<Void> auditActivity(@PathVariable Long id, @RequestParam Integer status,
                                       @RequestParam(required = false) String rejectReason) {
        activityService.auditActivity(id, status, rejectReason);
        return Result.success();
    }

    @PostMapping("/sign-up/{activityId}")
    public Result<Void> signUpActivity(@PathVariable Long activityId) {
        activityService.signUpActivity(activityId);
        return Result.success();
    }

    @DeleteMapping("/sign-up/{activityId}")
    public Result<Void> cancelSignUp(@PathVariable Long activityId) {
        activityService.cancelSignUp(activityId);
        return Result.success();
    }

    @PutMapping("/sign-up/audit/{signUpId}")
    public Result<Void> auditSignUp(@PathVariable Long signUpId, @RequestParam Integer status) {
        activityService.auditSignUp(signUpId, status);
        return Result.success();
    }

    @PutMapping("/check-in/{signUpId}")
    public Result<Void> checkIn(@PathVariable Long signUpId) {
        activityService.checkIn(signUpId);
        return Result.success();
    }

    @GetMapping("/sign-up/list/{activityId}")
    public Result<Page<ActivitySignUp>> getSignUpList(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(activityService.getSignUpList(activityId, current, size));
    }

    @PutMapping("/rating/{signUpId}")
    public Result<Void> submitRating(@PathVariable Long signUpId, 
                                      @RequestParam Integer rating,
                                      @RequestParam(required = false) String comment) {
        activityService.submitRating(signUpId, rating, comment);
        return Result.success();
    }
}
