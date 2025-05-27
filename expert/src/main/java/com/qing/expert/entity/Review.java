package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 评价实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reviews")
public class Review extends BaseEntity {

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 达人ID
     */
    @TableField("expert_id")
    private Long expertId;

    /**
     * 服务ID
     */
    @TableField("service_id")
    private Long serviceId;

    /**
     * 评价类型：1-用户评价达人，2-达人评价用户
     */
    @TableField("review_type")
    private Integer reviewType;

    /**
     * 评分（1-5分）
     */
    @TableField("rating")
    private BigDecimal rating;

    /**
     * 评价内容
     */
    @TableField("content")
    private String content;

    /**
     * 评价图片（JSON数组）
     */
    @TableField("images")
    private String images;

    /**
     * 是否匿名：0-否，1-是
     */
    @TableField("is_anonymous")
    private Integer isAnonymous;

    /**
     * 回复内容
     */
    @TableField("reply_content")
    private String replyContent;

    /**
     * 回复时间
     */
    @TableField("reply_time")
    private java.time.LocalDateTime replyTime;

    /**
     * 状态：1-正常，2-隐藏
     */
    @TableField("status")
    private Integer status;
}
