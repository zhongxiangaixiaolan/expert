package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通告实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("announcements")
@Schema(description = "通告")
public class Announcement extends BaseEntity {

    @Schema(description = "通告标题")
    @TableField("title")
    private String title;

    @Schema(description = "通告内容")
    @TableField("content")
    private String content;

    @Schema(description = "通告类型：NOTICE,ACTIVITY,SYSTEM")
    @TableField("type")
    private String type;

    @Schema(description = "是否滚动显示：0-否，1-是")
    @TableField("is_scroll")
    private Integer isScroll;

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
