package com.qing.expert.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 审核记录DTO
 */
@Data
public class AuditRecordDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 审核类型
     */
    @NotBlank(message = "审核类型不能为空")
    private String auditType;

    /**
     * 关联业务ID
     */
    @NotNull(message = "关联业务ID不能为空")
    private Long businessId;

    /**
     * 审核标题
     */
    @NotBlank(message = "审核标题不能为空")
    private String auditTitle;

    /**
     * 审核内容
     */
    private String auditContent;

    /**
     * 审核状态
     */
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

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 提交人ID
     */
    private Long submitterId;

    /**
     * 提交人姓名
     */
    private String submitterName;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 附件信息
     */
    private String attachments;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;
}
