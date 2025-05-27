package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.entity.TransactionRecord;
import com.qing.expert.vo.transaction.TransactionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 交易记录Mapper接口
 */
@Mapper
public interface TransactionMapper extends BaseMapper<TransactionRecord> {

    /**
     * 分页查询用户交易记录
     */
    IPage<TransactionVO> selectUserTransactionPage(Page<TransactionVO> page,
                                                 @Param("userId") Long userId,
                                                 @Param("type") String type,
                                                 @Param("status") Integer status,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 获取用户交易记录详情
     */
    TransactionVO selectUserTransactionDetail(@Param("transactionId") Long transactionId,
                                            @Param("userId") Long userId);

    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStatistics(@Param("userId") Long userId);

    /**
     * 根据订单号查询交易记录
     */
    TransactionRecord selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据第三方交易号查询
     */
    TransactionRecord selectByThirdPartyNo(@Param("thirdPartyNo") String thirdPartyNo);

    /**
     * 获取用户指定类型的交易总额
     */
    Double getUserTransactionAmount(@Param("userId") Long userId,
                                  @Param("type") String type,
                                  @Param("status") Integer status);

    /**
     * 获取用户最近的交易记录
     */
    IPage<TransactionVO> selectUserRecentTransactions(Page<TransactionVO> page,
                                                    @Param("userId") Long userId,
                                                    @Param("limit") Integer limit);

    /**
     * 统计用户交易数量
     */
    Map<String, Object> getUserTransactionCount(@Param("userId") Long userId);

    /**
     * 获取系统交易统计（管理端用）
     */
    Map<String, Object> getSystemTransactionStatistics(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 更新交易状态
     */
    int updateTransactionStatus(@Param("transactionId") Long transactionId,
                              @Param("status") Integer status,
                              @Param("finishTime") LocalDateTime finishTime);
}
