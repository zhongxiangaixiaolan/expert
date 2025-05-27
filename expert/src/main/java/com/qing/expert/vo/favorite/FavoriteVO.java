package com.qing.expert.vo.favorite;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏VO
 */
@Data
@Schema(description = "收藏信息")
public class FavoriteVO {

    /**
     * 收藏记录ID
     */
    @Schema(description = "收藏记录ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 收藏类型
     */
    @Schema(description = "收藏类型：service-服务，expert-达人")
    private String favoriteType;

    /**
     * 目标ID
     */
    @Schema(description = "目标ID（服务ID或达人ID）")
    private Long targetId;

    /**
     * 服务信息（当收藏类型为service时）
     */
    @Schema(description = "服务信息")
    private ServiceInfo service;

    /**
     * 达人信息（当收藏类型为expert时）
     */
    @Schema(description = "达人信息")
    private ExpertInfo expert;

    /**
     * 收藏时间
     */
    @Schema(description = "收藏时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 服务信息
     */
    @Data
    @Schema(description = "服务信息")
    public static class ServiceInfo {
        @Schema(description = "服务ID")
        private Long id;

        @Schema(description = "服务标题")
        private String title;

        @Schema(description = "服务描述")
        private String description;

        @Schema(description = "服务价格")
        private Double price;

        @Schema(description = "服务图片")
        private String images;

        @Schema(description = "达人名称")
        private String expertName;

        @Schema(description = "达人头像")
        private String expertAvatar;

        @Schema(description = "服务评分")
        private Double rating;

        @Schema(description = "订单数量")
        private Integer orderCount;
    }

    /**
     * 达人信息
     */
    @Data
    @Schema(description = "达人信息")
    public static class ExpertInfo {
        @Schema(description = "达人ID")
        private Long id;

        @Schema(description = "达人名称")
        private String name;

        @Schema(description = "达人描述")
        private String description;

        @Schema(description = "达人头像")
        private String avatar;

        @Schema(description = "达人评分")
        private Double rating;

        @Schema(description = "订单数量")
        private Integer orderCount;

        @Schema(description = "完成数量")
        private Integer completeCount;

        @Schema(description = "分类名称")
        private String categoryName;
    }
}
