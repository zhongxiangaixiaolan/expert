package com.qing.expert.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 更新订单DTO
 */
@Data
@Schema(description = "更新订单DTO")
public class OrderUpdateDTO {

    @Schema(description = "订单ID", required = true)
    @NotNull(message = "订单ID不能为空")
    private Long id;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "用户备注")
    @Size(max = 200, message = "用户备注长度不能超过200个字符")
    private String userRemark;

    @Schema(description = "达人备注")
    @Size(max = 200, message = "达人备注长度不能超过200个字符")
    private String expertRemark;

    @Schema(description = "管理员备注")
    @Size(max = 200, message = "管理员备注长度不能超过200个字符")
    private String adminRemark;

    @Schema(description = "联系方式")
    @Size(max = 50, message = "联系方式长度不能超过50个字符")
    private String contactInfo;

    @Schema(description = "服务地址")
    @Size(max = 200, message = "服务地址长度不能超过200个字符")
    private String serviceAddress;

    @Schema(description = "预约时间")
    private LocalDateTime appointmentTime;

    @Schema(description = "取消原因")
    @Size(max = 200, message = "取消原因长度不能超过200个字符")
    private String cancelReason;
}
