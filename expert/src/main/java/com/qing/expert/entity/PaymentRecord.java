package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("payment_records")
public class PaymentRecord {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 支付单号（系统生成）
     */
    @TableField("payment_no")
    private String paymentNo;

    /**
     * 第三方支付单号
     */
    @TableField("third_party_no")
    private String thirdPartyNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 订单ID（可为空，充值时为空）
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 支付类型（WECHAT_PAY-微信支付，BALANCE_PAY-余额支付，ALIPAY-支付宝支付）
     */
    @TableField("payment_type")
    private String paymentType;

    /**
     * 支付金额
     */
    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    /**
     * 实际支付金额
     */
    @TableField("actual_amount")
    private BigDecimal actualAmount;

    /**
     * 支付状态（PENDING-待支付，PAYING-支付中，SUCCESS-支付成功，FAILED-支付失败，CANCELLED-支付取消，REFUNDED-已退款）
     */
    @TableField("payment_status")
    private String paymentStatus;

    /**
     * 支付描述
     */
    @TableField("payment_desc")
    private String paymentDesc;

    /**
     * 支付时间
     */
    @TableField("payment_time")
    private LocalDateTime paymentTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 回调时间
     */
    @TableField("callback_time")
    private LocalDateTime callbackTime;

    /**
     * 回调数据（JSON格式）
     */
    @TableField("callback_data")
    private String callbackData;

    /**
     * 退款金额
     */
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    /**
     * 退款时间
     */
    @TableField("refund_time")
    private LocalDateTime refundTime;

    /**
     * 退款原因
     */
    @TableField("refund_reason")
    private String refundReason;

    /**
     * 客户端IP
     */
    @TableField("client_ip")
    private String clientIp;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
