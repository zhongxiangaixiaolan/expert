package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发送渠道枚举
 */
@Getter
@AllArgsConstructor
public enum SendChannelEnum {

    WECHAT("WECHAT", "微信"),
    SMS("SMS", "短信"),
    EMAIL("EMAIL", "邮件"),
    SYSTEM("SYSTEM", "系统内消息");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static SendChannelEnum getByCode(String code) {
        for (SendChannelEnum channelEnum : values()) {
            if (channelEnum.getCode().equals(code)) {
                return channelEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否微信渠道
     */
    public static boolean isWechat(String channel) {
        return WECHAT.getCode().equals(channel);
    }

    /**
     * 判断是否短信渠道
     */
    public static boolean isSms(String channel) {
        return SMS.getCode().equals(channel);
    }

    /**
     * 判断是否邮件渠道
     */
    public static boolean isEmail(String channel) {
        return EMAIL.getCode().equals(channel);
    }

    /**
     * 判断是否系统内消息
     */
    public static boolean isSystem(String channel) {
        return SYSTEM.getCode().equals(channel);
    }
}
