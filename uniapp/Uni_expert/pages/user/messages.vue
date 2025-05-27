<template>
  <view class="messages-container">
    <!-- 顶部筛选栏 -->
    <view class="filter-bar">
      <view class="filter-tabs">
        <view
          class="tab-item"
          :class="{ active: activeTab === 'all' }"
          @click="switchTab('all')"
        >
          <text>全部</text>
          <view v-if="unreadCount.all > 0" class="badge">{{ unreadCount.all }}</view>
        </view>
        <view
          class="tab-item"
          :class="{ active: activeTab === 'system' }"
          @click="switchTab('system')"
        >
          <text>系统消息</text>
          <view v-if="unreadCount.system > 0" class="badge">{{ unreadCount.system }}</view>
        </view>
        <view
          class="tab-item"
          :class="{ active: activeTab === 'order' }"
          @click="switchTab('order')"
        >
          <text>订单消息</text>
          <view v-if="unreadCount.order > 0" class="badge">{{ unreadCount.order }}</view>
        </view>
        <view
          class="tab-item"
          :class="{ active: activeTab === 'service' }"
          @click="switchTab('service')"
        >
          <text>服务消息</text>
          <view v-if="unreadCount.service > 0" class="badge">{{ unreadCount.service }}</view>
        </view>
      </view>
      <view class="filter-actions">
        <text class="mark-all-read" @click="markAllAsRead">全部已读</text>
      </view>
    </view>

    <!-- 消息列表 -->
    <scroll-view class="messages-scroll" scroll-y @scrolltolower="loadMore">
      <view class="messages-list">
        <view
          class="message-item"
          v-for="item in messageList"
          :key="item.id"
          :class="{ unread: item.status === 0 }"
          @click="openMessage(item)"
        >
          <view class="message-content">
            <view class="message-icon">
              <image
                class="icon-image"
                :src="getMessageIcon(item.messageType)"
                mode="aspectFit"
              ></image>
              <view v-if="item.status === 0" class="unread-dot"></view>
            </view>
            <view class="message-info">
              <text class="message-title">{{ item.title }}</text>
              <text class="message-desc">{{ item.content }}</text>
              <view class="message-meta">
                <text class="message-time">{{ formatTime(item.sendTime) }}</text>
                <text class="message-type">{{ getMessageTypeText(item.messageType) }}</text>
              </view>
            </view>
            <view class="message-actions">
              <image
                v-if="item.status === 0"
                class="unread-icon"
                src="/static/icons/unread.svg"
              ></image>
              <image class="arrow-icon" src="/static/icons/arrow-right.svg"></image>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore" class="load-more">
        <text class="load-text">{{ loading ? '加载中...' : '上拉加载更多' }}</text>
      </view>

      <!-- 空状态 -->
      <view v-if="messageList.length === 0 && !loading" class="empty-state">
        <image class="empty-icon" src="/static/icons/message-empty.svg"></image>
        <text class="empty-text">暂无消息</text>
        <text class="empty-desc">您的消息将在这里显示</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { getMessageList, markMessageAsRead, markAllMessagesAsRead, getUnreadCount } from "@/api/message";
import { showError, showSuccess, requireAuth, formatTime } from "@/utils/index";

// 状态
const activeTab = ref("all");
const messageList = ref([]);
const unreadCount = ref({
  all: 0,
  system: 0,
  order: 0,
  service: 0
});
const loading = ref(false);
const hasMore = ref(true);
const currentPage = ref(1);
const pageSize = ref(10);

// 页面加载
onMounted(() => {
  if (!requireAuth()) return;
  loadUnreadCount();
  loadMessageList();
});

// 切换标签
const switchTab = (tab) => {
  if (activeTab.value === tab) return;
  
  activeTab.value = tab;
  currentPage.value = 1;
  hasMore.value = true;
  messageList.value = [];
  
  loadMessageList();
};

// 加载未读数量
const loadUnreadCount = async () => {
  try {
    const counts = await getUnreadCount();
    unreadCount.value = counts;
  } catch (error) {
    console.error("加载未读数量失败:", error);
  }
};

// 加载消息列表
const loadMessageList = async (isLoadMore = false) => {
  if (loading.value) return;
  
  try {
    loading.value = true;
    
    const params = {
      messageType: activeTab.value === "all" ? undefined : activeTab.value,
      current: isLoadMore ? currentPage.value + 1 : 1,
      size: pageSize.value
    };
    
    const result = await getMessageList(params);
    
    if (isLoadMore) {
      messageList.value.push(...result.records);
      currentPage.value++;
    } else {
      messageList.value = result.records;
      currentPage.value = 1;
    }
    
    hasMore.value = result.records.length === pageSize.value;
  } catch (error) {
    console.error("加载消息列表失败:", error);
    showError("加载失败");
  } finally {
    loading.value = false;
  }
};

// 加载更多
const loadMore = () => {
  if (hasMore.value && !loading.value) {
    loadMessageList(true);
  }
};

// 获取消息图标
const getMessageIcon = (messageType) => {
  const iconMap = {
    system: "/static/icons/system-message.svg",
    order: "/static/icons/order-message.svg",
    service: "/static/icons/service-message.svg",
    payment: "/static/icons/payment-message.svg",
    notification: "/static/icons/notification.svg"
  };
  return iconMap[messageType] || "/static/icons/message.svg";
};

