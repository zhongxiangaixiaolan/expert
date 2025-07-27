package com.qing.expert.controller.admin;

import com.qing.expert.common.result.Result;
import com.qing.expert.service.DashboardService;
import com.qing.expert.vo.DashboardStatisticsVO;
import com.qing.expert.vo.TrendDataVO;
import com.qing.expert.vo.DistributionDataVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Dashboard统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
    @GetMapping("/statistics")
    public Result<DashboardStatisticsVO> getDashboardStatistics() {
        try {
            DashboardStatisticsVO statistics = dashboardService.getDashboardStatistics();
            return Result.success("获取成功", statistics);
        } catch (Exception e) {
            log.error("获取Dashboard统计数据失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<TrendDataVO>> getUserRegistrationTrend(@RequestParam(defaultValue = "7") int days) {
        try {
            List<TrendDataVO> trendData = dashboardService.getUserRegistrationTrend(days);
            return Result.success("获取成功", trendData);
        } catch (Exception e) {
            log.error("获取用户注册趋势数据失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<TrendDataVO>> getExpertRegistrationTrend(@RequestParam(defaultValue = "7") int days) {
        try {
            List<TrendDataVO> trendData = dashboardService.getExpertRegistrationTrend(days);
            return Result.success("获取成功", trendData);
        } catch (Exception e) {
            log.error("获取达人注册趋势数据失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<TrendDataVO>> getOrderCreationTrend(@RequestParam(defaultValue = "7") int days) {
        try {
            List<TrendDataVO> trendData = dashboardService.getOrderCreationTrend(days);
            return Result.success("获取成功", trendData);
        } catch (Exception e) {
            log.error("获取订单创建趋势数据失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<TrendDataVO>> getRevenueTrend(@RequestParam(defaultValue = "7") int days) {
        try {
            List<TrendDataVO> trendData = dashboardService.getRevenueTrend(days);
            return Result.success("获取成功", trendData);
        } catch (Exception e) {
            log.error("获取收入趋势数据失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<DistributionDataVO>> getOrderStatusDistribution() {
        try {
            List<DistributionDataVO> distributionData = dashboardService.getOrderStatusDistribution();
            return Result.success("获取成功", distributionData);
        } catch (Exception e) {
            log.error("获取订单状态分布数据失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<DistributionDataVO>> getUserTypeDistribution() {
        try {
            List<DistributionDataVO> distributionData = dashboardService.getUserTypeDistribution();
            return Result.success("获取成功", distributionData);
        } catch (Exception e) {
            log.error("获取用户类型分布数据失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<DistributionDataVO>> getExpertStatusDistribution() {
        try {
            List<DistributionDataVO> distributionData = dashboardService.getExpertStatusDistribution();
            return Result.success("获取成功", distributionData);
        } catch (Exception e) {
            log.error("获取达人状态分布数据失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<List<DistributionDataVO>> getRevenueSourceDistribution() {
        try {
            List<DistributionDataVO> distributionData = dashboardService.getRevenueSourceDistribution();
            return Result.success("获取成功", distributionData);
        } catch (Exception e) {
            log.error("获取收入来源分布数据失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }
}
