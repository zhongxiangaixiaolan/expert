package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.common.result.Result;
import com.qing.expert.service.PaymentService;
import com.qing.expert.vo.PaymentRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "管理端支付管理", description = "支付记录查询、退款处理、统计分析等功能")
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping("/records/page")
    @Operation(summary = "分页查询支付记录列表", description = "支持按用户、订单、支付类型、状态等条件筛选")
    public Result<IPage<PaymentRecordVO>> getPaymentRecordPage(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "订单ID") @RequestParam(required = false) Long orderId,
            @Parameter(description = "支付类型") @RequestParam(required = false) String paymentType,
            @Parameter(description = "支付状态") @RequestParam(required = false) String paymentStatus,
            @Parameter(description = "用户昵称") @RequestParam(required = false) String userNickname,
            @Parameter(description = "用户手机号") @RequestParam(required = false) String userPhone,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        Page<PaymentRecordVO> page = new Page<>(current, size);
        IPage<PaymentRecordVO> result = paymentService.getPaymentRecordPage(
                page, userId, orderId, paymentType, paymentStatus, userNickname, userPhone, startTime, endTime);
        return Result.success(result);
    }

    @GetMapping("/records/{id}")
    @Operation(summary = "根据ID查询支付记录详情")
    public Result<PaymentRecordVO> getPaymentRecordById(
            @Parameter(description = "支付记录ID") @PathVariable @NotNull Long id) {
        PaymentRecordVO paymentRecord = paymentService.getPaymentRecordById(id);
        return Result.success(paymentRecord);
    }

    @PostMapping("/refund")
    @Operation(summary = "申请退款")
    public Result<Boolean> refundPayment(
            @Parameter(description = "支付单号") @RequestParam @NotBlank String paymentNo,
            @Parameter(description = "退款金额") @RequestParam @NotNull BigDecimal refundAmount,
            @Parameter(description = "退款原因") @RequestParam String refundReason) {
        boolean success = paymentService.refundPayment(paymentNo, refundAmount, refundReason);
        return Result.success(success);
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消支付")
    public Result<Boolean> cancelPayment(
            @Parameter(description = "支付单号") @RequestParam @NotBlank String paymentNo,
            @Parameter(description = "取消原因") @RequestParam String reason) {
        boolean success = paymentService.cancelPayment(paymentNo, reason);
        return Result.success(success);
    }

    @GetMapping("/statistics")
    @Operation(summary = "查询支付统计数据")
    public Result<List<Map<String, Object>>> getPaymentStatistics(
            @Parameter(description = "支付类型") @RequestParam(required = false) String paymentType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<Map<String, Object>> statistics = paymentService.getPaymentStatistics(paymentType, startTime, endTime);
        return Result.success(statistics);
    }

    @GetMapping("/summary")
    @Operation(summary = "查询平台支付汇总数据")
    public Result<Map<String, Object>> getPlatformPaymentSummary(
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> summary = paymentService.getPlatformPaymentSummary(startTime, endTime);
        return Result.success(summary);
    }

    @PostMapping("/handle-expired")
    @Operation(summary = "处理超时支付订单")
    public Result<Integer> handleExpiredPayments(
            @Parameter(description = "超时分钟数", example = "30") @RequestParam(defaultValue = "30") Integer minutes) {
        int count = paymentService.handleExpiredPayments(minutes);
        return Result.success(count);
    }

    @GetMapping("/user/{userId}/amount")
    @Operation(summary = "统计用户支付金额")
    public Result<BigDecimal> sumUserPaymentAmount(
            @Parameter(description = "用户ID") @PathVariable @NotNull Long userId,
            @Parameter(description = "支付状态") @RequestParam(required = false) String paymentStatus,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        BigDecimal amount = paymentService.sumUserPaymentAmount(userId, paymentStatus, startTime, endTime);
        return Result.success(amount);
    }
}
