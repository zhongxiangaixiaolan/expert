package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.order.OrderQueryDTO;
import com.qing.expert.dto.order.OrderUpdateDTO;
import com.qing.expert.service.OrderService;
import com.qing.expert.vo.order.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 管理端订单控制器
 */
@Tag(name = "管理端订单管理", description = "管理端订单相关接口")
@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "分页查询订单列表")
    @GetMapping("/page")
    public Result<IPage<OrderVO>> getOrderPage(@Valid OrderQueryDTO queryDTO) {
        IPage<OrderVO> page = orderService.getOrderPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "根据ID查询订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getOrderById(@Parameter(description = "订单ID") @PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderById(id);
        return Result.success(orderVO);
    }

    @Operation(summary = "更新订单")
    @PutMapping
    public Result<Boolean> updateOrder(@Valid @RequestBody OrderUpdateDTO updateDTO) {
        Boolean result = orderService.updateOrder(updateDTO);
        return Result.success(result);
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteOrder(@Parameter(description = "订单ID") @PathVariable Long id) {
        Boolean result = orderService.deleteOrder(id);
        return Result.success(result);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Boolean> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "取消原因") @RequestParam String cancelReason) {
        Boolean result = orderService.cancelOrder(id, cancelReason, 0L); // 管理员操作
        return Result.success(result);
    }

    @Operation(summary = "退款订单")
    @PostMapping("/{id}/refund")
    public Result<Boolean> refundOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "退款原因") @RequestParam String refundReason) {
        Boolean result = orderService.refundOrder(id, refundReason);
        return Result.success(result);
    }
}
