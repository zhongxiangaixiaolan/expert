<template>
  <view class="user-center-container">
    <!-- 用户信息卡片 -->
    <view class="user-card animate-fade-in">
      <view class="user-info interactive" @click="goToProfile">
        <image
          class="user-avatar animate-float"
          :src="userStore.avatar"
          mode="aspectFill"
        ></image>
        <view class="user-details">
          <text class="user-name">{{ userStore.nickname }}</text>
          <text class="user-role">{{
            getRoleText(userStore.userInfo?.roleType)
          }}</text>
        </view>
        <text class="arrow-icon">›</text>
      </view>

      <!-- 余额信息 -->
      <view class="balance-section">
        <view class="balance-item" @click="goToTransactions">
          <text class="balance-amount"
            >¥{{ formatMoney(userStore.balance) }}</text
          >
          <text class="balance-label">账户余额</text>
        </view>
        <view class="balance-actions">
          <button class="balance-btn" @click="recharge">充值</button>
          <button class="balance-btn secondary" @click="withdraw">提现</button>
        </view>
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-actions animate-slide-up">
      <view class="action-item interactive-glow" @click="goToFavorites">
        <text class="action-icon">❤️</text>
        <text class="action-text">我的收藏</text>
      </view>
      <view class="action-item interactive-glow" @click="goToMessages">
        <text class="action-icon">💬</text>
        <text class="action-text">消息中心</text>
        <view class="message-badge animate-pulse" v-if="unreadCount > 0">{{
          unreadCount
        }}</view>
      </view>
      <view class="action-item interactive-glow" @click="goToCustomerService">
        <text class="action-icon">🎧</text>
        <text class="action-text">客服帮助</text>
      </view>
      <view class="action-item interactive-glow" @click="goToInvite">
        <text class="action-icon">🎁</text>
        <text class="action-text">邀请好友</text>
      </view>
    </view>

    <!-- 达人专区 -->
    <view class="expert-section" v-if="userStore.isExpert">
      <view class="section-header">
        <text class="section-title">达人专区</text>
      </view>
      <view class="expert-actions">
        <view class="expert-item" @click="goToWorkspace">
          <text class="expert-icon">💼</text>
          <text class="expert-text">工作台</text>
        </view>
        <view class="expert-item" @click="goToExpertOrders">
          <text class="expert-icon">📋</text>
          <text class="expert-text">接单管理</text>
        </view>
        <view class="expert-item" @click="goToExpertProfile">
          <text class="expert-icon">⚙️</text>
          <text class="expert-text">达人资料</text>
        </view>
        <view class="expert-item" @click="goToEarnings">
          <text class="expert-icon">💰</text>
          <text class="expert-text">收益统计</text>
        </view>
      </view>
    </view>

    <!-- 申请成为达人 -->
    <view class="become-expert" v-else>
      <view class="become-expert-card" @click="goToExpertApply">
        <view class="become-expert-content">
          <text class="become-expert-title">成为达人，开启赚钱之旅</text>
          <text class="become-expert-desc">分享你的技能，获得丰厚收益</text>
        </view>
        <text class="become-expert-btn">立即申请</text>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-item" @click="goToSettings">
        <text class="menu-icon">⚙️</text>
        <text class="menu-text">设置</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goToAbout">
        <text class="menu-icon">ℹ️</text>
        <text class="menu-text">关于我们</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goToFeedback">
        <text class="menu-icon">📝</text>
        <text class="menu-text">意见反馈</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>

    <!-- 底部导航栏 -->
    <Tabbar />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onShow } from "vue";
import { useUserStore } from "@/store/user";
import { formatMoney, showConfirm, showSuccess } from "@/utils/index";
import Tabbar from "@/components/Tabbar.vue";

// 状态
const userStore = useUserStore();
const unreadCount = ref(0);

// 页面加载
onMounted(() => {
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    uni.reLaunch({
      url: "/pages/auth/login",
    });
    return;
  }

  // 加载未读消息数量
  loadUnreadCount();
});

// 获取角色文本
const getRoleText = (roleType?: number): string => {
  const roleMap = {
    1: "普通用户",
    2: "服务达人",
    3: "管理员",
  };
  return roleMap[roleType || 1] || "普通用户";
};

