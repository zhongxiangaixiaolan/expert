package com.qing.expert.controller.api;

import com.qing.expert.common.result.PageResult;
import com.qing.expert.common.result.Result;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.entity.Expert;
import com.qing.expert.service.ExpertService;
import com.qing.expert.service.OrderService;
import com.qing.expert.vo.ExpertWorkspaceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 达人工作台控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/expert/workspace")
@RequiredArgsConstructor
    @GetMapping("/data")
    public Result<ExpertWorkspaceVO> getWorkspaceData() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            ExpertWorkspaceVO workspaceData = new ExpertWorkspaceVO();

            // 基本信息
            workspaceData.setExpertId(expert.getId());
            workspaceData.setExpertName(expert.getExpertName());
            workspaceData.setAvatar(expert.getAvatar());
            workspaceData.setStatus(expert.getStatus());
            workspaceData.setAuditStatus(expert.getAuditStatus());
            workspaceData.setRating(expert.getRating());
            workspaceData.setOrderCount(expert.getOrderCount());
            workspaceData.setCompleteCount(expert.getCompleteCount());

            // 今日统计数据
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

            Map<String, Object> todayStats = getTodayStatistics(expert.getId(), todayStart, todayEnd);
            workspaceData.setTodayStats(todayStats);

            // 待处理订单数量 - 暂时设为0，后续完善
            workspaceData.setPendingOrderCount(0);

            return Result.success("获取成功", workspaceData);
        } catch (Exception e) {
            log.error("获取工作台数据失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<Map<String, Object>> getTodayStats() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

            Map<String, Object> todayStats = getTodayStatistics(expert.getId(), todayStart, todayEnd);

            return Result.success("获取成功", todayStats);
        } catch (Exception e) {
            log.error("获取今日统计失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    public Result<Map<String, Object>> getEarningsStats(
            Expert expert = expertService.getByUserId(currentUserId);

            if (expert == null) {
                return Result.error("您还不是达人");
            }

            Map<String, Object> earningsStats = getEarningsStatistics(expert.getId(), type);

            return Result.success("获取成功", earningsStats);
        } catch (Exception e) {
            log.error("获取收益统计失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取今日统计数据
     */
    private Map<String, Object> getTodayStatistics(Long expertId, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // TODO: 后续完善订单统计接口后，从数据库获取真实数据
            // 暂时返回模拟数据
            stats.put("newOrders", 3);
            stats.put("completedOrders", 2);
            stats.put("earnings", new BigDecimal("580.00"));
            stats.put("responseTime", 15);

        } catch (Exception e) {
            log.error("获取今日统计数据失败", e);
            // 返回默认值
            stats.put("newOrders", 0);
            stats.put("completedOrders", 0);
            stats.put("earnings", BigDecimal.ZERO);
            stats.put("responseTime", 0);
        }

        return stats;
    }

    /**
     * 获取收益统计数据
     */
    private Map<String, Object> getEarningsStatistics(Long expertId, String type) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // TODO: 后续完善订单统计接口后，从数据库获取真实数据
            // 暂时返回模拟数据
            switch (type) {
                case "week":
                    stats.put("totalEarnings", new BigDecimal("2580.00"));
                    stats.put("orderCount", 12);
                    stats.put("avgOrderAmount", new BigDecimal("215.00"));
                    break;
                case "month":
                    stats.put("totalEarnings", new BigDecimal("8960.00"));
                    stats.put("orderCount", 45);
                    stats.put("avgOrderAmount", new BigDecimal("199.11"));
                    break;
                default: // today
                    stats.put("totalEarnings", new BigDecimal("580.00"));
                    stats.put("orderCount", 3);
                    stats.put("avgOrderAmount", new BigDecimal("193.33"));
                    break;
            }

        } catch (Exception e) {
            log.error("获取收益统计数据失败", e);
            stats.put("totalEarnings", BigDecimal.ZERO);
            stats.put("orderCount", 0);
            stats.put("avgOrderAmount", BigDecimal.ZERO);
        }

        return stats;
    }
}
