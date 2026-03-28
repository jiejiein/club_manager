package com.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.entity.Apply;
import com.club.entity.ClubMember;

import java.util.List;

public interface ApplyService {

    Page<Apply> getApplyPage(Integer current, Integer size, Long clubId, Integer status);

    void submitApply(Long clubId, String reason);

    void auditApply(Long applyId, Integer status, String rejectReason);

    void batchAudit(List<Long> applyIds, Integer status, String rejectReason);

    List<Apply> getMyApplies();

    Page<ClubMember> getClubMembers(Long clubId, Integer current, Integer size);

    void removeMember(Long clubId, Long userId);

    void batchRemoveMembers(Long clubId, List<Long> userIds);

    void cancelApply(Long applyId);
}
