package com.qing.expert.dto.banner;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 轮播图保存DTO
 */
@Data
@Schema(description = "轮播图保存参数")
public class BannerSaveDTO {

    @Schema(description = "轮播图ID（更新时必填）")
    private Long id;

    @Schema(description = "轮播图标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "轮播图标题不能为空")
    private String title;

    @Schema(description = "图片URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    @Schema(description = "跳转链接")
    private String linkUrl;

    @Schema(description = "链接类型：NONE,URL,SERVICE,CATEGORY", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "链接类型不能为空")
    private String linkType;

    @Schema(description = "关联ID（服务ID或分类ID）")
    private Long linkId;

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
