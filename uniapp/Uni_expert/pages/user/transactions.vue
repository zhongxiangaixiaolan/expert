<template>
  <view class="transactions-container">
    <!-- 顶部筛选栏 -->
    <view class="filter-bar">
      <view class="filter-tabs">
        <view
          class="tab-item"
          :class="{ active: activeTab === 'all' }"
          @click="switchTab('all')"
        >
          <text>全部</text>
        </view>
        <view
          class="tab-item"
          :class="{ active: activeTab === 'recharge' }"
          @click="switchTab('recharge')"
        >
          <text>充值</text>
        </view>
        <view
          class="tab-item"
          :class="{ active: activeTab === 'consume' }"
          @click="switchTab('consume')"
        >
          <text>消费</text>
        </view>
        <view
          class="tab-item"
          :class="{ active: activeTab === 'withdraw' }"
          @click="switchTab('withdraw')"
        >
          <text>提现</text>
        </view>
      </view>
    </view>

    <!-- 统计信息 -->
    <view class="stats-section">
      <view class="stats-card">
        <view class="stats-item">
          <text class="stats-label">总充值</text>
          <text class="stats-value recharge">+¥{{ userStats.totalRecharge || 0 }}</text>
        </view>
        <view class="stats-item">
          <text class="stats-label">总消费</text>
          <text class="stats-value consume">-¥{{ userStats.totalConsume || 0 }}</text>
        </view>
        <view class="stats-item">
          <text class="stats-label">当前余额</text>
          <text class="stats-value balance">¥{{ userStats.balance || 0 }}</text>
        </view>
      </view>
    </view>

    <!-- 交易记录列表 -->
    <scroll-view class="transactions-scroll" scroll-y @scrolltolower="loadMore">
      <view class="transactions-list">
        <view
          class="transaction-item"
          v-for="item in transactionList"
          :key="item.id"
          @click="goToTransactionDetail(item)"
        >
          <view class="transaction-content">
            <view class="transaction-icon">
              <image
                class="icon-image"
                :src="getTransactionIcon(item.type)"
                mode="aspectFit"
              ></image>
            </view>
            <view class="transaction-info">
              <text class="transaction-title">{{ getTransactionTitle(item) }}</text>
              <text class="transaction-desc">{{ getTransactionDesc(item) }}</text>
              <text class="transaction-time">{{ formatTime(item.createTime) }}</text>
            </view>
            <view class="transaction-amount">
              <text
                class="amount-text"
                :class="getAmountClass(item.type)"
              >
                {{ getAmountText(item) }}
              </text>
              <view class="transaction-status">
                <text
                  class="status-text"
                  :class="getStatusClass(item.status)"
                >
                  {{ getStatusText(item.status) }}
                </text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore" class="load-more">
        <text class="load-text">{{ loading ? '加载中...' : '上拉加载更多' }}</text>
      </view>

      <!-- 空状态 -->
      <view v-if="transactionList.length === 0 && !loading" class="empty-state">
        <image class="empty-icon" src="/static/icons/transaction-empty.svg"></image>
        <text class="empty-text">暂无交易记录</text>
        <text class="empty-desc">您的交易记录将在这里显示</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { getTransactionList, getUserStats } from "@/api/transaction";
import { showError, requireAuth, formatTime } from "@/utils/index";

// 状态
const activeTab = ref("all");
const transactionList = ref([]);
const userStats = ref({});
const loading = ref(false);
const hasMore = ref(true);
const currentPage = ref(1);
const pageSize = ref(10);

// 页面加载
onMounted(() => {
  if (!requireAuth()) return;
  loadUserStats();
  loadTransactionList();
});

// 切换标签
const switchTab = (tab) => {
  if (activeTab.value === tab) return;
  
  activeTab.value = tab;
  currentPage.value = 1;
  hasMore.value = true;
  transactionList.value = [];
  
  loadTransactionList();
};

// 加载用户统计
const loadUserStats = async () => {
  try {
    const stats = await getUserStats();
    userStats.value = stats;
  } catch (error) {
    console.error("加载用户统计失败:", error);
  }
};

// 加载交易记录
const loadTransactionList = async (isLoadMore = false) => {
  if (loading.value) return;
  
  try {
    loading.value = true;
    
    const params = {
      type: activeTab.value === "all" ? undefined : activeTab.value,
      current: isLoadMore ? currentPage.value + 1 : 1,
      size: pageSize.value
    };
    
    const result = await getTransactionList(params);
    
    if (isLoadMore) {
      transactionList.value.push(...result.records);
      currentPage.value++;
    } else {
      transactionList.value = result.records;
      currentPage.value = 1;
    }
    
    hasMore.value = result.records.length === pageSize.value;
  } catch (error) {
    console.error("加载交易记录失败:", error);
    showError("加载失败");
  } finally {
    loading.value = false;
  }
};

// 加载更多
const loadMore = () => {
  if (hasMore.value && !loading.value) {
    loadTransactionList(true);
  }
};

