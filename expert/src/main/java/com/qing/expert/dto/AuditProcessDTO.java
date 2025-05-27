package com.qing.expert.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 审核处理DTO
 */
@Data
public class AuditProcessDTO {

    /**
     * 审核记录ID
     */
    @NotNull(message = "审核记录ID不能为空")
    private Long id;

    /**
     * 审核状态
     */
    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;

    /**
     * 审核意见
     */
    private String auditOpinion;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核人姓名
     */
    private String auditorName;
}
