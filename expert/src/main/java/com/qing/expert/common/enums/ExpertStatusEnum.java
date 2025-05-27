package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 达人状态枚举
 */
@Getter
@AllArgsConstructor
public enum ExpertStatusEnum {

    OFFLINE(0, "下线"),
    ONLINE(1, "在线"),
    BUSY(2, "忙碌");

    private final Integer code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static ExpertStatusEnum getByCode(Integer code) {
        for (ExpertStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否在线
     */
    public static boolean isOnline(Integer status) {
        return ONLINE.getCode().equals(status);
    }

    /**
     * 判断是否忙碌
     */
    public static boolean isBusy(Integer status) {
        return BUSY.getCode().equals(status);
    }

    /**
     * 判断是否下线
     */
    public static boolean isOffline(Integer status) {
        return OFFLINE.getCode().equals(status);
    }
}
