<template>
  <view class="order-list-container">
    <!-- 状态筛选 -->
    <scroll-view class="status-tabs" scroll-x>
      <view class="tab-list">
        <view
          class="tab-item"
          :class="{ active: selectedStatus === status.value }"
          v-for="status in statusTabs"
          :key="status.value"
          @click="selectStatus(status.value)"
        >
          <text class="tab-text">{{ status.label }}</text>
          <view class="tab-badge" v-if="status.count > 0">{{
            status.count
          }}</view>
        </view>
      </view>
    </scroll-view>

    <!-- 订单列表 -->
    <scroll-view
      class="order-scroll"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      @refresherrefresh="onRefresh"
      :refresher-triggered="isRefreshing"
    >
      <view class="order-list">
        <view
          class="order-card"
          v-for="order in orderList"
          :key="order.id"
          @click="goToOrderDetail(order.id)"
        >
          <!-- 订单头部 -->
          <view class="order-header">
            <text class="order-no">订单号：{{ order.orderNo }}</text>
            <view
              class="order-status"
              :style="{ color: getOrderStatusColor(order.orderStatus) }"
            >
              {{ getOrderStatusText(order.orderStatus) }}
            </view>
          </view>

          <!-- 服务信息 -->
          <view class="service-info">
            <view class="service-main">
              <text class="service-name">{{ order.serviceName }}</text>
              <text class="service-desc">{{ order.taskDescription }}</text>
            </view>
            <text class="service-price">¥{{ order.servicePrice }}</text>
          </view>

          <!-- 达人信息 -->
          <view class="talent-info" v-if="order.talentInfo">
            <image
              class="talent-avatar"
              :src="order.talentInfo.avatar"
              mode="aspectFill"
            ></image>
            <view class="talent-details">
              <text class="talent-name">{{ order.talentInfo.name }}</text>
              <view class="talent-rating">
                <text class="rating-star">⭐</text>
                <text class="rating-score">{{ order.talentInfo.rating }}</text>
              </view>
            </view>
            <text
              class="contact-btn"
              @click.stop="contactTalent(order.talentInfo)"
              >联系</text
            >
          </view>
          <view class="no-talent" v-else>
            <text class="waiting-text">等待达人接单...</text>
          </view>

          <!-- 时间信息 -->
          <view class="time-info">
            <text class="create-time"
              >下单时间：{{ formatTime(order.createTime) }}</text
            >
            <text class="expected-time" v-if="order.expectedTime">
              期望完成：{{ formatTime(order.expectedTime) }}
            </text>
          </view>

          <!-- 操作按钮 -->
          <view class="order-actions">
            <button
              class="action-btn secondary"
              v-if="canCancel(order)"
              @click.stop="cancelOrder(order)"
            >
              取消订单
            </button>
            <button
              class="action-btn secondary"
              v-if="canContact(order)"
              @click.stop="contactTalent(order.talentInfo)"
            >
              联系达人
            </button>
            <button
              class="action-btn primary"
              v-if="canPay(order)"
              @click.stop="payOrder(order)"
            >
              立即支付
            </button>
            <button
              class="action-btn primary"
              v-if="canConfirm(order)"
              @click.stop="confirmOrder(order)"
            >
              确认完成
            </button>
            <button
              class="action-btn secondary"
              v-if="canReview(order)"
              @click.stop="reviewOrder(order)"
            >
              评价订单
            </button>
            <button
              class="action-btn secondary"
              v-if="canAfterSale(order)"
              @click.stop="applyAfterSale(order)"
            >
              申请售后
            </button>
          </view>
        </view>
      </view>

      <!-- 加载状态 -->
      <view class="load-more" v-if="hasMore">
        <text>{{ isLoading ? "加载中..." : "上拉加载更多" }}</text>
      </view>
      <view class="no-more" v-else-if="orderList.length > 0">
        <text>没有更多了</text>
      </view>
      <view class="empty" v-else-if="!isLoading">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无订单</text>
        <button class="go-browse-btn" @click="goBrowse">去逛逛</button>
      </view>
    </scroll-view>

    <!-- 底部导航栏 -->
    <Tabbar />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onShow, reactive, computed } from "vue";
import { useUserStore } from "@/store/user";
import {
  getUserOrders,
  getOrderStatusText,
  getOrderStatusColor,
  OrderStatus,
  PaymentStatus,
  type Order,
} from "@/api/order";
import { formatTime, showError, showConfirm } from "@/utils/index";
import Tabbar from "@/components/Tabbar.vue";

