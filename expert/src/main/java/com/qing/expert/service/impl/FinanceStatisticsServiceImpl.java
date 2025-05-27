package com.qing.expert.service.impl;

import com.qing.expert.mapper.OrderMapper;
import com.qing.expert.mapper.RechargeRecordMapper;
import com.qing.expert.mapper.UserMapper;
import com.qing.expert.mapper.WithdrawRecordMapper;
import com.qing.expert.service.FinanceStatisticsService;
import com.qing.expert.vo.FinanceStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 财务统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceStatisticsServiceImpl implements FinanceStatisticsService {

    private final RechargeRecordMapper rechargeRecordMapper;
    private final WithdrawRecordMapper withdrawRecordMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    @Override
    public FinanceStatisticsVO getFinanceStatistics() {
        LocalDateTime now = LocalDateTime.now();
        return getFinanceStatistics(null, now);
    }

    @Override
    public FinanceStatisticsVO getFinanceStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        FinanceStatisticsVO statistics = new FinanceStatisticsVO();
        
        // 计算时间范围
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime monthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        
        // 总充值金额
        statistics.setTotalRechargeAmount(rechargeRecordMapper.getTotalRechargeAmount(null, null));
        
        // 总提现金额
        statistics.setTotalWithdrawAmount(withdrawRecordMapper.getTotalWithdrawAmount(null, null));
        
        // 总手续费
        statistics.setTotalFeeAmount(withdrawRecordMapper.getTotalFeeAmount(null, null));
        
        // 今日数据
        statistics.setTodayRechargeAmount(rechargeRecordMapper.getTotalRechargeAmount(todayStart, now));
        statistics.setTodayWithdrawAmount(withdrawRecordMapper.getTotalWithdrawAmount(todayStart, now));
        statistics.setTodayRechargeCount(rechargeRecordMapper.getRechargeCount(todayStart, now, null));
        statistics.setTodayWithdrawCount(withdrawRecordMapper.getWithdrawCount(todayStart, now, null));
        
        // 本月数据
        statistics.setMonthRechargeAmount(rechargeRecordMapper.getTotalRechargeAmount(monthStart, now));
        statistics.setMonthWithdrawAmount(withdrawRecordMapper.getTotalWithdrawAmount(monthStart, now));
        
        // 记录总数
        statistics.setTotalRechargeCount(rechargeRecordMapper.getRechargeCount(null, null, null));
        statistics.setTotalWithdrawCount(withdrawRecordMapper.getWithdrawCount(null, null, null));
        
        // 待审核提现数量
        statistics.setPendingWithdrawCount(withdrawRecordMapper.getPendingWithdrawCount());
        
        // 处理null值
        setDefaultValues(statistics);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getRechargeStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        // 充值总金额
        BigDecimal totalAmount = rechargeRecordMapper.getTotalRechargeAmount(startTime, endTime);
        result.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        
        // 充值总数量
        Long totalCount = rechargeRecordMapper.getRechargeCount(startTime, endTime, null);
        result.put("totalCount", totalCount != null ? totalCount : 0L);
        
        // 成功充值数量
        Long successCount = rechargeRecordMapper.getRechargeCount(startTime, endTime, 1);
        result.put("successCount", successCount != null ? successCount : 0L);
        
        // 微信支付金额
        BigDecimal wechatAmount = rechargeRecordMapper.getRechargeAmountByPayType("WECHAT", startTime, endTime);
        result.put("wechatAmount", wechatAmount != null ? wechatAmount : BigDecimal.ZERO);
        
        // 支付宝金额
        BigDecimal alipayAmount = rechargeRecordMapper.getRechargeAmountByPayType("ALIPAY", startTime, endTime);
        result.put("alipayAmount", alipayAmount != null ? alipayAmount : BigDecimal.ZERO);
        
        return result;
    }

    @Override
    public Map<String, Object> getWithdrawStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        // 提现总金额
        BigDecimal totalAmount = withdrawRecordMapper.getTotalWithdrawAmount(startTime, endTime);
        result.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        
        // 手续费总金额
        BigDecimal totalFee = withdrawRecordMapper.getTotalFeeAmount(startTime, endTime);
        result.put("totalFee", totalFee != null ? totalFee : BigDecimal.ZERO);
        
        // 提现总数量
        Long totalCount = withdrawRecordMapper.getWithdrawCount(startTime, endTime, null);
        result.put("totalCount", totalCount != null ? totalCount : 0L);
        
        // 待审核数量
        Long pendingCount = withdrawRecordMapper.getWithdrawCount(startTime, endTime, 0);
        result.put("pendingCount", pendingCount != null ? pendingCount : 0L);
        
        // 审核通过数量
        Long approvedCount = withdrawRecordMapper.getWithdrawCount(startTime, endTime, 1);
        result.put("approvedCount", approvedCount != null ? approvedCount : 0L);
        
        // 已转账数量
        Long transferredCount = withdrawRecordMapper.getWithdrawCount(startTime, endTime, 3);
        result.put("transferredCount", transferredCount != null ? transferredCount : 0L);
        
        return result;
    }

    @Override
    public Map<String, Object> getOrderStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        // TODO: 实现订单统计逻辑
        // 这里需要根据OrderMapper中的方法来实现
        result.put("totalAmount", BigDecimal.ZERO);
        result.put("totalCount", 0L);
        result.put("completedCount", 0L);
        result.put("cancelledCount", 0L);
        
        return result;
    }

    @Override
    public Map<String, Object> getUserBalanceStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        // TODO: 实现用户余额统计逻辑
        // 这里需要根据UserMapper中的方法来实现
        result.put("totalBalance", BigDecimal.ZERO);
        result.put("avgBalance", BigDecimal.ZERO);
        result.put("userCount", 0L);
        
        return result;
    }

    @Override
    public BigDecimal getPlatformIncome(LocalDateTime startTime, LocalDateTime endTime) {
        // 平台收入 = 手续费收入
        BigDecimal feeIncome = withdrawRecordMapper.getTotalFeeAmount(startTime, endTime);
        return feeIncome != null ? feeIncome : BigDecimal.ZERO;
    }

    @Override
    public Map<String, Object> getDailyFinanceData(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        // TODO: 实现每日财务数据统计
        // 这里需要按日期分组统计数据
        result.put("dates", new String[0]);
        result.put("rechargeAmounts", new BigDecimal[0]);
        result.put("withdrawAmounts", new BigDecimal[0]);
        result.put("orderAmounts", new BigDecimal[0]);
        
        return result;
    }

    @Override
    public Map<String, Object> getMonthlyFinanceData(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        // TODO: 实现每月财务数据统计
        // 这里需要按月份分组统计数据
        result.put("months", new String[0]);
        result.put("rechargeAmounts", new BigDecimal[0]);
        result.put("withdrawAmounts", new BigDecimal[0]);
        result.put("orderAmounts", new BigDecimal[0]);
        
        return result;
    }

    @Override
    public Map<String, Object> getPayTypeStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        // 微信支付统计
        BigDecimal wechatAmount = rechargeRecordMapper.getRechargeAmountByPayType("WECHAT", startTime, endTime);
        result.put("wechatAmount", wechatAmount != null ? wechatAmount : BigDecimal.ZERO);
        
        // 支付宝统计
        BigDecimal alipayAmount = rechargeRecordMapper.getRechargeAmountByPayType("ALIPAY", startTime, endTime);
        result.put("alipayAmount", alipayAmount != null ? alipayAmount : BigDecimal.ZERO);
        
        return result;
    }

    @Override
    public Map<String, Object> getFinanceFlowStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        // 收入统计
        BigDecimal rechargeIncome = rechargeRecordMapper.getTotalRechargeAmount(startTime, endTime);
        BigDecimal feeIncome = withdrawRecordMapper.getTotalFeeAmount(startTime, endTime);
        BigDecimal totalIncome = (rechargeIncome != null ? rechargeIncome : BigDecimal.ZERO)
                .add(feeIncome != null ? feeIncome : BigDecimal.ZERO);
        
        // 支出统计
        BigDecimal withdrawExpense = withdrawRecordMapper.getTotalWithdrawAmount(startTime, endTime);
        
        result.put("totalIncome", totalIncome);
        result.put("rechargeIncome", rechargeIncome != null ? rechargeIncome : BigDecimal.ZERO);
        result.put("feeIncome", feeIncome != null ? feeIncome : BigDecimal.ZERO);
        result.put("totalExpense", withdrawExpense != null ? withdrawExpense : BigDecimal.ZERO);
        result.put("netIncome", totalIncome.subtract(withdrawExpense != null ? withdrawExpense : BigDecimal.ZERO));
        
        return result;
    }

    /**
     * 设置默认值
     */
    private void setDefaultValues(FinanceStatisticsVO statistics) {
        if (statistics.getTotalRechargeAmount() == null) {
            statistics.setTotalRechargeAmount(BigDecimal.ZERO);
        }
        if (statistics.getTotalWithdrawAmount() == null) {
            statistics.setTotalWithdrawAmount(BigDecimal.ZERO);
        }
        if (statistics.getTotalFeeAmount() == null) {
            statistics.setTotalFeeAmount(BigDecimal.ZERO);
        }
        if (statistics.getTodayRechargeAmount() == null) {
            statistics.setTodayRechargeAmount(BigDecimal.ZERO);
        }
        if (statistics.getTodayWithdrawAmount() == null) {
            statistics.setTodayWithdrawAmount(BigDecimal.ZERO);
        }
        if (statistics.getMonthRechargeAmount() == null) {
            statistics.setMonthRechargeAmount(BigDecimal.ZERO);
        }
        if (statistics.getMonthWithdrawAmount() == null) {
            statistics.setMonthWithdrawAmount(BigDecimal.ZERO);
        }
        if (statistics.getTotalRechargeCount() == null) {
            statistics.setTotalRechargeCount(0L);
        }
        if (statistics.getTotalWithdrawCount() == null) {
            statistics.setTotalWithdrawCount(0L);
        }
        if (statistics.getPendingWithdrawCount() == null) {
            statistics.setPendingWithdrawCount(0L);
        }
        if (statistics.getTodayRechargeCount() == null) {
            statistics.setTodayRechargeCount(0L);
        }
        if (statistics.getTodayWithdrawCount() == null) {
            statistics.setTodayWithdrawCount(0L);
        }
    }
}
