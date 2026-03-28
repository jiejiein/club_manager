package com.club.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.common.Result;
import com.club.dto.PaymentDTO;
import com.club.entity.PaymentRecord;
import com.club.service.PaymentService;
import com.club.vo.PaymentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/page")
    public Result<Page<PaymentVO>> getPaymentPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) Integer status) {
        return Result.success(paymentService.getPaymentPage(current, size, clubId, status));
    }

    @GetMapping("/{id}")
    public Result<PaymentVO> getPaymentById(@PathVariable Long id) {
        return Result.success(paymentService.getPaymentById(id));
    }

    @PostMapping
    public Result<Void> createPayment(@RequestBody PaymentDTO dto) {
        paymentService.createPayment(dto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updatePayment(@RequestBody PaymentDTO dto) {
        paymentService.updatePayment(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return Result.success();
    }

    @PostMapping("/pay/{paymentId}")
    public Result<Void> pay(@PathVariable Long paymentId) {
        paymentService.pay(paymentId);
        return Result.success();
    }

    @GetMapping("/records/{paymentId}")
    public Result<Page<PaymentRecord>> getPaymentRecords(
            @PathVariable Long paymentId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(paymentService.getPaymentRecords(paymentId, current, size));
    }

    @GetMapping("/my")
    public Result<List<PaymentVO>> getMyPayments() {
        return Result.success(paymentService.getMyPayments());
    }

    @GetMapping("/my-records")
    public Result<List<PaymentRecord>> getMyPaymentRecords() {
        return Result.success(paymentService.getMyPaymentRecords());
    }

    @PostMapping("/reminder/{paymentId}/{userId}")
    public Result<Void> sendReminder(@PathVariable Long paymentId, @PathVariable Long userId) {
        paymentService.sendReminder(paymentId, userId);
        return Result.success();
    }
}
