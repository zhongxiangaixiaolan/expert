package com.qing.expert.dto.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 提现审核DTO
 */
@Data
@Schema(description = "提现审核DTO")
public class WithdrawAuditDTO {

    @Schema(description = "提现记录ID", required = true)
    @NotNull(message = "提现记录ID不能为空")
    private Long withdrawId;

    @Schema(description = "审核状态", required = true)
    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态必须是1(通过)或2(拒绝)")
    @Max(value = 2, message = "审核状态必须是1(通过)或2(拒绝)")
    private Integer status;

    @Schema(description = "审核备注")
    @Size(max = 255, message = "审核备注长度不能超过255个字符")
    private String auditRemark;
}
