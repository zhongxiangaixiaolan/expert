package com.qing.expert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.dto.order.OrderCreateDTO;
import com.qing.expert.dto.order.OrderQueryDTO;
import com.qing.expert.dto.order.OrderUpdateDTO;
import com.qing.expert.entity.Order;
import com.qing.expert.vo.order.OrderVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Long createOrder(OrderCreateDTO createDTO, Long userId);

    /**
     * 更新订单
     */
    Boolean updateOrder(OrderUpdateDTO updateDTO);

    /**
     * 删除订单
     */
    Boolean deleteOrder(Long id);

    /**
     * 根据ID查询订单详情
     */
    OrderVO getOrderById(Long id);

    /**
     * 分页查询订单列表
     */
    IPage<OrderVO> getOrderPage(OrderQueryDTO queryDTO);

    /**
     * 查询用户订单列表
     */
    List<OrderVO> getUserOrders(Long userId, Integer status);

    /**
     * 查询达人订单列表
     */
    List<OrderVO> getExpertOrders(Long expertId, Integer status);

    /**
     * 接单
     */
    Boolean acceptOrder(Long orderId, Long expertId);

    /**
     * 开始服务
     */
    Boolean startService(Long orderId, Long expertId);

    /**
     * 完成订单
     */
    Boolean finishOrder(Long orderId, Long expertId);

    /**
     * 取消订单
     */
    Boolean cancelOrder(Long orderId, String cancelReason, Long operatorId);

    /**
     * 支付订单
     */
    Boolean payOrder(Long orderId);

    /**
     * 退款订单
     */
    Boolean refundOrder(Long orderId, String refundReason);

    /**
     * 根据订单编号查询订单
     */
    Order getOrderByOrderNo(String orderNo);

    /**
     * 查询待接单的订单列表
     */
    List<OrderVO> getPendingOrders(Long serviceId, Integer limit);

    /**
     * 生成订单编号
     */
    String generateOrderNo();

    /**
     * 获取订单总数
     */
    Long getTotalOrderCount();

    /**
     * 根据时间范围获取订单数量
     */
    Long getOrderCountByTime(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 取消订单（用户端）
     */
    Boolean cancelOrder(Long orderId, Long userId, String reason);

    /**
     * 确认订单
     */
    Boolean confirmOrder(Long orderId, Long userId);

    /**
     * 申请售后
     */
    Boolean applyAfterSale(Long orderId, Long userId, String reason);

    /**
     * 获取用户订单统计
     */
    Object getUserOrderStatistics(Long userId);

    /**
     * 获取达人订单统计
     */
    Map<String, Object> getExpertOrderStatistics(Long expertId);

    /**
     * 完成订单（达人端）
     */
    Boolean completeOrder(Long orderId, Long expertId, Map<String, Object> data);
}
