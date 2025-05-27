package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.entity.PaymentRecord;
import com.qing.expert.vo.PaymentRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 支付记录Mapper接口
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    /**
     * 分页查询支付记录列表（带关联信息）
     */
    IPage<PaymentRecordVO> selectPaymentRecordPage(Page<PaymentRecordVO> page,
                                                   @Param("userId") Long userId,
                                                   @Param("orderId") Long orderId,
                                                   @Param("paymentType") String paymentType,
                                                   @Param("paymentStatus") String paymentStatus,
                                                   @Param("userNickname") String userNickname,
                                                   @Param("userPhone") String userPhone,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 根据ID查询支付记录详情（带关联信息）
     */
    PaymentRecordVO selectPaymentRecordById(@Param("id") Long id);

    /**
     * 根据支付单号查询支付记录
     */
    PaymentRecord selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据第三方支付单号查询支付记录
     */
    PaymentRecord selectByThirdPartyNo(@Param("thirdPartyNo") String thirdPartyNo);

    /**
     * 查询用户支付记录列表
     */
    List<PaymentRecordVO> selectUserPaymentRecords(@Param("userId") Long userId,
                                                   @Param("paymentStatus") String paymentStatus,
                                                   @Param("limit") Integer limit);

    /**
     * 查询支付统计数据
     */
    List<Map<String, Object>> selectPaymentStatistics(@Param("paymentType") String paymentType,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 查询超时未支付的记录
     */
    List<PaymentRecord> selectExpiredPayments(@Param("minutes") Integer minutes);

    /**
     * 统计用户支付金额
     */
    BigDecimal sumUserPaymentAmount(@Param("userId") Long userId,
                                    @Param("paymentStatus") String paymentStatus,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 统计平台支付数据
     */
    Map<String, Object> selectPlatformPaymentSummary(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);
}
