package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.dto.withdraw.WithdrawQueryDTO;
import com.qing.expert.entity.WithdrawRecord;
import com.qing.expert.vo.withdraw.WithdrawRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录Mapper接口
 */
@Mapper
public interface WithdrawRecordMapper extends BaseMapper<WithdrawRecord> {

    /**
     * 分页查询提现记录列表
     */
    IPage<WithdrawRecordVO> selectWithdrawRecordPage(Page<WithdrawRecordVO> page, @Param("query") WithdrawQueryDTO queryDTO);

    /**
     * 根据ID查询提现记录详情
     */
    WithdrawRecordVO selectWithdrawRecordById(@Param("id") Long id);

    /**
     * 根据订单号查询提现记录
     */
    WithdrawRecord selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询用户提现记录列表
     */
    IPage<WithdrawRecordVO> selectWithdrawRecordsByUserId(Page<WithdrawRecordVO> page, @Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 统计提现总金额
     */
    BigDecimal getTotalWithdrawAmount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计手续费总金额
     */
    BigDecimal getTotalFeeAmount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计提现记录数量
     */
    Long getWithdrawCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("status") Integer status);

    /**
     * 获取用户提现总金额
     */
    BigDecimal getUserTotalWithdrawAmount(@Param("userId") Long userId);

    /**
     * 获取待审核提现记录数量
     */
    Long getPendingWithdrawCount();

    /**
     * 获取最近提现记录
     */
    IPage<WithdrawRecordVO> selectRecentWithdrawRecords(Page<WithdrawRecordVO> page, @Param("limit") Integer limit);

    /**
     * 批量更新提现状态
     */
    int batchUpdateStatus(@Param("withdrawIds") java.util.List<Long> withdrawIds, @Param("status") Integer status);
}
