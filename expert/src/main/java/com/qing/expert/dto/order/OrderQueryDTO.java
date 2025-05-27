package com.qing.expert.dto.order;

import com.qing.expert.dto.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单查询DTO")
public class OrderQueryDTO extends BasePageDTO {

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "达人ID")
    private Long expertId;

    @Schema(description = "服务ID")
    private Long serviceId;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "支付状态")
    private Integer payStatus;

    @Schema(description = "订单标题")
    private String title;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "达人昵称")
    private String expertNickname;

    @Schema(description = "服务名称")
    private String serviceName;

    @Schema(description = "创建时间开始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束")
    private LocalDateTime createTimeEnd;

    @Schema(description = "支付时间开始")
    private LocalDateTime payTimeStart;

    @Schema(description = "支付时间结束")
    private LocalDateTime payTimeEnd;

    @Schema(description = "完成时间开始")
    private LocalDateTime finishTimeStart;

    @Schema(description = "完成时间结束")
    private LocalDateTime finishTimeEnd;
}
