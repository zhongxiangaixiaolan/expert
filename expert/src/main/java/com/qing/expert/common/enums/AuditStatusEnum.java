package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核状态枚举
 */
@Getter
@AllArgsConstructor
public enum AuditStatusEnum {

    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "审核通过"),
    REJECTED("REJECTED", "审核拒绝");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static AuditStatusEnum getByCode(String code) {
        for (AuditStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否待审核
     */
    public static boolean isPending(String status) {
        return PENDING.getCode().equals(status);
    }

    /**
     * 判断是否审核通过
     */
    public static boolean isApproved(String status) {
        return APPROVED.getCode().equals(status);
    }

    /**
     * 判断是否审核拒绝
     */
    public static boolean isRejected(String status) {
        return REJECTED.getCode().equals(status);
    }
}
