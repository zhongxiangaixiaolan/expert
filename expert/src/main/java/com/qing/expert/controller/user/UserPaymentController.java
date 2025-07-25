package com.qing.expert.controller.user;

import com.qing.expert.common.result.Result;
import com.qing.expert.dto.PaymentCallbackDTO;
import com.qing.expert.dto.PaymentCreateDTO;
import com.qing.expert.entity.PaymentRecord;
import com.qing.expert.service.PaymentService;
import com.qing.expert.service.WechatPayService;
import com.qing.expert.vo.PaymentRecordVO;
import com.qing.expert.vo.PaymentResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户端支付控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/user/payment")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户端支付管理", description = "创建支付、查询支付结果、支付记录等功能")
public class UserPaymentController {

    private final PaymentService paymentService;
    private final WechatPayService wechatPayService;

    @PostMapping("/create")
    @Operation(summary = "创建支付订单")
    public Result<PaymentResultVO> createPayment(@Valid @RequestBody PaymentCreateDTO dto) {
        PaymentResultVO result = paymentService.createPayment(dto);
        return Result.success(result);
    }

    @GetMapping("/query/{paymentNo}")
    @Operation(summary = "查询支付结果")
    public Result<PaymentResultVO> queryPaymentResult(
            @Parameter(description = "支付单号") @PathVariable @NotBlank String paymentNo) {
        PaymentResultVO result = paymentService.queryPaymentResult(paymentNo);
        return Result.success(result);
    }

    @PostMapping("/cancel/{paymentNo}")
    @Operation(summary = "取消支付")
    public Result<Boolean> cancelPayment(
            @Parameter(description = "支付单号") @PathVariable @NotBlank String paymentNo,
            @Parameter(description = "取消原因") @RequestParam(defaultValue = "用户主动取消") String reason) {
        boolean success = paymentService.cancelPayment(paymentNo, reason);
        return Result.success(success);
    }

    @GetMapping("/records/{userId}")
    @Operation(summary = "查询用户支付记录")
    public Result<List<PaymentRecordVO>> getUserPaymentRecords(
            @Parameter(description = "用户ID") @PathVariable @NotNull Long userId,
            @Parameter(description = "支付状态") @RequestParam(required = false) String paymentStatus,
            @Parameter(description = "查询数量限制") @RequestParam(defaultValue = "20") Integer limit) {
        List<PaymentRecordVO> records = paymentService.getUserPaymentRecords(userId, paymentStatus, limit);
        return Result.success(records);
    }

    @GetMapping("/status/{paymentNo}")
    @Operation(summary = "查询支付状态")
    public Result<PaymentRecordVO> queryPaymentStatus(@PathVariable String paymentNo) {
        // 先查询本地支付状态
        PaymentRecord record = paymentService.getByPaymentNo(paymentNo);
        if (record == null) {
            return Result.error("支付记录不存在");
        }

        // 如果是待支付状态，主动查询微信支付状态
        if ("PENDING".equals(record.getPaymentStatus())) {
            try {
                var statusResult = wechatPayService.queryPaymentStatus(paymentNo);
                if (statusResult.isSuccess()) {
                    // 支付成功，更新支付记录
                    paymentService.updatePaymentSuccess(paymentNo, statusResult.getTransactionId());
                    // 重新查询更新后的状态
                    record = paymentService.getByPaymentNo(paymentNo);
                } else if ("CLOSED".equals(statusResult.getTradeState())
                        || "REVOKED".equals(statusResult.getTradeState())) {
                    // 支付关闭或撤销，更新支付记录
                    paymentService.updatePaymentFailed(paymentNo, "支付已关闭");
                    // 重新查询更新后的状态
                    record = paymentService.getByPaymentNo(paymentNo);
                }
            } catch (Exception e) {
                log.error("查询微信支付状态失败", e);
            }
        }

        // 转换为VO对象
        PaymentRecordVO recordVO = paymentService.convertToVO(record);
        return Result.success(recordVO);
    }

    @PostMapping("/callback/wechat")
    @Operation(summary = "微信支付回调")
    public String wechatPayCallback(@RequestBody String callbackData) {
        try {
            log.info("收到微信支付回调：{}", callbackData);

            // 调用微信支付服务处理回调
            boolean success = wechatPayService.handlePaymentNotify(callbackData);

            if (success) {
                log.info("微信支付回调处理成功");
                // APIv3返回JSON格式
                return "{\"code\":\"SUCCESS\",\"message\":\"成功\"}";
            } else {
                log.error("微信支付回调处理失败");
                return "{\"code\":\"FAIL\",\"message\":\"失败\"}";
            }
        } catch (Exception e) {
            log.error("微信支付回调处理异常", e);
            return "{\"code\":\"FAIL\",\"message\":\"处理异常\"}";
        }
    }

    @PostMapping("/recharge")
    @Operation(summary = "余额充值")
    public Result<PaymentResultVO> recharge(@Valid @RequestBody PaymentCreateDTO dto) {
        // 充值时订单ID为空
        dto.setOrderId(null);
        dto.setPaymentDesc("余额充值");

        PaymentResultVO result = paymentService.createPayment(dto);
        return Result.success(result);
    }
}
