package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审核记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("audit_records")
public class AuditRecord {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 审核类型（EXPERT-达人审核，SERVICE-服务审核，REVIEW-评价审核，WITHDRAW-提现审核，CONTENT-内容审核）
     */
    @TableField("audit_type")
    private String auditType;

    /**
     * 关联业务ID
     */
    @TableField("business_id")
    private Long businessId;

    /**
     * 审核标题
     */
    @TableField("audit_title")
    private String auditTitle;

    /**
     * 审核内容
     */
    @TableField("audit_content")
    private String auditContent;

    /**
     * 审核状态（PENDING-待审核，APPROVED-审核通过，REJECTED-审核拒绝）
     */
    @TableField("audit_status")
    private String auditStatus;

    /**
     * 审核意见
     */
    @TableField("audit_opinion")
    private String auditOpinion;

    /**
     * 审核人ID
     */
    @TableField("auditor_id")
    private Long auditorId;

    /**
     * 审核人姓名
     */
    @TableField("auditor_name")
    private String auditorName;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 提交人ID
     */
    @TableField("submitter_id")
    private Long submitterId;

    /**
     * 提交人姓名
     */
    @TableField("submitter_name")
    private String submitterName;

    /**
     * 提交时间
     */
    @TableField("submit_time")
    private LocalDateTime submitTime;

    /**
     * 优先级（1-低，2-中，3-高）
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 附件信息（JSON格式）
     */
    @TableField("attachments")
    private String attachments;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
