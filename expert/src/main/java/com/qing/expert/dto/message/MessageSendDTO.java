package com.qing.expert.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 消息发送DTO
 */
@Data
@Schema(description = "消息发送参数")
public class MessageSendDTO {

    @Schema(description = "消息记录ID列表", required = true)
    @NotEmpty(message = "消息记录ID列表不能为空")
    private List<Long> messageIds;

    @Schema(description = "是否强制重新发送")
    private Boolean forceResend = false;

    @Schema(description = "发送备注")
    private String remark;
}
