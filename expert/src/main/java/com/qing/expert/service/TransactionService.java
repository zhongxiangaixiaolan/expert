package com.qing.expert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.dto.transaction.TransactionQueryDTO;
import com.qing.expert.vo.transaction.TransactionVO;
import com.qing.expert.vo.user.UserStatisticsVO;

/**
 * 交易记录服务接口
 */
public interface TransactionService {

    /**
     * 分页查询用户交易记录
     */
    IPage<TransactionVO> getUserTransactionPage(TransactionQueryDTO queryDTO);

    /**
     * 获取用户交易记录详情
     */
    TransactionVO getUserTransactionDetail(Long transactionId, Long userId);

    /**
     * 获取用户统计信息
     */
    UserStatisticsVO getUserStatistics(Long userId);

    /**
     * 创建充值记录
     */
    Long createRechargeTransaction(Long userId, Double amount, String paymentMethod, String orderNo);

    /**
     * 创建消费记录
     */
    Long createConsumeTransaction(Long userId, Double amount, String serviceName, Long orderId);

    /**
     * 创建提现记录
     */
    Long createWithdrawTransaction(Long userId, Double amount, String withdrawAccount, String orderNo);

    /**
     * 创建退款记录
     */
    Long createRefundTransaction(Long userId, Double amount, String orderNo, String reason);

    /**
     * 更新交易状态
     */
    boolean updateTransactionStatus(Long transactionId, Integer status);
}
