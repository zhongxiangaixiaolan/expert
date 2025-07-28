package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.AuditProcessDTO;
import com.qing.expert.dto.AuditRecordDTO;
import com.qing.expert.service.AuditRecordService;
import com.qing.expert.vo.AuditRecordVO;
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
public class AdminAuditController {

    private final AuditRecordService auditRecordService;

    @GetMapping("/records")
    public Result<IPage<AuditRecordVO>> getAuditRecordPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String auditType,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) String auditorName,
            @RequestParam(required = false) String submitterName,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Page<AuditRecordVO> page = new Page<>(current, size);
        IPage<AuditRecordVO> result = auditRecordService.getAuditRecordPage(
                page, auditType, auditStatus, auditorName, submitterName, priority, startTime, endTime);
        return Result.success(result);
    }

    @GetMapping("/records/{id}")
    public Result<AuditRecordVO> getAuditRecord(@PathVariable Long id) {
        AuditRecordVO auditRecord = auditRecordService.getAuditRecordById(id);
        return Result.success(auditRecord);
    }

    @PutMapping("/records/{id}")
    public Result<Boolean> updateAuditRecord(@PathVariable Long id,
            @Valid @RequestBody AuditRecordDTO dto) {
        boolean success = auditRecordService.updateAuditRecord(id, dto);
        return Result.success(success);
    }

    @DeleteMapping("/records/{id}")
    public Result<Boolean> deleteAuditRecord(@PathVariable Long id) {
        boolean success = auditRecordService.deleteAuditRecord(id);
        return Result.success(success);
    }

    @DeleteMapping("/records/batch")
    public Result<Boolean> batchDeleteAuditRecord(@RequestBody @NotEmpty List<Long> ids) {
        boolean success = auditRecordService.batchDeleteAuditRecord(ids);
        return Result.success(success);
    }

    @PostMapping("/process")
    public Result<Boolean> processAudit(@Valid @RequestBody AuditProcessDTO dto) {
        boolean success = auditRecordService.processAudit(dto);
        return Result.success(success);
    }

    @PostMapping("/process/batch")
    public Result<Boolean> batchProcessAudit(@RequestBody @NotEmpty List<AuditProcessDTO> dtoList) {
        boolean success = auditRecordService.batchProcessAudit(dtoList);
        return Result.success(success);
    }

    @GetMapping("/pending/count")
    public Result<Long> getPendingCount(@RequestParam(required = false) String auditType) {
        Long count = auditRecordService.getPendingAuditCount(auditType);
        return Result.success(count);
    }

    @GetMapping("/statistics")
    public Result<List<Map<String, Object>>> getAuditStatistics(
            @RequestParam(required = false) String auditType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<Map<String, Object>> statistics = auditRecordService.getAuditStatistics(auditType, startTime, endTime);
        return Result.success(statistics);
    }

    @GetMapping("/auditor/workload")
    public Result<List<Map<String, Object>>> getAuditorWorkload(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<Map<String, Object>> statistics = auditRecordService.getAuditorWorkloadStatistics(startTime, endTime);
        return Result.success(statistics);
    }

    @GetMapping("/overdue")
    public Result<List<AuditRecordVO>> getOverdueAuditRecords(@RequestParam(defaultValue = "24") Integer hours) {
        List<AuditRecordVO> overdueRecords = auditRecordService.getOverdueAuditRecords(hours);
        return Result.success(overdueRecords);
    }

    @PutMapping("/assign/{id}")
    public Result<Boolean> assignAuditor(@PathVariable Long id,
            @RequestParam @NotNull Long auditorId,
            @RequestParam String auditorName) {
        boolean success = auditRecordService.autoAssignAuditor(id, auditorId, auditorName);
        return Result.success(success);
    }

    @PostMapping("/submit")
    public Result<Boolean> submitAuditApplication(
            @RequestParam String auditType,
            @RequestParam Long businessId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long submitterId,
            @RequestParam String submitterName,
            @RequestParam(defaultValue = "1") Integer priority) {
        boolean success = auditRecordService.submitAuditApplication(
                auditType, businessId, title, content, submitterId, submitterName, priority);
        return Result.success(success);
    }
}
