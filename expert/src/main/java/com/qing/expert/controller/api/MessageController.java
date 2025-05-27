package com.qing.expert.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.message.MessageQueryDTO;
import com.qing.expert.service.MessageRecordService;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.vo.message.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 消息控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Tag(name = "消息管理", description = "用户消息相关接口")
public class MessageController {

    private final MessageRecordService messageRecordService;

    @Operation(summary = "获取消息列表", description = "分页获取用户消息列表")
    @GetMapping("/list")
    public Result<IPage<MessageVO>> getMessageList(@Validated MessageQueryDTO queryDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            queryDTO.setUserId(currentUserId);

            IPage<MessageVO> page = messageRecordService.getUserMessagePage(queryDTO);
            return Result.success("获取成功", page);
        } catch (Exception e) {
            log.error("获取消息列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取消息详情", description = "根据消息ID获取消息详情")
    @GetMapping("/{messageId}")
    public Result<MessageVO> getMessageDetail(@Parameter(description = "消息ID") @PathVariable Long messageId) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            MessageVO message = messageRecordService.getUserMessageDetail(messageId, currentUserId);
            return Result.success("获取成功", message);
        } catch (Exception e) {
            log.error("获取消息详情失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "标记消息为已读", description = "将指定消息标记为已读")
    @PutMapping("/{messageId}/read")
    public Result<Void> markMessageAsRead(@Parameter(description = "消息ID") @PathVariable Long messageId) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            boolean success = messageRecordService.markUserMessageAsRead(messageId, currentUserId);
            if (success) {
                return Result.success("标记成功");
            } else {
                return Result.error("标记失败");
            }
        } catch (Exception e) {
            log.error("标记消息已读失败", e);
            return Result.error("标记失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量标记已读", description = "批量标记消息为已读")
    @PutMapping("/batch-read")
    public Result<Void> batchMarkAsRead(@RequestParam(required = false) String messageType) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            boolean success = messageRecordService.batchMarkUserMessagesAsRead(currentUserId, messageType);
            if (success) {
                return Result.success("标记成功");
            } else {
                return Result.error("标记失败");
            }
        } catch (Exception e) {
            log.error("批量标记已读失败", e);
            return Result.error("标记失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除消息", description = "删除指定消息")
    @DeleteMapping("/{messageId}")
    public Result<Void> deleteMessage(@Parameter(description = "消息ID") @PathVariable Long messageId) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            boolean success = messageRecordService.deleteUserMessage(messageId, currentUserId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除消息失败", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取未读消息数量", description = "获取用户各类型未读消息数量")
    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> getUnreadCount() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            Map<String, Integer> unreadCount = messageRecordService.getUserUnreadCount(currentUserId);
            return Result.success("获取成功", unreadCount);
        } catch (Exception e) {
            log.error("获取未读消息数量失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "清空消息", description = "清空指定类型的消息")
    @DeleteMapping("/clear")
    public Result<Void> clearMessages(@RequestParam(required = false) String messageType) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();

            boolean success = messageRecordService.clearUserMessages(currentUserId, messageType);
            if (success) {
                return Result.success("清空成功");
            } else {
                return Result.error("清空失败");
            }
        } catch (Exception e) {
            log.error("清空消息失败", e);
            return Result.error("清空失败：" + e.getMessage());
        }
    }
}
