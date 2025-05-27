package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 推荐服务实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("recommend_services")
public class RecommendService {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 服务ID
     */
    @TableField("service_id")
    private Long serviceId;

    /**
     * 推荐类型（HOT-热门推荐，FEATURED-精选推荐，NEW-新品推荐，DISCOUNT-优惠推荐）
     */
    @TableField("recommend_type")
    private String recommendType;

    /**
     * 排序权重（数值越大越靠前）
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 推荐理由
     */
    @TableField("recommend_reason")
    private String recommendReason;

    /**
     * 状态（0-禁用，1-启用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

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
