package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.MessageStatusEnum;
import com.qing.expert.common.enums.MessageTypeEnum;
import com.qing.expert.common.enums.SendChannelEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.message.MessageCreateDTO;
import com.qing.expert.dto.message.MessageQueryDTO;
import com.qing.expert.dto.message.MessageSendDTO;
import com.qing.expert.entity.MessageRecord;
import com.qing.expert.mapper.MessageRecordMapper;
import com.qing.expert.service.MessageRecordService;
import com.qing.expert.service.UserService;
import com.qing.expert.vo.message.MessageRecordVO;
import com.qing.expert.vo.message.MessageStatisticsVO;
import com.qing.expert.vo.message.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageRecordServiceImpl extends ServiceImpl<MessageRecordMapper, MessageRecord>
        implements MessageRecordService {

    private final MessageRecordMapper messageRecordMapper;
    private final UserService userService;
    // 注意：MessageSendService可能还没有实现类，先注释掉，避免循环依赖
    // private final MessageSendService messageSendService;

    @Override
    public PageResult<MessageRecordVO> getMessageRecordPage(MessageQueryDTO queryDTO) {
        Page<MessageRecordVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        var pageResult = messageRecordMapper.selectMessageRecordPage(page, queryDTO);

        // 处理状态描述和类型描述
        pageResult.getRecords().forEach(this::fillStatusDesc);

        return PageResult.of(pageResult);
    }

    @Override
    public MessageRecordVO getMessageRecordDetail(Long messageId) {
        if (messageId == null) {
            throw new BusinessException("消息ID不能为空");
        }

        MessageRecordVO messageRecordVO = messageRecordMapper.selectMessageRecordById(messageId);
        if (messageRecordVO == null) {
            throw new BusinessException("消息记录不存在");
        }

        fillStatusDesc(messageRecordVO);
        return messageRecordVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMessageRecord(MessageCreateDTO createDTO) {
        // 验证用户是否存在
        if (userService.getById(createDTO.getUserId()) == null) {
            throw new BusinessException("用户不存在");
        }

        // 创建消息记录
        MessageRecord messageRecord = new MessageRecord();
        BeanUtils.copyProperties(createDTO, messageRecord);

        // 设置默认值
        messageRecord.setStatus(MessageStatusEnum.PENDING.getCode());
        messageRecord.setSendChannel(
                createDTO.getSendChannel() != null ? createDTO.getSendChannel() : SendChannelEnum.SYSTEM.getCode());
        messageRecord.setRetryCount(0);
        messageRecord.setMaxRetryCount(createDTO.getMaxRetryCount() != null ? createDTO.getMaxRetryCount() : 3);

        boolean saved = save(messageRecord);
        if (!saved) {
            throw new BusinessException("创建消息记录失败");
        }

        log.info("创建消息记录成功，记录ID：{}，用户ID：{}", messageRecord.getId(), messageRecord.getUserId());
        return messageRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchCreateMessageRecords(List<MessageCreateDTO> createDTOs) {
        List<Long> messageIds = new ArrayList<>();

        for (MessageCreateDTO createDTO : createDTOs) {
            Long messageId = createMessageRecord(createDTO);
            messageIds.add(messageId);
        }

        return messageIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendMessage(Long messageId) {
        MessageRecord messageRecord = getById(messageId);
        if (messageRecord == null) {
            throw new BusinessException("消息记录不存在");
        }

        if (!MessageStatusEnum.isPending(messageRecord.getStatus())) {
            throw new BusinessException("只能发送待发送状态的消息");
        }

        // 更新状态为发送中
        messageRecord.setStatus(MessageStatusEnum.SENDING.getCode());
        messageRecord.setSendTime(LocalDateTime.now());
        updateById(messageRecord);

        // TODO: 调用消息发送服务
        // boolean sendResult = messageSendService.sendMessage(messageRecord);
        boolean sendResult = true; // 临时设置为成功

        // 更新发送结果
        if (sendResult) {
            messageRecord.setStatus(MessageStatusEnum.SUCCESS.getCode());
        } else {
            messageRecord.setStatus(MessageStatusEnum.FAILED.getCode());
            messageRecord.setFailReason("发送失败");
        }
        updateById(messageRecord);

        return sendResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSendMessages(MessageSendDTO sendDTO) {
        List<Long> messageIds = sendDTO.getMessageIds();
        boolean allSuccess = true;

        for (Long messageId : messageIds) {
            try {
                sendMessage(messageId);
            } catch (Exception e) {
                log.error("发送消息失败，消息ID：{}", messageId, e);
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resendFailedMessage(Long messageId) {
        MessageRecord messageRecord = getById(messageId);
        if (messageRecord == null) {
            throw new BusinessException("消息记录不存在");
        }

        if (!MessageStatusEnum.isFailed(messageRecord.getStatus())) {
            throw new BusinessException("只能重新发送失败状态的消息");
        }

        // 检查重试次数
        if (messageRecord.getRetryCount() >= messageRecord.getMaxRetryCount()) {
            throw new BusinessException("已达到最大重试次数");
        }

        // 增加重试次数
        messageRecord.setRetryCount(messageRecord.getRetryCount() + 1);
        messageRecord.setStatus(MessageStatusEnum.PENDING.getCode());
        messageRecord.setFailReason(null);
        updateById(messageRecord);

        return sendMessage(messageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markMessageAsRead(Long messageId) {
        MessageRecord messageRecord = getById(messageId);
        if (messageRecord == null) {
            throw new BusinessException("消息记录不存在");
        }

        if (MessageStatusEnum.isRead(messageRecord.getStatus())) {
            return true; // 已经是已读状态
        }

        messageRecord.setStatus(MessageStatusEnum.READ.getCode());
        messageRecord.setReadTime(LocalDateTime.now());

        return updateById(messageRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchMarkMessagesAsRead(List<Long> messageIds) {
        LambdaQueryWrapper<MessageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MessageRecord::getId, messageIds)
                .ne(MessageRecord::getStatus, MessageStatusEnum.READ.getCode());

        MessageRecord updateRecord = new MessageRecord();
        updateRecord.setStatus(MessageStatusEnum.READ.getCode());
        updateRecord.setReadTime(LocalDateTime.now());

        return update(updateRecord, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelMessage(Long messageId) {
        MessageRecord messageRecord = getById(messageId);
        if (messageRecord == null) {
            throw new BusinessException("消息记录不存在");
        }

        if (!MessageStatusEnum.isPending(messageRecord.getStatus()) &&
                !MessageStatusEnum.isFailed(messageRecord.getStatus())) {
            throw new BusinessException("只能取消待发送或发送失败状态的消息");
        }

        messageRecord.setStatus(MessageStatusEnum.CANCELLED.getCode());
        return updateById(messageRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCancelMessages(List<Long> messageIds) {
        LambdaQueryWrapper<MessageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MessageRecord::getId, messageIds)
                .in(MessageRecord::getStatus, MessageStatusEnum.PENDING.getCode(),
                        MessageStatusEnum.FAILED.getCode());

        MessageRecord updateRecord = new MessageRecord();
        updateRecord.setStatus(MessageStatusEnum.CANCELLED.getCode());

        return update(updateRecord, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMessageRecord(Long messageId) {
        MessageRecord messageRecord = getById(messageId);
        if (messageRecord == null) {
            throw new BusinessException("消息记录不存在");
        }

        return removeById(messageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteMessageRecords(List<Long> messageIds) {
        return removeByIds(messageIds);
    }

    @Override
    public PageResult<MessageRecordVO> getUserMessageRecords(Long userId, String messageType, String status,
            Integer pageNum, Integer pageSize) {
        Page<MessageRecordVO> page = new Page<>(pageNum, pageSize);

        var pageResult = messageRecordMapper.selectMessageRecordsByUserId(page, userId, messageType, status);

        // 处理状态描述
        pageResult.getRecords().forEach(this::fillStatusDesc);

        return PageResult.of(pageResult);
    }

    @Override
    public Long countUnreadMessagesByUserId(Long userId) {
        return messageRecordMapper.countUnreadMessagesByUserId(userId);
    }

    @Override
    public List<MessageRecord> getPendingMessages(Integer limit) {
        return messageRecordMapper.selectPendingMessages(limit);
    }

    @Override
    public List<MessageRecord> getRetryMessages(Integer limit) {
        return messageRecordMapper.selectRetryMessages(limit);
    }

    @Override
    public void processMessageSendTask() {
        // 获取待发送的消息
        List<MessageRecord> pendingMessages = getPendingMessages(100);

        for (MessageRecord messageRecord : pendingMessages) {
            try {
                sendMessage(messageRecord.getId());
            } catch (Exception e) {
                log.error("处理消息发送任务失败，消息ID：{}", messageRecord.getId(), e);
            }
        }
    }

    @Override
    public void processMessageRetryTask() {
        // 获取需要重试的消息
        List<MessageRecord> retryMessages = getRetryMessages(50);

        for (MessageRecord messageRecord : retryMessages) {
            try {
                resendFailedMessage(messageRecord.getId());
            } catch (Exception e) {
                log.error("处理消息重试任务失败，消息ID：{}", messageRecord.getId(), e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanExpiredMessages() {
        // 删除30天前的已读消息和已取消消息
        LocalDateTime expireTime = LocalDateTime.now().minusDays(30);

        LambdaQueryWrapper<MessageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MessageRecord::getStatus, MessageStatusEnum.READ.getCode(),
                MessageStatusEnum.CANCELLED.getCode())
                .lt(MessageRecord::getCreateTime, expireTime);

        List<MessageRecord> expiredMessages = list(wrapper);
        if (!expiredMessages.isEmpty()) {
            remove(wrapper);
            log.info("清理过期消息记录，数量：{}", expiredMessages.size());
            return expiredMessages.size();
        }

        return 0;
    }

    @Override
    public MessageStatisticsVO getMessageStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        MessageStatisticsVO statistics = new MessageStatisticsVO();

        // 统计各种状态的消息数量
        statistics.setTotalMessages(countMessagesByStatus(null, startTime, endTime));
        statistics.setPendingMessages(countMessagesByStatus(MessageStatusEnum.PENDING.getCode(), startTime, endTime));
        statistics.setSendingMessages(countMessagesByStatus(MessageStatusEnum.SENDING.getCode(), startTime, endTime));
        statistics.setSuccessMessages(countMessagesByStatus(MessageStatusEnum.SUCCESS.getCode(), startTime, endTime));
        statistics.setFailedMessages(countMessagesByStatus(MessageStatusEnum.FAILED.getCode(), startTime, endTime));
        statistics.setReadMessages(countMessagesByStatus(MessageStatusEnum.READ.getCode(), startTime, endTime));
        statistics
                .setCancelledMessages(countMessagesByStatus(MessageStatusEnum.CANCELLED.getCode(), startTime, endTime));

        // 计算未读消息数量
        statistics.setUnreadMessages(statistics.getSuccessMessages() - statistics.getReadMessages());

        // 计算成功率和阅读率
        if (statistics.getTotalMessages() > 0) {
            statistics.setSuccessRate((double) statistics.getSuccessMessages() / statistics.getTotalMessages() * 100);
        } else {
            statistics.setSuccessRate(0.0);
        }

        if (statistics.getSuccessMessages() > 0) {
            statistics.setReadRate((double) statistics.getReadMessages() / statistics.getSuccessMessages() * 100);
        } else {
            statistics.setReadRate(0.0);
        }

        return statistics;
    }

    @Override
    public List<MessageRecord> getMessagesByBusinessIdAndType(Long businessId, String businessType) {
        LambdaQueryWrapper<MessageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageRecord::getBusinessId, businessId)
                .eq(MessageRecord::getBusinessType, businessType)
                .orderByDesc(MessageRecord::getCreateTime);

        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrderStatusMessage(Long orderId, Long userId, String orderStatus, String orderNo) {
        MessageCreateDTO createDTO = new MessageCreateDTO();
        createDTO.setMessageType(MessageTypeEnum.ORDER_STATUS.getCode());
        createDTO.setUserId(userId);
        createDTO.setTitle("订单状态更新通知");
        createDTO.setContent(String.format("您的订单 %s 状态已更新为：%s", orderNo, orderStatus));
        createDTO.setBusinessId(orderId);
        createDTO.setBusinessType("ORDER");
        createDTO.setSendChannel(SendChannelEnum.SYSTEM.getCode());

        return createMessageRecord(createDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPaymentSuccessMessage(Long paymentId, Long userId, String orderNo, String amount) {
        MessageCreateDTO createDTO = new MessageCreateDTO();
        createDTO.setMessageType(MessageTypeEnum.PAYMENT_SUCCESS.getCode());
        createDTO.setUserId(userId);
        createDTO.setTitle("支付成功通知");
        createDTO.setContent(String.format("您的订单 %s 支付成功，金额：¥%s", orderNo, amount));
        createDTO.setBusinessId(paymentId);
        createDTO.setBusinessType("PAYMENT");
        createDTO.setSendChannel(SendChannelEnum.SYSTEM.getCode());

        return createMessageRecord(createDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWithdrawAuditMessage(Long withdrawId, Long userId, String auditStatus, String amount) {
        MessageCreateDTO createDTO = new MessageCreateDTO();
        createDTO.setMessageType("APPROVED".equals(auditStatus) ? MessageTypeEnum.WITHDRAW_APPROVED.getCode()
                : MessageTypeEnum.WITHDRAW_REJECTED.getCode());
        createDTO.setUserId(userId);
        createDTO.setTitle("提现审核通知");
        createDTO.setContent(String.format("您的提现申请（金额：¥%s）审核%s", amount,
                "APPROVED".equals(auditStatus) ? "通过" : "未通过"));
        createDTO.setBusinessId(withdrawId);
        createDTO.setBusinessType("WITHDRAW");
        createDTO.setSendChannel(SendChannelEnum.SYSTEM.getCode());

        return createMessageRecord(createDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSystemNoticeMessage(Long userId, String title, String content) {
        MessageCreateDTO createDTO = new MessageCreateDTO();
        createDTO.setMessageType(MessageTypeEnum.SYSTEM_NOTICE.getCode());
        createDTO.setUserId(userId);
        createDTO.setTitle(title);
        createDTO.setContent(content);
        createDTO.setSendChannel(SendChannelEnum.SYSTEM.getCode());

        return createMessageRecord(createDTO);
    }

    @Override
    public IPage<MessageVO> getUserMessagePage(MessageQueryDTO queryDTO) {
        Page<MessageRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<MessageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageRecord::getUserId, queryDTO.getUserId())
                .eq(queryDTO.getMessageType() != null, MessageRecord::getMessageType, queryDTO.getMessageType())
                .eq(queryDTO.getStatus() != null, MessageRecord::getStatus, queryDTO.getStatus())
                .orderByDesc(MessageRecord::getCreateTime);

        IPage<MessageRecord> messageRecordPage = page(page, wrapper);

        // 转换为MessageVO
        IPage<MessageVO> messageVOPage = messageRecordPage.convert(this::convertToMessageVO);

        return messageVOPage;
    }

    @Override
    public MessageVO getUserMessageDetail(Long messageId, Long userId) {
        MessageRecord messageRecord = getById(messageId);
        if (messageRecord == null) {
            throw new BusinessException("消息不存在");
        }

        if (!messageRecord.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该消息");
        }

        return convertToMessageVO(messageRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markUserMessageAsRead(Long messageId, Long userId) {
        MessageRecord messageRecord = getById(messageId);
        if (messageRecord == null) {
            throw new BusinessException("消息不存在");
        }

        if (!messageRecord.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该消息");
        }

        return markMessageAsRead(messageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchMarkUserMessagesAsRead(Long userId, String messageType) {
        LambdaQueryWrapper<MessageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageRecord::getUserId, userId)
                .eq(messageType != null, MessageRecord::getMessageType, messageType)
                .ne(MessageRecord::getStatus, MessageStatusEnum.READ.getCode());

        MessageRecord updateRecord = new MessageRecord();
        updateRecord.setStatus(MessageStatusEnum.READ.getCode());
        updateRecord.setReadTime(LocalDateTime.now());

        return update(updateRecord, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserMessage(Long messageId, Long userId) {
        MessageRecord messageRecord = getById(messageId);
        if (messageRecord == null) {
            throw new BusinessException("消息不存在");
        }

        if (!messageRecord.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该消息");
        }

        return removeById(messageId);
    }

    @Override
    public Map<String, Integer> getUserUnreadCount(Long userId) {
        Map<String, Integer> unreadCount = new HashMap<>();

        // 统计各类型未读消息数量
        LambdaQueryWrapper<MessageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageRecord::getUserId, userId)
                .eq(MessageRecord::getStatus, MessageStatusEnum.SUCCESS.getCode());

        List<MessageRecord> unreadMessages = list(wrapper);

        // 按消息类型分组统计
        Map<String, Long> typeCount = unreadMessages.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        MessageRecord::getMessageType,
                        java.util.stream.Collectors.counting()));

        // 转换为Integer类型
        unreadCount.put("all", unreadMessages.size());
        unreadCount.put("system", Math.toIntExact(typeCount.getOrDefault(MessageTypeEnum.SYSTEM_NOTICE.getCode(), 0L)));
        unreadCount.put("order", Math.toIntExact(typeCount.getOrDefault(MessageTypeEnum.ORDER_STATUS.getCode(), 0L)));
        unreadCount.put("payment",
                Math.toIntExact(typeCount.getOrDefault(MessageTypeEnum.PAYMENT_SUCCESS.getCode(), 0L) +
                        typeCount.getOrDefault(MessageTypeEnum.PAYMENT_FAILED.getCode(), 0L)));
        unreadCount.put("withdraw",
                Math.toIntExact(typeCount.getOrDefault(MessageTypeEnum.WITHDRAW_APPROVED.getCode(), 0L) +
                        typeCount.getOrDefault(MessageTypeEnum.WITHDRAW_REJECTED.getCode(), 0L)));

        return unreadCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearUserMessages(Long userId, String messageType) {
        LambdaQueryWrapper<MessageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageRecord::getUserId, userId)
                .eq(messageType != null, MessageRecord::getMessageType, messageType);

        return remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchCreateSystemNoticeMessages(List<Long> userIds, String title, String content) {
        List<Long> messageIds = new ArrayList<>();

        for (Long userId : userIds) {
            Long messageId = createSystemNoticeMessage(userId, title, content);
            messageIds.add(messageId);
        }

        return messageIds;
    }

    /**
     * 统计指定状态的消息数量
     */
    private Long countMessagesByStatus(String status, LocalDateTime startTime, LocalDateTime endTime) {
        return messageRecordMapper.countMessagesByStatus(status, startTime, endTime);
    }

    /**
     * 转换MessageRecord为MessageVO
     */
    private MessageVO convertToMessageVO(MessageRecord messageRecord) {
        MessageVO messageVO = new MessageVO();
        BeanUtils.copyProperties(messageRecord, messageVO);

        // 设置状态（MessageVO中status是Integer类型，表示是否已读：0-未读，1-已读）
        if (MessageStatusEnum.READ.getCode().equals(messageRecord.getStatus())) {
            messageVO.setStatus(1); // 已读
        } else {
            messageVO.setStatus(0); // 未读
        }

        return messageVO;
    }

    /**
     * 填充状态描述
     */
    private void fillStatusDesc(MessageRecordVO recordVO) {
        // 状态描述
        MessageStatusEnum statusEnum = MessageStatusEnum.getByCode(recordVO.getStatus());
        if (statusEnum != null) {
            recordVO.setStatusDesc(statusEnum.getDesc());
        }

        // 消息类型描述
        MessageTypeEnum typeEnum = MessageTypeEnum.getByCode(recordVO.getMessageType());
        if (typeEnum != null) {
            recordVO.setMessageTypeDesc(typeEnum.getDesc());
        }
    }
}
