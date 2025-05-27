package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
@AllArgsConstructor
public enum PaymentStatusEnum {

    PENDING("PENDING", "待支付"),
    PAYING("PAYING", "支付中"),
    SUCCESS("SUCCESS", "支付成功"),
    FAILED("FAILED", "支付失败"),
    CANCELLED("CANCELLED", "支付取消"),
    REFUNDED("REFUNDED", "已退款"),
    REFUNDING("REFUNDING", "退款中");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static PaymentStatusEnum getByCode(String code) {
        for (PaymentStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否待支付
     */
    public static boolean isPending(String status) {
        return PENDING.getCode().equals(status);
    }

    /**
     * 判断是否支付中
     */
    public static boolean isPaying(String status) {
        return PAYING.getCode().equals(status);
    }

    /**
     * 判断是否支付成功
     */
    public static boolean isSuccess(String status) {
        return SUCCESS.getCode().equals(status);
    }

    /**
     * 判断是否支付失败
     */
    public static boolean isFailed(String status) {
        return FAILED.getCode().equals(status);
    }

    /**
     * 判断是否已取消
     */
    public static boolean isCancelled(String status) {
        return CANCELLED.getCode().equals(status);
    }

    /**
     * 判断是否已退款
     */
    public static boolean isRefunded(String status) {
        return REFUNDED.getCode().equals(status);
    }

    /**
     * 判断是否退款中
     */
    public static boolean isRefunding(String status) {
        return REFUNDING.getCode().equals(status);
    }
}
