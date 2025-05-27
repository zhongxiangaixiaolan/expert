package com.qing.expert.dto.review;

import com.qing.expert.dto.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评价查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "评价查询DTO")
public class ReviewQueryDTO extends BasePageDTO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "达人ID")
    private Long expertId;

    @Schema(description = "服务ID")
    private Long serviceId;

    @Schema(description = "评价类型：1-用户评价达人，2-达人评价用户")
    private Integer reviewType;

    @Schema(description = "最低评分")
    private BigDecimal minRating;

    @Schema(description = "最高评分")
    private BigDecimal maxRating;

    @Schema(description = "评价内容关键词")
    private String contentKeyword;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "达人昵称")
    private String expertNickname;

    @Schema(description = "服务名称")
    private String serviceName;

    @Schema(description = "状态：1-正常，2-隐藏")
    private Integer status;

    @Schema(description = "是否匿名：0-否，1-是")
    private Integer isAnonymous;

    @Schema(description = "创建时间开始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束")
    private LocalDateTime createTimeEnd;
}
