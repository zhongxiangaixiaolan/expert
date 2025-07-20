/**
 * ECharts 配置文件
 * 统一管理 ECharts 组件的注册和配置
 */

import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import {
  LineChart,
  PieChart,
  BarChart
} from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  ToolboxComponent,
  DataZoomComponent
} from "echarts/components";

// 注册必要的组件
use([
  // 渲染器
  CanvasRenderer,

  // 图表类型
  LineChart,
  PieChart,
  BarChart,

  // 组件
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  ToolboxComponent,
  DataZoomComponent
]);

// 默认主题配置
export const defaultTheme = {
  color: [
    '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
    '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#ff9f7f',
    '#ffdb5c', '#ff7875', '#87d068', '#108ee9', '#722ed1'
  ],
  backgroundColor: 'transparent',
  textStyle: {
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
    fontSize: 12,
    color: '#666'
  },
  title: {
    textStyle: {
      color: '#333',
      fontSize: 16,
      fontWeight: 'bold'
    }
  },
  legend: {
    textStyle: {
      color: '#666'
    }
  },
  tooltip: {
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    borderColor: 'transparent',
    textStyle: {
      color: '#fff'
    }
  }
};

// 通用图表配置
export const commonChartOptions = {
  animation: true,
  animationDuration: 1000,
  animationEasing: 'cubicOut'
};

// 线图默认配置
export const lineChartDefaults = {
  ...commonChartOptions,
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    axisLine: {
      lineStyle: {
        color: '#e0e0e0'
      }
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      color: '#666'
    }
  },
  yAxis: {
    type: 'value',
    axisLine: {
      show: false
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      color: '#666'
    },
    splitLine: {
      lineStyle: {
        color: '#f0f0f0'
      }
    }
  }
};

// 饼图默认配置
export const pieChartDefaults = {
  ...commonChartOptions,
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  }
};

export default {
  defaultTheme,
  commonChartOptions,
  lineChartDefaults,
  pieChartDefaults
};
