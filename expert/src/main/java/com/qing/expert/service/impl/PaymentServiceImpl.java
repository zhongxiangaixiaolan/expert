package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.PaymentStatusEnum;
import com.qing.expert.common.enums.PaymentTypeEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.PaymentCallbackDTO;
import com.qing.expert.dto.PaymentCreateDTO;
import com.qing.expert.entity.PaymentRecord;
import com.qing.expert.entity.User;
import com.qing.expert.mapper.PaymentRecordMapper;
import com.qing.expert.service.PaymentService;
import com.qing.expert.service.UserService;
import com.qing.expert.service.WechatPayService;
import com.qing.expert.vo.PaymentRecordVO;
import com.qing.expert.vo.PaymentResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 支付服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord>
        implements PaymentService {

    private final UserService userService;
    private final WechatPayService wechatPayService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO createPayment(PaymentCreateDTO dto) {
        // 验证用户
        User user = userService.getById(dto.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证支付类型
        PaymentTypeEnum paymentType = PaymentTypeEnum.getByCode(dto.getPaymentType());
        if (paymentType == null) {
            throw new BusinessException("支付类型不正确");
        }

        // 根据支付类型处理
        switch (paymentType) {
            case BALANCE_PAY:
                return balancePay(dto);
            case WECHAT_PAY:
                return wechatPay(dto);
            case ALIPAY:
                return alipay(dto);
            default:
                throw new BusinessException("不支持的支付类型");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO balancePay(PaymentCreateDTO dto) {
        // 检查用户余额
        User user = userService.getById(dto.getUserId());
        if (user.getBalance().compareTo(dto.getPaymentAmount()) < 0) {
            throw new BusinessException("余额不足");
        }

        // 创建支付记录
        PaymentRecord paymentRecord = createPaymentRecord(dto);
        save(paymentRecord);

        // 扣减用户余额
        boolean success = userService.updateUserBalance(dto.getUserId(), dto.getPaymentAmount().negate());
        if (!success) {
            throw new BusinessException("余额扣减失败");
        }

        // 更新支付状态为成功
        paymentRecord.setPaymentStatus(PaymentStatusEnum.SUCCESS.getCode());
        paymentRecord.setActualAmount(dto.getPaymentAmount());
        paymentRecord.setPaymentTime(LocalDateTime.now());
        updateById(paymentRecord);

        // 构建返回结果
        PaymentResultVO result = new PaymentResultVO();
        result.setPaymentNo(paymentRecord.getPaymentNo());
        result.setPaymentStatus(PaymentStatusEnum.SUCCESS.getCode());
        result.setPaymentStatusDesc(PaymentStatusEnum.SUCCESS.getDesc());
        result.setPaymentAmount(dto.getPaymentAmount());
        result.setActualAmount(dto.getPaymentAmount());
        result.setPaymentTime(LocalDateTime.now());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO wechatPay(PaymentCreateDTO dto) {
        try {
            // 创建支付记录
            PaymentRecord paymentRecord = createPaymentRecord(dto);
            paymentRecord.setPaymentType("WECHAT_PAY");
            save(paymentRecord);

            // 调用微信支付服务创建支付订单
            var wechatPayVO = wechatPayService.createMiniAppPayment(dto, paymentRecord.getPaymentNo());

            // 构建返回结果
            PaymentResultVO result = new PaymentResultVO();
            result.setPaymentNo(wechatPayVO.getPaymentNo());
            result.setPaymentStatus(PaymentStatusEnum.PENDING.getCode());
            result.setPaymentStatusDesc(PaymentStatusEnum.PENDING.getDesc());
            result.setPaymentAmount(dto.getPaymentAmount());

            // 设置微信支付参数
            result.setPaymentParams(wechatPayVO);

            log.info("微信支付订单创建成功，支付单号：{}", wechatPayVO.getPaymentNo());
            return result;

        } catch (Exception e) {
            log.error("微信支付订单创建失败", e);
            throw new BusinessException("微信支付创建失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO alipay(PaymentCreateDTO dto) {
        // 创建支付记录
        PaymentRecord paymentRecord = createPaymentRecord(dto);
        save(paymentRecord);

        // TODO: 调用支付宝支付API
        log.info("调用支付宝支付API，支付单号：{}", paymentRecord.getPaymentNo());

        // 构建返回结果
        PaymentResultVO result = new PaymentResultVO();
        result.setPaymentNo(paymentRecord.getPaymentNo());
        result.setPaymentStatus(PaymentStatusEnum.PENDING.getCode());
        result.setPaymentStatusDesc(PaymentStatusEnum.PENDING.getDesc());
        result.setPaymentAmount(dto.getPaymentAmount());
        result.setExpireTime(paymentRecord.getExpireTime());

        return result;
    }

    /**
     * 创建支付记录
     */
    private PaymentRecord createPaymentRecord(PaymentCreateDTO dto) {
        PaymentRecord paymentRecord = new PaymentRecord();
        BeanUtils.copyProperties(dto, paymentRecord);

        // 生成支付单号
        paymentRecord.setPaymentNo(generatePaymentNo());
        paymentRecord.setPaymentStatus(PaymentStatusEnum.PENDING.getCode());
        paymentRecord.setActualAmount(BigDecimal.ZERO);

        // 设置过期时间（30分钟）
        paymentRecord.setExpireTime(LocalDateTime.now().plusMinutes(30));

        return paymentRecord;
    }

    /**
     * 生成支付单号
     */
    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaymentCallback(PaymentCallbackDTO dto) {
        PaymentRecord paymentRecord = getByPaymentNo(dto.getPaymentNo());
        if (paymentRecord == null) {
            log.error("支付回调处理失败，支付记录不存在：{}", dto.getPaymentNo());
            return false;
        }

        // 验证支付状态
        PaymentStatusEnum statusEnum = PaymentStatusEnum.getByCode(dto.getPaymentStatus());
        if (statusEnum == null) {
            log.error("支付回调处理失败，支付状态不正确：{}", dto.getPaymentStatus());
            return false;
        }

        // 更新支付记录
        LambdaUpdateWrapper<PaymentRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentRecord::getId, paymentRecord.getId())
                .set(PaymentRecord::getPaymentStatus, dto.getPaymentStatus())
                .set(PaymentRecord::getThirdPartyNo, dto.getThirdPartyNo())
                .set(PaymentRecord::getActualAmount, dto.getActualAmount())
                .set(PaymentRecord::getPaymentTime, dto.getPaymentTime())
                .set(PaymentRecord::getCallbackTime, LocalDateTime.now())
                .set(PaymentRecord::getCallbackData, dto.getCallbackData())
                .set(PaymentRecord::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    public PaymentResultVO queryPaymentResult(String paymentNo) {
        PaymentRecord paymentRecord = getByPaymentNo(paymentNo);
        if (paymentRecord == null) {
            throw new BusinessException("支付记录不存在");
        }

        PaymentResultVO result = new PaymentResultVO();
        result.setPaymentNo(paymentRecord.getPaymentNo());
        result.setThirdPartyNo(paymentRecord.getThirdPartyNo());
        result.setPaymentStatus(paymentRecord.getPaymentStatus());
        result.setPaymentStatusDesc(PaymentStatusEnum.getByCode(paymentRecord.getPaymentStatus()).getDesc());
        result.setPaymentAmount(paymentRecord.getPaymentAmount());
        result.setActualAmount(paymentRecord.getActualAmount());
        result.setPaymentTime(paymentRecord.getPaymentTime());
        result.setExpireTime(paymentRecord.getExpireTime());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelPayment(String paymentNo, String reason) {
        PaymentRecord paymentRecord = getByPaymentNo(paymentNo);
        if (paymentRecord == null) {
            throw new BusinessException("支付记录不存在");
        }

        // 只有待支付和支付中状态才能取消
        if (!PaymentStatusEnum.PENDING.getCode().equals(paymentRecord.getPaymentStatus()) &&
                !PaymentStatusEnum.PAYING.getCode().equals(paymentRecord.getPaymentStatus())) {
            throw new BusinessException("当前状态不允许取消支付");
        }

        LambdaUpdateWrapper<PaymentRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentRecord::getId, paymentRecord.getId())
                .set(PaymentRecord::getPaymentStatus, PaymentStatusEnum.CANCELLED.getCode())
                .set(PaymentRecord::getRemark, reason)
                .set(PaymentRecord::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundPayment(String paymentNo, BigDecimal refundAmount, String refundReason) {
        PaymentRecord paymentRecord = getByPaymentNo(paymentNo);
        if (paymentRecord == null) {
            throw new BusinessException("支付记录不存在");
        }

        // 只有支付成功状态才能退款
        if (!PaymentStatusEnum.SUCCESS.getCode().equals(paymentRecord.getPaymentStatus())) {
            throw new BusinessException("只有支付成功的订单才能退款");
        }

        // 验证退款金额
        if (refundAmount.compareTo(paymentRecord.getActualAmount()) > 0) {
            throw new BusinessException("退款金额不能大于实际支付金额");
        }

        // TODO: 调用第三方退款API
        log.info("申请退款，支付单号：{}，退款金额：{}", paymentNo, refundAmount);

        // 更新支付记录
        LambdaUpdateWrapper<PaymentRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentRecord::getId, paymentRecord.getId())
                .set(PaymentRecord::getPaymentStatus, PaymentStatusEnum.REFUNDING.getCode())
                .set(PaymentRecord::getRefundAmount, refundAmount)
                .set(PaymentRecord::getRefundTime, LocalDateTime.now())
                .set(PaymentRecord::getRefundReason, refundReason)
                .set(PaymentRecord::getUpdateTime, LocalDateTime.now());

        return update(updateWrapper);
    }

    @Override
    public IPage<PaymentRecordVO> getPaymentRecordPage(Page<PaymentRecordVO> page,
            Long userId,
            Long orderId,
            String paymentType,
            String paymentStatus,
            String userNickname,
            String userPhone,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        return baseMapper.selectPaymentRecordPage(page, userId, orderId, paymentType, paymentStatus,
                userNickname, userPhone, startTime, endTime);
    }

    @Override
    public PaymentRecordVO getPaymentRecordById(Long id) {
        if (id == null) {
            throw new BusinessException("支付记录ID不能为空");
        }
        return baseMapper.selectPaymentRecordById(id);
    }

    @Override
    public PaymentRecord getByPaymentNo(String paymentNo) {
        if (paymentNo == null || paymentNo.trim().isEmpty()) {
            throw new BusinessException("支付单号不能为空");
        }
        return baseMapper.selectByPaymentNo(paymentNo);
    }

    @Override
    public List<PaymentRecordVO> getUserPaymentRecords(Long userId, String paymentStatus, Integer limit) {
        return baseMapper.selectUserPaymentRecords(userId, paymentStatus, limit);
    }

    @Override
    public List<Map<String, Object>> getPaymentStatistics(String paymentType, LocalDateTime startTime,
            LocalDateTime endTime) {
        return baseMapper.selectPaymentStatistics(paymentType, startTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handleExpiredPayments(Integer minutes) {
        if (minutes == null || minutes <= 0) {
            minutes = 30; // 默认30分钟
        }

        List<PaymentRecord> expiredPayments = baseMapper.selectExpiredPayments(minutes);
        if (expiredPayments.isEmpty()) {
            return 0;
        }

        // 批量更新为已取消状态
        for (PaymentRecord payment : expiredPayments) {
            LambdaUpdateWrapper<PaymentRecord> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(PaymentRecord::getId, payment.getId())
                    .set(PaymentRecord::getPaymentStatus, PaymentStatusEnum.CANCELLED.getCode())
                    .set(PaymentRecord::getRemark, "支付超时自动取消")
                    .set(PaymentRecord::getUpdateTime, LocalDateTime.now());
            update(updateWrapper);
        }

        log.info("处理超时支付订单完成，共处理{}条记录", expiredPayments.size());
        return expiredPayments.size();
    }

    @Override
    public BigDecimal sumUserPaymentAmount(Long userId, String paymentStatus, LocalDateTime startTime,
            LocalDateTime endTime) {
        return baseMapper.sumUserPaymentAmount(userId, paymentStatus, startTime, endTime);
    }

    @Override
    public Map<String, Object> getPlatformPaymentSummary(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectPlatformPaymentSummary(startTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePaymentSuccess(String paymentNo, String thirdPartyNo) {
        PaymentRecord paymentRecord = getByPaymentNo(paymentNo);
        if (paymentRecord == null) {
            log.error("更新支付成功状态失败，支付记录不存在：{}", paymentNo);
            return false;
        }

        LambdaUpdateWrapper<PaymentRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentRecord::getId, paymentRecord.getId())
                .set(PaymentRecord::getPaymentStatus, PaymentStatusEnum.SUCCESS.getCode())
                .set(PaymentRecord::getThirdPartyNo, thirdPartyNo)
                .set(PaymentRecord::getActualAmount, paymentRecord.getPaymentAmount())
                .set(PaymentRecord::getPaymentTime, LocalDateTime.now())
                .set(PaymentRecord::getUpdateTime, LocalDateTime.now());

        boolean success = update(updateWrapper);
        if (success) {
            log.info("支付成功状态更新完成，支付单号：{}，第三方单号：{}", paymentNo, thirdPartyNo);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePaymentFailed(String paymentNo, String failReason) {
        PaymentRecord paymentRecord = getByPaymentNo(paymentNo);
        if (paymentRecord == null) {
            log.error("更新支付失败状态失败，支付记录不存在：{}", paymentNo);
            return false;
        }

        LambdaUpdateWrapper<PaymentRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentRecord::getId, paymentRecord.getId())
                .set(PaymentRecord::getPaymentStatus, PaymentStatusEnum.FAILED.getCode())
                .set(PaymentRecord::getRemark, failReason)
                .set(PaymentRecord::getUpdateTime, LocalDateTime.now());

        boolean success = update(updateWrapper);
        if (success) {
            log.info("支付失败状态更新完成，支付单号：{}，失败原因：{}", paymentNo, failReason);
        }
        return success;
    }

    @Override
    public PaymentRecordVO convertToVO(PaymentRecord record) {
        if (record == null) {
            return null;
        }

        PaymentRecordVO vo = new PaymentRecordVO();
        vo.setId(record.getId());
        vo.setPaymentNo(record.getPaymentNo());
        vo.setUserId(record.getUserId());
        vo.setOrderId(record.getOrderId());
        vo.setPaymentAmount(record.getPaymentAmount());
        vo.setActualAmount(record.getActualAmount());
        vo.setPaymentType(record.getPaymentType());
        vo.setPaymentStatus(record.getPaymentStatus());
        vo.setPaymentDesc(record.getPaymentDesc());
        vo.setThirdPartyNo(record.getThirdPartyNo());
        vo.setPaymentTime(record.getPaymentTime());
        vo.setExpireTime(record.getExpireTime());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());
        vo.setUpdateTime(record.getUpdateTime());

        return vo;
    }
}
