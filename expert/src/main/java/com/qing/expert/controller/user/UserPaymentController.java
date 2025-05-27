package com.qing.expert.controller.user;

import com.qing.expert.common.result.Result;
import com.qing.expert.dto.PaymentCallbackDTO;
import com.qing.expert.dto.PaymentCreateDTO;
import com.qing.expert.service.PaymentService;
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
import java.util.Map;

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

    @PostMapping("/callback/wechat")
    @Operation(summary = "微信支付回调")
    public String wechatPayCallback(@RequestBody String callbackData) {
        try {
            // TODO: 解析微信支付回调数据
            log.info("收到微信支付回调：{}", callbackData);

            // 这里需要根据微信支付的回调格式解析数据
            // PaymentCallbackDTO dto = parseWechatCallback(callbackData);
            // boolean success = paymentService.handlePaymentCallback(dto);

            // 返回微信要求的格式
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } catch (Exception e) {
            log.error("微信支付回调处理失败", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>";
        }
    }

    @PostMapping("/callback/alipay")
    @Operation(summary = "支付宝支付回调")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        try {
            // TODO: 解析支付宝回调数据
            log.info("收到支付宝支付回调：{}", params);

            // 这里需要根据支付宝的回调格式解析数据
            // PaymentCallbackDTO dto = parseAlipayCallback(params);
            // boolean success = paymentService.handlePaymentCallback(dto);

            return "success";
        } catch (Exception e) {
            log.error("支付宝支付回调处理失败", e);
            return "fail";
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
