package com.qing.expert.service.impl;

import com.qing.expert.service.DashboardService;
import com.qing.expert.service.UserService;
import com.qing.expert.service.ExpertService;
import com.qing.expert.service.OrderService;
import com.qing.expert.service.FinanceStatisticsService;
import com.qing.expert.vo.DashboardStatisticsVO;
import com.qing.expert.vo.UserStatisticsVO;
import com.qing.expert.vo.ExpertStatisticsVO;
import com.qing.expert.vo.TrendDataVO;
import com.qing.expert.vo.DistributionDataVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Dashboard服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserService userService;
    private final ExpertService expertService;
    private final OrderService orderService;
    private final FinanceStatisticsService financeStatisticsService;

    @Override
    public DashboardStatisticsVO getDashboardStatistics() {
        DashboardStatisticsVO dashboard = new DashboardStatisticsVO();

        try {
            // 获取用户统计
            UserStatisticsVO userStats = userService.getUserStatistics();
            if (userStats != null) {
                dashboard.setUserCount(userStats.getTotalUsers());
                dashboard.setTodayNewUsers(userStats.getNewUsersToday());
            } else {
                dashboard.setUserCount(0L);
                dashboard.setTodayNewUsers(0L);
            }

            // 获取达人统计
            ExpertStatisticsVO expertStats = expertService.getExpertStatistics();
            if (expertStats != null) {
                dashboard.setExpertCount(expertStats.getTotalCount());
                dashboard.setTodayNewExperts(expertStats.getTodayNewCount());
            } else {
                dashboard.setExpertCount(0L);
                dashboard.setTodayNewExperts(0L);
            }

            // 获取订单统计
            Long totalOrderCount = orderService.getTotalOrderCount();
            dashboard.setOrderCount(totalOrderCount != null ? totalOrderCount : 0L);

            // 获取今日订单数
            LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime todayEnd = LocalDateTime.now();
            Long todayOrderCount = orderService.getOrderCountByTime(todayStart, todayEnd);
            dashboard.setTodayNewOrders(todayOrderCount != null ? todayOrderCount : 0L);

            // 获取总收入
            BigDecimal totalRevenue = financeStatisticsService.getPlatformIncome(null, null);
            dashboard.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

            // 获取今日收入
            BigDecimal todayRevenue = financeStatisticsService.getPlatformIncome(todayStart, todayEnd);
            dashboard.setTodayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO);

            // 获取最近订单（暂时使用模拟数据）
            dashboard.setRecentOrders(getRecentOrders());

            // 获取系统通知（暂时使用模拟数据）
            dashboard.setSystemNotices(getSystemNotices());

            // 获取图表数据（默认7天）
            dashboard.setUserRegistrationTrend(getUserRegistrationTrend(7));
            dashboard.setExpertRegistrationTrend(getExpertRegistrationTrend(7));
            dashboard.setOrderCreationTrend(getOrderCreationTrend(7));
            dashboard.setRevenueTrend(getRevenueTrend(7));
            dashboard.setOrderStatusDistribution(getOrderStatusDistribution());
            dashboard.setUserTypeDistribution(getUserTypeDistribution());
            dashboard.setExpertStatusDistribution(getExpertStatusDistribution());
            dashboard.setRevenueSourceDistribution(getRevenueSourceDistribution());

        } catch (Exception e) {
            log.error("获取Dashboard统计数据失败", e);
            // 返回默认值
            dashboard.setUserCount(0L);
            dashboard.setExpertCount(0L);
            dashboard.setOrderCount(0L);
            dashboard.setTotalRevenue(BigDecimal.ZERO);
            dashboard.setTodayNewUsers(0L);
            dashboard.setTodayNewExperts(0L);
            dashboard.setTodayNewOrders(0L);
            dashboard.setTodayRevenue(BigDecimal.ZERO);
            dashboard.setRecentOrders(new ArrayList<>());
            dashboard.setSystemNotices(new ArrayList<>());
        }

        return dashboard;
    }

    /**
     * 获取最近订单（模拟数据）
     */
    private List<DashboardStatisticsVO.RecentOrderVO> getRecentOrders() {
        List<DashboardStatisticsVO.RecentOrderVO> orders = new ArrayList<>();

        // TODO: 实现真实的最近订单查询
        // 这里暂时使用模拟数据
        DashboardStatisticsVO.RecentOrderVO order1 = new DashboardStatisticsVO.RecentOrderVO();
        order1.setId(1001L);
        order1.setUserName("张三");
        order1.setExpertName("李师傅");
        order1.setAmount(new BigDecimal("299"));
        order1.setStatus(1);
        order1.setCreateTime(LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        orders.add(order1);

        DashboardStatisticsVO.RecentOrderVO order2 = new DashboardStatisticsVO.RecentOrderVO();
        order2.setId(1002L);
        order2.setUserName("李四");
        order2.setExpertName("王师傅");
        order2.setAmount(new BigDecimal("199"));
        order2.setStatus(2);
        order2.setCreateTime(LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        orders.add(order2);

        DashboardStatisticsVO.RecentOrderVO order3 = new DashboardStatisticsVO.RecentOrderVO();
        order3.setId(1003L);
        order3.setUserName("王五");
        order3.setExpertName("赵师傅");
        order3.setAmount(new BigDecimal("399"));
        order3.setStatus(0);
        order3.setCreateTime(LocalDateTime.now().minusHours(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        orders.add(order3);

        return orders;
    }

    /**
     * 获取系统通知（模拟数据）
     */
    private List<DashboardStatisticsVO.SystemNoticeVO> getSystemNotices() {
        List<DashboardStatisticsVO.SystemNoticeVO> notices = new ArrayList<>();

        // TODO: 实现真实的系统通知查询
        // 这里暂时使用模拟数据
        DashboardStatisticsVO.SystemNoticeVO notice1 = new DashboardStatisticsVO.SystemNoticeVO();
        notice1.setId(1L);
        notice1.setContent("系统配置已更新");
        notice1.setTime(LocalDateTime.now().minusMinutes(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        notice1.setType("primary");
        notices.add(notice1);

        DashboardStatisticsVO.SystemNoticeVO notice2 = new DashboardStatisticsVO.SystemNoticeVO();
        notice2.setId(2L);
        notice2.setContent("新用户注册");
        notice2.setTime(LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        notice2.setType("success");
        notices.add(notice2);

        DashboardStatisticsVO.SystemNoticeVO notice3 = new DashboardStatisticsVO.SystemNoticeVO();
        notice3.setId(3L);
        notice3.setContent("订单支付成功");
        notice3.setTime(LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        notice3.setType("info");
        notices.add(notice3);

        return notices;
    }

    @Override
    public List<TrendDataVO> getUserRegistrationTrend(int days) {
        List<TrendDataVO> trendData = new ArrayList<>();

        try {
            // TODO: 实现真实的用户注册趋势查询
            // 这里暂时使用模拟数据
            LocalDate endDate = LocalDate.now();
            Random random = new Random();

            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = endDate.minusDays(i);
                String dateStr = date.format(DateTimeFormatter.ofPattern("MM-dd"));

                // 模拟用户注册数据，基础值 + 随机波动
                long baseValue = days == 7 ? 15 : 12; // 7天显示较高基础值
                long value = baseValue + random.nextInt(20) - 5; // 随机波动
                value = Math.max(0, value); // 确保非负

                trendData.add(new TrendDataVO(dateStr, value));
            }

        } catch (Exception e) {
            log.error("获取用户注册趋势数据失败", e);
        }

        return trendData;
    }

    @Override
    public List<TrendDataVO> getExpertRegistrationTrend(int days) {
        List<TrendDataVO> trendData = new ArrayList<>();

        try {
            // TODO: 实现真实的达人注册趋势查询
            // 这里暂时使用模拟数据
            LocalDate endDate = LocalDate.now();
            Random random = new Random();

            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = endDate.minusDays(i);
                String dateStr = date.format(DateTimeFormatter.ofPattern("MM-dd"));

                // 模拟达人注册数据，相对较少
                long baseValue = days == 7 ? 3 : 2;
                long value = baseValue + random.nextInt(6) - 2;
                value = Math.max(0, value);

                trendData.add(new TrendDataVO(dateStr, value));
            }

        } catch (Exception e) {
            log.error("获取达人注册趋势数据失败", e);
        }

        return trendData;
    }

    @Override
    public List<TrendDataVO> getOrderCreationTrend(int days) {
        List<TrendDataVO> trendData = new ArrayList<>();

        try {
            // TODO: 实现真实的订单创建趋势查询
            // 这里暂时使用模拟数据
            LocalDate endDate = LocalDate.now();
            Random random = new Random();

            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = endDate.minusDays(i);
                String dateStr = date.format(DateTimeFormatter.ofPattern("MM-dd"));

                // 模拟订单创建数据
                long baseValue = days == 7 ? 25 : 20;
                long value = baseValue + random.nextInt(30) - 10;
                value = Math.max(0, value);

                trendData.add(new TrendDataVO(dateStr, value));
            }

        } catch (Exception e) {
            log.error("获取订单创建趋势数据失败", e);
        }

        return trendData;
    }

    @Override
    public List<TrendDataVO> getRevenueTrend(int days) {
        List<TrendDataVO> trendData = new ArrayList<>();

        try {
            // TODO: 实现真实的收入趋势查询
            // 这里暂时使用模拟数据
            LocalDate endDate = LocalDate.now();
            Random random = new Random();

            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = endDate.minusDays(i);
                String dateStr = date.format(DateTimeFormatter.ofPattern("MM-dd"));

                // 模拟收入数据
                BigDecimal baseValue = days == 7 ? new BigDecimal("5000") : new BigDecimal("4000");
                BigDecimal randomValue = new BigDecimal(random.nextInt(4000) - 1000);
                BigDecimal value = baseValue.add(randomValue);
                value = value.max(BigDecimal.ZERO);

                trendData.add(new TrendDataVO(dateStr, value));
            }

        } catch (Exception e) {
            log.error("获取收入趋势数据失败", e);
        }

        return trendData;
    }

    @Override
    public List<DistributionDataVO> getOrderStatusDistribution() {
        List<DistributionDataVO> distributionData = new ArrayList<>();

        try {
            // TODO: 实现真实的订单状态分布查询
            // 这里暂时使用模拟数据
            distributionData.add(new DistributionDataVO("待支付", 15L, new BigDecimal("25.0"), "#f56c6c"));
            distributionData.add(new DistributionDataVO("进行中", 25L, new BigDecimal("41.7"), "#e6a23c"));
            distributionData.add(new DistributionDataVO("已完成", 18L, new BigDecimal("30.0"), "#67c23a"));
            distributionData.add(new DistributionDataVO("已取消", 2L, new BigDecimal("3.3"), "#909399"));

        } catch (Exception e) {
            log.error("获取订单状态分布数据失败", e);
        }

        return distributionData;
    }

    @Override
    public List<DistributionDataVO> getUserTypeDistribution() {
        List<DistributionDataVO> distributionData = new ArrayList<>();

        try {
            // TODO: 实现真实的用户类型分布查询
            // 这里暂时使用模拟数据
            distributionData.add(new DistributionDataVO("普通用户", 120L, new BigDecimal("85.7"), "#409eff"));
            distributionData.add(new DistributionDataVO("达人用户", 20L, new BigDecimal("14.3"), "#67c23a"));

        } catch (Exception e) {
            log.error("获取用户类型分布数据失败", e);
        }

        return distributionData;
    }

    @Override
    public List<DistributionDataVO> getExpertStatusDistribution() {
        List<DistributionDataVO> distributionData = new ArrayList<>();

        try {
            // TODO: 实现真实的达人状态分布查询
            // 这里暂时使用模拟数据
            distributionData.add(new DistributionDataVO("在线", 8L, new BigDecimal("40.0"), "#67c23a"));
            distributionData.add(new DistributionDataVO("忙碌", 5L, new BigDecimal("25.0"), "#e6a23c"));
            distributionData.add(new DistributionDataVO("离线", 7L, new BigDecimal("35.0"), "#909399"));

        } catch (Exception e) {
            log.error("获取达人状态分布数据失败", e);
        }

        return distributionData;
    }

    @Override
    public List<DistributionDataVO> getRevenueSourceDistribution() {
        List<DistributionDataVO> distributionData = new ArrayList<>();

        try {
            // TODO: 实现真实的收入来源分布查询
            // 这里暂时使用模拟数据
            distributionData.add(new DistributionDataVO("咨询服务", 45000L, new BigDecimal("60.0"), "#409eff"));
            distributionData.add(new DistributionDataVO("技能服务", 22500L, new BigDecimal("30.0"), "#67c23a"));
            distributionData.add(new DistributionDataVO("其他服务", 7500L, new BigDecimal("10.0"), "#e6a23c"));

        } catch (Exception e) {
            log.error("获取收入来源分布数据失败", e);
        }

        return distributionData;
    }
}