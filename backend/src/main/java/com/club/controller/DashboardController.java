package com.club.controller;

import com.club.common.Result;
import com.club.service.DashboardService;
import com.club.vo.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin")
    public Result<DashboardVO> getAdminDashboard() {
        return Result.success(dashboardService.getAdminDashboard());
    }

    @GetMapping("/president/{clubId}")
    public Result<DashboardVO> getPresidentDashboard(@PathVariable Long clubId) {
        return Result.success(dashboardService.getPresidentDashboard(clubId));
    }

    @GetMapping("/stats/member/{clubId}")
    public Result<Map<String, Object>> getMemberStats(@PathVariable Long clubId) {
        return Result.success(dashboardService.getMemberStats(clubId));
    }

    @GetMapping("/stats/activity/{clubId}")
    public Result<Map<String, Object>> getActivityStats(@PathVariable Long clubId) {
        return Result.success(dashboardService.getActivityStats(clubId));
    }

    @GetMapping("/stats/payment/{clubId}")
    public Result<Map<String, Object>> getPaymentStats(@PathVariable Long clubId) {
        return Result.success(dashboardService.getPaymentStats(clubId));
    }
}
