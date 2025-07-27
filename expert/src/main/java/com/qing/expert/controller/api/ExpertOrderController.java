package com.qing.expert.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.order.OrderQueryDTO;
import com.qing.expert.entity.Expert;
import com.qing.expert.service.ExpertService;
import com.qing.expert.service.OrderService;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.vo.order.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 达人端订单控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/expert")
@RequiredArgsConstructor
    @GetMapping("/orders")
    public Result<IPage<OrderVO>> getExpertOrders(@Validated OrderQueryDTO queryDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            queryDTO.setExpertId(expert.getId());
            IPage<OrderVO> page = orderService.getOrderPage(queryDTO);
            return Result.success("获取成功", page);
        } catch (Exception e) {
            log.error("获取达人订单列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<IPage<OrderVO>> getPendingOrders(@Validated OrderQueryDTO queryDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            // 设置查询条件：待接单状态，且分类匹配
            queryDTO.setStatus(1); // 待接单
            queryDTO.setCategoryId(expert.getCategoryId());
            queryDTO.setExpertId(null); // 未分配达人

            IPage<OrderVO> page = orderService.getOrderPage(queryDTO);
            return Result.success("获取成功", page);
        } catch (Exception e) {
            log.error("获取待接单列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<Map<String, Object>> getExpertOrderStatistics() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            Map<String, Object> statistics = orderService.getExpertOrderStatistics(expert.getId());
            return Result.success("获取成功", statistics);
        } catch (Exception e) {
            log.error("获取达人订单统计失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            // 检查达人状态
            if (expert.getAuditStatus() != 1) {
                return Result.error("您的达人申请还未通过审核");
            }

            if (expert.getStatus() == 0) {
                return Result.error("您当前处于下线状态，无法接单");
            }

            boolean success = orderService.acceptOrder(orderId, expert.getId());
            if (success) {
                return Result.success("接单成功");
            } else {
                return Result.error("接单失败");
            }
        } catch (Exception e) {
            log.error("接单失败", e);
            return Result.error("接单失败：" + e.getMessage());
        }
    }

            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            boolean success = orderService.startService(orderId, expert.getId());
            if (success) {
                return Result.success("开始服务成功");
            } else {
                return Result.error("开始服务失败");
            }
        } catch (Exception e) {
            log.error("开始服务失败", e);
            return Result.error("开始服务失败：" + e.getMessage());
        }
    }

    public Result<Void> completeOrder(
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            boolean success = orderService.completeOrder(orderId, expert.getId(), data);
            if (success) {
                return Result.success("完成订单成功");
            } else {
                return Result.error("完成订单失败");
            }
        } catch (Exception e) {
            log.error("完成订单失败", e);
            return Result.error("完成订单失败：" + e.getMessage());
        }
    }
}
