package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 达人审核状态枚举
 */
@Getter
@AllArgsConstructor
public enum ExpertAuditStatusEnum {

    PENDING(0, "待审核"),
    APPROVED(1, "审核通过"),
    REJECTED(2, "审核拒绝");

    private final Integer code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static ExpertAuditStatusEnum getByCode(Integer code) {
        for (ExpertAuditStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否待审核
     */
    public static boolean isPending(Integer auditStatus) {
        return PENDING.getCode().equals(auditStatus);
    }

    /**
     * 判断是否审核通过
     */
    public static boolean isApproved(Integer auditStatus) {
        return APPROVED.getCode().equals(auditStatus);
    }

    /**
     * 判断是否审核拒绝
     */
    public static boolean isRejected(Integer auditStatus) {
        return REJECTED.getCode().equals(auditStatus);
    }
}
