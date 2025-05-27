package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    PENDING(1, "待接单"),
    ACCEPTED(2, "已接单"),
    IN_SERVICE(3, "服务中"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    AFTER_SALE(6, "售后中");

    private final Integer code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static OrderStatusEnum getByCode(Integer code) {
        for (OrderStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否为待接单
     */
    public static boolean isPending(Integer status) {
        return PENDING.getCode().equals(status);
    }

    /**
     * 判断是否为已接单
     */
    public static boolean isAccepted(Integer status) {
        return ACCEPTED.getCode().equals(status);
    }

    /**
     * 判断是否为服务中
     */
    public static boolean isInService(Integer status) {
        return IN_SERVICE.getCode().equals(status);
    }

    /**
     * 判断是否为已完成
     */
    public static boolean isCompleted(Integer status) {
        return COMPLETED.getCode().equals(status);
    }

    /**
     * 判断是否为已取消
     */
    public static boolean isCancelled(Integer status) {
        return CANCELLED.getCode().equals(status);
    }

    /**
     * 判断是否为售后中
     */
    public static boolean isAfterSale(Integer status) {
        return AFTER_SALE.getCode().equals(status);
    }
}
