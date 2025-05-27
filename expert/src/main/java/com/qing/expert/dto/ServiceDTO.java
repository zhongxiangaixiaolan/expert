package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 服务DTO
 */
@Data
@Schema(description = "服务DTO")
public class ServiceDTO {

    /**
     * 达人ID
     */
    @Schema(description = "达人ID")
    @NotNull(message = "达人ID不能为空")
    private Long expertId;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    @NotBlank(message = "服务名称不能为空")
    @Size(max = 100, message = "服务名称长度不能超过100个字符")
    private String name;

    /**
     * 服务描述
     */
    @Schema(description = "服务描述")
    @Size(max = 2000, message = "服务描述长度不能超过2000个字符")
    private String description;

    /**
     * 服务价格
     */
    @Schema(description = "服务价格")
    @NotNull(message = "服务价格不能为空")
    @DecimalMin(value = "0.01", message = "服务价格必须大于0")
    @DecimalMax(value = "99999.99", message = "服务价格不能超过99999.99")
    private BigDecimal price;

    /**
     * 服务图片（JSON数组）
     */
    @Schema(description = "服务图片")
    private String images;

    /**
     * 服务标签
     */
    @Schema(description = "服务标签")
    @Size(max = 255, message = "服务标签长度不能超过255个字符")
    private String tags;

    /**
     * 是否热门：0-否，1-是
     */
    @Schema(description = "是否热门")
    private Integer isHot;

    /**
     * 是否推荐：0-否，1-是
     */
    @Schema(description = "是否推荐")
    private Integer isRecommend;

    /**
     * 排序权重
     */
    @Schema(description = "排序权重")
    private Integer sortOrder;

    /**
     * 状态：0-下架，1-上架
     */
    @Schema(description = "状态")
    private Integer status;
}
