package com.qing.expert.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 达人实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("experts")
@Schema(description = "达人")
public class Expert extends BaseEntity {

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "服务分类ID")
    @TableField("category_id")
    private Long categoryId;

    @Schema(description = "达人名称")
    @TableField("expert_name")
    private String expertName;

    @Schema(description = "达人描述")
    @TableField("description")
    private String description;

    @Schema(description = "达人头像")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "最低价格")
    @TableField("price_min")
    private BigDecimal priceMin;

    @Schema(description = "最高价格")
    @TableField("price_max")
    private BigDecimal priceMax;

    @Schema(description = "评分（1-5分）")
    @TableField("rating")
    private BigDecimal rating;

    @Schema(description = "接单数量")
    @TableField("order_count")
    private Integer orderCount;

    @Schema(description = "完成数量")
    @TableField("complete_count")
    private Integer completeCount;

    @Schema(description = "完成率")
    @TableField("complete_rate")
    private BigDecimal completeRate;

    @Schema(description = "状态：0-下线，1-在线，2-忙碌")
    @TableField("status")
    private Integer status;

    @Schema(description = "审核状态：0-待审核，1-通过，2-拒绝")
    @TableField("audit_status")
    private Integer auditStatus;

    @Schema(description = "审核备注")
    @TableField("audit_remark")
    private String auditRemark;

    // 关联字段（非数据库字段）
    @Schema(description = "用户信息")
    @TableField(exist = false)
    private User user;

    @Schema(description = "分类信息")
    @TableField(exist = false)
    private Category category;
}
