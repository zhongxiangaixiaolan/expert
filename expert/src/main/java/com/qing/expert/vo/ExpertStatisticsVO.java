package com.qing.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 达人统计信息VO
 */
@Data
@Schema(description = "达人统计信息")
public class ExpertStatisticsVO {

    @Schema(description = "达人总数")
    private Long totalCount;

    @Schema(description = "在线达人数")
    private Long onlineCount;

    @Schema(description = "忙碌达人数")
    private Long busyCount;

    @Schema(description = "下线达人数")
    private Long offlineCount;

    @Schema(description = "待审核达人数")
    private Long pendingCount;

    @Schema(description = "审核通过达人数")
    private Long approvedCount;

    @Schema(description = "审核拒绝达人数")
    private Long rejectedCount;

    @Schema(description = "今日新增达人数")
    private Long todayNewCount;

    @Schema(description = "本周新增达人数")
    private Long weekNewCount;

    @Schema(description = "本月新增达人数")
    private Long monthNewCount;

    @Schema(description = "平均评分")
    private Double avgRating;

    @Schema(description = "总接单数")
    private Long totalOrderCount;

    @Schema(description = "总完成数")
    private Long totalCompleteCount;

    @Schema(description = "平均完成率")
    private Double avgCompleteRate;
}
