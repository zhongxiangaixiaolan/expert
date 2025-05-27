package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    ORDER_STATUS("ORDER_STATUS", "订单状态通知"),
    PAYMENT_SUCCESS("PAYMENT_SUCCESS", "支付成功通知"),
    PAYMENT_FAILED("PAYMENT_FAILED", "支付失败通知"),
    WITHDRAW_APPROVED("WITHDRAW_APPROVED", "提现审核通过"),
    WITHDRAW_REJECTED("WITHDRAW_REJECTED", "提现审核拒绝"),
    EXPERT_APPROVED("EXPERT_APPROVED", "达人认证通过"),
    EXPERT_REJECTED("EXPERT_REJECTED", "达人认证拒绝"),
    SERVICE_APPROVED("SERVICE_APPROVED", "服务审核通过"),
    SERVICE_REJECTED("SERVICE_REJECTED", "服务审核拒绝"),
    SYSTEM_NOTICE("SYSTEM_NOTICE", "系统通知"),
    PROMOTION("PROMOTION", "推广消息");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static MessageTypeEnum getByCode(String code) {
        for (MessageTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否订单状态通知
     */
    public static boolean isOrderStatus(String type) {
        return ORDER_STATUS.getCode().equals(type);
    }

    /**
     * 判断是否支付相关通知
     */
    public static boolean isPaymentRelated(String type) {
        return PAYMENT_SUCCESS.getCode().equals(type) || PAYMENT_FAILED.getCode().equals(type);
    }

    /**
     * 判断是否提现相关通知
     */
    public static boolean isWithdrawRelated(String type) {
        return WITHDRAW_APPROVED.getCode().equals(type) || WITHDRAW_REJECTED.getCode().equals(type);
    }

    /**
     * 判断是否达人相关通知
     */
    public static boolean isExpertRelated(String type) {
        return EXPERT_APPROVED.getCode().equals(type) || EXPERT_REJECTED.getCode().equals(type);
    }

    /**
     * 判断是否服务相关通知
     */
    public static boolean isServiceRelated(String type) {
        return SERVICE_APPROVED.getCode().equals(type) || SERVICE_REJECTED.getCode().equals(type);
    }

    /**
     * 判断是否系统通知
     */
    public static boolean isSystemNotice(String type) {
        return SYSTEM_NOTICE.getCode().equals(type);
    }

    /**
     * 判断是否推广消息
     */
    public static boolean isPromotion(String type) {
        return PROMOTION.getCode().equals(type);
    }
}
