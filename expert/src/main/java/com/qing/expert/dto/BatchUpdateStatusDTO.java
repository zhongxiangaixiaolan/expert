package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量更新状态DTO
 */
@Data
@Schema(description = "批量更新状态请求")
public class BatchUpdateStatusDTO {

    @Schema(description = "ID列表")
    @NotEmpty(message = "ID列表不能为空")
    private List<Long> userIds;

    @Schema(description = "状态值")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
