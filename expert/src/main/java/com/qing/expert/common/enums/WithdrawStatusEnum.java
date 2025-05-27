package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提现状态枚举
 */
@Getter
@AllArgsConstructor
public enum WithdrawStatusEnum {

    PENDING(0, "待审核"),
    APPROVED(1, "审核通过"),
    REJECTED(2, "审核拒绝"),
    TRANSFERRED(3, "已转账");

    private final Integer code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static WithdrawStatusEnum getByCode(Integer code) {
        for (WithdrawStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否待审核
     */
    public static boolean isPending(Integer status) {
        return PENDING.getCode().equals(status);
    }

    /**
     * 判断是否审核通过
     */
    public static boolean isApproved(Integer status) {
        return APPROVED.getCode().equals(status);
    }

    /**
     * 判断是否审核拒绝
     */
    public static boolean isRejected(Integer status) {
        return REJECTED.getCode().equals(status);
    }

    /**
     * 判断是否已转账
     */
    public static boolean isTransferred(Integer status) {
        return TRANSFERRED.getCode().equals(status);
    }
}
