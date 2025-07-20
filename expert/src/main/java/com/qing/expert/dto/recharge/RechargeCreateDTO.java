package com.qing.expert.dto.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 创建充值记录DTO
 */
@Data
@Schema(description = "创建充值记录DTO")
public class RechargeCreateDTO {

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "充值金额", required = true)
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    @DecimalMax(value = "99999.99", message = "充值金额不能超过99999.99")
    @Digits(integer = 5, fraction = 2, message = "充值金额格式不正确")
    private BigDecimal amount;

    @Schema(description = "支付方式", required = true)
    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "^(WECHAT)$", message = "支付方式只能是WECHAT")
    private String payType;
}
