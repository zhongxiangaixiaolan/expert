package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 配置更新DTO
 */
@Data
@Schema(description = "配置更新DTO")
public class ConfigUpdateDTO {

    @Schema(description = "配置键", required = true)
    @NotBlank(message = "配置键不能为空")
    private String configKey;

    @Schema(description = "配置值", required = true)
    @NotBlank(message = "配置值不能为空")
    private String configValue;
}
