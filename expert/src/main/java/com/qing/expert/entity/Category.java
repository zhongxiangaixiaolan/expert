package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("categories")
@Schema(description = "服务分类")
public class Category extends BaseEntity {

    @Schema(description = "分类名称")
    @TableField("name")
    private String name;

    @Schema(description = "分类描述")
    @TableField("description")
    private String description;

    @Schema(description = "分类图标URL")
    @TableField("icon")
    private String icon;

    @Schema(description = "图标类型：iconify,emoji,url")
    @TableField("icon_type")
    private String iconType;

    @Schema(description = "图标颜色")
    @TableField("icon_color")
    private String iconColor;

    @Schema(description = "排序权重")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用")
    @TableField("status")
    private Integer status;
}
