package com.qing.expert.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 交易记录查询DTO
 */
@Data
@Schema(description = "交易记录查询参数")
public class TransactionQueryDTO {

    /**
     * 用户ID（由系统自动设置）
     */
    @Schema(description = "用户ID", hidden = true)
    private Long userId;

    /**
     * 交易类型
     */
    @Schema(description = "交易类型：recharge-充值，consume-消费，withdraw-提现，refund-退款", example = "recharge")
    private String type;

    /**
     * 交易状态
     */
    @Schema(description = "交易状态：0-处理中，1-成功，2-失败，3-已取消", example = "1")
    private Integer status;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private String endTime;

    /**
     * 页码
     */
    @Schema(description = "页码", example = "1")
    private Integer current = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}
