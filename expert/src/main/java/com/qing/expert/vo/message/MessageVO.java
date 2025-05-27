package com.qing.expert.vo.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息VO
 */
@Data
@Schema(description = "消息信息")
public class MessageVO {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    private Long id;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private String messageType;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String content;

    /**
     * 消息状态
     */
    @Schema(description = "消息状态：0-未读，1-已读")
    private Integer status;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private Long businessId;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String businessType;

    /**
     * 发送时间
     */
    @Schema(description = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    /**
     * 阅读时间
     */
    @Schema(description = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
}
