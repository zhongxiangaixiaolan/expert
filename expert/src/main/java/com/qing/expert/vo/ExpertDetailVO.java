package com.qing.expert.vo;

import com.qing.expert.entity.Category;
import com.qing.expert.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 达人详情VO
 */
@Data
@Schema(description = "达人详情信息")
public class ExpertDetailVO {

    @Schema(description = "达人ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "服务分类ID")
    private Long categoryId;

    @Schema(description = "达人名称")
    private String expertName;

    @Schema(description = "达人描述")
    private String description;

    @Schema(description = "达人头像")
    private String avatar;

    @Schema(description = "最低价格")
    private BigDecimal priceMin;

    @Schema(description = "最高价格")
    private BigDecimal priceMax;

    @Schema(description = "评分（1-5分）")
    private BigDecimal rating;

    @Schema(description = "接单数量")
    private Integer orderCount;

    @Schema(description = "完成数量")
    private Integer completeCount;

    @Schema(description = "完成率")
    private BigDecimal completeRate;

    @Schema(description = "状态：0-下线，1-在线，2-忙碌")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "审核状态：0-待审核，1-审核通过，2-审核拒绝")
    private Integer auditStatus;

    @Schema(description = "审核状态描述")
    private String auditStatusDesc;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "用户信息")
    private User user;

    @Schema(description = "分类信息")
    private Category category;
}
