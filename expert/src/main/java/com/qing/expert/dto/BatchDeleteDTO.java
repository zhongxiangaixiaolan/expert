package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量删除DTO
 */
@Data
@Schema(description = "批量删除请求")
public class BatchDeleteDTO {

    @Schema(description = "用户ID列表")
    @NotEmpty(message = "用户ID列表不能为空")
    private List<Long> userIds;
}
