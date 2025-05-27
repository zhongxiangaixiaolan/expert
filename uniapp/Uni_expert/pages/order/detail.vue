<template>
  <view class="order-detail-container">
    <scroll-view class="detail-scroll" scroll-y v-if="orderInfo">
      <!-- 订单状态 -->
      <view class="status-section">
        <view class="status-header">
          <image
            class="status-icon"
            :src="getStatusIcon(orderInfo.orderStatus)"
          ></image>
          <view class="status-info">
            <text class="status-text">{{
              getOrderStatusText(orderInfo.orderStatus)
            }}</text>
            <text class="status-desc">{{
              getStatusDescription(orderInfo.orderStatus)
            }}</text>
          </view>
        </view>

        <!-- 进度条 -->
        <view class="progress-bar">
          <view
            class="progress-step"
            :class="{ active: step <= currentStep }"
            v-for="step in 4"
            :key="step"
          >
            <view class="step-dot"></view>
            <text class="step-text">{{ getStepText(step) }}</text>
          </view>
        </view>
      </view>

      <!-- 服务信息 -->
      <view class="service-section">
        <view class="section-title">服务信息</view>
        <view class="service-card">
          <view class="service-header">
            <text class="service-name">{{ orderInfo.serviceName }}</text>
            <text class="service-price">¥{{ orderInfo.servicePrice }}</text>
          </view>
          <text class="task-desc">{{ orderInfo.taskDescription }}</text>
          <view class="service-meta">
            <text class="order-no">订单号：{{ orderInfo.orderNo }}</text>
            <text class="create-time"
              >下单时间：{{ formatTime(orderInfo.createTime) }}</text
            >
            <text class="expected-time" v-if="orderInfo.expectedTime">
              期望完成：{{ formatTime(orderInfo.expectedTime) }}
            </text>
          </view>
        </view>
      </view>

      <!-- 达人信息 -->
      <view class="talent-section" v-if="orderInfo.talentInfo">
        <view class="section-title">服务达人</view>
        <view class="talent-card" @click="goToTalentDetail">
          <image
            class="talent-avatar"
            :src="orderInfo.talentInfo.avatar"
            mode="aspectFill"
          ></image>
          <view class="talent-info">
            <text class="talent-name">{{ orderInfo.talentInfo.name }}</text>
            <view class="talent-rating">
              <text class="rating-star">⭐</text>
              <text class="rating-score">{{
                orderInfo.talentInfo.rating
              }}</text>
            </view>
            <text class="talent-phone" v-if="orderInfo.talentInfo.phone">
              {{ formatPhone(orderInfo.talentInfo.phone) }}
            </text>
          </view>
          <view class="contact-actions">
            <view class="contact-btn" @click.stop="callTalent">
              <image class="contact-icon" src="/static/icons/phone.svg"></image>
            </view>
            <view class="contact-btn" @click.stop="messageTalent">
              <image
                class="contact-icon"
                src="/static/icons/message.svg"
              ></image>
            </view>
          </view>
        </view>
      </view>
      <view class="no-talent-section" v-else>
        <view class="section-title">等待达人接单</view>
        <view class="waiting-card">
          <image class="waiting-icon" src="/static/status/loading.svg"></image>
          <text class="waiting-text">正在为您匹配合适的达人...</text>
          <text class="waiting-tip">预计等待时间：30分钟内</text>
        </view>
      </view>

      <!-- 订单进展 -->
      <view class="timeline-section">
        <view class="section-title">订单进展</view>
        <view class="timeline-list">
          <view
            class="timeline-item"
            v-for="(item, index) in timelineList"
            :key="index"
          >
            <view
              class="timeline-dot"
              :class="{ active: item.status === 'completed' }"
            ></view>
            <view class="timeline-content">
              <text class="timeline-title">{{ item.title }}</text>
              <text class="timeline-time">{{ formatTime(item.time) }}</text>
              <text class="timeline-desc" v-if="item.description">{{
                item.description
              }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 完成截图 -->
      <view
        class="screenshots-section"
        v-if="
          orderInfo.completionScreenshots &&
          orderInfo.completionScreenshots.length > 0
        "
      >
        <view class="section-title">完成截图</view>
        <view class="screenshots-grid">
          <image
            class="screenshot-item"
            v-for="(img, index) in orderInfo.completionScreenshots"
            :key="index"
            :src="img"
            mode="aspectFill"
            @click="previewImage(orderInfo.completionScreenshots, index)"
          ></image>
        </view>
      </view>

      <!-- 评价信息 -->
      <view class="review-section" v-if="orderInfo.reviewInfo">
        <view class="section-title">我的评价</view>
        <view class="review-card">
          <view class="review-rating">
            <text
              class="star"
              v-for="n in 5"
              :key="n"
              :class="{ filled: n <= orderInfo.reviewInfo.rating }"
            >
              ⭐
            </text>
            <text class="rating-text">{{ orderInfo.reviewInfo.rating }}分</text>
          </view>
          <text class="review-content">{{ orderInfo.reviewInfo.content }}</text>
          <text class="review-time">{{
            formatTime(orderInfo.reviewInfo.createTime)
          }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 加载状态 -->
    <view class="loading" v-else-if="isLoading">
      <image class="loading-icon" src="/static/status/loading.svg"></image>
      <text>加载中...</text>
    </view>

    <!-- 错误状态 -->
    <view class="error" v-else>
      <image class="error-icon" src="/static/status/error.svg"></image>
      <text class="error-text">加载失败</text>
      <button class="retry-btn" @click="loadOrderDetail">重试</button>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar" v-if="orderInfo">
      <button
        class="action-btn secondary"
        v-if="canCancel"
        @click="cancelOrder"
      >
        取消订单
      </button>
      <button class="action-btn primary" v-if="canPay" @click="payOrder">
        立即支付
      </button>
      <button
        class="action-btn primary"
        v-if="canConfirm"
        @click="confirmOrder"
      >
        确认完成
      </button>
      <button
        class="action-btn secondary"
        v-if="canReview"
        @click="reviewOrder"
      >
        评价订单
      </button>
      <button
        class="action-btn secondary"
        v-if="canAfterSale"
        @click="applyAfterSale"
      >
        申请售后
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import {
  getOrderDetail,
  getOrderStatusText,
  getOrderStatusColor,
  OrderStatus,
  PaymentStatus,
  type Order,
} from "@/api/order";
import {
  formatTime,
  formatPhone,
  showError,
  showConfirm,
  previewImage,
} from "@/utils/index";

// 获取页面参数
const pages = getCurrentPages();
const currentPage = pages[pages.length - 1];
const options = currentPage.options || {};

// 状态
const orderInfo = ref<Order | null>(null);
const timelineList = ref([]);
const isLoading = ref(false);

// 计算属性
const currentStep = computed(() => {
  if (!orderInfo.value) return 1;

  switch (orderInfo.value.orderStatus) {
    case OrderStatus.PENDING:
      return 1;
    case OrderStatus.IN_SERVICE:
      return 2;
    case OrderStatus.COMPLETED:
      return 4;
    case OrderStatus.CANCELLED:
      return 1;
    default:
      return 1;
  }
});

const canCancel = computed(() => {
  return (
    orderInfo.value?.orderStatus === OrderStatus.PENDING &&
    orderInfo.value?.paymentStatus === PaymentStatus.UNPAID
  );
});

const canPay = computed(() => {
  return orderInfo.value?.paymentStatus === PaymentStatus.UNPAID;
});

const canConfirm = computed(() => {
  return orderInfo.value?.orderStatus === OrderStatus.IN_SERVICE;
});

const canReview = computed(() => {
  return (
    orderInfo.value?.orderStatus === OrderStatus.COMPLETED &&
    !orderInfo.value?.reviewInfo
  );
});

const canAfterSale = computed(() => {
  return orderInfo.value?.orderStatus === OrderStatus.COMPLETED;
});

// 页面加载
onMounted(() => {
  const id = options.id;
  if (id) {
    loadOrderDetail(Number(id));
  } else {
    showError("缺少订单ID");
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }
});

// 加载订单详情
const loadOrderDetail = async (id?: number) => {
  const orderId = id || Number(options.id);
  if (!orderId) return;

  try {
    isLoading.value = true;
    const result = await getOrderDetail(orderId);
    orderInfo.value = result;

    // 构建时间线
    buildTimeline(result);
  } catch (error) {
    console.error("加载订单详情失败:", error);
    showError("加载失败");
  } finally {
    isLoading.value = false;
  }
};

// 构建时间线
const buildTimeline = (order: Order) => {
  const timeline = [];

  // 下单
  timeline.push({
    title: "订单创建",
    time: order.createTime,
    status: "completed",
    description: "订单已创建，等待达人接单",
  });

  // 接单
  if (order.acceptTime) {
    timeline.push({
      title: "达人接单",
      time: order.acceptTime,
      status: "completed",
      description: `${order.talentInfo?.name} 已接受您的订单`,
    });
  }

  // 完成
  if (order.completeTime) {
    timeline.push({
      title: "服务完成",
      time: order.completeTime,
      status: "completed",
      description: "达人已完成服务，请确认",
    });
  }

  // 取消
  if (order.cancelTime) {
    timeline.push({
      title: "订单取消",
      time: order.cancelTime,
      status: "cancelled",
      description: "订单已取消",
    });
  }

  timelineList.value = timeline.reverse();
};

// 获取状态图标
const getStatusIcon = (status: OrderStatus) => {
  const iconMap = {
    [OrderStatus.PENDING]: "/static/status/loading.svg",
    [OrderStatus.IN_SERVICE]: "/static/icons/check.svg",
    [OrderStatus.COMPLETED]: "/static/status/success.svg",
    [OrderStatus.CANCELLED]: "/static/status/error.svg",
  };
  return iconMap[status] || "/static/status/loading.svg";
};

// 获取状态描述
const getStatusDescription = (status: OrderStatus) => {
  const descMap = {
    [OrderStatus.PENDING]: "等待达人接单中，请耐心等待",
    [OrderStatus.IN_SERVICE]: "达人正在为您提供服务",
    [OrderStatus.COMPLETED]: "服务已完成，感谢您的使用",
    [OrderStatus.CANCELLED]: "订单已取消",
  };
  return descMap[status] || "";
};

// 获取步骤文本
const getStepText = (step: number) => {
  const stepMap = {
    1: "下单",
    2: "接单",
    3: "服务",
    4: "完成",
  };
  return stepMap[step] || "";
};

// 跳转达人详情
const goToTalentDetail = () => {
  if (orderInfo.value?.talentInfo) {
    uni.navigateTo({
      url: `/pages/expert/detail?id=${orderInfo.value.talentInfo.id}`,
    });
  }
};

// 拨打电话
const callTalent = () => {
  if (orderInfo.value?.talentInfo?.phone) {
    uni.makePhoneCall({
      phoneNumber: orderInfo.value.talentInfo.phone,
    });
  }
};

// 发送消息
const messageTalent = () => {
  // TODO: 打开聊天界面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 取消订单
const cancelOrder = async () => {
  const confirmed = await showConfirm({
    content: "确定要取消这个订单吗？",
  });

  if (confirmed) {
    // TODO: 调用取消订单接口
    console.log("取消订单");
  }
};

// 支付订单
const payOrder = () => {
  // TODO: 跳转支付页面
  console.log("支付订单");
};

// 确认完成
const confirmOrder = async () => {
  const confirmed = await showConfirm({
    content: "确认服务已完成？",
  });

  if (confirmed) {
    // TODO: 调用确认完成接口
    console.log("确认完成");
  }
};

// 评价订单
const reviewOrder = () => {
  uni.navigateTo({
    url: `/pages/order/review?id=${options.id}`,
  });
};

// 申请售后
const applyAfterSale = () => {
  // TODO: 跳转售后申请页面
  console.log("申请售后");
};
</script>

<style lang="scss" scoped>
.order-detail-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: $bg-color-page;
}

.detail-scroll {
  flex: 1;
  padding-bottom: 120rpx;
}

.status-section,
.service-section,
.talent-section,
.no-talent-section,
.timeline-section,
.screenshots-section,
.review-section {
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

.status-header {
  display: flex;
  align-items: center;
  margin-bottom: $spacing-lg;

  .status-icon {
    width: 80rpx;
    height: 80rpx;
    margin-right: $spacing-base;
  }

  .status-info {
    flex: 1;

    .status-text {
      display: block;
      font-size: $font-size-xl;
      font-weight: bold;
      color: $text-color-primary;
      margin-bottom: $spacing-xs;
    }

    .status-desc {
      display: block;
      font-size: $font-size-base;
      color: $text-color-secondary;
    }
  }
}

.progress-bar {
  display: flex;
  justify-content: space-between;
  position: relative;

  &::before {
    content: "";
    position: absolute;
    top: 20rpx;
    left: 40rpx;
    right: 40rpx;
    height: 4rpx;
    background-color: $border-color-light;
    z-index: $z-index-base;
  }

  .progress-step {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    z-index: 2;

    .step-dot {
      width: 40rpx;
      height: 40rpx;
      border-radius: 20rpx;
      background-color: $border-color-light;
      margin-bottom: $spacing-sm;
    }

    .step-text {
      font-size: $font-size-sm;
      color: $text-color-placeholder;
    }

    &.active {
      .step-dot {
        background-color: $primary-color;
      }

      .step-text {
        color: $primary-color;
      }
    }
  }
}

.service-card {
  .service-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-base;

    .service-name {
      font-size: $font-size-lg;
      font-weight: 500;
      color: $text-color-primary;
    }

    .service-price {
      font-size: $font-size-xl;
      font-weight: bold;
      color: $primary-color;
    }
  }

  .task-desc {
    font-size: $font-size-base;
    color: $text-color-secondary;
    line-height: 1.6;
    margin-bottom: $spacing-base;
  }

  .service-meta {
    .order-no,
    .create-time,
    .expected-time {
      display: block;
      font-size: $font-size-sm;
      color: $text-color-placeholder;
      margin-bottom: $spacing-xs;
    }
  }
}

.talent-card {
  display: flex;
  align-items: center;

  .talent-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    margin-right: $spacing-base;
  }

  .talent-info {
    flex: 1;

    .talent-name {
      display: block;
      font-size: $font-size-lg;
      font-weight: 500;
      color: $text-color-primary;
      margin-bottom: $spacing-xs;
    }

    .talent-rating {
      display: flex;
      align-items: center;
      margin-bottom: $spacing-xs;

      .rating-star {
        font-size: $font-size-base;
        margin-right: 4rpx;
      }

      .rating-score {
        font-size: $font-size-base;
        color: $secondary-color;
      }
    }

    .talent-phone {
      font-size: $font-size-sm;
      color: $text-color-secondary;
    }
  }

  .contact-actions {
    display: flex;
    gap: $spacing-base;

    .contact-btn {
      width: 80rpx;
      height: 80rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1rpx solid $border-color;
      border-radius: 40rpx;

      .contact-icon {
        width: 40rpx;
        height: 40rpx;
      }
    }
  }
}

.waiting-card {
  text-align: center;
  padding: $spacing-xl;

  .waiting-icon {
    width: 120rpx;
    height: 120rpx;
    margin-bottom: $spacing-base;
  }

  .waiting-text {
    display: block;
    font-size: $font-size-lg;
    color: $text-color-primary;
    margin-bottom: $spacing-sm;
  }

  .waiting-tip {
    display: block;
    font-size: $font-size-base;
    color: $text-color-secondary;
  }
}

.timeline-list {
  .timeline-item {
    display: flex;
    margin-bottom: $spacing-lg;

    &:last-child {
      margin-bottom: 0;
    }

    .timeline-dot {
      width: 24rpx;
      height: 24rpx;
      border-radius: 12rpx;
      background-color: $border-color-light;
      margin-right: $spacing-base;
      margin-top: 8rpx;
      flex-shrink: 0;

      &.active {
        background-color: $primary-color;
      }
    }

    .timeline-content {
      flex: 1;

      .timeline-title {
        display: block;
        font-size: $font-size-base;
        font-weight: 500;
        color: $text-color-primary;
        margin-bottom: $spacing-xs;
      }

      .timeline-time {
        display: block;
        font-size: $font-size-sm;
        color: $text-color-placeholder;
        margin-bottom: $spacing-xs;
      }

      .timeline-desc {
        display: block;
        font-size: $font-size-sm;
        color: $text-color-secondary;
      }
    }
  }
}

.screenshots-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-base;

  .screenshot-item {
    width: 100%;
    height: 200rpx;
    border-radius: $border-radius-base;
  }
}

