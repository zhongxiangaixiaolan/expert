package com.qing.expert.dto.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 创建提现记录DTO
 */
@Data
@Schema(description = "创建提现记录DTO")
public class WithdrawCreateDTO {

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "提现金额", required = true)
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.01", message = "提现金额必须大于0")
    @DecimalMax(value = "99999.99", message = "提现金额不能超过99999.99")
    @Digits(integer = 5, fraction = 2, message = "提现金额格式不正确")
    private BigDecimal amount;

    @Schema(description = "银行名称", required = true)
    @NotBlank(message = "银行名称不能为空")
    @Size(max = 50, message = "银行名称长度不能超过50个字符")
    private String bankName;

    @Schema(description = "银行账号", required = true)
    @NotBlank(message = "银行账号不能为空")
    @Size(max = 50, message = "银行账号长度不能超过50个字符")
    private String bankAccount;

    @Schema(description = "账户姓名", required = true)
    @NotBlank(message = "账户姓名不能为空")
    @Size(max = 50, message = "账户姓名长度不能超过50个字符")
    private String accountName;
}
