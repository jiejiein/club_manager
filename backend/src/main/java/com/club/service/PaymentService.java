package com.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.dto.PaymentDTO;
import com.club.entity.PaymentRecord;
import com.club.vo.PaymentVO;

import java.util.List;

public interface PaymentService {

    Page<PaymentVO> getPaymentPage(Integer current, Integer size, Long clubId, Integer status);

    PaymentVO getPaymentById(Long id);

    void createPayment(PaymentDTO dto);

    void updatePayment(PaymentDTO dto);

    void deletePayment(Long id);

    void pay(Long paymentId);

    Page<PaymentRecord> getPaymentRecords(Long paymentId, Integer current, Integer size);

    List<PaymentVO> getMyPayments();

    List<PaymentRecord> getMyPaymentRecords();

    void sendReminder(Long paymentId, Long userId);
}
