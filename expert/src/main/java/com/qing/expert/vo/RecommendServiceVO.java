package com.qing.expert.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐服务VO
 */
@Data
public class RecommendServiceVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 服务ID
     */
    private Long serviceId;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务价格
     */
    private String price;

    /**
     * 服务描述
     */
    private String description;

    /**
     * 服务图片
     */
    private String images;

    /**
     * 达人ID
     */
    private Long expertId;

    /**
     * 达人姓名
     */
    private String expertName;

    /**
     * 达人头像
     */
    private String expertAvatar;

    /**
     * 达人评分
     */
    private Double expertRating;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 推荐类型
     */
    private String recommendType;

    /**
     * 推荐类型描述
     */
    private String recommendTypeDesc;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 推荐理由
     */
    private String recommendReason;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;
}
