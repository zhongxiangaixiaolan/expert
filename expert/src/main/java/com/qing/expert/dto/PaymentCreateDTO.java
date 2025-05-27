package com.qing.expert.dto;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建支付DTO
 */
@Data
public class PaymentCreateDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 订单ID（充值时可为空）
     */
    private Long orderId;

    /**
     * 支付类型
     */
    @NotBlank(message = "支付类型不能为空")
    private String paymentType;

    /**
     * 支付金额
     */
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0.01")
    private BigDecimal paymentAmount;

    /**
     * 支付描述
     */
    private String paymentDesc;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 返回地址
     */
    private String returnUrl;

    /**
     * 备注
     */
    private String remark;
}
