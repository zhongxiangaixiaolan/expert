<template>
  <view class="workspace-container">
    <scroll-view class="workspace-scroll" scroll-y>
      <!-- 达人信息卡片 -->
      <view class="expert-card">
        <view class="expert-header">
          <image
            class="expert-avatar"
            :src="expertInfo?.avatar || '/static/images/default-avatar.svg'"
            mode="aspectFill"
          ></image>
          <view class="expert-info">
            <text class="expert-name">{{ expertInfo?.name || "未设置" }}</text>
            <view
              class="expert-status"
              :class="getStatusClass(expertInfo?.status)"
            >
              <text class="status-dot"></text>
              <text class="status-text">{{
                getStatusText(expertInfo?.status)
              }}</text>
            </view>
            <view class="expert-rating">
              <text class="rating-star">⭐</text>
              <text class="rating-score">{{ expertInfo?.rating || 0 }}</text>
              <text class="rating-count"
                >({{ expertInfo?.reviewCount || 0 }}评价)</text
              >
            </view>
          </view>
          <view class="expert-actions">
            <view class="action-btn" @click="goToProfile">
              <image class="action-icon" src="/static/icons/edit.svg"></image>
              <text class="action-text">编辑</text>
            </view>
          </view>
        </view>

        <!-- 服务开关 -->
        <view class="service-switch">
          <text class="switch-label">接单状态</text>
          <switch
            :checked="isServiceActive"
            @change="toggleServiceStatus"
            color="#007aff"
          />
        </view>
      </view>

      <!-- 数据统计 -->
      <view class="stats-section">
        <view class="section-title">今日数据</view>
        <view class="stats-grid">
          <view class="stat-item">
            <text class="stat-value">{{ todayStats.newOrders }}</text>
            <text class="stat-label">新订单</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">{{ todayStats.completedOrders }}</text>
            <text class="stat-label">已完成</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">¥{{ todayStats.earnings }}</text>
            <text class="stat-label">今日收益</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">{{ todayStats.responseTime }}min</text>
            <text class="stat-label">平均响应</text>
          </view>
        </view>
      </view>

      <!-- 快捷操作 -->
      <view class="quick-actions">
        <view class="section-title">快捷操作</view>
        <view class="actions-grid">
          <view class="action-item" @click="goToOrders">
            <image class="action-icon" src="/static/icons/order.svg"></image>
            <text class="action-text">订单管理</text>
            <view class="action-badge" v-if="pendingOrderCount > 0">{{
              pendingOrderCount
            }}</view>
          </view>
          <view class="action-item" @click="goToEarnings">
            <image class="action-icon" src="/static/icons/money.svg"></image>
            <text class="action-text">收益统计</text>
          </view>
          <view class="action-item" @click="goToSchedule">
            <image class="action-icon" src="/static/icons/calendar.svg"></image>
            <text class="action-text">工作安排</text>
          </view>
          <view class="action-item" @click="goToCustomers">
            <image class="action-icon" src="/static/icons/users.svg"></image>
            <text class="action-text">客户管理</text>
          </view>
        </view>
      </view>

      <!-- 待处理订单 -->
      <view class="pending-orders" v-if="pendingOrders.length > 0">
        <view class="section-header">
          <text class="section-title">待处理订单</text>
          <text class="view-all" @click="goToOrders">查看全部</text>
        </view>
        <view class="order-list">
          <view
            class="order-item"
            v-for="order in pendingOrders"
            :key="order.id"
            @click="goToOrderDetail(order.id)"
          >
            <view class="order-header">
              <text class="order-title">{{ order.serviceName }}</text>
              <text class="order-time">{{
                formatRelativeTime(order.createTime)
              }}</text>
            </view>
            <text class="order-desc">{{ order.taskDescription }}</text>
            <view class="order-footer">
              <text class="order-price">¥{{ order.servicePrice }}</text>
              <view class="order-actions">
                <button
                  class="action-btn secondary"
                  @click.stop="rejectOrder(order)"
                >
                  拒绝
                </button>
                <button
                  class="action-btn primary"
                  @click.stop="acceptOrder(order)"
                >
                  接单
                </button>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 最近消息 -->
      <view class="recent-messages">
        <view class="section-header">
          <text class="section-title">最近消息</text>
          <text class="view-all" @click="goToMessages">查看全部</text>
        </view>
        <view class="message-list" v-if="recentMessages.length > 0">
          <view
            class="message-item"
            v-for="message in recentMessages"
            :key="message.id"
            @click="goToChat(message.userId)"
          >
            <image
              class="message-avatar"
              :src="message.userAvatar"
              mode="aspectFill"
            ></image>
            <view class="message-content">
              <view class="message-header">
                <text class="message-name">{{ message.userName }}</text>
                <text class="message-time">{{
                  formatRelativeTime(message.createTime)
                }}</text>
              </view>
              <text class="message-text">{{ message.content }}</text>
            </view>
            <view class="message-badge" v-if="message.unreadCount > 0">{{
              message.unreadCount
            }}</view>
          </view>
        </view>
        <view class="empty-messages" v-else>
          <image class="empty-icon" src="/static/status/empty.svg"></image>
          <text class="empty-text">暂无消息</text>
        </view>
      </view>

      <!-- 服务提醒 -->
      <view class="service-tips">
        <view class="section-title">服务提醒</view>
        <view class="tips-list">
          <view class="tip-item">
            <image class="tip-icon" src="/static/icons/info.svg"></image>
            <text class="tip-text">及时回复客户消息，提高服务质量</text>
          </view>
          <view class="tip-item">
            <image class="tip-icon" src="/static/icons/warning.svg"></image>
            <text class="tip-text">完善个人资料，增加接单成功率</text>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useUserStore } from "@/store/user";
