package com.club.service;

import com.club.vo.DashboardVO;

import java.util.Map;

public interface DashboardService {

    DashboardVO getAdminDashboard();

    DashboardVO getPresidentDashboard(Long clubId);

    Map<String, Object> getMemberStats(Long clubId);

    Map<String, Object> getActivityStats(Long clubId);

    Map<String, Object> getPaymentStats(Long clubId);
}
