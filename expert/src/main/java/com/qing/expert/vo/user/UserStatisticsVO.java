package com.qing.expert.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户统计信息VO
 */
@Data
@Schema(description = "用户统计信息")
public class UserStatisticsVO {

    /**
     * 当前余额
     */
    @Schema(description = "当前余额")
    private Double balance;

    /**
     * 总充值金额
     */
    @Schema(description = "总充值金额")
    private Double totalRecharge;

    /**
     * 总消费金额
     */
    @Schema(description = "总消费金额")
    private Double totalConsume;

    /**
     * 总提现金额
     */
    @Schema(description = "总提现金额")
    private Double totalWithdraw;

    /**
     * 待处理金额
     */
    @Schema(description = "待处理金额")
    private Double pendingAmount;

    /**
     * 充值次数
     */
    @Schema(description = "充值次数")
    private Integer rechargeCount;

    /**
     * 消费次数
     */
    @Schema(description = "消费次数")
    private Integer consumeCount;

    /**
     * 提现次数
     */
    @Schema(description = "提现次数")
    private Integer withdrawCount;
}
