package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.withdraw.WithdrawAuditDTO;
import com.qing.expert.dto.withdraw.WithdrawCreateDTO;
import com.qing.expert.dto.withdraw.WithdrawQueryDTO;
import com.qing.expert.entity.WithdrawRecord;
import com.qing.expert.vo.withdraw.WithdrawRecordVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 提现记录服务接口
 */
public interface WithdrawRecordService extends IService<WithdrawRecord> {

    /**
     * 分页查询提现记录列表
     */
    PageResult<WithdrawRecordVO> getWithdrawRecordPage(WithdrawQueryDTO queryDTO);

    /**
     * 获取提现记录详情
     */
    WithdrawRecordVO getWithdrawRecordDetail(Long withdrawId);

    /**
     * 创建提现记录
     */
    Long createWithdrawRecord(WithdrawCreateDTO createDTO);

    /**
     * 根据订单号查询提现记录
     */
    WithdrawRecord getByOrderNo(String orderNo);

    /**
     * 审核提现申请
     */
    boolean auditWithdrawRecord(WithdrawAuditDTO auditDTO);

    /**
     * 批量审核提现申请
     */
    boolean batchAuditWithdrawRecords(List<WithdrawAuditDTO> auditDTOs);

    /**
     * 确认转账完成
     */
    boolean confirmTransfer(Long withdrawId);

    /**
     * 批量确认转账完成
     */
    boolean batchConfirmTransfer(List<Long> withdrawIds);

    /**
     * 查询用户提现记录
     */
    PageResult<WithdrawRecordVO> getUserWithdrawRecords(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 统计提现总金额
     */
    BigDecimal getTotalWithdrawAmount(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计手续费总金额
     */
    BigDecimal getTotalFeeAmount(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计提现记录数量
     */
    Long getWithdrawCount(LocalDateTime startTime, LocalDateTime endTime, Integer status);

    /**
     * 获取用户提现总金额
     */
    BigDecimal getUserTotalWithdrawAmount(Long userId);

    /**
     * 获取待审核提现记录数量
     */
    Long getPendingWithdrawCount();

    /**
     * 获取最近提现记录
     */
    List<WithdrawRecordVO> getRecentWithdrawRecords(Integer limit);

    /**
     * 删除提现记录
     */
    boolean deleteWithdrawRecord(Long withdrawId);

    /**
     * 批量删除提现记录
     */
    boolean batchDeleteWithdrawRecords(List<Long> withdrawIds);

    /**
     * 计算提现手续费
     */
    BigDecimal calculateWithdrawFee(BigDecimal amount);
}
