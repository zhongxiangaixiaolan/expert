package com.qing.expert.controller.admin;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.recharge.RechargeCreateDTO;
import com.qing.expert.dto.recharge.RechargeQueryDTO;
import com.qing.expert.service.RechargeRecordService;
import com.qing.expert.vo.recharge.RechargeRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "充值记录管理", description = "充值记录的查询、管理等接口")
public class RechargeRecordController {

    private final RechargeRecordService rechargeRecordService;

    @Operation(summary = "分页查询充值记录", description = "根据条件分页查询充值记录列表")
    @PostMapping("/page")
    public Result<PageResult<RechargeRecordVO>> getRechargeRecordPage(@Validated @RequestBody RechargeQueryDTO queryDTO) {
        PageResult<RechargeRecordVO> pageResult = rechargeRecordService.getRechargeRecordPage(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    @Operation(summary = "获取充值记录详情", description = "根据充值记录ID获取详细信息")
    @GetMapping("/{rechargeId}")
    public Result<RechargeRecordVO> getRechargeRecordDetail(
            @Parameter(description = "充值记录ID") @PathVariable Long rechargeId) {
        RechargeRecordVO recordVO = rechargeRecordService.getRechargeRecordDetail(rechargeId);
        if (recordVO != null) {
            return Result.success("获取成功", recordVO);
        } else {
            return Result.error("充值记录不存在");
        }
    }

    @Operation(summary = "创建充值记录", description = "创建新的充值记录")
    @PostMapping("/create")
    public Result<Long> createRechargeRecord(@Validated @RequestBody RechargeCreateDTO createDTO) {
        Long rechargeId = rechargeRecordService.createRechargeRecord(createDTO);
        return Result.success("创建成功", rechargeId);
    }

    @Operation(summary = "更新充值状态", description = "更新充值记录的状态")
    @PostMapping("/{rechargeId}/status")
    public Result<Void> updateRechargeStatus(
            @Parameter(description = "充值记录ID") @PathVariable Long rechargeId,
            @Parameter(description = "状态：0-待支付，1-已支付，2-支付失败") @RequestParam Integer status,
            @Parameter(description = "第三方支付订单号") @RequestParam(required = false) String payOrderNo) {
        boolean success = rechargeRecordService.updateRechargeStatus(rechargeId, status, payOrderNo);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    @Operation(summary = "支付成功回调", description = "处理支付成功的回调")
    @PostMapping("/payment/success")
    public Result<Void> handlePaymentSuccess(
            @Parameter(description = "订单号") @RequestParam String orderNo,
            @Parameter(description = "第三方支付订单号") @RequestParam String payOrderNo) {
        boolean success = rechargeRecordService.handlePaymentSuccess(orderNo, payOrderNo);
        if (success) {
            return Result.success("支付成功处理完成");
        } else {
            return Result.error("支付成功处理失败");
        }
    }

    @Operation(summary = "支付失败回调", description = "处理支付失败的回调")
    @PostMapping("/payment/failed")
    public Result<Void> handlePaymentFailed(
            @Parameter(description = "订单号") @RequestParam String orderNo,
            @Parameter(description = "失败原因") @RequestParam String failReason) {
        boolean success = rechargeRecordService.handlePaymentFailed(orderNo, failReason);
        if (success) {
            return Result.success("支付失败处理完成");
        } else {
            return Result.error("支付失败处理失败");
        }
    }

    @Operation(summary = "获取最近充值记录", description = "获取最近的充值记录列表")
    @GetMapping("/recent")
    public Result<List<RechargeRecordVO>> getRecentRechargeRecords(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<RechargeRecordVO> records = rechargeRecordService.getRecentRechargeRecords(limit);
        return Result.success("获取成功", records);
    }

    @Operation(summary = "删除充值记录", description = "删除指定的充值记录")
    @DeleteMapping("/{rechargeId}")
    public Result<Void> deleteRechargeRecord(
            @Parameter(description = "充值记录ID") @PathVariable Long rechargeId) {
        boolean success = rechargeRecordService.deleteRechargeRecord(rechargeId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "批量删除充值记录", description = "批量删除充值记录")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteRechargeRecords(
            @Parameter(description = "充值记录ID列表") @RequestBody List<Long> rechargeIds) {
        boolean success = rechargeRecordService.batchDeleteRechargeRecords(rechargeIds);
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }
}
