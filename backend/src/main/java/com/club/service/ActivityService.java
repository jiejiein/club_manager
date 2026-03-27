package com.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.dto.ActivityDTO;
import com.club.entity.ActivitySignUp;
import com.club.vo.ActivityVO;

import java.util.List;

public interface ActivityService {

    Page<ActivityVO> getActivityPage(Integer current, Integer size, String keyword, Long clubId, Integer status);

    ActivityVO getActivityById(Long id);

    void createActivity(ActivityDTO dto, Long clubId);

    void updateActivity(ActivityDTO dto);

    void deleteActivity(Long id);

    void auditActivity(Long id, Integer status, String rejectReason);

    void signUpActivity(Long activityId);

    void cancelSignUp(Long activityId);

    void auditSignUp(Long signUpId, Integer status);

    void checkIn(Long signUpId);

    Page<ActivitySignUp> getSignUpList(Long activityId, Integer current, Integer size);

    List<ActivityVO> getMyActivities();

    List<ActivityVO> getAvailableActivities();

    void submitRating(Long signUpId, Integer rating, String comment);
}
