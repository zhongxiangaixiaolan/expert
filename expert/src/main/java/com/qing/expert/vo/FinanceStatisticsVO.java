package com.qing.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务统计VO
 */
@Data
@Schema(description = "财务统计信息")
public class FinanceStatisticsVO {

    @Schema(description = "总充值金额")
    private BigDecimal totalRechargeAmount;

    @Schema(description = "总提现金额")
    private BigDecimal totalWithdrawAmount;

    @Schema(description = "总手续费")
    private BigDecimal totalFeeAmount;

    @Schema(description = "总订单金额")
    private BigDecimal totalOrderAmount;

    @Schema(description = "平台收入")
    private BigDecimal platformIncome;

    @Schema(description = "用户总余额")
    private BigDecimal totalUserBalance;

    @Schema(description = "今日充值金额")
    private BigDecimal todayRechargeAmount;

    @Schema(description = "今日提现金额")
    private BigDecimal todayWithdrawAmount;

    @Schema(description = "今日订单金额")
    private BigDecimal todayOrderAmount;

    @Schema(description = "本月充值金额")
    private BigDecimal monthRechargeAmount;

    @Schema(description = "本月提现金额")
    private BigDecimal monthWithdrawAmount;

    @Schema(description = "本月订单金额")
    private BigDecimal monthOrderAmount;

    @Schema(description = "充值记录总数")
    private Long totalRechargeCount;

    @Schema(description = "提现记录总数")
    private Long totalWithdrawCount;

    @Schema(description = "待审核提现数量")
    private Long pendingWithdrawCount;

    @Schema(description = "今日充值数量")
    private Long todayRechargeCount;

    @Schema(description = "今日提现数量")
    private Long todayWithdrawCount;
}
