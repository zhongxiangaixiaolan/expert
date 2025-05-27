package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.dto.message.MessageQueryDTO;
import com.qing.expert.entity.MessageRecord;
import com.qing.expert.vo.message.MessageRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息记录Mapper接口
 */
@Mapper
public interface MessageRecordMapper extends BaseMapper<MessageRecord> {

    /**
     * 分页查询消息记录列表
     */
    IPage<MessageRecordVO> selectMessageRecordPage(Page<MessageRecordVO> page, @Param("query") MessageQueryDTO queryDTO);

    /**
     * 根据ID查询消息记录详情
     */
    MessageRecordVO selectMessageRecordById(@Param("id") Long id);

    /**
     * 查询用户消息记录列表
     */
    IPage<MessageRecordVO> selectMessageRecordsByUserId(Page<MessageRecordVO> page, 
                                                        @Param("userId") Long userId, 
                                                        @Param("messageType") String messageType,
                                                        @Param("status") String status);

    /**
     * 查询待发送的消息记录
     */
    List<MessageRecord> selectPendingMessages(@Param("limit") Integer limit);

    /**
     * 查询需要重试的消息记录
     */
    List<MessageRecord> selectRetryMessages(@Param("limit") Integer limit);

    /**
     * 统计消息发送数量
     */
    Long countMessagesByStatus(@Param("status") String status, 
                               @Param("startTime") LocalDateTime startTime, 
                               @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户未读消息数量
     */
    Long countUnreadMessagesByUserId(@Param("userId") Long userId);

    /**
     * 根据业务ID和类型查询消息记录
     */
    List<MessageRecord> selectByBusinessIdAndType(@Param("businessId") Long businessId, 
                                                  @Param("businessType") String businessType);

    /**
     * 批量更新消息状态
     */
    int batchUpdateMessageStatus(@Param("ids") List<Long> ids, 
                                 @Param("status") String status, 
                                 @Param("failReason") String failReason);

    /**
     * 查询消息发送统计数据
     */
    List<Map<String, Object>> getMessageSendStatistics(@Param("startTime") LocalDateTime startTime, 
                                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 查询用户消息阅读统计
     */
    List<Map<String, Object>> getUserMessageReadStatistics(@Param("startTime") LocalDateTime startTime, 
                                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 清理过期消息记录
     */
    int deleteExpiredMessages(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 标记消息为已读
     */
    int markMessageAsRead(@Param("id") Long id, @Param("readTime") LocalDateTime readTime);

    /**
     * 批量标记消息为已读
     */
    int batchMarkMessagesAsRead(@Param("ids") List<Long> ids, @Param("readTime") LocalDateTime readTime);
}
