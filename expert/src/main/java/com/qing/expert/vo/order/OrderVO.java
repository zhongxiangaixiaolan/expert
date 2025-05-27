package com.qing.expert.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单VO
 */
@Data
@Schema(description = "订单VO")
public class OrderVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "达人ID")
    private Long expertId;

    @Schema(description = "达人昵称")
    private String expertNickname;

    @Schema(description = "达人头像")
    private String expertAvatar;

    @Schema(description = "服务ID")
    private Long serviceId;

    @Schema(description = "服务名称")
    private String serviceName;

    @Schema(description = "服务价格")
    private BigDecimal servicePrice;

    @Schema(description = "服务封面")
    private String serviceCover;

    @Schema(description = "订单标题")
    private String title;

    @Schema(description = "订单描述")
    private String description;

    @Schema(description = "订单金额")
    private BigDecimal amount;

    @Schema(description = "实付金额")
    private BigDecimal paidAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "订单状态描述")
    private String statusDesc;

    @Schema(description = "支付状态")
    private Integer payStatus;

    @Schema(description = "支付状态描述")
    private String payStatusDesc;

    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "期望完成时间")
    private LocalDateTime expectedTime;

    @Schema(description = "接单时间")
    private LocalDateTime acceptTime;

    @Schema(description = "开始服务时间")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "取消原因")
    private String cancelReason;

    @Schema(description = "用户备注")
    private String userRemark;

    @Schema(description = "达人备注")
    private String expertRemark;

    @Schema(description = "管理员备注")
    private String adminRemark;

    @Schema(description = "联系方式")
    private String contactInfo;

    @Schema(description = "服务地址")
    private String serviceAddress;

    @Schema(description = "预约时间")
    private LocalDateTime appointmentTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
