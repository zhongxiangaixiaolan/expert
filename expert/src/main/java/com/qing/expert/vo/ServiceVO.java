package com.qing.expert.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务VO
 */
@Data
@Schema(description = "服务VO")
public class ServiceVO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 达人ID
     */
    @Schema(description = "达人ID")
    private Long expertId;

    /**
     * 达人姓名
     */
    @Schema(description = "达人姓名")
    private String expertName;

    /**
     * 达人头像
     */
    @Schema(description = "达人头像")
    private String expertAvatar;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long categoryId;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String name;

    /**
     * 服务描述
     */
    @Schema(description = "服务描述")
    private String description;

    /**
     * 服务价格
     */
    @Schema(description = "服务价格")
    private BigDecimal price;

    /**
     * 服务图片（JSON数组）
     */
    @Schema(description = "服务图片")
    private String images;

    /**
     * 服务标签
     */
    @Schema(description = "服务标签")
    private String tags;

    /**
     * 是否热门：0-否，1-是
     */
    @Schema(description = "是否热门")
    private Integer isHot;

    /**
     * 是否推荐：0-否，1-是
     */
    @Schema(description = "是否推荐")
    private Integer isRecommend;

    /**
     * 排序权重
     */
    @Schema(description = "排序权重")
    private Integer sortOrder;

    /**
     * 状态：0-下架，1-上架
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述")
    private String statusText;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
