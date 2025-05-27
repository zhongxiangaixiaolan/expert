package com.qing.expert.service;

import com.qing.expert.vo.DashboardStatisticsVO;
import com.qing.expert.vo.TrendDataVO;
import com.qing.expert.vo.DistributionDataVO;

import java.util.List;

/**
 * Dashboard服务接口
 */
public interface DashboardService {

    /**
     * 获取Dashboard统计数据
     */
    DashboardStatisticsVO getDashboardStatistics();

    /**
     * 获取用户注册趋势数据
     * 
     * @param days 天数（7或30）
     */
    List<TrendDataVO> getUserRegistrationTrend(int days);

    /**
     * 获取达人注册趋势数据
     * 
     * @param days 天数（7或30）
     */
    List<TrendDataVO> getExpertRegistrationTrend(int days);

    /**
     * 获取订单创建趋势数据
     * 
     * @param days 天数（7或30）
     */
    List<TrendDataVO> getOrderCreationTrend(int days);

    /**
     * 获取收入趋势数据
     * 
     * @param days 天数（7或30）
     */
    List<TrendDataVO> getRevenueTrend(int days);

    /**
     * 获取订单状态分布数据
     */
    List<DistributionDataVO> getOrderStatusDistribution();

    /**
     * 获取用户类型分布数据
     */
    List<DistributionDataVO> getUserTypeDistribution();

    /**
     * 获取达人状态分布数据
     */
    List<DistributionDataVO> getExpertStatusDistribution();

    /**
     * 获取收入来源分布数据
     */
    List<DistributionDataVO> getRevenueSourceDistribution();
}
