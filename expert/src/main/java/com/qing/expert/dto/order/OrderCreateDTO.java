package com.qing.expert.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建订单DTO
 */
@Data
@Schema(description = "创建订单DTO")
public class OrderCreateDTO {

    @Schema(description = "服务ID", required = true)
    @NotNull(message = "服务ID不能为空")
    private Long serviceId;

    @Schema(description = "订单标题", required = true)
    @NotBlank(message = "订单标题不能为空")
    @Size(max = 100, message = "订单标题长度不能超过100个字符")
    private String title;

    @Schema(description = "订单描述")
    @Size(max = 500, message = "订单描述长度不能超过500个字符")
    private String description;

    @Schema(description = "订单金额", required = true)
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0")
    @Digits(integer = 8, fraction = 2, message = "订单金额格式不正确")
    private BigDecimal amount;

    @Schema(description = "用户备注")
    @Size(max = 200, message = "用户备注长度不能超过200个字符")
    private String userRemark;

    @Schema(description = "联系方式", required = true)
    @NotBlank(message = "联系方式不能为空")
    @Size(max = 50, message = "联系方式长度不能超过50个字符")
    private String contactInfo;

    @Schema(description = "服务地址")
    @Size(max = 200, message = "服务地址长度不能超过200个字符")
    private String serviceAddress;

    @Schema(description = "预约时间")
    private LocalDateTime appointmentTime;
}
