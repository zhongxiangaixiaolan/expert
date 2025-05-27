package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评价类型枚举
 */
@Getter
@AllArgsConstructor
public enum ReviewTypeEnum {

    USER_TO_EXPERT(1, "用户评价达人"),
    EXPERT_TO_USER(2, "达人评价用户");

    private final Integer code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static ReviewTypeEnum getByCode(Integer code) {
        for (ReviewTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否为用户评价达人
     */
    public static boolean isUserToExpert(Integer reviewType) {
        return USER_TO_EXPERT.getCode().equals(reviewType);
    }

    /**
     * 判断是否为达人评价用户
     */
    public static boolean isExpertToUser(Integer reviewType) {
        return EXPERT_TO_USER.getCode().equals(reviewType);
    }
}
