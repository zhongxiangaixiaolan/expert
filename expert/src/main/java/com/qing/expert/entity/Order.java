package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("orders")
public class Order extends BaseEntity {

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 达人ID
     */
    @TableField("expert_id")
    private Long expertId;

    /**
     * 服务ID
     */
    @TableField("service_id")
    private Long serviceId;

    /**
     * 服务名称
     */
    @TableField("service_name")
    private String serviceName;

    /**
     * 服务价格
     */
    @TableField("service_price")
    private BigDecimal servicePrice;

    /**
     * 订单标题
     */
    @TableField("title")
    private String title;

    /**
     * 订单描述
     */
    @TableField("description")
    private String description;

    /**
     * 订单金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 实付金额
     */
    @TableField("paid_amount")
    private BigDecimal paidAmount;

    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 订单状态：1-待接单，2-已接单，3-服务中，4-已完成，5-已取消，6-售后中
     */
    @TableField("status")
    private Integer status;

    /**
     * 支付状态：0-未支付，1-已支付，2-已退款
     */
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 支付方式：WECHAT,BALANCE
     */
    @TableField("pay_type")
    private String payType;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;

    /**
     * 期望完成时间
     */
    @TableField("expected_time")
    private LocalDateTime expectedTime;

    /**
     * 接单时间
     */
    @TableField("accept_time")
    private LocalDateTime acceptTime;

    /**
     * 开始服务时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    @TableField("finish_time")
    private LocalDateTime finishTime;

    /**
     * 取消时间
     */
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    @TableField("cancel_reason")
    private String cancelReason;

    /**
     * 用户备注
     */
    @TableField("user_remark")
    private String userRemark;

    /**
     * 达人备注
     */
    @TableField("expert_remark")
    private String expertRemark;

    /**
     * 管理员备注
     */
    @TableField("admin_remark")
    private String adminRemark;

    /**
     * 联系方式
     */
    @TableField("contact_info")
    private String contactInfo;

    /**
     * 服务地址
     */
    @TableField("service_address")
    private String serviceAddress;

    /**
     * 预约时间
     */
    @TableField("appointment_time")
    private LocalDateTime appointmentTime;
}
