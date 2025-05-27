package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.PayTypeEnum;
import com.qing.expert.common.enums.RechargeStatusEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.recharge.RechargeCreateDTO;
import com.qing.expert.dto.recharge.RechargeQueryDTO;
import com.qing.expert.entity.RechargeRecord;
import com.qing.expert.entity.User;
import com.qing.expert.mapper.RechargeRecordMapper;
import com.qing.expert.service.RechargeRecordService;
import com.qing.expert.service.UserService;
import com.qing.expert.vo.recharge.RechargeRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 充值记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeRecordServiceImpl extends ServiceImpl<RechargeRecordMapper, RechargeRecord> implements RechargeRecordService {

    private final RechargeRecordMapper rechargeRecordMapper;
    private final UserService userService;

    @Override
    public PageResult<RechargeRecordVO> getRechargeRecordPage(RechargeQueryDTO queryDTO) {
        Page<RechargeRecordVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        var pageResult = rechargeRecordMapper.selectRechargeRecordPage(page, queryDTO);
        
        // 处理状态描述和支付方式描述
        pageResult.getRecords().forEach(this::fillStatusDesc);
        
        return PageResult.of(pageResult);
    }

    @Override
    public RechargeRecordVO getRechargeRecordDetail(Long rechargeId) {
        RechargeRecordVO recordVO = rechargeRecordMapper.selectRechargeRecordById(rechargeId);
        if (recordVO != null) {
            fillStatusDesc(recordVO);
        }
        return recordVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRechargeRecord(RechargeCreateDTO createDTO) {
        // 验证用户是否存在
        User user = userService.getById(createDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 创建充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        BeanUtils.copyProperties(createDTO, rechargeRecord);
        
        // 设置订单号
        rechargeRecord.setOrderNo(generateOrderNo());
        rechargeRecord.setStatus(RechargeStatusEnum.PENDING.getCode());
        
        boolean saved = save(rechargeRecord);
        if (!saved) {
            throw new BusinessException("创建充值记录失败");
        }
        
        log.info("创建充值记录成功，记录ID：{}，订单号：{}", rechargeRecord.getId(), rechargeRecord.getOrderNo());
        return rechargeRecord.getId();
    }

    @Override
    public RechargeRecord getByOrderNo(String orderNo) {
        return rechargeRecordMapper.selectByOrderNo(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRechargeStatus(Long rechargeId, Integer status, String payOrderNo) {
        RechargeRecord rechargeRecord = getById(rechargeId);
        if (rechargeRecord == null) {
            throw new BusinessException("充值记录不存在");
        }
        
        rechargeRecord.setStatus(status);
        rechargeRecord.setPayOrderNo(payOrderNo);
        
        if (RechargeStatusEnum.isPaid(status)) {
            rechargeRecord.setPayTime(LocalDateTime.now());
        }
        
        boolean updated = updateById(rechargeRecord);
        if (updated) {
            log.info("更新充值状态成功，记录ID：{}，状态：{}", rechargeId, status);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaymentSuccess(String orderNo, String payOrderNo) {
        RechargeRecord rechargeRecord = getByOrderNo(orderNo);
        if (rechargeRecord == null) {
            throw new BusinessException("充值记录不存在");
        }
        
        if (!RechargeStatusEnum.isPending(rechargeRecord.getStatus())) {
            log.warn("充值记录状态不正确，订单号：{}，当前状态：{}", orderNo, rechargeRecord.getStatus());
            return false;
        }
        
        // 更新充值记录状态
        rechargeRecord.setStatus(RechargeStatusEnum.PAID.getCode());
        rechargeRecord.setPayOrderNo(payOrderNo);
        rechargeRecord.setPayTime(LocalDateTime.now());
        
        boolean updated = updateById(rechargeRecord);
        if (updated) {
            // 更新用户余额
            User user = userService.getById(rechargeRecord.getUserId());
            if (user != null) {
                BigDecimal newBalance = user.getBalance().add(rechargeRecord.getAmount());
                user.setBalance(newBalance);
                userService.updateById(user);
                log.info("充值成功，用户ID：{}，充值金额：{}，新余额：{}", user.getId(), rechargeRecord.getAmount(), newBalance);
            }
        }
        
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaymentFailed(String orderNo, String failReason) {
        RechargeRecord rechargeRecord = getByOrderNo(orderNo);
        if (rechargeRecord == null) {
            throw new BusinessException("充值记录不存在");
        }
        
        rechargeRecord.setStatus(RechargeStatusEnum.FAILED.getCode());
        
        boolean updated = updateById(rechargeRecord);
        if (updated) {
            log.info("充值失败，订单号：{}，失败原因：{}", orderNo, failReason);
        }
        return updated;
    }

    @Override
    public PageResult<RechargeRecordVO> getUserRechargeRecords(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<RechargeRecordVO> page = new Page<>(pageNum, pageSize);
        
        var pageResult = rechargeRecordMapper.selectRechargeRecordsByUserId(page, userId, status);
        
        // 处理状态描述
        pageResult.getRecords().forEach(this::fillStatusDesc);
        
        return PageResult.of(pageResult);
    }

    @Override
    public BigDecimal getTotalRechargeAmount(LocalDateTime startTime, LocalDateTime endTime) {
        BigDecimal amount = rechargeRecordMapper.getTotalRechargeAmount(startTime, endTime);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    public Long getRechargeCount(LocalDateTime startTime, LocalDateTime endTime, Integer status) {
        Long count = rechargeRecordMapper.getRechargeCount(startTime, endTime, status);
        return count != null ? count : 0L;
    }

    @Override
    public BigDecimal getRechargeAmountByPayType(String payType, LocalDateTime startTime, LocalDateTime endTime) {
        BigDecimal amount = rechargeRecordMapper.getRechargeAmountByPayType(payType, startTime, endTime);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getUserTotalRechargeAmount(Long userId) {
        BigDecimal amount = rechargeRecordMapper.getUserTotalRechargeAmount(userId);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    public List<RechargeRecordVO> getRecentRechargeRecords(Integer limit) {
        Page<RechargeRecordVO> page = new Page<>(1, limit);
        var pageResult = rechargeRecordMapper.selectRecentRechargeRecords(page, limit);
        
        List<RechargeRecordVO> records = pageResult.getRecords();
        records.forEach(this::fillStatusDesc);
        return records;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRechargeRecord(Long rechargeId) {
        RechargeRecord rechargeRecord = getById(rechargeId);
        if (rechargeRecord == null) {
            throw new BusinessException("充值记录不存在");
        }
        
        if (RechargeStatusEnum.isPaid(rechargeRecord.getStatus())) {
            throw new BusinessException("已支付的充值记录不能删除");
        }
        
        return removeById(rechargeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteRechargeRecords(List<Long> rechargeIds) {
        // 检查是否有已支付的记录
        LambdaQueryWrapper<RechargeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RechargeRecord::getId, rechargeIds)
               .eq(RechargeRecord::getStatus, RechargeStatusEnum.PAID.getCode());
        
        long paidCount = count(wrapper);
        if (paidCount > 0) {
            throw new BusinessException("存在已支付的充值记录，不能删除");
        }
        
        return removeByIds(rechargeIds);
    }

    /**
     * 填充状态描述
     */
    private void fillStatusDesc(RechargeRecordVO recordVO) {
        // 状态描述
        RechargeStatusEnum statusEnum = RechargeStatusEnum.getByCode(recordVO.getStatus());
        if (statusEnum != null) {
            recordVO.setStatusDesc(statusEnum.getDesc());
        }
        
        // 支付方式描述
        PayTypeEnum payTypeEnum = PayTypeEnum.getByCode(recordVO.getPayType());
        if (payTypeEnum != null) {
            recordVO.setPayTypeDesc(payTypeEnum.getDesc());
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "RC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
               String.format("%06d", (int)(Math.random() * 1000000));
    }
}
