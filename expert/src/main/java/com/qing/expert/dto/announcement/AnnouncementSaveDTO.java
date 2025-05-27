package com.qing.expert.dto.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 通告保存DTO
 */
@Data
@Schema(description = "通告保存参数")
public class AnnouncementSaveDTO {

    @Schema(description = "通告ID（更新时必填）")
    private Long id;

    @Schema(description = "通告标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "通告标题不能为空")
    private String title;

    @Schema(description = "通告内容")
    private String content;

    @Schema(description = "通告类型：NOTICE,ACTIVITY,SYSTEM", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "通告类型不能为空")
    private String type;

    @Schema(description = "是否滚动显示：0-否，1-是", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否滚动显示不能为空")
    private Integer isScroll;

    @Schema(description = "排序权重", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序权重不能为空")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
