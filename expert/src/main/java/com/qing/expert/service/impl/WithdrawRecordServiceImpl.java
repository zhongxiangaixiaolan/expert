package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.WithdrawStatusEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.withdraw.WithdrawAuditDTO;
import com.qing.expert.dto.withdraw.WithdrawCreateDTO;
import com.qing.expert.dto.withdraw.WithdrawQueryDTO;
import com.qing.expert.entity.User;
import com.qing.expert.entity.WithdrawRecord;
import com.qing.expert.mapper.WithdrawRecordMapper;
import com.qing.expert.service.SystemConfigService;
import com.qing.expert.service.UserService;
import com.qing.expert.service.WithdrawRecordService;
import com.qing.expert.vo.withdraw.WithdrawRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 提现记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawRecordServiceImpl extends ServiceImpl<WithdrawRecordMapper, WithdrawRecord> implements WithdrawRecordService {

    private final WithdrawRecordMapper withdrawRecordMapper;
    private final UserService userService;
    private final SystemConfigService systemConfigService;

    @Override
    public PageResult<WithdrawRecordVO> getWithdrawRecordPage(WithdrawQueryDTO queryDTO) {
        Page<WithdrawRecordVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        var pageResult = withdrawRecordMapper.selectWithdrawRecordPage(page, queryDTO);
        
        // 处理状态描述
        pageResult.getRecords().forEach(this::fillStatusDesc);
        
        return PageResult.of(pageResult);
    }

    @Override
    public WithdrawRecordVO getWithdrawRecordDetail(Long withdrawId) {
        WithdrawRecordVO recordVO = withdrawRecordMapper.selectWithdrawRecordById(withdrawId);
        if (recordVO != null) {
            fillStatusDesc(recordVO);
        }
        return recordVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWithdrawRecord(WithdrawCreateDTO createDTO) {
        // 验证用户是否存在
        User user = userService.getById(createDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户余额是否足够
        if (user.getBalance().compareTo(createDTO.getAmount()) < 0) {
            throw new BusinessException("用户余额不足");
        }

        // 计算手续费和实际到账金额
        BigDecimal fee = calculateWithdrawFee(createDTO.getAmount());
        BigDecimal realAmount = createDTO.getAmount().subtract(fee);

        // 创建提现记录
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        BeanUtils.copyProperties(createDTO, withdrawRecord);
        
        withdrawRecord.setOrderNo(generateOrderNo());
        withdrawRecord.setFee(fee);
        withdrawRecord.setRealAmount(realAmount);
        withdrawRecord.setStatus(WithdrawStatusEnum.PENDING.getCode());
        
        boolean saved = save(withdrawRecord);
        if (!saved) {
            throw new BusinessException("创建提现记录失败");
        }

        // 冻结用户余额
        BigDecimal newBalance = user.getBalance().subtract(createDTO.getAmount());
        user.setBalance(newBalance);
        userService.updateById(user);
        
        log.info("创建提现记录成功，记录ID：{}，订单号：{}，冻结金额：{}", 
                withdrawRecord.getId(), withdrawRecord.getOrderNo(), createDTO.getAmount());
        return withdrawRecord.getId();
    }

    @Override
    public WithdrawRecord getByOrderNo(String orderNo) {
        return withdrawRecordMapper.selectByOrderNo(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditWithdrawRecord(WithdrawAuditDTO auditDTO) {
        WithdrawRecord withdrawRecord = getById(auditDTO.getWithdrawId());
        if (withdrawRecord == null) {
            throw new BusinessException("提现记录不存在");
        }

        if (!WithdrawStatusEnum.isPending(withdrawRecord.getStatus())) {
            throw new BusinessException("提现记录状态不正确，无法审核");
        }

        withdrawRecord.setStatus(auditDTO.getStatus());
        withdrawRecord.setAuditRemark(auditDTO.getAuditRemark());
        withdrawRecord.setAuditTime(LocalDateTime.now());

        boolean updated = updateById(withdrawRecord);
        
        // 如果审核拒绝，需要退还用户余额
        if (updated && WithdrawStatusEnum.isRejected(auditDTO.getStatus())) {
            User user = userService.getById(withdrawRecord.getUserId());
            if (user != null) {
                BigDecimal newBalance = user.getBalance().add(withdrawRecord.getAmount());
                user.setBalance(newBalance);
                userService.updateById(user);
                log.info("提现审核拒绝，退还用户余额，用户ID：{}，退还金额：{}", user.getId(), withdrawRecord.getAmount());
            }
        }

        if (updated) {
            log.info("提现审核成功，记录ID：{}，审核状态：{}", auditDTO.getWithdrawId(), auditDTO.getStatus());
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAuditWithdrawRecords(List<WithdrawAuditDTO> auditDTOs) {
        for (WithdrawAuditDTO auditDTO : auditDTOs) {
            auditWithdrawRecord(auditDTO);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmTransfer(Long withdrawId) {
        WithdrawRecord withdrawRecord = getById(withdrawId);
        if (withdrawRecord == null) {
            throw new BusinessException("提现记录不存在");
        }

        if (!WithdrawStatusEnum.isApproved(withdrawRecord.getStatus())) {
            throw new BusinessException("提现记录状态不正确，无法确认转账");
        }

        withdrawRecord.setStatus(WithdrawStatusEnum.TRANSFERRED.getCode());
        withdrawRecord.setTransferTime(LocalDateTime.now());

        boolean updated = updateById(withdrawRecord);
        if (updated) {
            log.info("确认转账成功，记录ID：{}", withdrawId);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchConfirmTransfer(List<Long> withdrawIds) {
        return withdrawRecordMapper.batchUpdateStatus(withdrawIds, WithdrawStatusEnum.TRANSFERRED.getCode()) > 0;
    }

    @Override
    public PageResult<WithdrawRecordVO> getUserWithdrawRecords(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<WithdrawRecordVO> page = new Page<>(pageNum, pageSize);
        
        var pageResult = withdrawRecordMapper.selectWithdrawRecordsByUserId(page, userId, status);
        
        // 处理状态描述
        pageResult.getRecords().forEach(this::fillStatusDesc);
        
        return PageResult.of(pageResult);
    }

    @Override
    public BigDecimal getTotalWithdrawAmount(LocalDateTime startTime, LocalDateTime endTime) {
        BigDecimal amount = withdrawRecordMapper.getTotalWithdrawAmount(startTime, endTime);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalFeeAmount(LocalDateTime startTime, LocalDateTime endTime) {
        BigDecimal amount = withdrawRecordMapper.getTotalFeeAmount(startTime, endTime);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    public Long getWithdrawCount(LocalDateTime startTime, LocalDateTime endTime, Integer status) {
        Long count = withdrawRecordMapper.getWithdrawCount(startTime, endTime, status);
        return count != null ? count : 0L;
    }

    @Override
    public BigDecimal getUserTotalWithdrawAmount(Long userId) {
        BigDecimal amount = withdrawRecordMapper.getUserTotalWithdrawAmount(userId);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    public Long getPendingWithdrawCount() {
        Long count = withdrawRecordMapper.getPendingWithdrawCount();
        return count != null ? count : 0L;
    }

    @Override
    public List<WithdrawRecordVO> getRecentWithdrawRecords(Integer limit) {
        Page<WithdrawRecordVO> page = new Page<>(1, limit);
        var pageResult = withdrawRecordMapper.selectRecentWithdrawRecords(page, limit);
        
        List<WithdrawRecordVO> records = pageResult.getRecords();
        records.forEach(this::fillStatusDesc);
        return records;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWithdrawRecord(Long withdrawId) {
        WithdrawRecord withdrawRecord = getById(withdrawId);
        if (withdrawRecord == null) {
            throw new BusinessException("提现记录不存在");
        }
        
        if (!WithdrawStatusEnum.isPending(withdrawRecord.getStatus()) && 
            !WithdrawStatusEnum.isRejected(withdrawRecord.getStatus())) {
            throw new BusinessException("只能删除待审核或已拒绝的提现记录");
        }
        
        // 如果是待审核状态，需要退还用户余额
        if (WithdrawStatusEnum.isPending(withdrawRecord.getStatus())) {
            User user = userService.getById(withdrawRecord.getUserId());
            if (user != null) {
                BigDecimal newBalance = user.getBalance().add(withdrawRecord.getAmount());
                user.setBalance(newBalance);
                userService.updateById(user);
            }
        }
        
        return removeById(withdrawId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteWithdrawRecords(List<Long> withdrawIds) {
        for (Long withdrawId : withdrawIds) {
            deleteWithdrawRecord(withdrawId);
        }
        return true;
    }

    @Override
    public BigDecimal calculateWithdrawFee(BigDecimal amount) {
        // 从系统配置中获取提现手续费率，默认为0.5%
        BigDecimal feeRate = systemConfigService.getBooleanValue("withdraw_fee_rate", false) ? 
                            new BigDecimal(systemConfigService.getConfigValue("withdraw_fee_rate", "0.005")) : 
                            new BigDecimal("0.005");
        
        // 计算手续费，保留2位小数
        BigDecimal fee = amount.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
        
        // 最低手续费
        BigDecimal minFee = new BigDecimal(systemConfigService.getConfigValue("withdraw_min_fee", "1.00"));
        if (fee.compareTo(minFee) < 0) {
            fee = minFee;
        }
        
        // 最高手续费
        BigDecimal maxFee = new BigDecimal(systemConfigService.getConfigValue("withdraw_max_fee", "50.00"));
        if (fee.compareTo(maxFee) > 0) {
            fee = maxFee;
        }
        
        return fee;
    }

    /**
     * 填充状态描述
     */
    private void fillStatusDesc(WithdrawRecordVO recordVO) {
        WithdrawStatusEnum statusEnum = WithdrawStatusEnum.getByCode(recordVO.getStatus());
        if (statusEnum != null) {
            recordVO.setStatusDesc(statusEnum.getDesc());
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "WD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
               String.format("%06d", (int)(Math.random() * 1000000));
    }
}
