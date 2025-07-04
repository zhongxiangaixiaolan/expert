package com.qing.expert.service;

import com.qing.expert.dto.PaymentCreateDTO;
import com.qing.expert.vo.payment.WechatPayVO;

/**
 * 微信支付服务接口
 */
public interface WechatPayService {

    /**
     * 创建小程序支付订单
     *
     * @param dto       支付创建参数
     * @param paymentNo 支付单号
     * @return 小程序支付参数
     */
    WechatPayVO createMiniAppPayment(PaymentCreateDTO dto, String paymentNo);

    /**
     * 处理微信支付回调通知
     *
     * @param jsonData 微信回调JSON数据（APIv3格式）
     * @return 处理结果
     */
    boolean handlePaymentNotify(String jsonData);

    /**
     * 查询支付状态
     *
     * @param paymentNo 支付单号
     * @return 支付状态信息，包含状态和第三方交易号
     */
    PaymentStatusResult queryPaymentStatus(String paymentNo);

    /**
     * 支付状态查询结果
     */
    class PaymentStatusResult {
        private boolean success;
        private String tradeState;
        private String transactionId;
        private String message;

        public PaymentStatusResult(boolean success, String tradeState, String transactionId, String message) {
            this.success = success;
            this.tradeState = tradeState;
            this.transactionId = transactionId;
            this.message = message;
        }

        // Getters
        public boolean isSuccess() {
            return success;
        }

        public String getTradeState() {
            return tradeState;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getMessage() {
            return message;
        }
    }
}
