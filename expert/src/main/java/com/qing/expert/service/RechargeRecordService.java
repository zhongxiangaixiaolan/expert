package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.recharge.RechargeCreateDTO;
import com.qing.expert.dto.recharge.RechargeQueryDTO;
import com.qing.expert.entity.RechargeRecord;
import com.qing.expert.vo.recharge.RechargeRecordVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 充值记录服务接口
 */
public interface RechargeRecordService extends IService<RechargeRecord> {

    /**
     * 分页查询充值记录列表
     */
    PageResult<RechargeRecordVO> getRechargeRecordPage(RechargeQueryDTO queryDTO);

    /**
     * 获取充值记录详情
     */
    RechargeRecordVO getRechargeRecordDetail(Long rechargeId);

    /**
     * 创建充值记录
     */
    Long createRechargeRecord(RechargeCreateDTO createDTO);

    /**
     * 根据订单号查询充值记录
     */
    RechargeRecord getByOrderNo(String orderNo);

    /**
     * 更新充值状态
     */
    boolean updateRechargeStatus(Long rechargeId, Integer status, String payOrderNo);

    /**
     * 支付成功回调处理
     */
    boolean handlePaymentSuccess(String orderNo, String payOrderNo);

    /**
     * 支付失败回调处理
     */
    boolean handlePaymentFailed(String orderNo, String failReason);

    /**
     * 查询用户充值记录
     */
    PageResult<RechargeRecordVO> getUserRechargeRecords(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 统计充值总金额
     */
    BigDecimal getTotalRechargeAmount(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计充值记录数量
     */
    Long getRechargeCount(LocalDateTime startTime, LocalDateTime endTime, Integer status);

    /**
     * 根据支付方式统计充值金额
     */
    BigDecimal getRechargeAmountByPayType(String payType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户充值总金额
     */
    BigDecimal getUserTotalRechargeAmount(Long userId);

    /**
     * 获取最近充值记录
     */
    List<RechargeRecordVO> getRecentRechargeRecords(Integer limit);

    /**
     * 删除充值记录
     */
    boolean deleteRechargeRecord(Long rechargeId);

    /**
     * 批量删除充值记录
     */
    boolean batchDeleteRechargeRecords(List<Long> rechargeIds);
}
