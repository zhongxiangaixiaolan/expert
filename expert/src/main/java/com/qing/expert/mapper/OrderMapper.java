package com.qing.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qing.expert.dto.order.OrderQueryDTO;
import com.qing.expert.entity.Order;
import com.qing.expert.vo.order.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 分页查询订单列表
     */
    IPage<OrderVO> selectOrderPage(Page<OrderVO> page, @Param("query") OrderQueryDTO queryDTO);

    /**
     * 根据ID查询订单详情
     */
    OrderVO selectOrderById(@Param("id") Long id);

    /**
     * 查询用户订单列表
     */
    List<OrderVO> selectOrdersByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 查询达人订单列表
     */
    List<OrderVO> selectOrdersByExpertId(@Param("expertId") Long expertId, @Param("status") Integer status);

    /**
     * 统计订单数量
     */
    Long countOrders(@Param("query") OrderQueryDTO queryDTO);

    /**
     * 根据订单编号查询订单
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询待接单的订单列表
     */
    List<OrderVO> selectPendingOrders(@Param("serviceId") Long serviceId, @Param("limit") Integer limit);

    /**
     * 根据时间范围统计订单数量
     */
    Long getOrderCountByTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
