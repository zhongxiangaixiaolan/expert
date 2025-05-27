package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 达人保存DTO
 */
@Data
@Schema(description = "达人保存请求")
public class ExpertSaveDTO {

    @Schema(description = "达人ID（更新时需要）")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "服务分类ID", example = "1")
    @NotNull(message = "服务分类ID不能为空")
    private Long categoryId;

    @Schema(description = "达人名称", example = "专业游戏代练师")
    @NotBlank(message = "达人名称不能为空")
    @Size(max = 100, message = "达人名称长度不能超过100个字符")
    private String expertName;

    @Schema(description = "达人描述", example = "专业游戏代练，经验丰富，价格合理")
    @Size(max = 1000, message = "达人描述长度不能超过1000个字符")
    private String description;

    @Schema(description = "达人头像URL", example = "/avatars/expert_001.jpg")
    private String avatar;

    @Schema(description = "最低价格", example = "50.00")
    @DecimalMin(value = "0.01", message = "最低价格必须大于0")
    @Digits(integer = 6, fraction = 2, message = "价格格式不正确")
    private BigDecimal priceMin;

    @Schema(description = "最高价格", example = "500.00")
    @DecimalMin(value = "0.01", message = "最高价格必须大于0")
    @Digits(integer = 6, fraction = 2, message = "价格格式不正确")
    private BigDecimal priceMax;

    @Schema(description = "状态：0-下线，1-在线，2-忙碌", example = "1")
    @Min(value = 0, message = "状态值不正确")
    @Max(value = 2, message = "状态值不正确")
    private Integer status = 1;

    @Schema(description = "审核状态：0-待审核，1-审核通过，2-审核拒绝", example = "0")
    @Min(value = 0, message = "审核状态值不正确")
    @Max(value = 2, message = "审核状态值不正确")
    private Integer auditStatus = 0;

    @Schema(description = "审核备注", example = "资料完整，审核通过")
    @Size(max = 255, message = "审核备注长度不能超过255个字符")
    private String auditRemark;
}
