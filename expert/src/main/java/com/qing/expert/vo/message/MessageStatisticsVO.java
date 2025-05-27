package com.qing.expert.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 消息统计VO
 */
@Data
@Schema(description = "消息统计信息")
public class MessageStatisticsVO {

    @Schema(description = "总消息数")
    private Long totalMessages;

    @Schema(description = "待发送消息数")
    private Long pendingMessages;

    @Schema(description = "发送中消息数")
    private Long sendingMessages;

    @Schema(description = "发送成功消息数")
    private Long successMessages;

    @Schema(description = "发送失败消息数")
    private Long failedMessages;

    @Schema(description = "已读消息数")
    private Long readMessages;

    @Schema(description = "未读消息数")
    private Long unreadMessages;

    @Schema(description = "已取消消息数")
    private Long cancelledMessages;

    @Schema(description = "发送成功率")
    private Double successRate;

    @Schema(description = "阅读率")
    private Double readRate;

    @Schema(description = "按消息类型统计")
    private List<Map<String, Object>> messageTypeStats;

    @Schema(description = "按发送渠道统计")
    private List<Map<String, Object>> sendChannelStats;

    @Schema(description = "按日期统计")
    private List<Map<String, Object>> dailyStats;

    @Schema(description = "用户阅读统计")
    private List<Map<String, Object>> userReadStats;
}
