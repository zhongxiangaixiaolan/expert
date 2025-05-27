package com.qing.expert.service;

import com.qing.expert.vo.FinanceStatisticsVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 财务统计服务接口
 */
public interface FinanceStatisticsService {

    /**
     * 获取财务统计信息
     */
    FinanceStatisticsVO getFinanceStatistics();

    /**
     * 获取指定时间范围的财务统计
     */
    FinanceStatisticsVO getFinanceStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取充值统计数据
     */
    Map<String, Object> getRechargeStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取提现统计数据
     */
    Map<String, Object> getWithdrawStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取订单统计数据
     */
    Map<String, Object> getOrderStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户余额统计
     */
    Map<String, Object> getUserBalanceStatistics();

    /**
     * 获取平台收入统计
     */
    BigDecimal getPlatformIncome(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取每日财务数据（用于图表）
     */
    Map<String, Object> getDailyFinanceData(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取每月财务数据（用于图表）
     */
    Map<String, Object> getMonthlyFinanceData(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取支付方式统计
     */
    Map<String, Object> getPayTypeStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取财务流水统计
     */
    Map<String, Object> getFinanceFlowStatistics(LocalDateTime startTime, LocalDateTime endTime);
}