.review-card {
  .review-rating {
    display: flex;
    align-items: center;
    margin-bottom: $spacing-base;

    .star {
      font-size: $font-size-lg;
      color: #ddd;
      margin-right: 4rpx;

      &.filled {
        color: $secondary-color;
      }
    }

    .rating-text {
      font-size: $font-size-base;
      color: $secondary-color;
      margin-left: $spacing-sm;
    }
  }

  .review-content {
    font-size: $font-size-base;
    color: $text-color-primary;
    line-height: 1.6;
    margin-bottom: $spacing-base;
  }

  .review-time {
    font-size: $font-size-sm;
    color: $text-color-placeholder;
  }
}

.loading,
.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
  color: $text-color-placeholder;
  font-size: $font-size-base;

  .loading-icon,
  .error-icon {
    width: 120rpx;
    height: 120rpx;
    margin-bottom: $spacing-base;
  }

  .retry-btn {
    width: 200rpx;
    height: 64rpx;
    background-color: $primary-color;
    color: $text-color-white;
    border-radius: $border-radius-base;
    font-size: $font-size-base;
    margin-top: $spacing-lg;
  }
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: $bg-color-white;
  padding: $spacing-lg;
  border-top: 1rpx solid $border-color-light;
  display: flex;
  gap: $spacing-base;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);

  .action-btn {
    flex: 1;
    height: 88rpx;
    border-radius: $border-radius-base;
    font-size: $font-size-lg;
    font-weight: 500;

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
</style>
