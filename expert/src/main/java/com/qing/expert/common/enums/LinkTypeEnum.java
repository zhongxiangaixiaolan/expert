package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 轮播图链接类型枚举
 */
@Getter
@AllArgsConstructor
public enum LinkTypeEnum {

    NONE("NONE", "无链接"),
    URL("URL", "外部链接"),
    SERVICE("SERVICE", "服务详情"),
    CATEGORY("CATEGORY", "分类页面");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static LinkTypeEnum getByCode(String code) {
        for (LinkTypeEnum linkType : values()) {
            if (linkType.getCode().equals(code)) {
                return linkType;
            }
        }
        return NONE;
    }

    /**
     * 验证代码是否有效
     */
    public static boolean isValidCode(String code) {
        for (LinkTypeEnum linkType : values()) {
            if (linkType.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
