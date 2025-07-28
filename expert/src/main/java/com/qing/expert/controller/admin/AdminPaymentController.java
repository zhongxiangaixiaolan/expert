package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.common.result.Result;
import com.qing.expert.service.PaymentService;
import com.qing.expert.vo.PaymentRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理端支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/payment")
@RequiredArgsConstructor
@Validated
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping("/records")
    public Result<IPage<PaymentRecordVO>> getPaymentRecordPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) Integer paymentStatus,
            @RequestParam(required = false) String userNickname,
            @RequestParam(required = false) String userPhone,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Page<PaymentRecordVO> page = new Page<>(current, size);
        IPage<PaymentRecordVO> result = paymentService.getPaymentRecordPage(
                page, userId, orderId, paymentType, paymentStatus, userNickname, userPhone, startTime, endTime);
        return Result.success(result);
    }

    @GetMapping("/records/{id}")
    public Result<PaymentRecordVO> getPaymentRecord(@PathVariable Long id) {
        PaymentRecordVO paymentRecord = paymentService.getPaymentRecordById(id);
        return Result.success(paymentRecord);
    }

    @PostMapping("/refund")
    public Result<Boolean> refundPayment(
            @RequestParam @NotBlank String paymentNo,
            @RequestParam @NotNull BigDecimal refundAmount,
            @RequestParam String refundReason) {
        boolean success = paymentService.refundPayment(paymentNo, refundAmount, refundReason);
        return Result.success(success);
    }

    @PostMapping("/cancel")
    public Result<Boolean> cancelPayment(@RequestParam @NotBlank String paymentNo) {
        boolean success = paymentService.cancelPayment(paymentNo);
        return Result.success(success);
    }

    @GetMapping("/statistics")
    public Result<List<Map<String, Object>>> getPaymentStatistics(
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<Map<String, Object>> statistics = paymentService.getPaymentStatistics(paymentType, startTime, endTime);
        return Result.success(statistics);
    }

    @GetMapping("/summary")
    public Result<Map<String, Object>> getPaymentSummary(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> summary = paymentService.getPaymentSummary(startTime, endTime);
        return Result.success(summary);
    }

    @PostMapping("/handle-expired")
    public Result<Integer> handleExpiredPayments(@RequestParam(defaultValue = "30") Integer minutes) {
        int count = paymentService.handleExpiredPayments(minutes);
        return Result.success(count);
    }

    @GetMapping("/user/{userId}/amount")
    public Result<BigDecimal> getUserTotalPaymentAmount(@PathVariable Long userId) {
        BigDecimal amount = paymentService.getUserTotalPaymentAmount(userId);
        return Result.success(amount);
    }
}