import {
  getExpertWorkspace,
  updateExpertStatus,
  ExpertStatus,
} from "@/api/expert";
import {
  formatRelativeTime,
  showError,
  showConfirm,
  requireAuth,
} from "@/utils/index";

// 状态
const userStore = useUserStore();
const expertInfo = ref(null);
const isServiceActive = ref(true);
const pendingOrders = ref([]);
const recentMessages = ref([]);

// 统计数据
const todayStats = ref({
  newOrders: 0,
  completedOrders: 0,
  earnings: 0,
  responseTime: 0,
});

// 计算属性
const pendingOrderCount = computed(() => {
  return pendingOrders.value.length;
});

// 页面加载
onMounted(() => {
  if (!requireAuth()) return;

  // 检查是否为达人
  if (!userStore.isExpert) {
    uni.showModal({
      title: "提示",
      content: "您还不是达人，是否申请成为达人？",
      success: (res) => {
        if (res.confirm) {
          uni.redirectTo({
            url: "/pages/expert/apply",
          });
        } else {
          uni.switchTab({
            url: "/pages/index/index",
          });
        }
      },
    });
    return;
  }

  loadWorkspaceData();
});

// 加载工作台数据
const loadWorkspaceData = async () => {
  try {
    const workspaceData = await getExpertWorkspace();

    // 设置达人基本信息
    expertInfo.value = {
      name: workspaceData.expertName,
      avatar: workspaceData.avatar,
      status:
        workspaceData.auditStatus === 1
          ? 1
          : workspaceData.auditStatus === 0
          ? 2
          : 3,
      rating: workspaceData.rating,
      reviewCount: workspaceData.orderCount,
    };

    // 设置服务状态
    isServiceActive.value = workspaceData.status === ExpertStatus.ONLINE;

    // 设置今日统计
    todayStats.value = workspaceData.todayStats;

    // 设置待处理订单数量
    pendingOrderCount.value = workspaceData.pendingOrderCount;

    // 加载其他数据
    await Promise.all([loadPendingOrders(), loadRecentMessages()]);
  } catch (error) {
    console.error("加载工作台数据失败:", error);
    showError("加载数据失败");
  }
};

