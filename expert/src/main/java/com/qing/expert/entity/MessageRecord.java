package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 消息记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_records")
public class MessageRecord {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息类型（ORDER_STATUS-订单状态通知，PAYMENT_SUCCESS-支付成功通知等）
     */
    @TableField("message_type")
    private String messageType;

    /**
     * 接收用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 消息标题
     */
    @TableField("title")
    private String title;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息状态（PENDING-待发送，SENDING-发送中，SUCCESS-发送成功，FAILED-发送失败，READ-已读，CANCELLED-已取消）
     */
    @TableField("status")
    private String status;

    /**
     * 关联业务ID（订单ID、支付ID等）
     */
    @TableField("business_id")
    private Long businessId;

    /**
     * 关联业务类型（ORDER、PAYMENT、WITHDRAW等）
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 模板ID（微信模板消息ID）
     */
    @TableField("template_id")
    private String templateId;

    /**
     * 模板数据（JSON格式）
     */
    @TableField("template_data")
    private String templateData;

    /**
     * 发送渠道（WECHAT-微信，SMS-短信，EMAIL-邮件，SYSTEM-系统内消息）
     */
    @TableField("send_channel")
    private String sendChannel;

    /**
     * 发送时间
     */
    @TableField("send_time")
    private LocalDateTime sendTime;

    /**
     * 读取时间
     */
    @TableField("read_time")
    private LocalDateTime readTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    @TableField("max_retry_count")
    private Integer maxRetryCount;

    /**
     * 失败原因
     */
    @TableField("fail_reason")
    private String failReason;

    /**
     * 扩展数据（JSON格式）
     */
    @TableField("extra_data")
    private String extraData;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
