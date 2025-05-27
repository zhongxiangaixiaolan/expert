package com.qing.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 趋势数据VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "趋势数据")
public class TrendDataVO {

    @Schema(description = "日期")
    private String date;

    @Schema(description = "数值")
    private BigDecimal value;

    @Schema(description = "标签")
    private String label;

    /**
     * 构造函数 - 用于整数值
     */
    public TrendDataVO(String date, Long value, String label) {
        this.date = date;
        this.value = value != null ? new BigDecimal(value) : BigDecimal.ZERO;
        this.label = label;
    }

    /**
     * 构造函数 - 用于整数值，无标签
     */
    public TrendDataVO(String date, Long value) {
        this.date = date;
        this.value = value != null ? new BigDecimal(value) : BigDecimal.ZERO;
        this.label = null;
    }

    /**
     * 构造函数 - 用于BigDecimal值，无标签
     */
    public TrendDataVO(String date, BigDecimal value) {
        this.date = date;
        this.value = value != null ? value : BigDecimal.ZERO;
        this.label = null;
    }
}
