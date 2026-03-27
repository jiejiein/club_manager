package com.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.entity.PresidentTransfer;

public interface PresidentTransferService {

    Page<PresidentTransfer> getTransferPage(Integer current, Integer size, Integer status);

    void createTransfer(Long clubId, Long newPresidentId, String reason);

    void auditTransfer(Long transferId, Integer status, String rejectReason);

    PresidentTransfer getTransferById(Long id);
}