// 获取消息类型文本
const getMessageTypeText = (messageType) => {
  const typeMap = {
    system: "系统消息",
    order: "订单消息",
    service: "服务消息",
    payment: "支付消息",
    notification: "通知消息"
  };
  return typeMap[messageType] || "消息";
};

// 打开消息
const openMessage = async (item) => {
  try {
    // 标记为已读
    if (item.status === 0) {
      await markMessageAsRead(item.id);
      item.status = 1;
      
      // 更新未读数量
      loadUnreadCount();
    }
    
    // 跳转到消息详情或相关页面
    if (item.businessType && item.businessId) {
      switch (item.businessType) {
        case "order":
          uni.navigateTo({
            url: `/pages/order/detail?id=${item.businessId}`
          });
          break;
        case "service":
          uni.navigateTo({
            url: `/pages/service/detail?id=${item.businessId}`
          });
          break;
        default:
          uni.navigateTo({
            url: `/pages/user/message-detail?id=${item.id}`
          });
          break;
      }
    } else {
      uni.navigateTo({
        url: `/pages/user/message-detail?id=${item.id}`
      });
    }
  } catch (error) {
    console.error("打开消息失败:", error);
    showError("操作失败");
  }
};

// 全部标记为已读
const markAllAsRead = async () => {
  try {
    const messageType = activeTab.value === "all" ? undefined : activeTab.value;
    await markAllMessagesAsRead(messageType);
    
    // 更新列表中的状态
    messageList.value.forEach(item => {
      item.status = 1;
    });
    
    // 更新未读数量
    loadUnreadCount();
    
    showSuccess("已全部标记为已读");
  } catch (error) {
    console.error("标记已读失败:", error);
    showError("操作失败");
  }
};
</script>

<style lang="scss" scoped>
.messages-container {
  min-height: 100vh;
  background-color: $bg-color-page;
}

.filter-bar {
  background-color: $bg-color-white;
  padding: $spacing-base;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1rpx solid $border-color-light;

  .filter-tabs {
    display: flex;
    gap: $spacing-sm;

    .tab-item {
      position: relative;
      padding: $spacing-sm $spacing-base;
      border-radius: $border-radius-lg;
      background-color: $bg-color-gray;
      display: flex;
      align-items: center;
      gap: $spacing-xs;

      &.active {
        background-color: $primary-color;
        color: $text-color-white;
      }

      text {
        font-size: $font-size-sm;
      }

      .badge {
        background-color: $error-color;
        color: $text-color-white;
        font-size: $font-size-xs;
        padding: 2rpx 8rpx;
        border-radius: 20rpx;
        min-width: 32rpx;
        text-align: center;
        line-height: 1.2;
      }
    }
  }

  .filter-actions {
    .mark-all-read {
      font-size: $font-size-sm;
      color: $primary-color;
    }
  }
}

.messages-scroll {
  height: calc(100vh - 120rpx);
  padding: 0 $spacing-base;
}

.messages-list {
  .message-item {
    background-color: $bg-color-white;
    border-radius: $border-radius-lg;
    margin-bottom: $spacing-base;
    overflow: hidden;
    box-shadow: $box-shadow-sm;

    &.unread {
      border-left: 6rpx solid $primary-color;
    }

    .message-content {
      display: flex;
      align-items: center;
      padding: $spacing-lg;

      .message-icon {
        position: relative;
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        background-color: $bg-color-gray;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: $spacing-base;

        .icon-image {
          width: 40rpx;
          height: 40rpx;
        }

        .unread-dot {
          position: absolute;
          top: 8rpx;
          right: 8rpx;
          width: 16rpx;
          height: 16rpx;
          background-color: $error-color;
          border-radius: 50%;
        }
      }

      .message-info {
        flex: 1;

        .message-title {
          display: block;
          font-size: $font-size-lg;
          font-weight: bold;
          color: $text-color-primary;
          margin-bottom: $spacing-xs;
        }

        .message-desc {
          display: block;
          font-size: $font-size-base;
          color: $text-color-secondary;
          margin-bottom: $spacing-base;
          line-height: 1.5;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .message-meta {
          display: flex;
          align-items: center;
          justify-content: space-between;

          .message-time {
            font-size: $font-size-sm;
            color: $text-color-placeholder;
          }

          .message-type {
            font-size: $font-size-xs;
            color: $primary-color;
            background-color: rgba(0, 122, 255, 0.1);
            padding: $spacing-xs $spacing-sm;
            border-radius: $border-radius-sm;
          }
        }
      }

      .message-actions {
        display: flex;
        align-items: center;
        gap: $spacing-sm;

        .unread-icon {
          width: 24rpx;
          height: 24rpx;
        }

        .arrow-icon {
          width: 24rpx;
          height: 24rpx;
        }
      }
    }
  }
}

.load-more {
  text-align: center;
  padding: $spacing-lg;

  .load-text {
    font-size: $font-size-sm;
    color: $text-color-placeholder;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $spacing-xl 0;

  .empty-icon {
    width: 120rpx;
    height: 120rpx;
    margin-bottom: $spacing-base;
    opacity: 0.5;
  }

  .empty-text {
    font-size: $font-size-lg;
    color: $text-color-placeholder;
    margin-bottom: $spacing-xs;
  }

  .empty-desc {
    font-size: $font-size-base;
    color: $text-color-placeholder;
  }
}
</style>
