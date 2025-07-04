package com.qing.expert.vo.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 微信支付返回参数
 */
@Data
@Schema(description = "微信支付参数")
public class WechatPayVO {

    @Schema(description = "小程序ID")
    private String appId;

    @Schema(description = "时间戳")
    private String timeStamp;

    @Schema(description = "随机字符串")
    private String nonceStr;

    @Schema(description = "订单详情扩展字符串")
    private String packageValue;

    @Schema(description = "签名方式")
    private String signType;

    @Schema(description = "签名")
    private String paySign;

    @Schema(description = "支付单号")
    private String paymentNo;

    @Schema(description = "预支付交易会话ID")
    private String prepayId;

    @Schema(description = "支付金额")
    private BigDecimal paymentAmount;
}
