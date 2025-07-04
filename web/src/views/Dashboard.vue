<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user">
              <el-icon size="24"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.userCount || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon expert">
              <el-icon size="24"><Avatar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.expertCount || 0 }}</div>
              <div class="stat-label">达人总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order">
              <el-icon size="24"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.orderCount || 0 }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon revenue">
              <el-icon size="24"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">¥{{ stats.totalRevenue || 0 }}</div>
              <div class="stat-label">总收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span class="chart-title">趋势分析</span>
              <div class="chart-controls">
                <el-tabs
                  v-model="trendActiveTab"
                  @tab-change="handleTrendTabChange"
                >
                  <el-tab-pane label="用户注册" name="user-registration" />
                  <el-tab-pane label="达人注册" name="expert-registration" />
                  <el-tab-pane label="订单创建" name="order-creation" />
                  <el-tab-pane label="收入趋势" name="revenue" />
                </el-tabs>
                <el-radio-group
                  v-model="trendDays"
                  @change="handleTrendDaysChange"
                  size="small"
                >
                  <el-radio-button :value="7">7天</el-radio-button>
                  <el-radio-button :value="30">30天</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="chart-container">
            <v-chart
              v-if="trendChartData.length > 0"
              :option="trendChartOption"
              :loading="trendLoading"
              class="trend-chart"
            />
            <div v-else class="chart-placeholder">
              <el-icon size="48"><TrendCharts /></el-icon>
              <p>暂无数据</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span class="chart-title">分布分析</span>
              <el-tabs
                v-model="distributionActiveTab"
                @tab-change="handleDistributionTabChange"
              >
                <el-tab-pane label="订单状态" name="order-status" />
                <el-tab-pane label="用户类型" name="user-type" />
                <el-tab-pane label="达人状态" name="expert-status" />
                <el-tab-pane label="收入来源" name="revenue-source" />
              </el-tabs>
            </div>
          </template>
          <div class="chart-container">
            <v-chart
              v-if="distributionChartData.length > 0"
              :option="distributionChartOption"
              :loading="distributionLoading"
              class="distribution-chart"
            />
            <div v-else class="chart-placeholder">
              <el-icon size="48"><PieChart /></el-icon>
              <p>暂无数据</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="recent-section">
      <el-col :span="12">
        <el-card title="最近订单">
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="id" label="订单ID" width="80" />
            <el-table-column prop="userName" label="用户" />
            <el-table-column prop="expertName" label="达人" />
            <el-table-column prop="amount" label="金额">
              <template #default="{ row }"> ¥{{ row.amount }} </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getOrderStatusType(row.status)">
                  {{ getOrderStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card title="系统通知">
          <el-timeline>
            <el-timeline-item
              v-for="notice in systemNotices"
              :key="notice.id"
              :timestamp="notice.time"
              :type="notice.type"
            >
              {{ notice.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import {
  getDashboardStatistics,
  getUserRegistrationTrend,
  getExpertRegistrationTrend,
  getOrderCreationTrend,
  getRevenueTrend,
  getOrderStatusDistribution,
  getUserTypeDistribution,
  getExpertStatusDistribution,
  getRevenueSourceDistribution,
  type DashboardStatistics,
  type RecentOrder,
  type SystemNotice,
  type TrendData,
  type DistributionData,
} from "@/api/system";
import { ElMessage } from "element-plus";
import VChart from "vue-echarts";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { LineChart, PieChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from "echarts/components";

// 注册ECharts组件
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
]);

// 统计数据
const stats = ref({
  userCount: 0,
  expertCount: 0,
  orderCount: 0,
  totalRevenue: 0,
});

// 最近订单
const recentOrders = ref<RecentOrder[]>([]);

// 系统通知
const systemNotices = ref<SystemNotice[]>([]);

// 图表相关数据
const trendActiveTab = ref("user-registration");
const trendDays = ref(7);
const trendLoading = ref(false);
const trendChartData = ref<TrendData[]>([]);

const distributionActiveTab = ref("order-status");
const distributionLoading = ref(false);
const distributionChartData = ref<DistributionData[]>([]);

// 获取订单状态类型
const getOrderStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    0: "info",
    1: "warning",
    2: "success",
    3: "danger",
  };
  return statusMap[status] || "info";
};

// 获取订单状态文本
const getOrderStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: "待支付",
    1: "进行中",
    2: "已完成",
    3: "已取消",
  };
  return statusMap[status] || "未知";
};

