package com.qing.expert.dto.recharge;

import com.qing.expert.dto.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 充值记录查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "充值记录查询DTO")
public class RechargeQueryDTO extends BasePageDTO {

    @Schema(description = "充值订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "最小金额")
    private BigDecimal minAmount;

    @Schema(description = "最大金额")
    private BigDecimal maxAmount;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}
