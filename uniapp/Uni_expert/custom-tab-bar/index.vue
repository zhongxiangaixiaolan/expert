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
        <TabIcon
          :name="item.iconName"
          :color="selected === index ? '#007aff' : '#999999'"
        />
      </view>
      <text class="tab-text" :class="{ active: selected === index }">{{
        item.text
      }}</text>
    </view>
  </view>
</template>

<script>
import TabIcon from "@/components/TabIcon/index.vue";

export default {
  components: {
    TabIcon,
  },
  data() {
    return {
      selected: 0,
      tabList: [
        {
          pagePath: "pages/index/index",
          text: "首页",
          iconName: "home",
        },
        {
          pagePath: "pages/expert/list",
          text: "找达人",
          iconName: "expert",
        },
        {
          pagePath: "pages/order/list",
          text: "订单",
          iconName: "order",
        },
        {
          pagePath: "pages/user/index",
          text: "我的",
          iconName: "user",
        },
      ],
    };
  },

  mounted() {
    this.updateSelected();
    this.setupGlobalFunction();
  },

  methods: {
    // 切换标签
    switchTab(index) {
      const item = this.tabList[index];
      this.selected = index;

      uni.switchTab({
        url: `/${item.pagePath}`,
      });
    },

    // 更新选中状态
    updateSelected() {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      const currentRoute = currentPage.route;

      const index = this.tabList.findIndex(
        (item) => item.pagePath === currentRoute
      );
      if (index !== -1) {
        this.selected = index;
      }
    },

    // 设置全局函数
    setupGlobalFunction() {
      const app = getApp();
      if (!app.globalData) {
        app.globalData = {};
      }

      // 设置全局函数供页面调用
      app.globalData.setTabBarIndex = (index) => {
        this.selected = index;
      };
    },
  },
};
</script>

<style lang="scss" scoped>
.custom-tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 1) 100%);
  backdrop-filter: blur(20rpx);
  border-top: 1rpx solid rgba(229, 229, 229, 0.3);
  display: flex;
  z-index: $z-index-fixed;
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.08);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1rpx;
    background: linear-gradient(90deg, transparent 0%, rgba(229, 229, 229, 0.5) 50%, transparent 100%);
  }
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16rpx 8rpx 8rpx;
  position: relative;
  transition: all $transition-base $ease-out-quart;
  cursor: pointer;

  &:active {
    transform: scale(0.95);
  }

  &.active {
    .tab-icon {
      transform: translateY(-4rpx) scale(1.1);

      &::after {
        opacity: 1;
        transform: scale(1);
      }
    }

    .tab-text {
      color: $primary-color;
      font-weight: $font-weight-semibold;
      transform: translateY(-2rpx);
    }
  }

  .tab-icon {
    margin-bottom: 8rpx;
    width: 48rpx;
    height: 48rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    transition: all $transition-base $ease-out-quart;

    &::after {
      content: '';
      position: absolute;
      top: -8rpx;
      left: -8rpx;
      right: -8rpx;
      bottom: -8rpx;
      background: linear-gradient(135deg, rgba($primary-color, 0.1) 0%, rgba($primary-color, 0.05) 100%);
      border-radius: $border-radius-full;
      opacity: 0;
      transform: scale(0.8);
      transition: all $transition-base $ease-out-quart;
      z-index: -1;
    }
  }

  .tab-text {
    font-size: 22rpx;
    color: $text-color-tertiary;
    font-weight: $font-weight-normal;
    transition: all $transition-base $ease-out-quart;
    line-height: 1.2;
  }

  // 添加点击波纹效果
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    background: radial-gradient(circle, rgba($primary-color, 0.2) 0%, transparent 70%);
    border-radius: 50%;
    transform: translate(-50%, -50%);
    transition: all 0.3s ease-out;
    pointer-events: none;
    z-index: -1;
  }

  &:active::before {
    width: 120rpx;
    height: 120rpx;
  }

  // 为每个tab添加独特的悬停效果
  &:nth-child(1).active .tab-icon::after {
    background: linear-gradient(135deg, rgba(#007aff, 0.15) 0%, rgba(#007aff, 0.08) 100%);
  }

  &:nth-child(2).active .tab-icon::after {
    background: linear-gradient(135deg, rgba(#ff6b35, 0.15) 0%, rgba(#ff6b35, 0.08) 100%);
  }

  &:nth-child(3).active .tab-icon::after {
    background: linear-gradient(135deg, rgba(#10b981, 0.15) 0%, rgba(#10b981, 0.08) 100%);
  }

  &:nth-child(4).active .tab-icon::after {
    background: linear-gradient(135deg, rgba(#f59e0b, 0.15) 0%, rgba(#f59e0b, 0.08) 100%);
  }
}

// 添加进入动画
.custom-tab-bar {
  animation: slideUp 0.3s $ease-out-quart;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

// 响应式适配
@media (max-width: 375px) {
  .custom-tab-bar {
    height: 110rpx;
  }

  .tab-item {
    padding: 12rpx 6rpx 6rpx;

    .tab-icon {
      width: 44rpx;
      height: 44rpx;
      margin-bottom: 6rpx;
    }

    .tab-text {
      font-size: 20rpx;
    }
  }
}
</style>
