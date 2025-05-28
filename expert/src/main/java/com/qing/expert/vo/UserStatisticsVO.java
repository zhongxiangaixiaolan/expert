package com.qing.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户统计信息VO
 */
@Data
@Schema(description = "用户统计信息")
public class UserStatisticsVO {

    @Schema(description = "用户总数")
    private Long totalUsers;

    @Schema(description = "在线用户数")
    private Long onlineUsers;

    @Schema(description = "今日新增用户")
    private Long newUsersToday;

    @Schema(description = "本月新增用户")
    private Long newUsersThisMonth;

    @Schema(description = "达人用户数")
    private Long expertUsers;

    @Schema(description = "普通用户数")
    private Long normalUsers;

    @Schema(description = "活跃用户数")
    private Long activeUsers;

    @Schema(description = "用户总余额")
    private BigDecimal totalBalance;

    @Schema(description = "累计充值金额")
    private BigDecimal totalRecharge;

    @Schema(description = "累计消费金额")
    private BigDecimal totalConsume;
}
