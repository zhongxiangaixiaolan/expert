package com.qing.expert.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.transaction.TransactionQueryDTO;
import com.qing.expert.service.TransactionService;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.vo.transaction.TransactionVO;
import com.qing.expert.vo.user.UserStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 交易记录控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Tag(name = "交易记录", description = "用户交易记录相关接口")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "获取交易记录列表", description = "分页获取用户交易记录列表")
    @GetMapping("/list")
    public Result<IPage<TransactionVO>> getTransactionList(@Validated TransactionQueryDTO queryDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            queryDTO.setUserId(currentUserId);

            IPage<TransactionVO> page = transactionService.getUserTransactionPage(queryDTO);
            return Result.success("获取成功", page);
        } catch (Exception e) {
            log.error("获取交易记录列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取交易记录详情", description = "根据交易记录ID获取详情")
    @GetMapping("/{transactionId}")
    public Result<TransactionVO> getTransactionDetail(
            @Parameter(description = "交易记录ID") @PathVariable Long transactionId) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            TransactionVO transaction = transactionService.getUserTransactionDetail(transactionId, currentUserId);
            return Result.success("获取成功", transaction);
        } catch (Exception e) {
            log.error("获取交易记录详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取用户统计信息", description = "获取用户余额、充值、消费等统计信息")
    @GetMapping("/statistics")
    public Result<UserStatisticsVO> getUserStatistics() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            UserStatisticsVO statistics = transactionService.getUserStatistics(currentUserId);
            return Result.success("获取成功", statistics);
        } catch (Exception e) {
            log.error("获取用户统计信息失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }
}