// 加载未读消息数量
const loadUnreadCount = async () => {
  try {
    // TODO: 调用获取未读消息数量的接口
    // const count = await getUnreadMessageCount()
    // unreadCount.value = count
    unreadCount.value = 3; // 临时数据
  } catch (error) {
    console.error("加载未读消息数量失败:", error);
  }
};

// 跳转个人资料
const goToProfile = () => {
  uni.navigateTo({
    url: "/pages/user/profile",
  });
};

// 跳转交易记录
const goToTransactions = () => {
  uni.navigateTo({
    url: "/pages/user/transactions",
  });
};

// 充值
const recharge = () => {
  // TODO: 跳转充值页面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 提现
const withdraw = () => {
  // TODO: 跳转提现页面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 跳转收藏
const goToFavorites = () => {
  uni.navigateTo({
    url: "/pages/user/favorites",
  });
};

// 跳转消息中心
const goToMessages = () => {
  uni.navigateTo({
    url: "/pages/user/messages",
  });
};

// 客服帮助
const goToCustomerService = () => {
  // TODO: 打开客服聊天
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 邀请好友
const goToInvite = () => {
  // TODO: 跳转邀请页面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 达人工作台
const goToWorkspace = () => {
  uni.navigateTo({
    url: "/pages/expert/workspace",
  });
};

// 达人接单管理
const goToExpertOrders = () => {
  uni.navigateTo({
    url: "/pages/expert/orders",
  });
};

// 达人资料
const goToExpertProfile = () => {
  uni.navigateTo({
    url: "/pages/expert/profile",
  });
};

// 收益统计
const goToEarnings = () => {
  // TODO: 跳转收益统计页面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 申请成为达人
const goToExpertApply = () => {
  uni.navigateTo({
    url: "/pages/expert/apply",
  });
};

// 设置
const goToSettings = () => {
  // TODO: 跳转设置页面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 关于我们
const goToAbout = () => {
  uni.navigateTo({
    url: "/pages/common/webview?url=https://your-domain.com/about",
  });
};

// 意见反馈
const goToFeedback = () => {
  // TODO: 跳转意见反馈页面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 退出登录
const handleLogout = async () => {
  const confirmed = await showConfirm({
    content: "确定要退出登录吗？",
  });

  if (confirmed) {
    userStore.logout();
    showSuccess("已退出登录");

    setTimeout(() => {
      uni.reLaunch({
        url: "/pages/auth/login",
      });
    }, 1000);
  }
};
</script>

<style lang="scss" scoped>
@import "@/styles/common.scss";
@import "@/styles/components.scss";

.user-center-container {
  min-height: 100vh;
  background: linear-gradient(
    135deg,
    $primary-color 0%,
    $primary-light 50%,
    $bg-color-page 100%
  );
  padding-bottom: 140rpx;
  position: relative;

  // 添加动态背景效果
  &::before {
    content: "";
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 60vh;
    background: radial-gradient(
      ellipse at top,
      rgba(0, 122, 255, 0.3) 0%,
      transparent 70%
    );
    pointer-events: none;
    z-index: 0;
  }

  // 背景装饰圆圈
  &::after {
    content: "";
    position: fixed;
    top: -200rpx;
    right: -200rpx;
    width: 600rpx;
    height: 600rpx;
    background: radial-gradient(
      circle,
      rgba(255, 255, 255, 0.1) 0%,
      transparent 70%
    );
    border-radius: 50%;
    pointer-events: none;
    z-index: 0;
  }
}

.user-card {
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.25),
    rgba(255, 255, 255, 0.1)
  );
  -webkit-backdrop-filter: blur(24rpx);
  backdrop-filter: blur(24rpx);
  border-radius: $border-radius-2xl;
  padding: $spacing-2xl $spacing-xl;
  margin: $spacing-lg;
  margin-bottom: $spacing-xl;
  box-shadow: $box-shadow-glass, inset 0 1rpx 0 rgba(255, 255, 255, 0.4);
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  position: relative;
  z-index: 1;

  // 顶部高光效果
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1rpx;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.8),
      transparent
    );
    border-radius: $border-radius-2xl $border-radius-2xl 0 0;
  }

  .user-info {
    @extend .flex, .items-center;
    margin-bottom: $spacing-xl;
    cursor: pointer;
    transition: all $transition-base $ease-spring;
    border-radius: $border-radius-lg;
    padding: $spacing-sm;
    margin: -$spacing-sm -$spacing-sm $spacing-xl -$spacing-sm;

    &:active {
      transform: scale(0.98);
      background: rgba(255, 255, 255, 0.1);
    }

    .user-avatar {
      @extend .avatar, .avatar-xl;
      margin-right: $spacing-lg;
      border: 4rpx solid rgba(255, 255, 255, 0.4);
      box-shadow: $box-shadow-lg, 0 0 20rpx rgba(0, 122, 255, 0.2);
      transition: all $transition-base $ease-spring;
      position: relative;

      // 头像发光效果
      &::after {
        content: "";
        position: absolute;
        top: -4rpx;
        left: -4rpx;
        right: -4rpx;
        bottom: -4rpx;
        border-radius: $border-radius-full;
        background: linear-gradient(
          45deg,
          rgba(0, 122, 255, 0.3),
          rgba(255, 255, 255, 0.3)
        );
        z-index: -1;
        opacity: 0;
        transition: opacity $transition-base;
      }

      &:active {
        transform: scale(1.05);

        &::after {
          opacity: 1;
        }
      }
    }

    .user-details {
      flex: 1;

      .user-name {
        display: block;
        font-size: $font-size-xl;
        font-weight: $font-weight-bold;
        color: $text-color-white;
        margin-bottom: $spacing-xs;
        text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
      }

      .user-role {
        @extend .tag;
        background: rgba(255, 255, 255, 0.2);
        color: $text-color-white;
        font-size: $font-size-sm;
        padding: 6rpx $spacing-sm;
        border-radius: $border-radius-full;
        backdrop-filter: blur(10rpx);
      }
    }

    .arrow-icon {
      font-size: $font-size-xl;
      color: rgba(255, 255, 255, 0.8);
      transition: transform $transition-base;
    }

    &:active .arrow-icon {
      transform: translateX(4rpx);
    }
  }

  .balance-section {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .balance-item {
      .balance-amount {
        display: block;
        font-size: $font-size-xxl;
        font-weight: bold;
        color: $text-color-white;
        margin-bottom: $spacing-xs;
      }

      .balance-label {
        display: block;
        font-size: $font-size-base;
        color: rgba(255, 255, 255, 0.8);
      }
    }

    .balance-actions {
      display: flex;
      gap: $spacing-base;

      .balance-btn {
        height: 64rpx;
        padding: 0 $spacing-lg;
        background: linear-gradient(
          135deg,
          rgba(255, 255, 255, 0.3),
          rgba(255, 255, 255, 0.1)
        );
        color: $text-color-white;
        border-radius: $border-radius-xl;
        font-size: $font-size-base;
        font-weight: $font-weight-semibold;
        -webkit-backdrop-filter: blur(12rpx);
        backdrop-filter: blur(12rpx);
        border: 1rpx solid rgba(255, 255, 255, 0.3);
        box-shadow: inset 0 1rpx 0 rgba(255, 255, 255, 0.4);
        transition: all $transition-base $ease-spring;
        position: relative;
        overflow: hidden;

        // 按钮发光效果
        &::before {
          content: "";
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(
            90deg,
            transparent,
            rgba(255, 255, 255, 0.3),
            transparent
          );
          transition: left 0.5s ease;
        }

        &:active {
          transform: scale(0.95);
          background: linear-gradient(
            135deg,
            rgba(255, 255, 255, 0.2),
            rgba(255, 255, 255, 0.05)
          );

          &::before {
            left: 100%;
          }
        }

        &.secondary {
          background: rgba(255, 255, 255, 0.1);
          border: 1rpx solid rgba(255, 255, 255, 0.4);

          &:active {
            background: rgba(255, 255, 255, 0.05);
          }
        }
      }
    }
  }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-base;
  padding: $spacing-lg;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.9),
    rgba(255, 255, 255, 0.7)
  );
  -webkit-backdrop-filter: blur($blur-base);
  backdrop-filter: blur($blur-base);
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  border-radius: $border-radius-xl;
  margin: 0 $spacing-lg $spacing-base;
  box-shadow: $box-shadow-card, inset 0 1rpx 0 rgba(255, 255, 255, 0.6);
  position: relative;
  z-index: 1;

  .action-item {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: $spacing-lg $spacing-base;
    border-radius: $border-radius-lg;
    transition: all $transition-base $ease-spring;
    cursor: pointer;

    &:active {
      transform: scale(0.95);
      background: rgba(0, 122, 255, 0.1);
    }

    .action-icon {
      font-size: 48rpx;
      margin-bottom: $spacing-sm;
      transition: transform $transition-base $ease-spring;
      filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.1));
    }

    .action-text {
      font-size: $font-size-sm;
      color: $text-color-secondary;
      font-weight: $font-weight-medium;
      transition: color $transition-base;
    }

    &:active {
      .action-icon {
        transform: scale(1.1);
      }

      .action-text {
        color: $primary-color;
      }
    }

    .message-badge {
      position: absolute;
      top: $spacing-base;
      right: $spacing-base;
      min-width: 32rpx;
      height: 32rpx;
      background: linear-gradient(135deg, $secondary-color, $secondary-light);
      color: $text-color-white;
      font-size: $font-size-xs;
      font-weight: $font-weight-bold;
      border-radius: 16rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 8rpx;
      box-shadow: $box-shadow-sm;
      border: 2rpx solid rgba(255, 255, 255, 0.8);
      animation: pulse 2s infinite;
    }
  }
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.expert-section {
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.9),
    rgba(255, 255, 255, 0.7)
  );
  -webkit-backdrop-filter: blur($blur-base);
  backdrop-filter: blur($blur-base);
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  border-radius: $border-radius-xl;
  margin: 0 $spacing-lg $spacing-base;
  box-shadow: $box-shadow-card, inset 0 1rpx 0 rgba(255, 255, 255, 0.6);
  position: relative;
  z-index: 1;

  .section-header {
    padding: $spacing-lg $spacing-lg $spacing-base;
    border-bottom: 1rpx solid rgba(255, 255, 255, 0.3);
    position: relative;

    .section-title {
      font-size: $font-size-lg;
      font-weight: $font-weight-bold;
      color: $text-color-primary;
      position: relative;

      &::after {
        content: "";
        position: absolute;
        bottom: -$spacing-sm;
        left: 0;
        width: 60rpx;
        height: 4rpx;
        background: $primary-gradient;
        border-radius: 2rpx;
      }
    }
  }

  .expert-actions {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    padding: $spacing-lg;
    gap: $spacing-sm;

    .expert-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: $spacing-base;
      border-radius: $border-radius-lg;
      transition: all $transition-base $ease-spring;
      cursor: pointer;

      &:active {
        transform: scale(0.95);
        background: rgba(0, 122, 255, 0.1);
      }

      .expert-icon {
        font-size: 48rpx;
        margin-bottom: $spacing-sm;
        transition: transform $transition-base $ease-spring;
        filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.1));
      }

      .expert-text {
        font-size: $font-size-sm;
        color: $text-color-secondary;
        font-weight: $font-weight-medium;
        transition: color $transition-base;
      }

      &:active {
        .expert-icon {
          transform: scale(1.1);
        }

        .expert-text {
          color: $primary-color;
        }
      }
    }
  }
}