// 状态
const userStore = useUserStore();
const orderList = ref<Order[]>([]);
const selectedStatus = ref<number | undefined>(undefined);
const isLoading = ref(false);
const isRefreshing = ref(false);
const hasMore = ref(true);

// 分页参数
const pageParams = reactive({
  current: 1,
  size: 10,
});

// 状态标签
const statusTabs = ref([
  { label: "全部", value: undefined, count: 0 },
  { label: "待接单", value: OrderStatus.PENDING, count: 0 },
  { label: "服务中", value: OrderStatus.IN_SERVICE, count: 0 },
  { label: "已完成", value: OrderStatus.COMPLETED, count: 0 },
  { label: "已取消", value: OrderStatus.CANCELLED, count: 0 },
]);

// 页面加载
onMounted(() => {
  loadOrderList();
});

// 加载订单列表
const loadOrderList = async (refresh = false) => {
  if (isLoading.value) return;

  try {
    isLoading.value = true;

    if (refresh) {
      pageParams.current = 1;
      hasMore.value = true;
    }

    const params = {
      current: pageParams.current,
      size: pageParams.size,
      status: selectedStatus.value,
    };

    const result = await getUserOrders(params);

    if (refresh) {
      orderList.value = result.records;
    } else {
      orderList.value.push(...result.records);
    }

    hasMore.value = result.current < result.pages;
    pageParams.current++;

    // 更新状态计数（这里应该从接口获取）
    updateStatusCounts();
  } catch (error) {
    console.error("加载订单列表失败:", error);
    showError("加载失败");
  } finally {
    isLoading.value = false;
    isRefreshing.value = false;
  }
};

// 更新状态计数
const updateStatusCounts = () => {
  // TODO: 从接口获取各状态的订单数量
  // 这里暂时用本地计算
  const counts = {
    [OrderStatus.PENDING]: 0,
    [OrderStatus.IN_SERVICE]: 0,
    [OrderStatus.COMPLETED]: 0,
    [OrderStatus.CANCELLED]: 0,
  };

  orderList.value.forEach((order) => {
    counts[order.orderStatus] = (counts[order.orderStatus] || 0) + 1;
  });

  statusTabs.value.forEach((tab) => {
    if (tab.value !== undefined) {
      tab.count = counts[tab.value] || 0;
    }
  });
};

// 选择状态
const selectStatus = (status: number | undefined) => {
  selectedStatus.value = status;
  loadOrderList(true);
};

// 下拉刷新
const onRefresh = () => {
  isRefreshing.value = true;
  loadOrderList(true);
};

// 加载更多
const loadMore = () => {
  if (hasMore.value && !isLoading.value) {
    loadOrderList();
  }
};

// 跳转订单详情
const goToOrderDetail = (orderId: number) => {
  uni.navigateTo({
    url: `/pages/order/detail?id=${orderId}`,
  });
};

// 判断是否可以取消
const canCancel = (order: Order): boolean => {
  return (
    order.orderStatus === OrderStatus.PENDING &&
    order.paymentStatus === PaymentStatus.UNPAID
  );
};

// 判断是否可以支付
const canPay = (order: Order): boolean => {
  return order.paymentStatus === PaymentStatus.UNPAID;
};

// 判断是否可以联系
const canContact = (order: Order): boolean => {
  return order.orderStatus === OrderStatus.IN_SERVICE && order.talentInfo;
};

// 判断是否可以确认完成
const canConfirm = (order: Order): boolean => {
  return order.orderStatus === OrderStatus.IN_SERVICE;
};

// 判断是否可以评价
const canReview = (order: Order): boolean => {
  return order.orderStatus === OrderStatus.COMPLETED;
};

// 判断是否可以申请售后
const canAfterSale = (order: Order): boolean => {
  return order.orderStatus === OrderStatus.COMPLETED;
};

// 取消订单
const cancelOrder = async (order: Order) => {
  const confirmed = await showConfirm({
    content: "确定要取消这个订单吗？",
  });

  if (confirmed) {
    // TODO: 调用取消订单接口
    console.log("取消订单:", order.id);
  }
};

// 支付订单
const payOrder = (order: Order) => {
  // TODO: 跳转支付页面
  console.log("支付订单:", order.id);
};

// 联系达人
const contactTalent = (talentInfo: any) => {
  // TODO: 打开聊天或拨打电话
  console.log("联系达人:", talentInfo);
};

// 确认完成
const confirmOrder = async (order: Order) => {
  const confirmed = await showConfirm({
    content: "确认服务已完成？",
  });

  if (confirmed) {
    // TODO: 调用确认完成接口
    console.log("确认完成:", order.id);
  }
};

