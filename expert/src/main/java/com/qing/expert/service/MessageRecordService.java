package com.qing.expert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.message.MessageCreateDTO;
import com.qing.expert.dto.message.MessageQueryDTO;
import com.qing.expert.dto.message.MessageSendDTO;
import com.qing.expert.entity.MessageRecord;
import com.qing.expert.vo.message.MessageRecordVO;
import com.qing.expert.vo.message.MessageStatisticsVO;
import com.qing.expert.vo.message.MessageVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息记录服务接口
 */
public interface MessageRecordService extends IService<MessageRecord> {

    /**
     * 分页查询消息记录列表
     */
    PageResult<MessageRecordVO> getMessageRecordPage(MessageQueryDTO queryDTO);

    /**
     * 获取消息记录详情
     */
    MessageRecordVO getMessageRecordDetail(Long messageId);

    /**
     * 创建消息记录
     */
    Long createMessageRecord(MessageCreateDTO createDTO);

    /**
     * 批量创建消息记录
     */
    List<Long> batchCreateMessageRecords(List<MessageCreateDTO> createDTOs);

    /**
     * 发送消息
     */
    boolean sendMessage(Long messageId);

    /**
     * 批量发送消息
     */
    boolean batchSendMessages(MessageSendDTO sendDTO);

    /**
     * 重新发送失败消息
     */
    boolean resendFailedMessage(Long messageId);

    /**
     * 标记消息为已读
     */
    boolean markMessageAsRead(Long messageId);

    /**
     * 批量标记消息为已读
     */
    boolean batchMarkMessagesAsRead(List<Long> messageIds);

    /**
     * 取消消息发送
     */
    boolean cancelMessage(Long messageId);

    /**
     * 批量取消消息发送
     */
    boolean batchCancelMessages(List<Long> messageIds);

    /**
     * 删除消息记录
     */
    boolean deleteMessageRecord(Long messageId);

    /**
     * 批量删除消息记录
     */
    boolean batchDeleteMessageRecords(List<Long> messageIds);

    /**
     * 查询用户消息记录
     */
    PageResult<MessageRecordVO> getUserMessageRecords(Long userId, String messageType, String status, Integer pageNum,
            Integer pageSize);

    /**
     * 统计用户未读消息数量
     */
    Long countUnreadMessagesByUserId(Long userId);

    /**
     * 查询待发送的消息记录
     */
    List<MessageRecord> getPendingMessages(Integer limit);

    /**
     * 查询需要重试的消息记录
     */
    List<MessageRecord> getRetryMessages(Integer limit);

    /**
     * 处理消息发送任务
     */
    void processMessageSendTask();

    /**
     * 处理消息重试任务
     */
    void processMessageRetryTask();

    /**
     * 清理过期消息记录
     */
    int cleanExpiredMessages();

    /**
     * 获取消息统计数据
     */
    MessageStatisticsVO getMessageStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据业务ID和类型查询消息记录
     */
    List<MessageRecord> getMessagesByBusinessIdAndType(Long businessId, String businessType);

    /**
     * 创建订单状态通知消息
     */
    Long createOrderStatusMessage(Long orderId, Long userId, String orderStatus, String orderNo);

    /**
     * 创建支付成功通知消息
     */
    Long createPaymentSuccessMessage(Long paymentId, Long userId, String orderNo, String amount);

    /**
     * 创建提现审核通知消息
     */
    Long createWithdrawAuditMessage(Long withdrawId, Long userId, String auditStatus, String amount);

    /**
     * 创建系统通知消息
     */
    Long createSystemNoticeMessage(Long userId, String title, String content);

    /**
     * 分页查询用户消息列表
     */
    IPage<MessageVO> getUserMessagePage(MessageQueryDTO queryDTO);

    /**
     * 获取用户消息详情
     */
    MessageVO getUserMessageDetail(Long messageId, Long userId);

    /**
     * 标记用户消息为已读
     */
    boolean markUserMessageAsRead(Long messageId, Long userId);

    /**
     * 批量标记用户消息为已读
     */
    boolean batchMarkUserMessagesAsRead(Long userId, String messageType);

    /**
     * 删除用户消息
     */
    boolean deleteUserMessage(Long messageId, Long userId);

    /**
     * 获取用户未读消息数量
     */
    Map<String, Integer> getUserUnreadCount(Long userId);

    /**
     * 清空用户消息
     */
    boolean clearUserMessages(Long userId, String messageType);

    /**
     * 批量创建系统通知消息
     */
    List<Long> batchCreateSystemNoticeMessages(List<Long> userIds, String title, String content);
}
