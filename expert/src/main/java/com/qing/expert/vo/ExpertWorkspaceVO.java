package com.qing.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 达人工作台VO
 */
@Data
@Schema(description = "达人工作台数据")
public class ExpertWorkspaceVO {

    @Schema(description = "达人ID")
    private Long expertId;

    @Schema(description = "达人名称")
    private String expertName;

    @Schema(description = "达人头像")
    private String avatar;

    @Schema(description = "达人状态：0-下线，1-在线，2-忙碌")
    private Integer status;

    @Schema(description = "审核状态：0-待审核，1-审核通过，2-审核拒绝")
    private Integer auditStatus;

    @Schema(description = "评分（1-5分）")
    private BigDecimal rating;

    @Schema(description = "总接单数量")
    private Integer orderCount;

    @Schema(description = "总完成数量")
    private Integer completeCount;

    @Schema(description = "完成率")
    private BigDecimal completeRate;

    @Schema(description = "待处理订单数量")
    private Integer pendingOrderCount;

    @Schema(description = "今日统计数据")
    private Map<String, Object> todayStats;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "审核状态描述")
    private String auditStatusDesc;

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "下线";
            case 1:
                return "在线";
            case 2:
                return "忙碌";
            default:
                return "未知";
        }
    }

    /**
     * 获取审核状态描述
     */
    public String getAuditStatusDesc() {
        if (auditStatus == null) {
            return "未知";
        }
        switch (auditStatus) {
            case 0:
                return "待审核";
            case 1:
                return "审核通过";
            case 2:
                return "审核拒绝";
            default:
                return "未知";
        }
    }

    /**
     * 计算完成率
     */
    public BigDecimal getCompleteRate() {
        if (orderCount == null || orderCount == 0) {
            return BigDecimal.ZERO;
        }
        if (completeCount == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(completeCount)
                .divide(BigDecimal.valueOf(orderCount), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