// 加载达人信息
const loadExpertInfo = async () => {
  try {
    // TODO: 调用获取达人信息接口
    // const result = await getExpertInfo()
    // expertInfo.value = result

    // 临时数据
    expertInfo.value = {
      name: userStore.nickname,
      avatar: userStore.avatar,
      status: 1, // 1-正常 2-审核中 3-已拒绝
      rating: 4.8,
      reviewCount: 156,
    };
  } catch (error) {
    console.error("加载达人信息失败:", error);
  }
};

// 加载今日统计
const loadTodayStats = async () => {
  try {
    // TODO: 调用获取统计数据接口
    // const result = await getTodayStats()
    // todayStats.value = result

    // 临时数据
    todayStats.value = {
      newOrders: 3,
      completedOrders: 2,
      earnings: 580,
      responseTime: 15,
    };
  } catch (error) {
    console.error("加载统计数据失败:", error);
  }
};

// 加载待处理订单
const loadPendingOrders = async () => {
  try {
    // TODO: 调用获取待处理订单接口
    // const result = await getPendingOrders()
    // pendingOrders.value = result

    // 临时数据
    pendingOrders.value = [
      {
        id: 1,
        serviceName: "UI设计",
        taskDescription: "需要设计一个移动端APP界面",
        servicePrice: 300,
        createTime: new Date(Date.now() - 30 * 60 * 1000).toISOString(),
      },
    ];
  } catch (error) {
    console.error("加载待处理订单失败:", error);
  }
};

// 加载最近消息
const loadRecentMessages = async () => {
  try {
    // TODO: 调用获取最近消息接口
    // const result = await getRecentMessages()
    // recentMessages.value = result

    // 临时数据
    recentMessages.value = [
      {
        id: 1,
        userId: 1,
        userName: "张先生",
        userAvatar: "/static/images/default-avatar.svg",
        content: "您好，请问什么时候可以开始？",
        createTime: new Date(Date.now() - 10 * 60 * 1000).toISOString(),
        unreadCount: 2,
      },
    ];
  } catch (error) {
    console.error("加载最近消息失败:", error);
  }
};

// 获取状态样式类
const getStatusClass = (status) => {
  const classMap = {
    1: "active",
    2: "pending",
    3: "rejected",
  };
  return classMap[status] || "inactive";
};

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    1: "服务中",
    2: "审核中",
    3: "已拒绝",
  };
  return textMap[status] || "未激活";
};

// 切换服务状态
const toggleServiceStatus = async (e) => {
  const newStatus = e.detail.value;

  try {
    // 调用切换服务状态接口
    const status = newStatus ? ExpertStatus.ONLINE : ExpertStatus.OFFLINE;
    await updateExpertStatus(status);

    isServiceActive.value = newStatus;
    uni.showToast({
      title: newStatus ? "已开启接单" : "已关闭接单",
      icon: "success",
    });
  } catch (error) {
    console.error("切换服务状态失败:", error);
    showError(error.message || "操作失败");
    // 恢复原状态
    isServiceActive.value = !newStatus;
  }
};

// 跳转到个人资料
const goToProfile = () => {
  uni.navigateTo({
    url: "/pages/expert/profile",
  });
};

// 跳转到订单管理
const goToOrders = () => {
  uni.navigateTo({
    url: "/pages/expert/orders",
  });
};

