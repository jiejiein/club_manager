package com.club.vo;

import lombok.Data;

@Data
public class DashboardVO {
    private Long totalClubs;
    private Long totalUsers;
    private Long totalActivities;
    private Long totalMembers;
    private Long pendingApplies;
    private Long pendingActivities;
}
