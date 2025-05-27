package com.qing.expert.dto.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通告查询DTO
 */
@Data
@Schema(description = "通告查询参数")
public class AnnouncementQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "标题关键词")
    private String title;

    @Schema(description = "通告类型：NOTICE,ACTIVITY,SYSTEM")
    private String type;

    @Schema(description = "是否滚动显示：0-否，1-是")
    private Integer isScroll;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "开始时间-起始")
    private LocalDateTime startTimeBegin;

    @Schema(description = "开始时间-结束")
    private LocalDateTime startTimeEnd;

    @Schema(description = "结束时间-起始")
    private LocalDateTime endTimeBegin;

    @Schema(description = "结束时间-结束")
    private LocalDateTime endTimeEnd;
}
