package com.qing.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard统计数据VO
 */
@Data
@Schema(description = "Dashboard统计数据")
public class DashboardStatisticsVO {

    @Schema(description = "用户总数")
    private Long userCount;

    @Schema(description = "达人总数")
    private Long expertCount;

    @Schema(description = "订单总数")
    private Long orderCount;

    @Schema(description = "总收入")
    private BigDecimal totalRevenue;

    @Schema(description = "今日新增用户")
    private Long todayNewUsers;

    @Schema(description = "今日新增达人")
    private Long todayNewExperts;

    @Schema(description = "今日新增订单")
    private Long todayNewOrders;

    @Schema(description = "今日收入")
    private BigDecimal todayRevenue;

    @Schema(description = "最近订单列表")
    private List<RecentOrderVO> recentOrders;

    @Schema(description = "系统通知列表")
    private List<SystemNoticeVO> systemNotices;

    @Schema(description = "用户注册趋势数据")
    private List<TrendDataVO> userRegistrationTrend;

    @Schema(description = "达人注册趋势数据")
    private List<TrendDataVO> expertRegistrationTrend;

    @Schema(description = "订单创建趋势数据")
    private List<TrendDataVO> orderCreationTrend;

    @Schema(description = "收入趋势数据")
    private List<TrendDataVO> revenueTrend;

    @Schema(description = "订单状态分布数据")
    private List<DistributionDataVO> orderStatusDistribution;

    @Schema(description = "用户类型分布数据")
    private List<DistributionDataVO> userTypeDistribution;

    @Schema(description = "达人状态分布数据")
    private List<DistributionDataVO> expertStatusDistribution;

    @Schema(description = "收入来源分布数据")
    private List<DistributionDataVO> revenueSourceDistribution;

    /**
     * 最近订单VO
     */
    @Data
    @Schema(description = "最近订单")
    public static class RecentOrderVO {
        @Schema(description = "订单ID")
        private Long id;

        @Schema(description = "用户名")
        private String userName;

        @Schema(description = "达人名")
        private String expertName;

        @Schema(description = "订单金额")
        private BigDecimal amount;

        @Schema(description = "订单状态")
        private Integer status;

        @Schema(description = "创建时间")
        private String createTime;
    }

    /**
     * 系统通知VO
     */
    @Data
    @Schema(description = "系统通知")
    public static class SystemNoticeVO {
        @Schema(description = "通知ID")
        private Long id;

        @Schema(description = "通知内容")
        private String content;

        @Schema(description = "通知时间")
        private String time;

        @Schema(description = "通知类型")
        private String type;
    }
}
