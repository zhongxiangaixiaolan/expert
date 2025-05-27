package com.qing.expert.vo.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价VO
 */
@Data
@Schema(description = "评价VO")
public class ReviewVO {

    @Schema(description = "评价ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "达人ID")
    private Long expertId;

    @Schema(description = "达人昵称")
    private String expertNickname;

    @Schema(description = "达人头像")
    private String expertAvatar;

    @Schema(description = "服务ID")
    private Long serviceId;

    @Schema(description = "服务名称")
    private String serviceName;

    @Schema(description = "评价类型")
    private Integer reviewType;

    @Schema(description = "评价类型描述")
    private String reviewTypeDesc;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "评价图片列表")
    private List<String> images;

    @Schema(description = "是否匿名")
    private Integer isAnonymous;

    @Schema(description = "回复内容")
    private String replyContent;

    @Schema(description = "回复时间")
    private LocalDateTime replyTime;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
