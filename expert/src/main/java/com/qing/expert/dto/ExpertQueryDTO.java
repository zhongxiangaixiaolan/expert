package com.qing.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 达人查询DTO
 */
@Data
@Schema(description = "达人查询条件")
public class ExpertQueryDTO {

    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;

    @Schema(description = "搜索关键词（达人名称、用户昵称、手机号）")
    private String keyword;

    @Schema(description = "服务分类ID")
    private Long categoryId;

    @Schema(description = "达人状态：0-下线，1-在线，2-忙碌")
    private Integer status;

    @Schema(description = "审核状态：0-待审核，1-审核通过，2-审核拒绝")
    private Integer auditStatus;

    @Schema(description = "最低评分", example = "4.0")
    private Double minRating;

    @Schema(description = "最高评分", example = "5.0")
    private Double maxRating;

    @Schema(description = "最低价格", example = "100.00")
    private Double minPrice;

    @Schema(description = "最高价格", example = "1000.00")
    private Double maxPrice;

    @Schema(description = "创建开始时间", example = "2024-01-01")
    private String createStartTime;

    @Schema(description = "创建结束时间", example = "2024-12-31")
    private String createEndTime;

    @Schema(description = "排序字段：rating-评分，orderCount-接单数，createTime-创建时间")
    private String sortField;

    @Schema(description = "排序方式：asc-升序，desc-降序")
    private String sortOrder = "desc";
}
