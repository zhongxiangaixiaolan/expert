package com.qing.expert.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录VO
 */
@Data
public class PaymentRecordVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 第三方支付单号
     */
    private String thirdPartyNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付类型
     */
    private String paymentType;

    /**
     * 支付类型描述
     */
    private String paymentTypeDesc;

    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;

    /**
     * 支付状态
     */
    private String paymentStatus;

    /**
     * 支付状态描述
     */
    private String paymentStatusDesc;

    /**
     * 支付描述
     */
    private String paymentDesc;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 回调时间
     */
    private LocalDateTime callbackTime;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;
}