// 跳转到收益统计
const goToEarnings = () => {
  // TODO: 跳转到收益统计页面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 跳转到工作安排
const goToSchedule = () => {
  // TODO: 跳转到工作安排页面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 跳转到客户管理
const goToCustomers = () => {
  // TODO: 跳转到客户管理页面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 跳转到订单详情
const goToOrderDetail = (orderId) => {
  uni.navigateTo({
    url: `/pages/order/detail?id=${orderId}`,
  });
};

// 跳转到消息列表
const goToMessages = () => {
  uni.navigateTo({
    url: "/pages/user/messages",
  });
};

// 跳转到聊天
const goToChat = (userId) => {
  // TODO: 跳转到聊天页面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 接受订单
const acceptOrder = async (order) => {
  const confirmed = await showConfirm({
    content: `确定接受订单"${order.serviceName}"吗？`,
  });

  if (confirmed) {
    try {
      // TODO: 调用接受订单接口
      // await acceptExpertOrder(order.id)

      // 从待处理列表中移除
      const index = pendingOrders.value.findIndex(
        (item) => item.id === order.id
      );
      if (index > -1) {
        pendingOrders.value.splice(index, 1);
      }

      uni.showToast({
        title: "接单成功",
        icon: "success",
      });
    } catch (error) {
      console.error("接单失败:", error);
      showError("接单失败");
    }
  }
};

// 拒绝订单
const rejectOrder = async (order) => {
  const confirmed = await showConfirm({
    content: `确定拒绝订单"${order.serviceName}"吗？`,
  });

  if (confirmed) {
    try {
      // TODO: 调用拒绝订单接口
      // await rejectExpertOrder(order.id)

      // 从待处理列表中移除
      const index = pendingOrders.value.findIndex(
        (item) => item.id === order.id
      );
      if (index > -1) {
        pendingOrders.value.splice(index, 1);
      }

      uni.showToast({
        title: "已拒绝订单",
        icon: "success",
      });
    } catch (error) {
      console.error("拒绝订单失败:", error);
      showError("操作失败");
    }
  }
};
</script>

<style lang="scss" scoped>
.workspace-container {
  min-height: 100vh;
  background-color: $bg-color-page;
}

.workspace-scroll {
  padding-bottom: 120rpx;
}

.expert-card,
.stats-section,
.quick-actions,
.pending-orders,
.recent-messages,
.service-tips {
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;
  padding: $spacing-lg;
}

.section-title {
  font-size: $font-size-lg;
  font-weight: bold;
  color: $text-color-primary;
  margin-bottom: $spacing-lg;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-lg;

  .view-all {
    font-size: $font-size-base;
    color: $primary-color;
  }
}

.expert-header {
  display: flex;
  align-items: center;
  margin-bottom: $spacing-lg;

  .expert-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    margin-right: $spacing-base;
  }

  .expert-info {
    flex: 1;

    .expert-name {
      display: block;
      font-size: $font-size-xl;
      font-weight: bold;
      color: $text-color-primary;
      margin-bottom: $spacing-xs;
    }

    .expert-status {
      display: flex;
      align-items: center;
      margin-bottom: $spacing-xs;

      .status-dot {
        width: 16rpx;
        height: 16rpx;
        border-radius: 8rpx;
        margin-right: $spacing-xs;
      }

      .status-text {
        font-size: $font-size-sm;
      }

      &.active {
        .status-dot {
          background-color: $success-color;
        }
        .status-text {
          color: $success-color;
        }
      }

      &.pending {
        .status-dot {
          background-color: $warning-color;
        }
        .status-text {
          color: $warning-color;
        }
      }

      &.rejected {
        .status-dot {
          background-color: $error-color;
        }
        .status-text {
          color: $error-color;
        }
      }
    }

    .expert-rating {
      display: flex;
      align-items: center;

      .rating-star {
        font-size: $font-size-base;
        margin-right: 4rpx;
      }

      .rating-score {
        font-size: $font-size-base;
        color: $secondary-color;
        font-weight: 500;
        margin-right: $spacing-sm;
      }

      .rating-count {
        font-size: $font-size-sm;
        color: $text-color-placeholder;
      }
    }
  }

  .expert-actions {
    .action-btn {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: $spacing-base;
      border: 1rpx solid $border-color;
      border-radius: $border-radius-base;

      .action-icon {
        width: 48rpx;
        height: 48rpx;
        margin-bottom: $spacing-xs;
      }

      .action-text {
        font-size: $font-size-sm;
        color: $text-color-secondary;
      }
    }
  }
}

.service-switch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $spacing-base 0;
  border-top: 1rpx solid $border-color-light;

  .switch-label {
    font-size: $font-size-base;
    color: $text-color-primary;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-base;

  .stat-item {
    text-align: center;

    .stat-value {
      display: block;
      font-size: $font-size-xl;
      font-weight: bold;
      color: $primary-color;
      margin-bottom: $spacing-xs;
    }

    .stat-label {
      display: block;
      font-size: $font-size-sm;
      color: $text-color-secondary;
    }
  }
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-base;

  .action-item {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: $spacing-lg $spacing-base;

    .action-icon {
      width: 64rpx;
      height: 64rpx;
      margin-bottom: $spacing-sm;
    }

    .action-text {
      font-size: $font-size-sm;
      color: $text-color-secondary;
    }

    .action-badge {
      position: absolute;
      top: $spacing-base;
      right: $spacing-base;
      min-width: 32rpx;
      height: 32rpx;
      background-color: $error-color;
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

.order-list {
  .order-item {
    padding: $spacing-lg;
    border: 1rpx solid $border-color-light;
    border-radius: $border-radius-base;
    margin-bottom: $spacing-base;

    &:last-child {
      margin-bottom: 0;
    }

    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-sm;

      .order-title {
        font-size: $font-size-lg;
        font-weight: 500;
        color: $text-color-primary;
      }

      .order-time {
        font-size: $font-size-sm;
        color: $text-color-placeholder;
      }
    }

    .order-desc {
      font-size: $font-size-base;
      color: $text-color-secondary;
      margin-bottom: $spacing-base;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .order-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .order-price {
        font-size: $font-size-lg;
        font-weight: bold;
        color: $primary-color;
      }

      .order-actions {
        display: flex;
        gap: $spacing-base;

        .action-btn {
          height: 64rpx;
          padding: 0 $spacing-lg;
          border-radius: $border-radius-base;
          font-size: $font-size-base;

          &.primary {
            background-color: $primary-color;
            color: $text-color-white;
          }

          &.secondary {
            background-color: transparent;
            color: $text-color-secondary;
            border: 1rpx solid $border-color;
          }
        }
      }
    }
  }
}

.message-list {
  .message-item {
    position: relative;
    display: flex;
    align-items: center;
    padding: $spacing-base 0;
    border-bottom: 1rpx solid $border-color-light;

    &:last-child {
      border-bottom: none;
    }

    .message-avatar {
      width: 80rpx;
      height: 80rpx;
      border-radius: 40rpx;
      margin-right: $spacing-base;
    }

    .message-content {
      flex: 1;

      .message-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: $spacing-xs;

        .message-name {
          font-size: $font-size-base;
          color: $text-color-primary;
        }

        .message-time {
          font-size: $font-size-sm;
          color: $text-color-placeholder;
        }
      }

      .message-text {
        font-size: $font-size-base;
        color: $text-color-secondary;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .message-badge {
      position: absolute;
      top: $spacing-base;
      right: 0;
      min-width: 32rpx;
      height: 32rpx;
      background-color: $error-color;
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

.empty-messages {
  text-align: center;
  padding: $spacing-xl;

  .empty-icon {
    width: 120rpx;
    height: 120rpx;
    margin-bottom: $spacing-base;
  }

  .empty-text {
    font-size: $font-size-base;
    color: $text-color-placeholder;
  }
}

.tips-list {
  .tip-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: $spacing-base;

    &:last-child {
      margin-bottom: 0;
    }

    .tip-icon {
      width: 32rpx;
      height: 32rpx;
      margin-right: $spacing-base;
      margin-top: 4rpx;
      flex-shrink: 0;
    }

    .tip-text {
      flex: 1;
      font-size: $font-size-base;
      color: $text-color-secondary;
      line-height: 1.5;
    }
  }
}
</style>
