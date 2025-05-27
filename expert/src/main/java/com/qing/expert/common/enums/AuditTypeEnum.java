package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核类型枚举
 */
@Getter
@AllArgsConstructor
public enum AuditTypeEnum {

    EXPERT("EXPERT", "达人审核"),
    SERVICE("SERVICE", "服务审核"),
    REVIEW("REVIEW", "评价审核"),
    WITHDRAW("WITHDRAW", "提现审核"),
    CONTENT("CONTENT", "内容审核");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static AuditTypeEnum getByCode(String code) {
        for (AuditTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否达人审核
     */
    public static boolean isExpert(String type) {
        return EXPERT.getCode().equals(type);
    }

    /**
     * 判断是否服务审核
     */
    public static boolean isService(String type) {
        return SERVICE.getCode().equals(type);
    }

    /**
     * 判断是否评价审核
     */
    public static boolean isReview(String type) {
        return REVIEW.getCode().equals(type);
    }

    /**
     * 判断是否提现审核
     */
    public static boolean isWithdraw(String type) {
        return WITHDRAW.getCode().equals(type);
    }

    /**
     * 判断是否内容审核
     */
    public static boolean isContent(String type) {
        return CONTENT.getCode().equals(type);
    }
}
