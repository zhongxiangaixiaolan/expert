package com.qing.expert.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付回调DTO
 */
@Data
public class PaymentCallbackDTO {

    /**
     * 支付单号
     */
    @NotBlank(message = "支付单号不能为空")
    private String paymentNo;

    /**
     * 第三方支付单号
     */
    private String thirdPartyNo;

    /**
     * 支付状态
     */
    @NotBlank(message = "支付状态不能为空")
    private String paymentStatus;

    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 回调数据
     */
    private String callbackData;

    /**
     * 备注
     */
    private String remark;
}
