package com.qing.expert.controller.admin;

import com.qing.expert.common.result.Result;
import com.qing.expert.service.FinanceStatisticsService;
import com.qing.expert.vo.FinanceStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "财务统计", description = "财务数据统计分析接口")
public class FinanceStatisticsController {

    private final FinanceStatisticsService financeStatisticsService;

    @Operation(summary = "获取财务统计概览", description = "获取财务统计的总体概览数据")
    @GetMapping("/overview")
    public Result<FinanceStatisticsVO> getFinanceStatistics() {
        FinanceStatisticsVO statistics = financeStatisticsService.getFinanceStatistics();
        return Result.success("获取成功", statistics);
    }

    @Operation(summary = "获取指定时间范围的财务统计", description = "根据时间范围获取财务统计数据")
    @GetMapping("/range")
    public Result<FinanceStatisticsVO> getFinanceStatistics(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        FinanceStatisticsVO statistics = financeStatisticsService.getFinanceStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }

    @Operation(summary = "获取充值统计数据", description = "获取充值相关的统计数据")
    @GetMapping("/recharge")
    public Result<Map<String, Object>> getRechargeStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> statistics = financeStatisticsService.getRechargeStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }

    @Operation(summary = "获取提现统计数据", description = "获取提现相关的统计数据")
    @GetMapping("/withdraw")
    public Result<Map<String, Object>> getWithdrawStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> statistics = financeStatisticsService.getWithdrawStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }

    @Operation(summary = "获取订单统计数据", description = "获取订单相关的统计数据")
    @GetMapping("/order")
    public Result<Map<String, Object>> getOrderStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> statistics = financeStatisticsService.getOrderStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }

    @Operation(summary = "获取用户余额统计", description = "获取用户余额相关的统计数据")
    @GetMapping("/balance")
    public Result<Map<String, Object>> getUserBalanceStatistics() {
        Map<String, Object> statistics = financeStatisticsService.getUserBalanceStatistics();
        return Result.success("获取成功", statistics);
    }

    @Operation(summary = "获取平台收入统计", description = "获取平台收入统计数据")
    @GetMapping("/income")
    public Result<BigDecimal> getPlatformIncome(
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        BigDecimal income = financeStatisticsService.getPlatformIncome(startTime, endTime);
        return Result.success("获取成功", income);
    }

    @Operation(summary = "获取每日财务数据", description = "获取每日财务数据，用于图表展示")
    @GetMapping("/daily")
    public Result<Map<String, Object>> getDailyFinanceData(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> data = financeStatisticsService.getDailyFinanceData(startTime, endTime);
        return Result.success("获取成功", data);
    }

    @Operation(summary = "获取每月财务数据", description = "获取每月财务数据，用于图表展示")
    @GetMapping("/monthly")
    public Result<Map<String, Object>> getMonthlyFinanceData(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> data = financeStatisticsService.getMonthlyFinanceData(startTime, endTime);
        return Result.success("获取成功", data);
    }

    @Operation(summary = "获取支付方式统计", description = "获取各种支付方式的统计数据")
    @GetMapping("/paytype")
    public Result<Map<String, Object>> getPayTypeStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> statistics = financeStatisticsService.getPayTypeStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }

    @Operation(summary = "获取财务流水统计", description = "获取财务收支流水统计数据")
    @GetMapping("/flow")
    public Result<Map<String, Object>> getFinanceFlowStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> statistics = financeStatisticsService.getFinanceFlowStatistics(startTime, endTime);
        return Result.success("获取成功", statistics);
    }
}
