package com.qing.expert.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录VO
 */
@Data
@Schema(description = "充值记录VO")
public class RechargeRecordVO {

    @Schema(description = "充值记录ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "充值订单号")
    private String orderNo;

    @Schema(description = "充值金额")
    private BigDecimal amount;

    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "支付方式描述")
    private String payTypeDesc;

    @Schema(description = "第三方支付订单号")
    private String payOrderNo;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
