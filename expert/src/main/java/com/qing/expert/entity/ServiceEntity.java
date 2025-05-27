package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("services")
@Schema(description = "服务")
public class ServiceEntity {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 达人ID
     */
    @Schema(description = "达人ID")
    @TableField("expert_id")
    private Long expertId;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    @TableField("category_id")
    private Long categoryId;

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    @TableField("name")
    private String name;

    /**
     * 服务描述
     */
    @Schema(description = "服务描述")
    @TableField("description")
    private String description;

    /**
     * 服务价格
     */
    @Schema(description = "服务价格")
    @TableField("price")
    private BigDecimal price;

    /**
     * 服务图片（JSON数组）
     */
    @Schema(description = "服务图片")
    @TableField("images")
    private String images;

    /**
     * 服务标签
     */
    @Schema(description = "服务标签")
    @TableField("tags")
    private String tags;

    /**
     * 是否热门：0-否，1-是
     */
    @Schema(description = "是否热门")
    @TableField("is_hot")
    private Integer isHot;

    /**
     * 是否推荐：0-否，1-是
     */
    @Schema(description = "是否推荐")
    @TableField("is_recommend")
    private Integer isRecommend;

    /**
     * 排序权重
     */
    @Schema(description = "排序权重")
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态：0-下架，1-上架
     */
    @Schema(description = "状态")
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @Schema(description = "是否删除")
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
