package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.transaction.TransactionQueryDTO;
import com.qing.expert.entity.TransactionRecord;
import com.qing.expert.mapper.TransactionMapper;
import com.qing.expert.service.TransactionService;
import com.qing.expert.vo.transaction.TransactionVO;
import com.qing.expert.vo.user.UserStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 交易记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;

    @Override
    public IPage<TransactionVO> getUserTransactionPage(TransactionQueryDTO queryDTO) {
        Page<TransactionVO> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        
        if (queryDTO.getStartTime() != null && !queryDTO.getStartTime().isEmpty()) {
            startTime = LocalDateTime.parse(queryDTO.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (queryDTO.getEndTime() != null && !queryDTO.getEndTime().isEmpty()) {
            endTime = LocalDateTime.parse(queryDTO.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        return transactionMapper.selectUserTransactionPage(page, queryDTO.getUserId(), 
                queryDTO.getType(), queryDTO.getStatus(), startTime, endTime);
    }

    @Override
    public TransactionVO getUserTransactionDetail(Long transactionId, Long userId) {
        TransactionVO transaction = transactionMapper.selectUserTransactionDetail(transactionId, userId);
        if (transaction == null) {
            throw new BusinessException("交易记录不存在或无权限访问");
        }
        return transaction;
    }

    @Override
    public UserStatisticsVO getUserStatistics(Long userId) {
        Map<String, Object> statistics = transactionMapper.getUserStatistics(userId);
        
        UserStatisticsVO userStats = new UserStatisticsVO();
        userStats.setBalance(getDoubleValue(statistics, "balance"));
        userStats.setTotalRecharge(getDoubleValue(statistics, "totalRecharge"));
        userStats.setTotalConsume(getDoubleValue(statistics, "totalConsume"));
        
        return userStats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRechargeTransaction(Long userId, Double amount, String paymentMethod, String orderNo) {
        TransactionRecord transaction = new TransactionRecord();
        transaction.setUserId(userId);
        transaction.setType("recharge");
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setStatus(0); // 处理中
        transaction.setDescription("账户充值");
        transaction.setOrderNo(orderNo);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setUpdateTime(LocalDateTime.now());
        
        transactionMapper.insert(transaction);
        return transaction.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createConsumeTransaction(Long userId, Double amount, String serviceName, Long orderId) {
        TransactionRecord transaction = new TransactionRecord();
        transaction.setUserId(userId);
        transaction.setType("consume");
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setStatus(1); // 成功
        transaction.setDescription("购买服务：" + serviceName);
        transaction.setServiceName(serviceName);
        transaction.setOrderId(orderId);
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setFinishTime(LocalDateTime.now());
        transaction.setUpdateTime(LocalDateTime.now());
        
        transactionMapper.insert(transaction);
        return transaction.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWithdrawTransaction(Long userId, Double amount, String withdrawAccount, String orderNo) {
        TransactionRecord transaction = new TransactionRecord();
        transaction.setUserId(userId);
        transaction.setType("withdraw");
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setStatus(0); // 处理中
        transaction.setDescription("余额提现");
        transaction.setOrderNo(orderNo);
        transaction.setWithdrawAccount(withdrawAccount);
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setUpdateTime(LocalDateTime.now());
        
        transactionMapper.insert(transaction);
        return transaction.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRefundTransaction(Long userId, Double amount, String orderNo, String reason) {
        TransactionRecord transaction = new TransactionRecord();
        transaction.setUserId(userId);
        transaction.setType("refund");
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setStatus(1); // 成功
        transaction.setDescription("订单退款：" + reason);
        transaction.setOrderNo(orderNo);
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setFinishTime(LocalDateTime.now());
        transaction.setUpdateTime(LocalDateTime.now());
        
        transactionMapper.insert(transaction);
        return transaction.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTransactionStatus(Long transactionId, Integer status) {
        LocalDateTime finishTime = null;
        if (status == 1 || status == 2) { // 成功或失败时设置完成时间
            finishTime = LocalDateTime.now();
        }
        
        int result = transactionMapper.updateTransactionStatus(transactionId, status, finishTime);
        return result > 0;
    }

    /**
     * 安全获取Double值
     */
    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0.0;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}