// 趋势图表配置
const trendChartOption = computed(() => {
  const getTrendTitle = () => {
    const titleMap: Record<string, string> = {
      "user-registration": "用户注册趋势",
      "expert-registration": "达人注册趋势",
      "order-creation": "订单创建趋势",
      revenue: "收入趋势",
    };
    return titleMap[trendActiveTab.value] || "";
  };

  const getYAxisLabel = () => {
    return trendActiveTab.value === "revenue" ? "金额 (元)" : "数量";
  };

  return {
    title: {
      text: getTrendTitle(),
      left: "center",
      textStyle: {
        fontSize: 16,
        fontWeight: "bold",
        color: "#333",
      },
    },
    tooltip: {
      trigger: "axis",
      formatter: (params: any) => {
        const data = params[0];
        const value =
          trendActiveTab.value === "revenue"
            ? `¥${Number(data.value).toLocaleString()}`
            : data.value;
        return `${data.axisValue}<br/>${data.seriesName}: ${value}`;
      },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: trendChartData.value.map((item) => item.date),
      axisLine: {
        lineStyle: {
          color: "#e0e6ed",
        },
      },
      axisLabel: {
        color: "#666",
      },
    },
    yAxis: {
      type: "value",
      name: getYAxisLabel(),
      nameTextStyle: {
        color: "#666",
      },
      axisLine: {
        lineStyle: {
          color: "#e0e6ed",
        },
      },
      axisLabel: {
        color: "#666",
        formatter: (value: number) => {
          if (trendActiveTab.value === "revenue") {
            return value >= 1000
              ? `${(value / 1000).toFixed(1)}k`
              : value.toString();
          }
          return value.toString();
        },
      },
      splitLine: {
        lineStyle: {
          color: "#f0f0f0",
        },
      },
    },
    series: [
      {
        name: getTrendTitle(),
        type: "line",
        smooth: true,
        symbol: "circle",
        symbolSize: 6,
        lineStyle: {
          width: 3,
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: "#409eff" },
              { offset: 1, color: "#67c23a" },
            ],
          },
        },
        itemStyle: {
          color: "#409eff",
          borderColor: "#fff",
          borderWidth: 2,
        },
        areaStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: "rgba(64, 158, 255, 0.3)" },
              { offset: 1, color: "rgba(64, 158, 255, 0.05)" },
            ],
          },
        },
        data: trendChartData.value.map((item) => item.value),
      },
    ],
  };
});

// 分布图表配置
const distributionChartOption = computed(() => {
  const getDistributionTitle = () => {
    const titleMap: Record<string, string> = {
      "order-status": "订单状态分布",
      "user-type": "用户类型分布",
      "expert-status": "达人状态分布",
      "revenue-source": "收入来源分布",
    };
    return titleMap[distributionActiveTab.value] || "";
  };

  return {
    title: {
      text: getDistributionTitle(),
      left: "center",
      textStyle: {
        fontSize: 16,
        fontWeight: "bold",
        color: "#333",
      },
    },
    tooltip: {
      trigger: "item",
      formatter: (params: any) => {
        return `${params.name}<br/>数量: ${params.value}<br/>占比: ${params.percent}%`;
      },
    },
    legend: {
      orient: "vertical",
      left: "left",
      top: "middle",
      textStyle: {
        color: "#666",
      },
    },
    series: [
      {
        name: getDistributionTitle(),
        type: "pie",
        radius: ["40%", "70%"],
        center: ["60%", "50%"],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: false,
          position: "center",
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: "bold",
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
        labelLine: {
          show: false,
        },
        data: distributionChartData.value.map((item) => ({
          name: item.name,
          value: item.value,
          itemStyle: {
            color: item.color || "#409eff",
          },
        })),
      },
    ],
  };
});

// 加载统计数据
const loadStats = async () => {
  try {
    const data = await getDashboardStatistics();

    // 更新统计数据
    stats.value = {
      userCount: data.userCount,
      expertCount: data.expertCount,
      orderCount: data.orderCount,
      totalRevenue: data.totalRevenue,
    };

    // 更新最近订单
    recentOrders.value = data.recentOrders || [];

    // 更新系统通知
    systemNotices.value = data.systemNotices || [];
  } catch (error) {
    console.error("加载统计数据失败:", error);
    ElMessage.error("加载统计数据失败");

    // 使用默认值
    stats.value = {
      userCount: 0,
      expertCount: 0,
      orderCount: 0,
      totalRevenue: 0,
    };
    recentOrders.value = [];
    systemNotices.value = [];
  }
};

