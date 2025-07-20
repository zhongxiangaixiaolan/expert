package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
@AllArgsConstructor
public enum PayTypeEnum {

    WECHAT("WECHAT", "微信支付"),
    BALANCE("BALANCE", "余额支付");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static PayTypeEnum getByCode(String code) {
        for (PayTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否微信支付
     */
    public static boolean isWechat(String payType) {
        return WECHAT.getCode().equals(payType);
    }

    /**
     * 判断是否余额支付
     */
    public static boolean isBalance(String payType) {
        return BALANCE.getCode().equals(payType);
    }
}
