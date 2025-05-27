package com.qing.expert.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 收藏查询DTO
 */
@Data
@Schema(description = "收藏查询参数")
public class FavoriteQueryDTO {

    /**
     * 用户ID（由系统自动设置）
     */
    @Schema(description = "用户ID", hidden = true)
    private Long userId;

    /**
     * 收藏类型
     */
    @Schema(description = "收藏类型：service-服务，expert-达人，不传则查询全部", example = "service")
    private String favoriteType;

    /**
     * 页码
     */
    @Schema(description = "页码", example = "1")
    private Integer current = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}
