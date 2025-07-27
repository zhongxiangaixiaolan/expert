package com.qing.expert.controller.user;

import com.qing.expert.common.result.Result;
import com.qing.expert.dto.PaymentCallbackDTO;
import com.qing.expert.dto.PaymentCreateDTO;
import com.qing.expert.entity.PaymentRecord;
import com.qing.expert.service.PaymentService;
import com.qing.expert.service.WechatPayService;
import com.qing.expert.vo.PaymentRecordVO;
import com.qing.expert.vo.PaymentResultVO;
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
        PaymentResultVO result = paymentService.createPayment(dto);
        return Result.success(result);
    }

    @GetMapping("/query/{paymentNo}")
        PaymentResultVO result = paymentService.queryPaymentResult(paymentNo);
        return Result.success(result);
    }

    @PostMapping("/cancel/{paymentNo}")
        return Result.success(success);
    }

    @GetMapping("/records/{userId}")
        List<PaymentRecordVO> records = paymentService.getUserPaymentRecords(userId, paymentStatus, limit);
        return Result.success(records);
    }

    @GetMapping("/status/{paymentNo}")
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
        // 充值时订单ID为空
        dto.setOrderId(null);
        dto.setPaymentDesc("余额充值");

        PaymentResultVO result = paymentService.createPayment(dto);
        return Result.success(result);
    }
}