// 获取交易图标
const getTransactionIcon = (type) => {
  const iconMap = {
    recharge: "/static/icons/recharge.svg",
    consume: "/static/icons/consume.svg",
    withdraw: "/static/icons/withdraw.svg",
    refund: "/static/icons/refund.svg"
  };
  return iconMap[type] || "/static/icons/transaction.svg";
};

// 获取交易标题
const getTransactionTitle = (item) => {
  const titleMap = {
    recharge: "账户充值",
    consume: "服务消费",
    withdraw: "余额提现",
    refund: "订单退款"
  };
  return titleMap[item.type] || "交易记录";
};

// 获取交易描述
const getTransactionDesc = (item) => {
  switch (item.type) {
    case "recharge":
      return `通过${item.paymentMethod || "微信"}充值`;
    case "consume":
      return `购买服务：${item.serviceName || "未知服务"}`;
    case "withdraw":
      return `提现到${item.withdrawAccount || "银行卡"}`;
    case "refund":
      return `订单退款：${item.orderNo || ""}`;
    default:
      return item.description || "";
  }
};

// 获取金额文本
const getAmountText = (item) => {
  const prefix = item.type === "recharge" || item.type === "refund" ? "+" : "-";
  return `${prefix}¥${item.amount}`;
};

// 获取金额样式类
const getAmountClass = (type) => {
  if (type === "recharge" || type === "refund") {
    return "income";
  }
  return "expense";
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: "处理中",
    1: "成功",
    2: "失败",
    3: "已取消"
  };
  return statusMap[status] || "未知";
};

// 获取状态样式类
const getStatusClass = (status) => {
  const classMap = {
    0: "pending",
    1: "success",
    2: "failed",
    3: "cancelled"
  };
  return classMap[status] || "pending";
};

// 跳转到交易详情
const goToTransactionDetail = (item) => {
  uni.navigateTo({
    url: `/pages/user/transaction-detail?id=${item.id}`
  });
};
</script>

<style lang="scss" scoped>
.transactions-container {
  min-height: 100vh;
  background-color: $bg-color-page;
}

.filter-bar {
  background-color: $bg-color-white;
  padding: $spacing-base;
  border-bottom: 1rpx solid $border-color-light;

  .filter-tabs {
    display: flex;
    gap: $spacing-base;

    .tab-item {
      padding: $spacing-sm $spacing-lg;
      border-radius: $border-radius-lg;
      background-color: $bg-color-gray;

      &.active {
        background-color: $primary-color;
        color: $text-color-white;
      }

      text {
        font-size: $font-size-base;
      }
    }
  }
}

.stats-section {
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;
  padding: $spacing-lg;

  .stats-card {
    display: flex;
    justify-content: space-around;

    .stats-item {
      text-align: center;

      .stats-label {
        display: block;
        font-size: $font-size-sm;
        color: $text-color-secondary;
        margin-bottom: $spacing-xs;
      }

      .stats-value {
        display: block;
        font-size: $font-size-lg;
        font-weight: bold;

        &.recharge {
          color: $success-color;
        }

        &.consume {
          color: $error-color;
        }

        &.balance {
          color: $primary-color;
        }
      }
    }
  }
}

.transactions-scroll {
  height: calc(100vh - 280rpx);
  padding: 0 $spacing-base;
}

.transactions-list {
  .transaction-item {
    background-color: $bg-color-white;
    border-radius: $border-radius-lg;
    margin-bottom: $spacing-base;
    overflow: hidden;
    box-shadow: $box-shadow-sm;

    .transaction-content {
      display: flex;
      align-items: center;
      padding: $spacing-lg;

      .transaction-icon {
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
      }

      .transaction-info {
        flex: 1;

        .transaction-title {
          display: block;
          font-size: $font-size-lg;
          font-weight: bold;
          color: $text-color-primary;
          margin-bottom: $spacing-xs;
        }

        .transaction-desc {
          display: block;
          font-size: $font-size-base;
          color: $text-color-secondary;
          margin-bottom: $spacing-xs;
        }

        .transaction-time {
          font-size: $font-size-sm;
          color: $text-color-placeholder;
        }
      }

      .transaction-amount {
        text-align: right;

        .amount-text {
          display: block;
          font-size: $font-size-lg;
          font-weight: bold;
          margin-bottom: $spacing-xs;

          &.income {
            color: $success-color;
          }

          &.expense {
            color: $error-color;
          }
        }

        .transaction-status {
          .status-text {
            font-size: $font-size-sm;
            padding: $spacing-xs $spacing-sm;
            border-radius: $border-radius-sm;

            &.pending {
              background-color: rgba(250, 173, 20, 0.1);
              color: $warning-color;
            }

            &.success {
              background-color: rgba(82, 196, 26, 0.1);
              color: $success-color;
            }

            &.failed {
              background-color: rgba(255, 77, 79, 0.1);
              color: $error-color;
            }

            &.cancelled {
              background-color: rgba(153, 153, 153, 0.1);
              color: $text-color-placeholder;
            }
          }
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
