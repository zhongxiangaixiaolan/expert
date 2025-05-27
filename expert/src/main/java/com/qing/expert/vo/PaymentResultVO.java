package com.qing.expert.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付结果VO
 */
@Data
public class PaymentResultVO {

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 第三方支付单号
     */
    private String thirdPartyNo;

    /**
     * 支付状态
     */
    private String paymentStatus;

    /**
     * 支付状态描述
     */
    private String paymentStatusDesc;

    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 支付参数（用于调起支付）
     */
    private Object paymentParams;

    /**
     * 支付二维码（PC端使用）
     */
    private String qrCode;

    /**
     * 支付链接
     */
    private String paymentUrl;

    /**
     * 备注
     */
    private String remark;
}
