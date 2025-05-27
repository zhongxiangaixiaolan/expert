package com.qing.expert.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 推荐服务DTO
 */
@Data
public class RecommendServiceDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 服务ID
     */
    @NotNull(message = "服务ID不能为空")
    private Long serviceId;

    /**
     * 推荐类型
     */
    @NotBlank(message = "推荐类型不能为空")
    private String recommendType;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 推荐理由
     */
    private String recommendReason;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;
}
