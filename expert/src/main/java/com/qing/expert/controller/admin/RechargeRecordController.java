package com.qing.expert.controller.admin;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.recharge.RechargeCreateDTO;
import com.qing.expert.dto.recharge.RechargeQueryDTO;
import com.qing.expert.service.RechargeRecordService;
import com.qing.expert.vo.recharge.RechargeRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 充值记录管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/recharge")
@RequiredArgsConstructor
    @PostMapping("/page")
    public Result<PageResult<RechargeRecordVO>> getRechargeRecordPage(@Validated @RequestBody RechargeQueryDTO queryDTO) {
        PageResult<RechargeRecordVO> pageResult = rechargeRecordService.getRechargeRecordPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    public Result<RechargeRecordVO> getRechargeRecordDetail(
        if (recordVO != null) {
            return Result.success("获取成功", recordVO);
        } else {
            return Result.error("充值记录不存在");
        }
    }

    public Result<Long> createRechargeRecord(@Validated @RequestBody RechargeCreateDTO createDTO) {
        Long rechargeId = rechargeRecordService.createRechargeRecord(createDTO);
        return Result.success("创建成功", rechargeId);
    }

    public Result<Void> updateRechargeStatus(
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    public Result<Void> handlePaymentSuccess(
        boolean success = rechargeRecordService.handlePaymentSuccess(orderNo, payOrderNo);
        if (success) {
            return Result.success("支付成功处理完成");
        } else {
            return Result.error("支付成功处理失败");
        }
    }

    public Result<Void> handlePaymentFailed(
        boolean success = rechargeRecordService.handlePaymentFailed(orderNo, failReason);
        if (success) {
            return Result.success("支付失败处理完成");
        } else {
            return Result.error("支付失败处理失败");
        }
    }

    public Result<List<RechargeRecordVO>> getRecentRechargeRecords(
        return Result.success("获取成功", records);
    }

    public Result<Void> deleteRechargeRecord(
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    public Result<Void> batchDeleteRechargeRecords(
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }
}
