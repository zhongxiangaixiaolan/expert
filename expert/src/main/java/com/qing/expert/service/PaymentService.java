package com.qing.expert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.PaymentCallbackDTO;
import com.qing.expert.dto.PaymentCreateDTO;
import com.qing.expert.entity.PaymentRecord;
import com.qing.expert.vo.PaymentRecordVO;
import com.qing.expert.vo.PaymentResultVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService extends IService<PaymentRecord> {

    /**
     * 创建支付订单
     */
    PaymentResultVO createPayment(PaymentCreateDTO dto);

    /**
     * 处理支付回调
     */
    boolean handlePaymentCallback(PaymentCallbackDTO dto);

    /**
     * 查询支付结果
     */
    PaymentResultVO queryPaymentResult(String paymentNo);

    /**
     * 取消支付
     */
    boolean cancelPayment(String paymentNo, String reason);

    /**
     * 申请退款
     */
    boolean refundPayment(String paymentNo, BigDecimal refundAmount, String refundReason);

    /**
     * 分页查询支付记录列表
     */
    IPage<PaymentRecordVO> getPaymentRecordPage(Page<PaymentRecordVO> page,
            Long userId,
            Long orderId,
            String paymentType,
            String paymentStatus,
            String userNickname,
            String userPhone,
            LocalDateTime startTime,
            LocalDateTime endTime);

    /**
     * 根据ID查询支付记录详情
     */
    PaymentRecordVO getPaymentRecordById(Long id);

    /**
     * 根据支付单号查询支付记录
     */
    PaymentRecord getByPaymentNo(String paymentNo);

    /**
     * 查询用户支付记录列表
     */
    List<PaymentRecordVO> getUserPaymentRecords(Long userId, String paymentStatus, Integer limit);

    /**
     * 查询支付统计数据
     */
    List<Map<String, Object>> getPaymentStatistics(String paymentType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 处理超时支付订单
     */
    int handleExpiredPayments(Integer minutes);

    /**
     * 统计用户支付金额
     */
    BigDecimal sumUserPaymentAmount(Long userId, String paymentStatus, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计平台支付数据
     */
    Map<String, Object> getPlatformPaymentSummary(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 余额支付
     */
    PaymentResultVO balancePay(PaymentCreateDTO dto);

    /**
     * 微信支付
     */
    PaymentResultVO wechatPay(PaymentCreateDTO dto);

    /**
     * 支付宝支付
     */
    PaymentResultVO alipay(PaymentCreateDTO dto);

    /**
     * 更新支付成功状态
     */
    boolean updatePaymentSuccess(String paymentNo, String thirdPartyNo);

    /**
     * 更新支付失败状态
     */
    boolean updatePaymentFailed(String paymentNo, String failReason);

    /**
     * 转换为VO对象
     */
    PaymentRecordVO convertToVO(PaymentRecord record);
}
