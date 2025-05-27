package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.AuditProcessDTO;
import com.qing.expert.dto.AuditRecordDTO;
import com.qing.expert.service.AuditRecordService;
import com.qing.expert.vo.AuditRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理端审核控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/audit")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理端审核管理", description = "审核记录的增删改查、审核处理、统计分析等功能")
public class AdminAuditController {

    private final AuditRecordService auditRecordService;

    @GetMapping("/records/page")
    @Operation(summary = "分页查询审核记录列表", description = "支持按审核类型、状态、审核人、提交人等条件筛选")
    public Result<IPage<AuditRecordVO>> getAuditRecordPage(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "审核类型") @RequestParam(required = false) String auditType,
            @Parameter(description = "审核状态") @RequestParam(required = false) String auditStatus,
            @Parameter(description = "审核人姓名") @RequestParam(required = false) String auditorName,
            @Parameter(description = "提交人姓名") @RequestParam(required = false) String submitterName,
            @Parameter(description = "优先级") @RequestParam(required = false) Integer priority,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        Page<AuditRecordVO> page = new Page<>(current, size);
        IPage<AuditRecordVO> result = auditRecordService.getAuditRecordPage(
                page, auditType, auditStatus, auditorName, submitterName, priority, startTime, endTime);
        return Result.success(result);
    }

    @GetMapping("/records/{id}")
    @Operation(summary = "根据ID查询审核记录详情")
    public Result<AuditRecordVO> getAuditRecordById(
            @Parameter(description = "审核记录ID") @PathVariable @NotNull Long id) {
        AuditRecordVO auditRecord = auditRecordService.getAuditRecordById(id);
        return Result.success(auditRecord);
    }

    @PostMapping("/records")
    @Operation(summary = "创建审核记录")
    public Result<Boolean> createAuditRecord(@Valid @RequestBody AuditRecordDTO dto) {
        boolean success = auditRecordService.createAuditRecord(dto);
        return Result.success(success);
    }

    @PutMapping("/records/{id}")
    @Operation(summary = "更新审核记录")
    public Result<Boolean> updateAuditRecord(
            @Parameter(description = "审核记录ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody AuditRecordDTO dto) {
        boolean success = auditRecordService.updateAuditRecord(id, dto);
        return Result.success(success);
    }

    @DeleteMapping("/records/{id}")
    @Operation(summary = "删除审核记录")
    public Result<Boolean> deleteAuditRecord(
            @Parameter(description = "审核记录ID") @PathVariable @NotNull Long id) {
        boolean success = auditRecordService.deleteAuditRecord(id);
        return Result.success(success);
    }

    @DeleteMapping("/records/batch")
    @Operation(summary = "批量删除审核记录")
    public Result<Boolean> batchDeleteAuditRecord(
            @Parameter(description = "审核记录ID列表") @RequestBody @NotEmpty List<Long> ids) {
        boolean success = auditRecordService.batchDeleteAuditRecord(ids);
        return Result.success(success);
    }

    @PostMapping("/process")
    @Operation(summary = "处理审核")
    public Result<Boolean> processAudit(@Valid @RequestBody AuditProcessDTO dto) {
        boolean success = auditRecordService.processAudit(dto);
        return Result.success(success);
    }

    @PostMapping("/process/batch")
    @Operation(summary = "批量处理审核")
    public Result<Boolean> batchProcessAudit(
            @Parameter(description = "审核处理列表") @RequestBody @NotEmpty List<AuditProcessDTO> dtoList) {
        boolean success = auditRecordService.batchProcessAudit(dtoList);
        return Result.success(success);
    }

    @GetMapping("/pending/count")
    @Operation(summary = "查询待审核记录数量")
    public Result<Integer> countPendingAuditRecords(
            @Parameter(description = "审核类型") @RequestParam(required = false) String auditType,
            @Parameter(description = "审核人ID") @RequestParam(required = false) Long auditorId) {
        int count = auditRecordService.countPendingAuditRecords(auditType, auditorId);
        return Result.success(count);
    }

    @GetMapping("/statistics")
    @Operation(summary = "查询审核统计数据")
    public Result<List<Map<String, Object>>> getAuditStatistics(
            @Parameter(description = "审核类型") @RequestParam(required = false) String auditType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<Map<String, Object>> statistics = auditRecordService.getAuditStatistics(auditType, startTime, endTime);
        return Result.success(statistics);
    }

    @GetMapping("/auditor/workload")
    @Operation(summary = "查询审核人员工作量统计")
    public Result<List<Map<String, Object>>> getAuditorWorkloadStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<Map<String, Object>> statistics = auditRecordService.getAuditorWorkloadStatistics(startTime, endTime);
        return Result.success(statistics);
    }

    @GetMapping("/overdue")
    @Operation(summary = "查询超时未处理的审核记录")
    public Result<List<AuditRecordVO>> getOverdueAuditRecords(
            @Parameter(description = "超时小时数", example = "24") @RequestParam(defaultValue = "24") Integer hours) {
        List<AuditRecordVO> overdueRecords = auditRecordService.getOverdueAuditRecords(hours);
        return Result.success(overdueRecords);
    }

    @PutMapping("/assign/{id}")
    @Operation(summary = "分配审核人员")
    public Result<Boolean> autoAssignAuditor(
            @Parameter(description = "审核记录ID") @PathVariable @NotNull Long id,
            @Parameter(description = "审核人员ID") @RequestParam @NotNull Long auditorId,
            @Parameter(description = "审核人员姓名") @RequestParam String auditorName) {
        boolean success = auditRecordService.autoAssignAuditor(id, auditorId, auditorName);
        return Result.success(success);
    }

    @PostMapping("/submit")
    @Operation(summary = "提交审核申请")
    public Result<Boolean> submitAuditApplication(
            @Parameter(description = "审核类型") @RequestParam String auditType,
            @Parameter(description = "业务ID") @RequestParam Long businessId,
            @Parameter(description = "审核标题") @RequestParam String title,
            @Parameter(description = "审核内容") @RequestParam(required = false) String content,
            @Parameter(description = "提交人ID") @RequestParam Long submitterId,
            @Parameter(description = "提交人姓名") @RequestParam String submitterName,
            @Parameter(description = "优先级") @RequestParam(defaultValue = "2") Integer priority) {
        boolean success = auditRecordService.submitAuditApplication(
                auditType, businessId, title, content, submitterId, submitterName, priority);
        return Result.success(success);
    }
}
