package com.qing.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 分布数据VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分布数据")
public class DistributionDataVO {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "数值")
    private BigDecimal value;

    @Schema(description = "百分比")
    private BigDecimal percentage;

    @Schema(description = "颜色")
    private String color;

    /**
     * 构造函数 - 用于整数值
     */
    public DistributionDataVO(String name, Long value, BigDecimal percentage, String color) {
        this.name = name;
        this.value = value != null ? new BigDecimal(value) : BigDecimal.ZERO;
        this.percentage = percentage != null ? percentage : BigDecimal.ZERO;
        this.color = color;
    }

    /**
     * 构造函数 - 用于整数值，无颜色
     */
    public DistributionDataVO(String name, Long value, BigDecimal percentage) {
        this.name = name;
        this.value = value != null ? new BigDecimal(value) : BigDecimal.ZERO;
        this.percentage = percentage != null ? percentage : BigDecimal.ZERO;
        this.color = null;
    }

    /**
     * 构造函数 - 用于整数值，无百分比和颜色
     */
    public DistributionDataVO(String name, Long value) {
        this.name = name;
        this.value = value != null ? new BigDecimal(value) : BigDecimal.ZERO;
        this.percentage = BigDecimal.ZERO;
        this.color = null;
    }
}