.become-expert {
  padding: 0 $spacing-lg;
  margin-bottom: $spacing-base;

  .become-expert-card {
    background: linear-gradient(
      135deg,
      $secondary-color 0%,
      $secondary-light 100%
    );
    border-radius: $border-radius-xl;
    padding: $spacing-xl;
    display: flex;
    align-items: center;
    box-shadow: $box-shadow-lg, inset 0 1rpx 0 rgba(255, 255, 255, 0.3);
    border: 1rpx solid rgba(255, 255, 255, 0.2);
    position: relative;
    overflow: hidden;
    transition: all $transition-base $ease-spring;

    // 背景装饰
    &::before {
      content: "";
      position: absolute;
      top: -50%;
      right: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(
        circle,
        rgba(255, 255, 255, 0.1) 0%,
        transparent 70%
      );
      animation: rotate 20s linear infinite;
    }

    &:active {
      transform: scale(0.98);
      box-shadow: $box-shadow-base;
    }

    .become-expert-content {
      flex: 1;
      position: relative;
      z-index: 1;

      .become-expert-title {
        display: block;
        font-size: $font-size-lg;
        font-weight: $font-weight-bold;
        color: $text-color-white;
        margin-bottom: $spacing-xs;
        text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
      }

      .become-expert-desc {
        display: block;
        font-size: $font-size-base;
        color: rgba(255, 255, 255, 0.9);
        text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.1);
      }
    }

    .become-expert-btn {
      padding: $spacing-sm $spacing-xl;
      background: linear-gradient(
        135deg,
        rgba(255, 255, 255, 0.3),
        rgba(255, 255, 255, 0.1)
      );
      color: $text-color-white;
      border-radius: $border-radius-xl;
      font-size: $font-size-base;
      font-weight: $font-weight-semibold;
      -webkit-backdrop-filter: blur(12rpx);
      backdrop-filter: blur(12rpx);
      border: 1rpx solid rgba(255, 255, 255, 0.4);
      box-shadow: inset 0 1rpx 0 rgba(255, 255, 255, 0.5);
      transition: all $transition-base $ease-spring;
      position: relative;
      z-index: 1;

      &:active {
        transform: scale(0.95);
        background: linear-gradient(
          135deg,
          rgba(255, 255, 255, 0.2),
          rgba(255, 255, 255, 0.05)
        );
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.menu-section {
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.9),
    rgba(255, 255, 255, 0.7)
  );
  -webkit-backdrop-filter: blur($blur-base);
  backdrop-filter: blur($blur-base);
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  border-radius: $border-radius-xl;
  margin: 0 $spacing-lg $spacing-base;
  box-shadow: $box-shadow-card, inset 0 1rpx 0 rgba(255, 255, 255, 0.6);
  position: relative;
  z-index: 1;
  overflow: hidden;

  .menu-item {
    display: flex;
    align-items: center;
    padding: $spacing-lg;
    border-bottom: 1rpx solid rgba(255, 255, 255, 0.3);
    transition: all $transition-base $ease-spring;
    cursor: pointer;
    position: relative;

    &:last-child {
      border-bottom: none;
    }

    &:active {
      background: rgba(0, 122, 255, 0.1);
      transform: scale(0.98);
    }

    .menu-icon {
      font-size: $font-size-xl;
      margin-right: $spacing-base;
      transition: transform $transition-base $ease-spring;
      filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.1));
    }

    .menu-text {
      flex: 1;
      font-size: $font-size-base;
      color: $text-color-primary;
      font-weight: $font-weight-medium;
      transition: color $transition-base;
    }

    .menu-arrow {
      font-size: $font-size-xl;
      color: $text-color-placeholder;
      transition: all $transition-base $ease-spring;
    }

    &:active {
      .menu-icon {
        transform: scale(1.1);
      }

      .menu-text {
        color: $primary-color;
      }

      .menu-arrow {
        color: $primary-color;
        transform: translateX(4rpx);
      }
    }
  }
}

.logout-section {
  padding: 0 $spacing-lg;

  .logout-btn {
    width: 100%;
    height: 88rpx;
    background: $bg-neumorphism;
    color: $secondary-color;
    border: none;
    border-radius: $border-radius-xl;
    font-size: $font-size-lg;
    font-weight: $font-weight-semibold;
    box-shadow: $box-shadow-neumorphism-sm;
    transition: all $transition-base $ease-spring;
    position: relative;
    overflow: hidden;

    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(
        90deg,
        transparent,
        rgba(255, 107, 53, 0.2),
        transparent
      );
      transition: left 0.5s ease;
    }

    &:active {
      box-shadow: $box-shadow-neumorphism-inset;
      transform: translateY(1rpx);

      &::before {
        left: 100%;
      }
    }
  }
}
</style>
