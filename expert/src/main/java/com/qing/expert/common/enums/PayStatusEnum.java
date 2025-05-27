package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
@AllArgsConstructor
public enum PayStatusEnum {

    UNPAID(0, "未支付"),
    PAID(1, "已支付"),
    REFUNDED(2, "已退款");

    private final Integer code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static PayStatusEnum getByCode(Integer code) {
        for (PayStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否未支付
     */
    public static boolean isUnpaid(Integer payStatus) {
        return UNPAID.getCode().equals(payStatus);
    }

    /**
     * 判断是否已支付
     */
    public static boolean isPaid(Integer payStatus) {
        return PAID.getCode().equals(payStatus);
    }

    /**
     * 判断是否已退款
     */
    public static boolean isRefunded(Integer payStatus) {
        return REFUNDED.getCode().equals(payStatus);
    }
}
