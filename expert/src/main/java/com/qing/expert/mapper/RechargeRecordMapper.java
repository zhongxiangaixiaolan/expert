package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.dto.recharge.RechargeQueryDTO;
import com.qing.expert.entity.RechargeRecord;
import com.qing.expert.vo.recharge.RechargeRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录Mapper接口
 */
@Mapper
public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {

    /**
     * 分页查询充值记录列表
     */
    IPage<RechargeRecordVO> selectRechargeRecordPage(Page<RechargeRecordVO> page, @Param("query") RechargeQueryDTO queryDTO);

    /**
     * 根据ID查询充值记录详情
     */
    RechargeRecordVO selectRechargeRecordById(@Param("id") Long id);

    /**
     * 根据订单号查询充值记录
     */
    RechargeRecord selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询用户充值记录列表
     */
    IPage<RechargeRecordVO> selectRechargeRecordsByUserId(Page<RechargeRecordVO> page, @Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 统计充值总金额
     */
    BigDecimal getTotalRechargeAmount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计充值记录数量
     */
    Long getRechargeCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("status") Integer status);

    /**
     * 根据支付方式统计充值金额
     */
    BigDecimal getRechargeAmountByPayType(@Param("payType") String payType, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 获取用户充值总金额
     */
    BigDecimal getUserTotalRechargeAmount(@Param("userId") Long userId);

    /**
     * 获取最近充值记录
     */
    IPage<RechargeRecordVO> selectRecentRechargeRecords(Page<RechargeRecordVO> page, @Param("limit") Integer limit);
}
