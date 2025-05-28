<template>
  <view class="custom-tab-bar">
    <view
      v-for="(item, index) in tabList"
      :key="index"
      class="tab-item"
      :class="{ active: selected === index }"
      @click="switchTab(index)"
    >
      <view class="tab-icon">
        <image
          :src="selected === index ? item.iconActive : item.iconDefault"
          class="icon-image"
          :class="{ 'icon-active': selected === index }"
          mode="aspectFit"
        />
      </view>
      <text class="tab-text" :class="{ active: selected === index }">{{
        item.text
      }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';

// 响应式数据
const selected = ref<number>(0);
const isTabSwitching = ref<boolean>(false);
let statusCheckTimer: any = null;

// Tab列表配置
const tabList = [
  {
    pagePath: "pages/index/index",
    text: "首页",
    iconDefault: "/static/tabbar/home.svg",
    iconActive: "/static/tabbar/home-active.svg",
  },
  {
    pagePath: "pages/expert/list",
    text: "找达人",
    iconDefault: "/static/tabbar/expert.svg",
    iconActive: "/static/tabbar/expert-active.svg",
  },
  {
    pagePath: "pages/order/list",
    text: "订单",
    iconDefault: "/static/tabbar/order.svg",
    iconActive: "/static/tabbar/order-active.svg",
  },
  {
    pagePath: "pages/user/index",
    text: "我的",
    iconDefault: "/static/tabbar/user.svg",
    iconActive: "/static/tabbar/user-active.svg",
  },
];

// 组件挂载
onMounted(() => {
  // 组件挂载后立即更新状态
  nextTick(() => {
    updateSelected();
  });

  // 定期检查页面状态，确保状态同步
  startStatusCheck();
});

// 组件卸载前清理
onBeforeUnmount(() => {
  // 清理定时器
  stopStatusCheck();
});

// 切换标签
const switchTab = (index: number) => {
  // 防止重复点击同一个tab或正在切换中
  if (selected.value === index || isTabSwitching.value) {
    return;
  }

  const item = tabList[index];

  // 设置切换状态标记
  isTabSwitching.value = true;

  // 立即更新选中状态，避免延迟
  selected.value = index;

  uni.switchTab({
    url: `/${item.pagePath}`,
    success: () => {
      // 跳转成功后确认状态并清除切换标记
      nextTick(() => {
        selected.value = index;
        isTabSwitching.value = false;
      });
    },
    fail: (err) => {
      console.error('TabBar跳转失败:', err);
      // 跳转失败时恢复之前的状态并清除切换标记
      updateSelected();
      isTabSwitching.value = false;
    }
  });
};

// 更新选中状态 - 自动检测当前页面
const updateSelected = () => {
  const pages = getCurrentPages();
  if (!pages || pages.length === 0) {
    return;
  }

  const currentPage = pages[pages.length - 1];
  const currentRoute = currentPage.route;

  // 确保路径格式一致，移除开头的斜杠
  const normalizedRoute = currentRoute.startsWith('/') ? currentRoute.substring(1) : currentRoute;

  const index = tabList.findIndex(
    (item) => item.pagePath === normalizedRoute
  );

  if (index !== -1) {
    selected.value = index;
  }
};

// 开始状态检查
const startStatusCheck = () => {
  // 定期检查页面状态，确保tabbar状态正确
  statusCheckTimer = setInterval(() => {
    if (!isTabSwitching.value) {
      updateSelected();
    }
  }, 1000); // 每秒检查一次
};

// 停止状态检查
const stopStatusCheck = () => {
  if (statusCheckTimer) {
    clearInterval(statusCheckTimer);
    statusCheckTimer = null;
  }
};
</script>

<style lang="scss" scoped>
.custom-tab-bar {
  position: fixed;
  bottom: calc(32rpx + env(safe-area-inset-bottom));
  left: 32rpx;
  right: 32rpx;
  height: 120rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20rpx);
  border-radius: 32rpx;
  display: flex;
  z-index: 999;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.12),
              0 2rpx 8rpx rgba(0, 0, 0, 0.08),
              inset 0 1rpx 0 rgba(255, 255, 255, 0.8);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  animation: slideUpFloat 0.6s cubic-bezier(0.25, 1, 0.5, 1);

  // 悬浮发光效果
  &::before {
    content: '';
    position: absolute;
    top: -2rpx;
    left: -2rpx;
    right: -2rpx;
    bottom: -2rpx;
    background: linear-gradient(135deg,
      rgba(0, 122, 255, 0.1) 0%,
      rgba(255, 255, 255, 0.1) 50%,
      rgba(0, 122, 255, 0.1) 100%);
    border-radius: 34rpx;
    z-index: -1;
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  // 悬停时显示发光效果
  &:hover::before {
    opacity: 1;
  }
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20rpx 12rpx 16rpx;
  position: relative;
  transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);
  cursor: pointer;
  border-radius: 24rpx;
  margin: 8rpx 4rpx;

  // 点击缩放效果
  &:active {
    transform: scale(0.92);
  }

  // 激活状态
  &.active {
    background: linear-gradient(135deg,
      rgba(0, 122, 255, 0.08) 0%,
      rgba(0, 122, 255, 0.04) 100%);

    .tab-icon {
      transform: translateY(-2rpx);
    }

    .tab-text {
      color: #007aff;
      font-weight: 600;
      transform: translateY(-1rpx);
    }
  }

  // 点击波纹效果
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    background: radial-gradient(circle, rgba(0, 122, 255, 0.15) 0%, transparent 70%);
    border-radius: 50%;
    transform: translate(-50%, -50%);
    transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
    pointer-events: none;
    z-index: -1;
  }

  &:active::before {
    width: 120rpx;
    height: 120rpx;
  }

  .tab-icon {
    margin-bottom: 6rpx;
    width: 48rpx;
    height: 48rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);

    .icon-image {
      width: 44rpx;
      height: 44rpx;
      transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);

      &.icon-active {
        transform: scale(1.1);
        filter: drop-shadow(0 2rpx 8rpx rgba(0, 122, 255, 0.3));
      }
    }
  }

  .tab-text {
    font-size: 20rpx;
    color: #999999;
    font-weight: 400;
    transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);
    line-height: 1.2;
    letter-spacing: 0.5rpx;
  }
}

// 悬浮进入动画
@keyframes slideUpFloat {
  0% {
    transform: translateY(200rpx) scale(0.8);
    opacity: 0;
  }
  60% {
    transform: translateY(-8rpx) scale(1.02);
    opacity: 0.8;
  }
  100% {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

// 响应式适配
@media (max-width: 375px) {
  .custom-tab-bar {
    height: 110rpx;
    bottom: calc(24rpx + env(safe-area-inset-bottom));
    left: 24rpx;
    right: 24rpx;
    border-radius: 28rpx;
  }

  .tab-item {
    padding: 16rpx 8rpx 12rpx;
    margin: 6rpx 2rpx;
    border-radius: 20rpx;

    .tab-icon {
      width: 40rpx;
      height: 40rpx;
      margin-bottom: 4rpx;

      .icon-image {
        width: 36rpx;
        height: 36rpx;
      }
    }

    .tab-text {
      font-size: 18rpx;
    }
  }
}
</style>
