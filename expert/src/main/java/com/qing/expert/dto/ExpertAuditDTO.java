package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 达人审核DTO
 */
@Data
@Schema(description = "达人审核请求")
public class ExpertAuditDTO {

    @Schema(description = "达人ID", example = "1")
    @NotNull(message = "达人ID不能为空")
    private Long expertId;

    @Schema(description = "审核状态：1-审核通过，2-审核拒绝", example = "1")
    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态值不正确")
    @Max(value = 2, message = "审核状态值不正确")
    private Integer auditStatus;

    @Schema(description = "审核备注", example = "资料完整，审核通过")
    @NotBlank(message = "审核备注不能为空")
    @Size(max = 255, message = "审核备注长度不能超过255个字符")
    private String auditRemark;
}
