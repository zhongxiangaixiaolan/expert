package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recharge_records")
@Schema(description = "充值记录")
public class RechargeRecord extends BaseEntity {

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "充值订单号")
    @TableField("order_no")
    private String orderNo;

    @Schema(description = "充值金额")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(description = "支付方式：WECHAT")
    @TableField("pay_type")
    private String payType;

    @Schema(description = "第三方支付订单号")
    @TableField("pay_order_no")
    private String payOrderNo;

    @Schema(description = "状态：0-待支付，1-已支付，2-支付失败")
    @TableField("status")
    private Integer status;

    @Schema(description = "支付时间")
    @TableField("pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime payTime;
}
