package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 管理员角色枚举
 */
@Getter
@AllArgsConstructor
public enum AdminRoleEnum {

    SUPER_ADMIN("SUPER_ADMIN", "超级管理员"),
    ADMIN("ADMIN", "管理员");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static AdminRoleEnum getByCode(String code) {
        for (AdminRoleEnum roleEnum : values()) {
            if (roleEnum.getCode().equals(code)) {
                return roleEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否为超级管理员
     */
    public static boolean isSuperAdmin(String role) {
        return SUPER_ADMIN.getCode().equals(role);
    }
}
