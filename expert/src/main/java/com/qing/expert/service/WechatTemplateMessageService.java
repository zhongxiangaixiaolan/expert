package com.qing.expert.service;

import com.qing.expert.dto.message.WechatTemplateMessageDTO;

/**
 * 微信模板消息服务接口
 */
public interface WechatTemplateMessageService {

    /**
     * 发送微信模板消息
     */
    boolean sendTemplateMessage(WechatTemplateMessageDTO messageDTO);

    /**
     * 获取微信访问令牌
     */
    String getAccessToken();

    /**
     * 刷新微信访问令牌
     */
    String refreshAccessToken();

    /**
     * 验证访问令牌是否有效
     */
    boolean isAccessTokenValid(String accessToken);

    /**
     * 构建订单状态通知模板消息
     */
    WechatTemplateMessageDTO buildOrderStatusMessage(String openId, String orderNo, String status, String amount, String time);

    /**
     * 构建支付成功通知模板消息
     */
    WechatTemplateMessageDTO buildPaymentSuccessMessage(String openId, String orderNo, String amount, String time);

    /**
     * 构建提现审核通知模板消息
     */
    WechatTemplateMessageDTO buildWithdrawAuditMessage(String openId, String amount, String status, String reason, String time);

    /**
     * 构建系统通知模板消息
     */
    WechatTemplateMessageDTO buildSystemNoticeMessage(String openId, String title, String content, String time);
}
