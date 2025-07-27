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
    }

    @PutMapping("/records/{id}")
            @Valid @RequestBody AuditRecordDTO dto) {
        boolean success = auditRecordService.updateAuditRecord(id, dto);
        return Result.success(success);
    }

    @DeleteMapping("/records/{id}")
        boolean success = auditRecordService.deleteAuditRecord(id);
        return Result.success(success);
    }

    @DeleteMapping("/records/batch")
        boolean success = auditRecordService.batchDeleteAuditRecord(ids);
        return Result.success(success);
    }

    @PostMapping("/process")
        boolean success = auditRecordService.processAudit(dto);
        return Result.success(success);
    }

    @PostMapping("/process/batch")
        boolean success = auditRecordService.batchProcessAudit(dtoList);
        return Result.success(success);
    }

    @GetMapping("/pending/count")
        return Result.success(count);
    }

    @GetMapping("/statistics")
        List<Map<String, Object>> statistics = auditRecordService.getAuditStatistics(auditType, startTime, endTime);
        return Result.success(statistics);
    }

    @GetMapping("/auditor/workload")
        return Result.success(statistics);
    }

    @GetMapping("/overdue")
        List<AuditRecordVO> overdueRecords = auditRecordService.getOverdueAuditRecords(hours);
        return Result.success(overdueRecords);
    }

    @PutMapping("/assign/{id}")
        boolean success = auditRecordService.autoAssignAuditor(id, auditorId, auditorName);
        return Result.success(success);
    }

    @PostMapping("/submit")
        boolean success = auditRecordService.submitAuditApplication(
                auditType, businessId, title, content, submitterId, submitterName, priority);
        return Result.success(success);
    }
}
