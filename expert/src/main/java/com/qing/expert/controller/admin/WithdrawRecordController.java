package com.qing.expert.controller.admin;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.withdraw.WithdrawAuditDTO;
import com.qing.expert.dto.withdraw.WithdrawCreateDTO;
import com.qing.expert.dto.withdraw.WithdrawQueryDTO;
import com.qing.expert.service.WithdrawRecordService;
import com.qing.expert.vo.withdraw.WithdrawRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "提现记录管理", description = "提现记录的查询、审核、管理等接口")
public class WithdrawRecordController {

    private final WithdrawRecordService withdrawRecordService;

    @Operation(summary = "分页查询提现记录", description = "根据条件分页查询提现记录列表")
    @PostMapping("/page")
    public Result<PageResult<WithdrawRecordVO>> getWithdrawRecordPage(@Validated @RequestBody WithdrawQueryDTO queryDTO) {
        PageResult<WithdrawRecordVO> pageResult = withdrawRecordService.getWithdrawRecordPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    @Operation(summary = "获取提现记录详情", description = "根据提现记录ID获取详细信息")
    @GetMapping("/{withdrawId}")
    public Result<WithdrawRecordVO> getWithdrawRecordDetail(
            @Parameter(description = "提现记录ID") @PathVariable Long withdrawId) {
        WithdrawRecordVO recordVO = withdrawRecordService.getWithdrawRecordDetail(withdrawId);
        if (recordVO != null) {
            return Result.success("获取成功", recordVO);
        } else {
            return Result.error("提现记录不存在");
        }
    }

    @Operation(summary = "创建提现记录", description = "创建新的提现记录")
    @PostMapping("/create")
    public Result<Long> createWithdrawRecord(@Validated @RequestBody WithdrawCreateDTO createDTO) {
        Long withdrawId = withdrawRecordService.createWithdrawRecord(createDTO);
        return Result.success("创建成功", withdrawId);
    }

    @Operation(summary = "审核提现申请", description = "审核用户的提现申请")
    @PostMapping("/audit")
    public Result<Void> auditWithdrawRecord(@Validated @RequestBody WithdrawAuditDTO auditDTO) {
        boolean success = withdrawRecordService.auditWithdrawRecord(auditDTO);
        if (success) {
            return Result.success("审核成功");
        } else {
            return Result.error("审核失败");
        }
    }

    @Operation(summary = "批量审核提现申请", description = "批量审核多个提现申请")
    @PostMapping("/audit/batch")
    public Result<Void> batchAuditWithdrawRecords(@Validated @RequestBody List<WithdrawAuditDTO> auditDTOs) {
        boolean success = withdrawRecordService.batchAuditWithdrawRecords(auditDTOs);
        if (success) {
            return Result.success("批量审核成功");
        } else {
            return Result.error("批量审核失败");
        }
    }

    @Operation(summary = "确认转账完成", description = "确认提现转账已完成")
    @PostMapping("/{withdrawId}/transfer")
    public Result<Void> confirmTransfer(
            @Parameter(description = "提现记录ID") @PathVariable Long withdrawId) {
        boolean success = withdrawRecordService.confirmTransfer(withdrawId);
        if (success) {
            return Result.success("确认转账成功");
        } else {
            return Result.error("确认转账失败");
        }
    }

    @Operation(summary = "批量确认转账完成", description = "批量确认多个提现转账已完成")
    @PostMapping("/transfer/batch")
    public Result<Void> batchConfirmTransfer(
            @Parameter(description = "提现记录ID列表") @RequestBody List<Long> withdrawIds) {
        boolean success = withdrawRecordService.batchConfirmTransfer(withdrawIds);
        if (success) {
            return Result.success("批量确认转账成功");
        } else {
            return Result.error("批量确认转账失败");
        }
    }

    @Operation(summary = "获取待审核提现数量", description = "获取待审核的提现记录数量")
    @GetMapping("/pending/count")
    public Result<Long> getPendingWithdrawCount() {
        Long count = withdrawRecordService.getPendingWithdrawCount();
        return Result.success("获取成功", count);
    }

    @Operation(summary = "获取最近提现记录", description = "获取最近的提现记录列表")
    @GetMapping("/recent")
    public Result<List<WithdrawRecordVO>> getRecentWithdrawRecords(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<WithdrawRecordVO> records = withdrawRecordService.getRecentWithdrawRecords(limit);
        return Result.success("获取成功", records);
    }

    @Operation(summary = "计算提现手续费", description = "根据提现金额计算手续费")
    @GetMapping("/fee/calculate")
    public Result<BigDecimal> calculateWithdrawFee(
            @Parameter(description = "提现金额") @RequestParam BigDecimal amount) {
        BigDecimal fee = withdrawRecordService.calculateWithdrawFee(amount);
        return Result.success("计算成功", fee);
    }

    @Operation(summary = "删除提现记录", description = "删除指定的提现记录")
    @DeleteMapping("/{withdrawId}")
    public Result<Void> deleteWithdrawRecord(
            @Parameter(description = "提现记录ID") @PathVariable Long withdrawId) {
        boolean success = withdrawRecordService.deleteWithdrawRecord(withdrawId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "批量删除提现记录", description = "批量删除提现记录")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteWithdrawRecords(
            @Parameter(description = "提现记录ID列表") @RequestBody List<Long> withdrawIds) {
        boolean success = withdrawRecordService.batchDeleteWithdrawRecords(withdrawIds);
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }
}
