<template>
  <view class="user-center-container">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-info" @click="goToProfile">
        <image
          class="user-avatar"
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
    <view class="quick-actions">
      <view class="action-item" @click="goToFavorites">
        <text class="action-icon">❤️</text>
        <text class="action-text">我的收藏</text>
      </view>
      <view class="action-item" @click="goToMessages">
        <text class="action-icon">💬</text>
        <text class="action-text">消息中心</text>
        <view class="message-badge" v-if="unreadCount > 0">{{
          unreadCount
        }}</view>
      </view>
      <view class="action-item" @click="goToCustomerService">
        <text class="action-icon">🎧</text>
        <text class="action-text">客服帮助</text>
      </view>
      <view class="action-item" @click="goToInvite">
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
@import '@/styles/common.scss';
@import '@/styles/components.scss';

.user-center-container {
  min-height: 100vh;
  background: linear-gradient(180deg, $primary-color 0%, $bg-color-page 40%);
  padding-bottom: 140rpx;
}

.user-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20rpx);
  border-radius: $border-radius-2xl;
  padding: $spacing-2xl $spacing-xl;
  margin: $spacing-lg;
  margin-bottom: $spacing-xl;
  box-shadow: $box-shadow-float;
  border: 1rpx solid rgba(255, 255, 255, 0.2);

  .user-info {
    @extend .flex, .items-center;
    margin-bottom: $spacing-xl;
    cursor: pointer;
    transition: all $transition-base;

    &:active {
      transform: scale(0.98);
    }

    .user-avatar {
      @extend .avatar, .avatar-xl;
      margin-right: $spacing-lg;
      border: 6rpx solid rgba(255, 255, 255, 0.3);
      box-shadow: $box-shadow-lg;
      transition: all $transition-base;

      &:active {
        transform: scale(1.05);
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
        background-color: rgba(255, 255, 255, 0.2);
        color: $text-color-white;
        border-radius: $border-radius-base;
        font-size: $font-size-base;
        backdrop-filter: blur(10px);

        &.secondary {
          background-color: transparent;
          border: 1rpx solid rgba(255, 255, 255, 0.3);
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
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;

  .action-item {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: $spacing-lg $spacing-base;

    .action-icon {
      font-size: 48rpx;
      margin-bottom: $spacing-sm;
    }

    .action-text {
      font-size: $font-size-sm;
      color: $text-color-secondary;
    }

    .message-badge {
      position: absolute;
      top: $spacing-base;
      right: $spacing-base;
      min-width: 32rpx;
      height: 32rpx;
      background-color: $secondary-color;
      color: $text-color-white;
      font-size: $font-size-xs;
      border-radius: 16rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 8rpx;
    }
  }
}

.expert-section {
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;

  .section-header {
    padding: $spacing-lg $spacing-lg $spacing-base;
    border-bottom: 1rpx solid $border-color-light;

    .section-title {
      font-size: $font-size-lg;
      font-weight: bold;
      color: $text-color-primary;
    }
  }

  .expert-actions {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    padding: $spacing-lg;

    .expert-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: $spacing-base;

      .expert-icon {
        font-size: 48rpx;
        margin-bottom: $spacing-sm;
      }

      .expert-text {
        font-size: $font-size-sm;
        color: $text-color-secondary;
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
    border-radius: $border-radius-lg;
    padding: $spacing-lg;
    display: flex;
    align-items: center;

    .become-expert-content {
      flex: 1;

      .become-expert-title {
        display: block;
        font-size: $font-size-lg;
        font-weight: bold;
        color: $text-color-white;
        margin-bottom: $spacing-xs;
      }

      .become-expert-desc {
        display: block;
        font-size: $font-size-base;
        color: rgba(255, 255, 255, 0.8);
      }
    }

    .become-expert-btn {
      padding: $spacing-sm $spacing-lg;
      background-color: rgba(255, 255, 255, 0.2);
      color: $text-color-white;
      border-radius: $border-radius-base;
      font-size: $font-size-base;
      backdrop-filter: blur(10px);
    }
  }
}

.menu-section {
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;

  .menu-item {
    display: flex;
    align-items: center;
    padding: $spacing-lg;
    border-bottom: 1rpx solid $border-color-light;

    &:last-child {
      border-bottom: none;
    }

    .menu-icon {
      font-size: $font-size-xl;
      margin-right: $spacing-base;
    }

    .menu-text {
      flex: 1;
      font-size: $font-size-base;
      color: $text-color-primary;
    }

    .menu-arrow {
      font-size: $font-size-xl;
      color: $text-color-placeholder;
    }
  }
}

.logout-section {
  padding: 0 $spacing-lg;

  .logout-btn {
    width: 100%;
    height: 88rpx;
    background-color: transparent;
    color: $secondary-color;
    border: 1rpx solid $secondary-color;
    border-radius: $border-radius-base;
    font-size: $font-size-lg;
  }
}
</style>
