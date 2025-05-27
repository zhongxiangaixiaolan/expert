package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态枚举
 */
@Getter
@AllArgsConstructor
public enum MessageStatusEnum {

    PENDING("PENDING", "待发送"),
    SENDING("SENDING", "发送中"),
    SUCCESS("SUCCESS", "发送成功"),
    FAILED("FAILED", "发送失败"),
    READ("READ", "已读"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static MessageStatusEnum getByCode(String code) {
        for (MessageStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否待发送
     */
    public static boolean isPending(String status) {
        return PENDING.getCode().equals(status);
    }

    /**
     * 判断是否发送中
     */
    public static boolean isSending(String status) {
        return SENDING.getCode().equals(status);
    }

    /**
     * 判断是否发送成功
     */
    public static boolean isSuccess(String status) {
        return SUCCESS.getCode().equals(status);
    }

    /**
     * 判断是否发送失败
     */
    public static boolean isFailed(String status) {
        return FAILED.getCode().equals(status);
    }

    /**
     * 判断是否已读
     */
    public static boolean isRead(String status) {
        return READ.getCode().equals(status);
    }

    /**
     * 判断是否已取消
     */
    public static boolean isCancelled(String status) {
        return CANCELLED.getCode().equals(status);
    }
}
