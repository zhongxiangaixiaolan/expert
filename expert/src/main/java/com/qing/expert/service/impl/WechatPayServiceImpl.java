package com.qing.expert.service.impl;

import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.qing.expert.common.constant.ConfigConstant;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.PaymentCreateDTO;
import com.qing.expert.service.SystemConfigService;
import com.qing.expert.service.WechatPayService;
import com.qing.expert.vo.payment.WechatPayVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 微信支付服务实现类
 * 使用官方wechatpay-java SDK实现
 */
@Slf4j
@Service
public class WechatPayServiceImpl implements WechatPayService {

    @Autowired(required = false)
    private JsapiServiceExtension jsapiServiceExtension;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 检查微信支付服务是否可用
     */
    private boolean isWechatPayAvailable() {
        return jsapiServiceExtension != null;
    }

    @Override
    public WechatPayVO createMiniAppPayment(PaymentCreateDTO dto, String paymentNo) {
        try {
            // 检查微信支付是否可用
            if (!isWechatPayAvailable()) {
                throw new BusinessException("微信支付服务未配置，请联系管理员");
            }

            // 构建微信支付请求
            PrepayRequest request = buildPrepayRequest(dto, paymentNo);

            // 调用微信支付API，直接获取包含支付参数的响应
            PrepayWithRequestPaymentResponse response = jsapiServiceExtension.prepayWithRequestPayment(request);

            // 构建小程序支付参数
            WechatPayVO payVO = new WechatPayVO();
            payVO.setAppId(response.getAppId());
            payVO.setTimeStamp(response.getTimeStamp());
            payVO.setNonceStr(response.getNonceStr());
            payVO.setPackageValue(response.getPackageVal());
            payVO.setSignType(response.getSignType());
            payVO.setPaySign(response.getPaySign()); // 官方SDK自动生成正确的签名

            // 设置支付相关信息
            payVO.setPaymentNo(paymentNo);
            // 从package参数中提取prepay_id
            String prepayId = extractPrepayId(response.getPackageVal());
            payVO.setPrepayId(prepayId);
            payVO.setPaymentAmount(dto.getPaymentAmount());

            return payVO;

        } catch (HttpException e) {
            log.error("微信支付HTTP请求失败", e);
            throw new BusinessException("微信支付网络请求失败：" + e.getMessage());
        } catch (ServiceException e) {
            log.error("微信支付API调用失败，错误码：{}，错误信息：{}", e.getErrorCode(), e.getErrorMessage());
            throw new BusinessException("微信支付创建失败：" + e.getErrorMessage());
        } catch (ValidationException e) {
            log.error("微信支付签名验证失败", e);
            throw new BusinessException("微信支付签名验证失败：" + e.getMessage());
        } catch (MalformedMessageException e) {
            log.error("微信支付响应格式异常", e);
            throw new BusinessException("微信支付响应格式异常：" + e.getMessage());
        } catch (Exception e) {
            log.error("创建微信支付订单失败", e);
            throw new BusinessException("创建支付订单失败：" + e.getMessage());
        }
    }

    @Override
    public boolean handlePaymentNotify(String jsonData) {
        // TODO: 实现APIv3的回调处理逻辑
        // APIv3使用JSON格式而不是XML格式
        // 需要验证回调签名并解析JSON数据
        return false;
    }

    @Override
    public PaymentStatusResult queryPaymentStatus(String paymentNo) {
        try {
            // 检查微信支付是否可用
            if (!isWechatPayAvailable()) {
                return new PaymentStatusResult(false, "UNAVAILABLE", null, "微信支付服务未配置");
            }

            // 构建查询请求
            QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
            request.setMchid(systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_MCH_ID, ""));
            request.setOutTradeNo(paymentNo);

            // 查询订单状态
            Transaction result = jsapiServiceExtension.queryOrderByOutTradeNo(request);
            String tradeState = result.getTradeState().name();

            log.info("查询微信支付状态，支付单号：{}，交易状态：{}", paymentNo, tradeState);

            if ("SUCCESS".equals(tradeState)) {
                // 支付成功
                return new PaymentStatusResult(true, tradeState, result.getTransactionId(), "支付成功");
            } else if ("CLOSED".equals(tradeState) || "REVOKED".equals(tradeState)) {
                // 支付关闭或撤销
                return new PaymentStatusResult(false, tradeState, result.getTransactionId(), "支付已关闭");
            } else {
                // 其他状态（如NOTPAY等）
                return new PaymentStatusResult(false, tradeState, result.getTransactionId(), "支付未完成");
            }

        } catch (HttpException e) {
            log.error("微信支付HTTP请求失败，支付单号：{}", paymentNo, e);
            return new PaymentStatusResult(false, "ERROR", null, "网络请求失败");
        } catch (ServiceException e) {
            log.error("查询微信支付状态失败，支付单号：{}，错误码：{}，错误信息：{}",
                    paymentNo, e.getErrorCode(), e.getErrorMessage(), e);
            return new PaymentStatusResult(false, "ERROR", null, "微信支付API错误：" + e.getErrorMessage());
        } catch (Exception e) {
            log.error("查询支付状态失败，支付单号：{}", paymentNo, e);
            return new PaymentStatusResult(false, "ERROR", null, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 构建微信支付预下单请求
     */
    private PrepayRequest buildPrepayRequest(PaymentCreateDTO dto, String paymentNo) {
        PrepayRequest request = new PrepayRequest();

        // 基本信息
        request.setAppid(systemConfigService.getConfigValue(ConfigConstant.WeChat.MINIAPP_APP_ID, ""));
        request.setMchid(systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_MCH_ID, ""));
        request.setDescription(dto.getPaymentDesc());
        request.setOutTradeNo(paymentNo);

        // 回调地址
        String notifyUrl = systemConfigService.getConfigValue(ConfigConstant.WeChat.PAY_NOTIFY_URL,
                "https://yourdomain.com/api/user/payment/callback/wechat");
        request.setNotifyUrl(notifyUrl);

        // 金额信息（单位：分）
        Amount amount = new Amount();
        amount.setTotal(dto.getPaymentAmount().multiply(new BigDecimal("100")).intValue());
        amount.setCurrency("CNY");
        request.setAmount(amount);

        // 支付者信息
        Payer payer = new Payer();
        payer.setOpenid(dto.getOpenid());
        request.setPayer(payer);

        return request;
    }

    /**
     * 从package参数中提取prepay_id
     */
    private String extractPrepayId(String packageValue) {
        if (packageValue != null && packageValue.startsWith("prepay_id=")) {
            return packageValue.substring("prepay_id=".length());
        }
        return "";
    }

}
