package com.qing.expert.controller.admin;

import com.qing.expert.common.result.Result;
import com.qing.expert.service.FinanceStatisticsService;
import com.qing.expert.vo.FinanceStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 财务统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/finance/statistics")
@RequiredArgsConstructor
    @GetMapping("/overview")
    public Result<FinanceStatisticsVO> getFinanceStatistics() {
        FinanceStatisticsVO statistics = financeStatisticsService.getFinanceStatistics();
        return Result.success("获取成功", statistics);
    }

    public Result<FinanceStatisticsVO> getFinanceStatistics(
        FinanceStatisticsVO statistics = financeStatisticsService.getFinanceStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }

    public Result<Map<String, Object>> getRechargeStatistics(
        Map<String, Object> statistics = financeStatisticsService.getRechargeStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }

    public Result<Map<String, Object>> getWithdrawStatistics(
        Map<String, Object> statistics = financeStatisticsService.getWithdrawStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }

    public Result<Map<String, Object>> getOrderStatistics(
        Map<String, Object> statistics = financeStatisticsService.getOrderStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }

    public Result<Map<String, Object>> getUserBalanceStatistics() {
        Map<String, Object> statistics = financeStatisticsService.getUserBalanceStatistics();
        return Result.success("获取成功", statistics);
    }

    public Result<BigDecimal> getPlatformIncome(
        BigDecimal income = financeStatisticsService.getPlatformIncome(startTime, endTime);
        return Result.success("获取成功", income);
    }

    public Result<Map<String, Object>> getDailyFinanceData(
        Map<String, Object> data = financeStatisticsService.getDailyFinanceData(startTime, endTime);
        return Result.success("获取成功", data);
    }

    public Result<Map<String, Object>> getMonthlyFinanceData(
        Map<String, Object> data = financeStatisticsService.getMonthlyFinanceData(startTime, endTime);
        return Result.success("获取成功", data);
    }

    public Result<Map<String, Object>> getPayTypeStatistics(
        Map<String, Object> statistics = financeStatisticsService.getPayTypeStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }

    public Result<Map<String, Object>> getFinanceFlowStatistics(
        Map<String, Object> statistics = financeStatisticsService.getFinanceFlowStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }
}