// 加载趋势图表数据
const loadTrendData = async () => {
  trendLoading.value = true;
  try {
    let data: TrendData[] = [];

    switch (trendActiveTab.value) {
      case "user-registration":
        data = await getUserRegistrationTrend(trendDays.value);
        break;
      case "expert-registration":
        data = await getExpertRegistrationTrend(trendDays.value);
        break;
      case "order-creation":
        data = await getOrderCreationTrend(trendDays.value);
        break;
      case "revenue":
        data = await getRevenueTrend(trendDays.value);
        break;
    }

    trendChartData.value = data;
  } catch (error) {
    console.error("加载趋势图表数据失败:", error);
    ElMessage.error("加载趋势图表数据失败");
    trendChartData.value = [];
  } finally {
    trendLoading.value = false;
  }
};

// 加载分布图表数据
const loadDistributionData = async () => {
  distributionLoading.value = true;
  try {
    let data: DistributionData[] = [];

    switch (distributionActiveTab.value) {
      case "order-status":
        data = await getOrderStatusDistribution();
        break;
      case "user-type":
        data = await getUserTypeDistribution();
        break;
      case "expert-status":
        data = await getExpertStatusDistribution();
        break;
      case "revenue-source":
        data = await getRevenueSourceDistribution();
        break;
    }

    distributionChartData.value = data;
  } catch (error) {
    console.error("加载分布图表数据失败:", error);
    ElMessage.error("加载分布图表数据失败");
    distributionChartData.value = [];
  } finally {
    distributionLoading.value = false;
  }
};

// 趋势Tab切换处理
const handleTrendTabChange = (tabName: string) => {
  trendActiveTab.value = tabName;
  loadTrendData();
};

// 趋势天数切换处理
const handleTrendDaysChange = () => {
  loadTrendData();
};

// 分布Tab切换处理
const handleDistributionTabChange = (tabName: string) => {
  distributionActiveTab.value = tabName;
  loadDistributionData();
};

onMounted(() => {
  loadStats();
  loadTrendData();
  loadDistributionData();
});
</script>

<style scoped>
.dashboard {
  padding: 0;
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stats-cards {
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  height: 140px;
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border-light);
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(
    90deg,
    var(--primary-500) 0%,
    var(--secondary-500) 100%
  );
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--color-border);
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: var(--spacing-lg);
  position: relative;
}

.stat-icon {
  width: 72px;
  height: 72px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--spacing-lg);
  color: white;
  position: relative;
  box-shadow: var(--shadow-md);
  transition: all var(--transition-base);
}

.stat-card:hover .stat-icon {
  transform: scale(1.05);
  box-shadow: var(--shadow-lg);
}

.stat-icon.user {
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--primary-500) 100%
  );
}

.stat-icon.expert {
  background: linear-gradient(
    135deg,
    var(--secondary-600) 0%,
    var(--secondary-500) 100%
  );
}

.stat-icon.order {
  background: linear-gradient(
    135deg,
    var(--success-600) 0%,
    var(--success-500) 100%
  );
}

.stat-icon.revenue {
  background: linear-gradient(
    135deg,
    var(--warning-600) 0%,
    var(--warning-500) 100%
  );
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  line-height: 1;
  margin-bottom: var(--spacing-sm);
  background: linear-gradient(
    135deg,
    var(--primary-600) 0%,
    var(--secondary-600) 100%
  );
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.charts-row {
  margin-bottom: var(--spacing-xl);
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: var(--spacing-base);
}

.chart-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.chart-controls {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
}

.chart-container {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(
    135deg,
    var(--gray-50) 0%,
    var(--primary-50) 100%
  );
  border-radius: var(--radius-lg);
  position: relative;
  overflow: hidden;
}

.trend-chart,
.distribution-chart {
  width: 100%;
  height: 100%;
}

.chart-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(
      circle at 30% 70%,
      var(--primary-100) 0%,
      transparent 50%
    ),
    radial-gradient(circle at 70% 30%, var(--secondary-100) 0%, transparent 50%);
  opacity: 0.5;
}

.chart-placeholder {
  text-align: center;
  color: var(--color-text-muted);
  position: relative;
  z-index: 1;
  padding: var(--spacing-xl);
  background: rgba(255, 255, 255, 0.8);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--color-border-light);
}

.chart-placeholder .el-icon {
  color: var(--color-primary);
  margin-bottom: var(--spacing-base);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.05);
    opacity: 1;
  }
}

