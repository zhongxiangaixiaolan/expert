package com.qing.expert.dto.message;

import com.qing.expert.dto.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 消息记录查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息记录查询参数")
public class MessageQueryDTO extends BasePageDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "消息类型")
    private String messageType;

    @Schema(description = "消息状态")
    private String status;

    @Schema(description = "发送渠道")
    private String sendChannel;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务ID")
    private Long businessId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户手机号")
    private String userPhone;

    @Schema(description = "消息标题")
    private String title;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "是否只查询未读消息")
    private Boolean unreadOnly;

    @Schema(description = "是否只查询失败消息")
    private Boolean failedOnly;
}
