package com.qing.expert.vo.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 交易记录VO
 */
@Data
@Schema(description = "交易记录信息")
public class TransactionVO {

    /**
     * 交易记录ID
     */
    @Schema(description = "交易记录ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 交易类型
     */
    @Schema(description = "交易类型：recharge-充值，consume-消费，withdraw-提现，refund-退款")
    private String type;

    /**
     * 交易金额
     */
    @Schema(description = "交易金额")
    private Double amount;

    /**
     * 交易状态
     */
    @Schema(description = "交易状态：0-处理中，1-成功，2-失败，3-已取消")
    private Integer status;

    /**
     * 交易描述
     */
    @Schema(description = "交易描述")
    private String description;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo;

    /**
     * 支付方式（充值时）
     */
    @Schema(description = "支付方式")
    private String paymentMethod;

    /**
     * 服务名称（消费时）
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 提现账户（提现时）
     */
    @Schema(description = "提现账户")
    private String withdrawAccount;

    /**
     * 关联订单ID
     */
    @Schema(description = "关联订单ID")
    private Long orderId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    @Schema(description = "完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;
}
