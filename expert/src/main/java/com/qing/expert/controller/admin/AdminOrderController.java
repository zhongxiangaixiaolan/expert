package com.qing.expert.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qing.expert.common.result.Result;
import com.qing.expert.dto.order.OrderQueryDTO;
import com.qing.expert.dto.order.OrderUpdateDTO;
import com.qing.expert.service.OrderService;
import com.qing.expert.vo.order.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 管理端订单控制器
 */
@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping("/page")
    public Result<IPage<OrderVO>> getOrderPage(@Valid OrderQueryDTO queryDTO) {
        IPage<OrderVO> page = orderService.getOrderPage(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    @PutMapping("/{id}")
    public Result<Boolean> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderUpdateDTO updateDTO) {
        updateDTO.setId(id);
        Boolean result = orderService.updateOrder(updateDTO);
        return Result.success(result);
    }

    @PostMapping("/{id}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable Long id, @RequestParam String cancelReason) {
        Boolean result = orderService.cancelOrder(id, cancelReason, 0L); // 管理员操作
        return Result.success(result);
    }

    @PostMapping("/{id}/refund")
    public Result<Boolean> refundOrder(@PathVariable Long id, @RequestParam String refundReason) {
        Boolean result = orderService.refundOrder(id, refundReason);
        return Result.success(result);
    }
}
