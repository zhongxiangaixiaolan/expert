package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("transaction_records")
public class TransactionRecord {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 交易类型：recharge-充值，consume-消费，withdraw-提现，refund-退款
     */
    @TableField("type")
    private String type;

    /**
     * 交易金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 交易状态：0-处理中，1-成功，2-失败，3-已取消
     */
    @TableField("status")
    private Integer status;

    /**
     * 交易描述
     */
    @TableField("description")
    private String description;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 支付方式（充值时）
     */
    @TableField("payment_method")
    private String paymentMethod;

    /**
     * 服务名称（消费时）
     */
    @TableField("service_name")
    private String serviceName;

    /**
     * 提现账户（提现时）
     */
    @TableField("withdraw_account")
    private String withdrawAccount;

    /**
     * 关联订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 第三方交易号
     */
    @TableField("third_party_no")
    private String thirdPartyNo;

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
     * 完成时间
     */
    @TableField("finish_time")
    private LocalDateTime finishTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