// 评价订单
const reviewOrder = (order: Order) => {
  uni.navigateTo({
    url: `/pages/order/review?id=${order.id}`,
  });
};

// 申请售后
const applyAfterSale = (order: Order) => {
  // TODO: 跳转售后申请页面
  console.log("申请售后:", order.id);
};

// 去逛逛
const goBrowse = () => {
  uni.switchTab({
    url: "/pages/expert/list",
  });
};
</script>

<style lang="scss" scoped>
.order-list-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: $bg-color-page;
}

.status-tabs {
  background-color: $bg-color-white;
  border-bottom: 1rpx solid $border-color-light;

  .tab-list {
    display: flex;
    padding: $spacing-base;
    white-space: nowrap;

    .tab-item {
      position: relative;
      padding: $spacing-sm $spacing-lg;
      margin-right: $spacing-base;
      border-radius: $border-radius-xl;
      background-color: $bg-color-gray;
      white-space: nowrap;

      &.active {
        background-color: $primary-color;

        .tab-text {
          color: $text-color-white;
        }
      }

      .tab-text {
        font-size: $font-size-base;
        color: $text-color-secondary;
      }

      .tab-badge {
        position: absolute;
        top: -8rpx;
        right: -8rpx;
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
}

.order-scroll {
  flex: 1;
  padding-bottom: calc(100rpx + env(safe-area-inset-bottom));
}

.order-list {
  padding: $spacing-base;
}

.order-card {
  background-color: $bg-color-white;
  border-radius: $border-radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-base;
  box-shadow: $box-shadow-sm;

  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-base;

    .order-no {
      font-size: $font-size-base;
      color: $text-color-secondary;
    }

    .order-status {
      font-size: $font-size-base;
      font-weight: 500;
    }
  }

  .service-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: $spacing-base;

    .service-main {
      flex: 1;
      margin-right: $spacing-base;

      .service-name {
        display: block;
        font-size: $font-size-lg;
        font-weight: 500;
        color: $text-color-primary;
        margin-bottom: $spacing-xs;
      }

      .service-desc {
        display: block;
        font-size: $font-size-base;
        color: $text-color-secondary;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .service-price {
      font-size: $font-size-xl;
      font-weight: bold;
      color: $primary-color;
    }
  }

  .talent-info {
    display: flex;
    align-items: center;
    padding: $spacing-base;
    background-color: $bg-color-gray;
    border-radius: $border-radius-base;
    margin-bottom: $spacing-base;

    .talent-avatar {
      width: 80rpx;
      height: 80rpx;
      border-radius: 40rpx;
      margin-right: $spacing-base;
    }

    .talent-details {
      flex: 1;

      .talent-name {
        display: block;
        font-size: $font-size-base;
        font-weight: 500;
        color: $text-color-primary;
        margin-bottom: $spacing-xs;
      }

      .talent-rating {
        display: flex;
        align-items: center;

        .rating-star {
          font-size: $font-size-sm;
          margin-right: 4rpx;
        }

        .rating-score {
          font-size: $font-size-sm;
          color: $secondary-color;
        }
      }
    }

    .contact-btn {
      padding: $spacing-sm $spacing-base;
      background-color: $primary-color;
      color: $text-color-white;
      border-radius: $border-radius-base;
      font-size: $font-size-sm;
    }
  }

  .no-talent {
    text-align: center;
    padding: $spacing-lg;
    background-color: $bg-color-gray;
    border-radius: $border-radius-base;
    margin-bottom: $spacing-base;

    .waiting-text {
      font-size: $font-size-base;
      color: $text-color-placeholder;
    }
  }

  .time-info {
    margin-bottom: $spacing-base;

    .create-time,
    .expected-time {
      display: block;
      font-size: $font-size-sm;
      color: $text-color-placeholder;
      margin-bottom: $spacing-xs;
    }
  }

  .order-actions {
    display: flex;
    justify-content: flex-end;
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

.load-more,
.no-more,
.empty {
  text-align: center;
  padding: $spacing-lg;
  color: $text-color-placeholder;
  font-size: $font-size-base;
}

.empty {
  .empty-icon {
    display: block;
    font-size: 120rpx;
    margin-bottom: $spacing-base;
  }

  .empty-text {
    display: block;
    margin-bottom: $spacing-lg;
  }

  .go-browse-btn {
    width: 200rpx;
    height: 64rpx;
    background-color: $primary-color;
    color: $text-color-white;
    border-radius: $border-radius-base;
    font-size: $font-size-base;
  }
}
</style>
