package com.qing.expert.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 推荐类型枚举
 */
@Getter
@AllArgsConstructor
public enum RecommendTypeEnum {

    HOT("HOT", "热门推荐"),
    FEATURED("FEATURED", "精选推荐"),
    NEW("NEW", "新品推荐"),
    DISCOUNT("DISCOUNT", "优惠推荐");

    private final String code;
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static RecommendTypeEnum getByCode(String code) {
        for (RecommendTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否热门推荐
     */
    public static boolean isHot(String type) {
        return HOT.getCode().equals(type);
    }

    /**
     * 判断是否精选推荐
     */
    public static boolean isFeatured(String type) {
        return FEATURED.getCode().equals(type);
    }

    /**
     * 判断是否新品推荐
     */
    public static boolean isNew(String type) {
        return NEW.getCode().equals(type);
    }

    /**
     * 判断是否优惠推荐
     */
    public static boolean isDiscount(String type) {
        return DISCOUNT.getCode().equals(type);
    }
}
