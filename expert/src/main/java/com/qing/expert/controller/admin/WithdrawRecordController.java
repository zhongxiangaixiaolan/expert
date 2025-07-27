package com.qing.expert.controller.admin;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.withdraw.WithdrawAuditDTO;
import com.qing.expert.dto.withdraw.WithdrawCreateDTO;
import com.qing.expert.dto.withdraw.WithdrawQueryDTO;
import com.qing.expert.service.WithdrawRecordService;
import com.qing.expert.vo.withdraw.WithdrawRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提现记录管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/withdraw")
@RequiredArgsConstructor
    @PostMapping("/page")
    public Result<PageResult<WithdrawRecordVO>> getWithdrawRecordPage(@Validated @RequestBody WithdrawQueryDTO queryDTO) {
        PageResult<WithdrawRecordVO> pageResult = withdrawRecordService.getWithdrawRecordPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    public Result<WithdrawRecordVO> getWithdrawRecordDetail(
        if (recordVO != null) {
            return Result.success("获取成功", recordVO);
        } else {
            return Result.error("提现记录不存在");
        }
    }

    public Result<Long> createWithdrawRecord(@Validated @RequestBody WithdrawCreateDTO createDTO) {
        Long withdrawId = withdrawRecordService.createWithdrawRecord(createDTO);
        return Result.success("创建成功", withdrawId);
    }

    public Result<Void> auditWithdrawRecord(@Validated @RequestBody WithdrawAuditDTO auditDTO) {
        boolean success = withdrawRecordService.auditWithdrawRecord(auditDTO);
        if (success) {
            return Result.success("审核成功");
        } else {
            return Result.error("审核失败");
        }
    }

    public Result<Void> batchAuditWithdrawRecords(@Validated @RequestBody List<WithdrawAuditDTO> auditDTOs) {
        boolean success = withdrawRecordService.batchAuditWithdrawRecords(auditDTOs);
        if (success) {
            return Result.success("批量审核成功");
        } else {
            return Result.error("批量审核失败");
        }
    }

    public Result<Void> confirmTransfer(
        if (success) {
            return Result.success("确认转账成功");
        } else {
            return Result.error("确认转账失败");
        }
    }

    public Result<Void> batchConfirmTransfer(
        if (success) {
            return Result.success("批量确认转账成功");
        } else {
            return Result.error("批量确认转账失败");
        }
    }

    public Result<Long> getPendingWithdrawCount() {
        Long count = withdrawRecordService.getPendingWithdrawCount();
        return Result.success("获取成功", count);
    }

    public Result<List<WithdrawRecordVO>> getRecentWithdrawRecords(
        return Result.success("获取成功", records);
    }

    public Result<BigDecimal> calculateWithdrawFee(
        return Result.success("计算成功", fee);
    }

    public Result<Void> deleteWithdrawRecord(
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    public Result<Void> batchDeleteWithdrawRecords(
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }
}