.chart-placeholder p {
  margin-top: var(--spacing-base);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-secondary);
}

.recent-section {
  margin-bottom: var(--spacing-xl);
}

:deep(.el-card__header) {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--color-border-light);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  background: linear-gradient(
    135deg,
    var(--color-background-soft) 0%,
    var(--primary-50) 100%
  );
  position: relative;
}

:deep(.el-card__header::after) {
  content: "";
  position: absolute;
  bottom: 0;
  left: var(--spacing-xl);
  right: var(--spacing-xl);
  height: 2px;
  background: linear-gradient(
    90deg,
    var(--primary-500) 0%,
    var(--secondary-500) 100%
  );
  border-radius: var(--radius-base);
}

:deep(.el-card__body) {
  padding: var(--spacing-xl);
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

:deep(.el-table .el-table__header-wrapper) {
  background: var(--color-background-soft);
}

:deep(.el-table th) {
  background-color: var(--color-background-soft);
  color: var(--color-text-primary);
  font-weight: var(--font-weight-semibold);
  border-bottom: 2px solid var(--color-border);
}

:deep(.el-table td) {
  border-bottom: 1px solid var(--color-border-light);
  transition: background-color var(--transition-fast);
}

:deep(.el-table__row:hover td) {
  background-color: var(--primary-50);
}

/* 时间线样式优化 */
:deep(.el-timeline) {
  padding: var(--spacing-base) 0;
}

:deep(.el-timeline-item__wrapper) {
  padding-left: var(--spacing-xl);
}

:deep(.el-timeline-item__node) {
  background-color: var(--color-primary);
  border: 3px solid var(--primary-200);
  width: 16px;
  height: 16px;
}

:deep(.el-timeline-item__content) {
  color: var(--color-text-primary);
  font-weight: var(--font-weight-medium);
  line-height: 1.6;
}

/* 图表Tab样式优化 */
:deep(.chart-header .el-tabs) {
  margin: 0;
}

:deep(.chart-header .el-tabs__header) {
  margin: 0;
}

:deep(.chart-header .el-tabs__nav-wrap) {
  margin-bottom: 0;
}

:deep(.chart-header .el-tabs__item) {
  padding: var(--spacing-sm) var(--spacing-base);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  border-bottom: 2px solid transparent;
  transition: all var(--transition-base);
}

:deep(.chart-header .el-tabs__item:hover) {
  color: var(--color-primary);
}

:deep(.chart-header .el-tabs__item.is-active) {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
  font-weight: var(--font-weight-semibold);
}

:deep(.chart-header .el-tabs__active-bar) {
  background-color: var(--color-primary);
}

/* 单选按钮组样式优化 */
:deep(.chart-controls .el-radio-group) {
  background: var(--color-background-soft);
  border-radius: var(--radius-base);
  padding: 2px;
}

:deep(.chart-controls .el-radio-button__inner) {
  border: none;
  background: transparent;
  color: var(--color-text-secondary);
  padding: var(--spacing-xs) var(--spacing-sm);
  font-size: var(--font-size-sm);
  border-radius: var(--radius-sm);
  transition: all var(--transition-base);
}

:deep(.chart-controls .el-radio-button__inner:hover) {
  color: var(--color-primary);
  background: var(--primary-100);
}

:deep(.chart-controls .el-radio-button.is-active .el-radio-button__inner) {
  background: var(--color-primary);
  color: white;
  box-shadow: var(--shadow-sm);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .chart-controls {
    width: 100%;
    justify-content: space-between;
  }

  .chart-container {
    height: 280px;
  }
}

:deep(.el-timeline-item__timestamp) {
  color: var(--color-text-muted);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

/* 标签样式优化 */
:deep(.el-tag) {
  border-radius: var(--radius-base);
  font-weight: var(--font-weight-medium);
  padding: var(--spacing-xs) var(--spacing-sm);
}

:deep(.el-tag--success) {
  background-color: var(--success-50);
  color: var(--success-600);
  border-color: var(--success-200);
}

:deep(.el-tag--warning) {
  background-color: var(--warning-50);
  color: var(--warning-600);
  border-color: var(--warning-200);
}

:deep(.el-tag--danger) {
  background-color: var(--error-50);
  color: var(--error-600);
  border-color: var(--error-200);
}

:deep(.el-tag--info) {
  background-color: var(--gray-100);
  color: var(--gray-600);
  border-color: var(--gray-200);
}
</style>
