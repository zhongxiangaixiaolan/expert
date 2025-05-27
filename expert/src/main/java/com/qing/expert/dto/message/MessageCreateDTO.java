package com.qing.expert.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 消息创建DTO
 */
@Data
@Schema(description = "消息创建参数")
public class MessageCreateDTO {

    @Schema(description = "消息类型", required = true)
    @NotBlank(message = "消息类型不能为空")
    private String messageType;

    @Schema(description = "接收用户ID", required = true)
    @NotNull(message = "接收用户ID不能为空")
    private Long userId;

    @Schema(description = "消息标题", required = true)
    @NotBlank(message = "消息标题不能为空")
    private String title;

    @Schema(description = "消息内容", required = true)
    @NotBlank(message = "消息内容不能为空")
    private String content;

    @Schema(description = "关联业务ID")
    private Long businessId;

    @Schema(description = "关联业务类型")
    private String businessType;

    @Schema(description = "模板ID")
    private String templateId;

    @Schema(description = "模板数据(JSON格式)")
    private String templateData;

    @Schema(description = "发送渠道", required = true)
    @NotBlank(message = "发送渠道不能为空")
    private String sendChannel;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "最大重试次数")
    private Integer maxRetryCount;

    @Schema(description = "扩展数据(JSON格式)")
    private String extraData;

    @Schema(description = "是否立即发送")
    private Boolean sendImmediately = true;
}
