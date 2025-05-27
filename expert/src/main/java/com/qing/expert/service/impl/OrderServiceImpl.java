package com.qing.expert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.enums.OrderStatusEnum;
import com.qing.expert.common.enums.PayStatusEnum;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.dto.order.OrderCreateDTO;
import com.qing.expert.dto.order.OrderQueryDTO;
import com.qing.expert.dto.order.OrderUpdateDTO;
import com.qing.expert.entity.Order;
import com.qing.expert.mapper.OrderMapper;
import com.qing.expert.service.OrderService;
import com.qing.expert.vo.order.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderCreateDTO createDTO, Long userId) {
        // 创建订单实体
        Order order = new Order();
        BeanUtils.copyProperties(createDTO, order);

        // 设置订单基本信息
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        order.setPayStatus(PayStatusEnum.UNPAID.getCode());
        order.setPaidAmount(createDTO.getAmount());
        order.setDiscountAmount(java.math.BigDecimal.ZERO);

        // 保存订单
        boolean saved = save(order);
        if (!saved) {
            throw new BusinessException("创建订单失败");
        }

        log.info("创建订单成功，订单ID：{}，订单编号：{}", order.getId(), order.getOrderNo());
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrder(OrderUpdateDTO updateDTO) {
        Order existOrder = getById(updateDTO.getId());
        if (existOrder == null) {
            throw new BusinessException("订单不存在");
        }

        Order order = new Order();
        BeanUtils.copyProperties(updateDTO, order);

        boolean updated = updateById(order);
        if (updated) {
            log.info("更新订单成功，订单ID：{}", updateDTO.getId());
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrder(Long id) {
        Order existOrder = getById(id);
        if (existOrder == null) {
            throw new BusinessException("订单不存在");
        }

        boolean deleted = removeById(id);
        if (deleted) {
            log.info("删除订单成功，订单ID：{}", id);
        }
        return deleted;
    }

    @Override
    public OrderVO getOrderById(Long id) {
        return orderMapper.selectOrderById(id);
    }

    @Override
    public IPage<OrderVO> getOrderPage(OrderQueryDTO queryDTO) {
        Page<OrderVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return orderMapper.selectOrderPage(page, queryDTO);
    }

    @Override
    public List<OrderVO> getUserOrders(Long userId, Integer status) {
        return orderMapper.selectOrdersByUserId(userId, status);
    }

    @Override
    public List<OrderVO> getExpertOrders(Long expertId, Integer status) {
        return orderMapper.selectOrdersByExpertId(expertId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean acceptOrder(Long orderId, Long expertId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!OrderStatusEnum.isPending(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法接单");
        }

        order.setExpertId(expertId);
        order.setStatus(OrderStatusEnum.ACCEPTED.getCode());
        order.setAcceptTime(LocalDateTime.now());

        boolean updated = updateById(order);
        if (updated) {
            log.info("接单成功，订单ID：{}，达人ID：{}", orderId, expertId);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startService(Long orderId, Long expertId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!OrderStatusEnum.isAccepted(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法开始服务");
        }

        if (!expertId.equals(order.getExpertId())) {
            throw new BusinessException("只有接单达人才能开始服务");
        }

        order.setStatus(OrderStatusEnum.IN_SERVICE.getCode());
        order.setStartTime(LocalDateTime.now());

        boolean updated = updateById(order);
        if (updated) {
            log.info("开始服务成功，订单ID：{}", orderId);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean finishOrder(Long orderId, Long expertId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!OrderStatusEnum.isInService(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法完成订单");
        }

        if (!expertId.equals(order.getExpertId())) {
            throw new BusinessException("只有服务达人才能完成订单");
        }

        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        order.setFinishTime(LocalDateTime.now());

        boolean updated = updateById(order);
        if (updated) {
            log.info("完成订单成功，订单ID：{}", orderId);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long orderId, String cancelReason, Long operatorId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (OrderStatusEnum.isCompleted(order.getStatus()) || OrderStatusEnum.isCancelled(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法取消");
        }

        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(cancelReason);

        boolean updated = updateById(order);
        if (updated) {
            log.info("取消订单成功，订单ID：{}，操作人：{}", orderId, operatorId);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!PayStatusEnum.isUnpaid(order.getPayStatus())) {
            throw new BusinessException("订单已支付或已退款");
        }

        order.setPayStatus(PayStatusEnum.PAID.getCode());
        order.setPayTime(LocalDateTime.now());

        boolean updated = updateById(order);
        if (updated) {
            log.info("支付订单成功，订单ID：{}", orderId);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean refundOrder(Long orderId, String refundReason) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!PayStatusEnum.isPaid(order.getPayStatus())) {
            throw new BusinessException("订单未支付，无法退款");
        }

        order.setPayStatus(PayStatusEnum.REFUNDED.getCode());
        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(refundReason);

        boolean updated = updateById(order);
        if (updated) {
            log.info("退款订单成功，订单ID：{}", orderId);
        }
        return updated;
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<OrderVO> getPendingOrders(Long serviceId, Integer limit) {
        return orderMapper.selectPendingOrders(serviceId, limit);
    }

    @Override
    public String generateOrderNo() {
        // 生成订单编号：时间戳 + 随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD" + timestamp + random;
    }

    @Override
    public Long getTotalOrderCount() {
        return this.count();
    }

    @Override
    public Long getOrderCountByTime(LocalDateTime startTime, LocalDateTime endTime) {
        return orderMapper.getOrderCountByTime(startTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long orderId, Long userId, String reason) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!userId.equals(order.getUserId())) {
            throw new BusinessException("只能取消自己的订单");
        }

        if (OrderStatusEnum.isCompleted(order.getStatus()) || OrderStatusEnum.isCancelled(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法取消");
        }

        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);

        boolean updated = updateById(order);
        if (updated) {
            log.info("用户取消订单成功，订单ID：{}，用户ID：{}", orderId, userId);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmOrder(Long orderId, Long userId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!userId.equals(order.getUserId())) {
            throw new BusinessException("只能确认自己的订单");
        }

        if (!OrderStatusEnum.isCompleted(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法确认");
        }

        // 这里可以添加确认订单的业务逻辑，比如更新订单状态、结算费用等
        log.info("用户确认订单成功，订单ID：{}，用户ID：{}", orderId, userId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean applyAfterSale(Long orderId, Long userId, String reason) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!userId.equals(order.getUserId())) {
            throw new BusinessException("只能对自己的订单申请售后");
        }

        if (!OrderStatusEnum.isCompleted(order.getStatus())) {
            throw new BusinessException("只有已完成的订单才能申请售后");
        }

        order.setStatus(OrderStatusEnum.AFTER_SALE.getCode());
        order.setCancelReason(reason); // 复用取消原因字段存储售后原因

        boolean updated = updateById(order);
        if (updated) {
            log.info("申请售后成功，订单ID：{}，用户ID：{}", orderId, userId);
        }
        return updated;
    }

    @Override
    public Object getUserOrderStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();

        // 统计各状态订单数量
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId).eq(Order::getDeleted, 0);

        // 总订单数
        Long totalCount = count(wrapper);
        statistics.put("totalCount", totalCount);

        // 待支付订单数
        wrapper.clear();
        wrapper.eq(Order::getUserId, userId).eq(Order::getDeleted, 0)
                .eq(Order::getPayStatus, PayStatusEnum.UNPAID.getCode());
        Long unpaidCount = count(wrapper);
        statistics.put("unpaidCount", unpaidCount);

        // 进行中订单数（已支付但未完成）
        wrapper.clear();
        wrapper.eq(Order::getUserId, userId).eq(Order::getDeleted, 0)
                .eq(Order::getPayStatus, PayStatusEnum.PAID.getCode())
                .in(Order::getStatus, OrderStatusEnum.PENDING.getCode(),
                        OrderStatusEnum.ACCEPTED.getCode(), OrderStatusEnum.IN_SERVICE.getCode());
        Long inProgressCount = count(wrapper);
        statistics.put("inProgressCount", inProgressCount);

        // 已完成订单数
        wrapper.clear();
        wrapper.eq(Order::getUserId, userId).eq(Order::getDeleted, 0)
                .eq(Order::getStatus, OrderStatusEnum.COMPLETED.getCode());
        Long completedCount = count(wrapper);
        statistics.put("completedCount", completedCount);

        // 售后订单数
        wrapper.clear();
        wrapper.eq(Order::getUserId, userId).eq(Order::getDeleted, 0)
                .eq(Order::getStatus, OrderStatusEnum.AFTER_SALE.getCode());
        Long afterSaleCount = count(wrapper);
        statistics.put("afterSaleCount", afterSaleCount);

        return statistics;
    }

    @Override
    public Map<String, Object> getExpertOrderStatistics(Long expertId) {
        Map<String, Object> statistics = new HashMap<>();

        // 统计各状态订单数量
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getExpertId, expertId).eq(Order::getDeleted, 0);

        // 总订单数
        Long totalCount = count(wrapper);
        statistics.put("totalCount", totalCount);

        // 待接单数（状态为待接单且未分配达人的订单不在这里统计）
        // 这里统计的是已接单但未开始服务的订单
        wrapper.clear();
        wrapper.eq(Order::getExpertId, expertId).eq(Order::getDeleted, 0)
                .eq(Order::getStatus, OrderStatusEnum.ACCEPTED.getCode());
        Long acceptedCount = count(wrapper);
        statistics.put("acceptedCount", acceptedCount);

        // 服务中订单数
        wrapper.clear();
        wrapper.eq(Order::getExpertId, expertId).eq(Order::getDeleted, 0)
                .eq(Order::getStatus, OrderStatusEnum.IN_SERVICE.getCode());
        Long inServiceCount = count(wrapper);
        statistics.put("inServiceCount", inServiceCount);

        // 已完成订单数
        wrapper.clear();
        wrapper.eq(Order::getExpertId, expertId).eq(Order::getDeleted, 0)
                .eq(Order::getStatus, OrderStatusEnum.COMPLETED.getCode());
        Long completedCount = count(wrapper);
        statistics.put("completedCount", completedCount);

        // 售后订单数
        wrapper.clear();
        wrapper.eq(Order::getExpertId, expertId).eq(Order::getDeleted, 0)
                .eq(Order::getStatus, OrderStatusEnum.AFTER_SALE.getCode());
        Long afterSaleCount = count(wrapper);
        statistics.put("afterSaleCount", afterSaleCount);

        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeOrder(Long orderId, Long expertId, Map<String, Object> data) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!expertId.equals(order.getExpertId())) {
            throw new BusinessException("只有服务达人才能完成订单");
        }

        if (!OrderStatusEnum.isInService(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法完成订单");
        }

        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        order.setFinishTime(LocalDateTime.now());

        // 处理完成订单时的额外数据（如完成截图、达人签名等）
        if (data != null) {
            // 可以将额外数据存储到订单的备注字段或者单独的表中
            if (data.containsKey("completionScreenshots")) {
                // 存储完成截图
                order.setExpertRemark("完成截图：" + data.get("completionScreenshots"));
            }
            if (data.containsKey("talentSignature")) {
                // 存储达人签名
                String currentRemark = order.getExpertRemark() != null ? order.getExpertRemark() : "";
                order.setExpertRemark(currentRemark + " 达人签名：" + data.get("talentSignature"));
            }
        }

        boolean updated = updateById(order);
        if (updated) {
            log.info("达人完成订单成功，订单ID：{}，达人ID：{}", orderId, expertId);
        }
        return updated;
    }
}
