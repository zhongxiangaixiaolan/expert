package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 轮播图实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("banners")
@Schema(description = "轮播图")
public class Banner extends BaseEntity {

    @Schema(description = "轮播图标题")
    @TableField("title")
    private String title;

    @Schema(description = "图片URL")
    @TableField("image_url")
    private String imageUrl;

    @Schema(description = "跳转链接")
    @TableField("link_url")
    private String linkUrl;

    @Schema(description = "链接类型：NONE,URL,SERVICE,CATEGORY")
    @TableField("link_type")
    private String linkType;

    @Schema(description = "关联ID（服务ID或分类ID）")
    @TableField("link_id")
    private Long linkId;

    @Schema(description = "排序权重")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用")
    @TableField("status")
    private Integer status;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;
}
