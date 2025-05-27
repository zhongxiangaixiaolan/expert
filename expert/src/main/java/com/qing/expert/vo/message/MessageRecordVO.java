package com.qing.expert.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息记录VO
 */
@Data
@Schema(description = "消息记录信息")
public class MessageRecordVO {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "消息类型")
    private String messageType;

    @Schema(description = "消息类型描述")
    private String messageTypeDesc;

    @Schema(description = "接收用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "用户手机号")
    private String userPhone;

    @Schema(description = "消息标题")
    private String title;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息状态")
    private String status;

    @Schema(description = "消息状态描述")
    private String statusDesc;

    @Schema(description = "关联业务ID")
    private Long businessId;

    @Schema(description = "关联业务类型")
    private String businessType;

    @Schema(description = "模板ID")
    private String templateId;

    @Schema(description = "模板数据")
    private String templateData;

    @Schema(description = "发送渠道")
    private String sendChannel;

    @Schema(description = "发送渠道描述")
    private String sendChannelDesc;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "读取时间")
    private LocalDateTime readTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "重试次数")
    private Integer retryCount;

    @Schema(description = "最大重试次数")
    private Integer maxRetryCount;

    @Schema(description = "失败原因")
    private String failReason;

    @Schema(description = "扩展数据")
    private String extraData;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "更新者")
    private String updater;

    @Schema(description = "是否已读")
    private Boolean isRead;

    @Schema(description = "是否过期")
    private Boolean isExpired;

    @Schema(description = "是否可重试")
    private Boolean canRetry;
}
