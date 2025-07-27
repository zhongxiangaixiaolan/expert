package com.qing.expert.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.transaction.TransactionQueryDTO;
import com.qing.expert.service.TransactionService;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.vo.transaction.TransactionVO;
import com.qing.expert.vo.user.UserStatisticsVO;
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

    public Result<TransactionVO> getTransactionDetail(

            TransactionVO transaction = transactionService.getUserTransactionDetail(transactionId, currentUserId);
            return Result.success("获取成功", transaction);
        } catch (Exception e) {
            log.error("获取交易记录详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

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
