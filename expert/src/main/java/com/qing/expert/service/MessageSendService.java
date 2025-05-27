package com.qing.expert.service;

import com.qing.expert.entity.MessageRecord;

/**
 * 消息发送服务接口
 */
public interface MessageSendService {

    /**
     * 发送消息
     */
    boolean sendMessage(MessageRecord messageRecord);

    /**
     * 发送微信模板消息
     */
    boolean sendWechatMessage(MessageRecord messageRecord);

    /**
     * 发送短信消息
     */
    boolean sendSmsMessage(MessageRecord messageRecord);

    /**
     * 发送邮件消息
     */
    boolean sendEmailMessage(MessageRecord messageRecord);

    /**
     * 发送系统内消息
     */
    boolean sendSystemMessage(MessageRecord messageRecord);

    /**
     * 更新消息发送状态
     */
    boolean updateMessageStatus(Long messageId, String status, String failReason);

    /**
     * 增加重试次数
     */
    boolean incrementRetryCount(Long messageId);

    /**
     * 检查是否可以重试
     */
    boolean canRetry(MessageRecord messageRecord);

    /**
     * 获取用户OpenID
     */
    String getUserOpenId(Long userId);
}
