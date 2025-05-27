package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态枚举
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private final Integer code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static StatusEnum getByCode(Integer code) {
        for (StatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否启用
     */
    public static boolean isEnable(Integer status) {
        return ENABLE.getCode().equals(status);
    }

    /**
     * 判断是否禁用
     */
    public static boolean isDisable(Integer status) {
        return DISABLE.getCode().equals(status);
    }
}
