package com.qing.expert.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建评价DTO
 */
@Data
@Schema(description = "创建评价DTO")
public class ReviewCreateDTO {

    @Schema(description = "订单ID", required = true)
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "评价类型：1-用户评价达人，2-达人评价用户", required = true)
    @NotNull(message = "评价类型不能为空")
    @Min(value = 1, message = "评价类型值不正确")
    @Max(value = 2, message = "评价类型值不正确")
    private Integer reviewType;

    @Schema(description = "评分（1-5分）", required = true)
    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "1.0", message = "评分不能小于1分")
    @DecimalMax(value = "5.0", message = "评分不能大于5分")
    private BigDecimal rating;

    @Schema(description = "评价内容", required = true)
    @NotBlank(message = "评价内容不能为空")
    @Size(max = 500, message = "评价内容长度不能超过500个字符")
    private String content;

    @Schema(description = "评价图片列表")
    private List<String> images;

    @Schema(description = "是否匿名：0-否，1-是")
    private Integer isAnonymous = 0;
}
