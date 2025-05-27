package com.qing.expert.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审核记录VO
 */
@Data
public class AuditRecordVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 审核类型
     */
    private String auditType;

    /**
     * 审核类型描述
     */
    private String auditTypeDesc;

    /**
     * 关联业务ID
     */
    private Long businessId;

    /**
     * 审核标题
     */
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
     * 审核状态描述
     */
    private String auditStatusDesc;

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
     * 优先级描述
     */
    private String priorityDesc;

    /**
     * 附件信息
     */
    private String attachments;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 处理耗时（分钟）
     */
    private Long processingTime;
}
