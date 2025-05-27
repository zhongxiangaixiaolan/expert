package com.qing.expert.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 收藏创建DTO
 */
@Data
@Schema(description = "收藏创建请求参数")
public class FavoriteCreateDTO {

    /**
     * 用户ID（由系统自动设置）
     */
    @Schema(description = "用户ID", hidden = true)
    private Long userId;

    /**
     * 收藏类型
     */
    @Schema(description = "收藏类型：service-服务，expert-达人", example = "service")
    @NotBlank(message = "收藏类型不能为空")
    private String favoriteType;

    /**
     * 目标ID
     */
    @Schema(description = "目标ID（服务ID或达人ID）", example = "1")
    @NotNull(message = "目标ID不能为空")
    private Long targetId;
}
