package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 充值状态枚举
 */
@Getter
@AllArgsConstructor
public enum RechargeStatusEnum {

    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    FAILED(2, "支付失败");

    private final Integer code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static RechargeStatusEnum getByCode(Integer code) {
        for (RechargeStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否待支付
     */
    public static boolean isPending(Integer status) {
        return PENDING.getCode().equals(status);
    }

    /**
     * 判断是否已支付
     */
    public static boolean isPaid(Integer status) {
        return PAID.getCode().equals(status);
    }

    /**
     * 判断是否支付失败
     */
    public static boolean isFailed(Integer status) {
        return FAILED.getCode().equals(status);
    }
}
