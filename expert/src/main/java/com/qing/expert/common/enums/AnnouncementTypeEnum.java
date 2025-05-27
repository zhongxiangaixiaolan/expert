package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通告类型枚举
 */
@Getter
@AllArgsConstructor
public enum AnnouncementTypeEnum {

    NOTICE("NOTICE", "通知公告"),
    ACTIVITY("ACTIVITY", "活动公告"),
    SYSTEM("SYSTEM", "系统公告");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static AnnouncementTypeEnum getByCode(String code) {
        for (AnnouncementTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return NOTICE;
    }

    /**
     * 验证代码是否有效
     */
    public static boolean isValidCode(String code) {
        for (AnnouncementTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
