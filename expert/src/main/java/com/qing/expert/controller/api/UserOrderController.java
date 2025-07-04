package com.qing.expert.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.order.OrderCreateDTO;
import com.qing.expert.dto.order.OrderQueryDTO;
import com.qing.expert.service.OrderService;
import com.qing.expert.util.SecurityUtil;
import com.qing.expert.vo.order.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端订单控制器
 * 注意：由于配置了context-path: /api，这里的路径是相对于context-path的
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户端订单管理", description = "用户订单相关接口")
public class UserOrderController {

    private final OrderService orderService;

    @Operation(summary = "获取用户订单列表", description = "分页获取当前用户的订单列表")
    @GetMapping("/orders")
    public Result<IPage<OrderVO>> getUserOrders(@Validated OrderQueryDTO queryDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId == null) {
                log.error("用户未认证，无法获取订单列表");
                return Result.error("用户未认证，请重新登录");
            }

            queryDTO.setUserId(currentUserId);

            IPage<OrderVO> page = orderService.getOrderPage(queryDTO);
            return Result.success("获取成功", page);
        } catch (Exception e) {
            log.error("获取用户订单列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取用户订单统计", description = "获取用户各状态订单数量统计")
    @GetMapping("/orders/statistics")
    public Result<Object> getUserOrderStatistics() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Object statistics = orderService.getUserOrderStatistics(currentUserId);
            return Result.success("获取成功", statistics);
        } catch (Exception e) {
            log.error("获取用户订单统计失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "创建订单", description = "用户创建新订单")
    @PostMapping("/orders")
    public Result<Long> createOrder(@Validated @RequestBody OrderCreateDTO createDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            Long orderId = orderService.createOrder(createDTO, currentUserId);
            return Result.success("创建成功", orderId);
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    @Operation(summary = "取消订单", description = "用户取消订单")
    @PutMapping("/orders/{orderId}/cancel")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @RequestBody(required = false) String reason) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            boolean success = orderService.cancelOrder(orderId, currentUserId, reason);
            if (success) {
                return Result.success("取消成功");
            } else {
                return Result.error("取消失败");
            }
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return Result.error("取消失败：" + e.getMessage());
        }
    }

    @Operation(summary = "确认订单", description = "用户确认订单完成")
    @PutMapping("/orders/{orderId}/confirm")
    public Result<Void> confirmOrder(@Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            boolean success = orderService.confirmOrder(orderId, currentUserId);
            if (success) {
                return Result.success("确认成功");
            } else {
                return Result.error("确认失败");
            }
        } catch (Exception e) {
            log.error("确认订单失败", e);
            return Result.error("确认失败：" + e.getMessage());
        }
    }

    @Operation(summary = "申请售后", description = "用户申请订单售后")
    @PostMapping("/orders/{orderId}/after-sale")
    public Result<Void> applyAfterSale(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @RequestBody String reason) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            boolean success = orderService.applyAfterSale(orderId, currentUserId, reason);
            if (success) {
                return Result.success("申请成功");
            } else {
                return Result.error("申请失败");
            }
        } catch (Exception e) {
            log.error("申请售后失败", e);
            return Result.error("申请失败：" + e.getMessage());
        }
    }
}
