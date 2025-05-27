package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 分类保存DTO
 */
@Data
@Schema(description = "分类保存请求")
public class CategorySaveDTO {

    @Schema(description = "分类ID（更新时需要）")
    private Long id;

    @Schema(description = "分类名称", example = "游戏代练")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String name;

    @Schema(description = "分类描述", example = "各类游戏代练服务")
    @Size(max = 255, message = "分类描述长度不能超过255个字符")
    private String description;

    @Schema(description = "分类图标URL", example = "/icons/game.png")
    private String icon;

    @Schema(description = "图标类型：iconify,emoji,url", example = "iconify")
    private String iconType;

    @Schema(description = "图标颜色", example = "#ff6b35")
    private String iconColor;

    @Schema(description = "排序权重", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status = 1;
}
