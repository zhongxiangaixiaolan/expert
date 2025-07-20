package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付类型枚举
 */
@Getter
@AllArgsConstructor
public enum PaymentTypeEnum {

    WECHAT_PAY("WECHAT_PAY", "微信支付"),
    BALANCE_PAY("BALANCE_PAY", "余额支付");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static PaymentTypeEnum getByCode(String code) {
        for (PaymentTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否微信支付
     */
    public static boolean isWechatPay(String type) {
        return WECHAT_PAY.getCode().equals(type);
    }

    /**
     * 判断是否余额支付
     */
    public static boolean isBalancePay(String type) {
        return BALANCE_PAY.getCode().equals(type);
    }
}
