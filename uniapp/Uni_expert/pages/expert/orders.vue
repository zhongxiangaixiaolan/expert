<template>
  <view class="expert-orders-container">
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
      class="orders-scroll"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      @refresherrefresh="onRefresh"
      :refresher-triggered="isRefreshing"
    >
      <view class="orders-list">
        <view
          class="order-card"
          v-for="order in orderList"
          :key="order.id"
          @click="goToOrderDetail(order.id)"
        >
          <!-- 订单头部 -->
          <view class="order-header">
            <view class="order-info">
              <text class="order-title">{{ order.serviceName }}</text>
              <text class="order-no">订单号：{{ order.orderNo }}</text>
            </view>
            <view
              class="order-status"
              :style="{ color: getOrderStatusColor(order.orderStatus) }"
            >
              {{ getOrderStatusText(order.orderStatus) }}
            </view>
          </view>

          <!-- 客户信息 -->
          <view class="customer-info">
            <image
              class="customer-avatar"
              :src="
                order.userInfo?.avatar || '/static/images/default-avatar.svg'
              "
              mode="aspectFill"
            ></image>
            <view class="customer-details">
              <text class="customer-name">{{
                order.userInfo?.nickname || "匿名用户"
              }}</text>
              <text class="customer-phone" v-if="order.userInfo?.phone">{{
                formatPhone(order.userInfo.phone)
              }}</text>
            </view>
            <view class="contact-actions">
              <view
                class="contact-btn"
                @click.stop="callCustomer(order.userInfo)"
              >
                <image
                  class="contact-icon"
                  src="/static/icons/phone.svg"
                ></image>
              </view>
              <view
                class="contact-btn"
                @click.stop="messageCustomer(order.userInfo)"
              >
                <image
                  class="contact-icon"
                  src="/static/icons/message.svg"
                ></image>
              </view>
            </view>
          </view>

          <!-- 任务描述 -->
          <view class="task-description">
            <text class="task-text">{{ order.taskDescription }}</text>
          </view>

          <!-- 时间信息 -->
          <view class="time-info">
            <text class="create-time"
              >下单时间：{{ formatTime(order.createTime) }}</text
            >
            <text class="expected-time" v-if="order.expectedTime">
              期望完成：{{ formatTime(order.expectedTime) }}
            </text>
            <text class="accept-time" v-if="order.acceptTime">
              接单时间：{{ formatTime(order.acceptTime) }}
            </text>
          </view>

          <!-- 价格信息 -->
          <view class="price-info">
            <text class="price-label">服务费用：</text>
            <text class="price-value">¥{{ order.servicePrice }}</text>
          </view>

          <!-- 操作按钮 -->
          <view class="order-actions">
            <button
              class="action-btn secondary"
              v-if="canReject(order)"
              @click.stop="rejectOrder(order)"
            >
              拒绝
            </button>
            <button
              class="action-btn primary"
              v-if="canAccept(order)"
              @click.stop="acceptOrder(order)"
            >
              接单
            </button>
            <button
              class="action-btn primary"
              v-if="canStart(order)"
              @click.stop="startService(order)"
            >
              开始服务
            </button>
            <button
              class="action-btn primary"
              v-if="canComplete(order)"
              @click.stop="completeOrder(order)"
            >
              完成订单
            </button>
            <button
              class="action-btn secondary"
              v-if="canViewResult(order)"
              @click.stop="viewOrderResult(order)"
            >
              查看结果
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
        <image class="empty-icon" src="/static/status/empty.svg"></image>
        <text class="empty-text">暂无订单</text>
      </view>
    </scroll-view>

    <!-- 完成订单弹窗 -->
    <view
      class="complete-popup"
      v-if="showCompletePopup"
      @click="showCompletePopup = false"
    >
      <view class="popup-content" @click.stop>
        <view class="popup-header">
          <text class="popup-title">完成订单</text>
          <text class="popup-close" @click="showCompletePopup = false">✕</text>
        </view>

        <view class="popup-body">
          <view class="form-item">
            <text class="form-label">完成截图</text>
            <view class="upload-area">
              <view
                class="uploaded-images"
                v-if="completeData.screenshots.length > 0"
              >
                <view
                  class="image-item"
                  v-for="(img, index) in completeData.screenshots"
                  :key="index"
                >
                  <image
                    class="screenshot-image"
                    :src="img"
                    mode="aspectFill"
                  ></image>
                  <view class="image-remove" @click="removeScreenshot(index)">
                    <image
                      class="remove-icon"
                      src="/static/icons/close.svg"
                    ></image>
                  </view>
                </view>
              </view>
              <view
                class="upload-btn"
                @click="chooseScreenshots"
                v-if="completeData.screenshots.length < 6"
              >
                <image
                  class="upload-icon"
                  src="/static/icons/camera.svg"
                ></image>
                <text class="upload-text">添加截图</text>
              </view>
            </view>
          </view>

          <view class="form-item">
            <text class="form-label">完成说明</text>
            <textarea
              class="form-textarea"
              v-model="completeData.description"
              placeholder="请简要说明完成情况"
              maxlength="200"
            ></textarea>
          </view>
        </view>

        <view class="popup-actions">
          <button class="cancel-btn" @click="showCompletePopup = false">
            取消
          </button>
          <button class="confirm-btn" @click="submitComplete">确认完成</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue";
import {
  getExpertOrders,
  getOrderStatusText,
  getOrderStatusColor,
  OrderStatus,
  type Order,
} from "@/api/order";
import {
  formatTime,
  formatPhone,
  showError,
  showConfirm,
  chooseImage,
  requireAuth,
} from "@/utils/index";

// 状态
const orderList = ref<Order[]>([]);
const selectedStatus = ref<number | undefined>(undefined);
const isLoading = ref(false);
const isRefreshing = ref(false);
const hasMore = ref(true);
const showCompletePopup = ref(false);
const currentOrder = ref<Order | null>(null);

// 分页参数
const pageParams = reactive({
  current: 1,
  size: 10,
});

// 完成订单数据
const completeData = reactive({
  screenshots: [] as string[],
  description: "",
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
onMounted(async () => {
  const isAuthenticated = await requireAuth();
  if (!isAuthenticated) return;
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

    const params: any = {
      current: pageParams.current,
      size: pageParams.size,
    };

    // 只有当status不为undefined时才添加到参数中
    if (selectedStatus.value !== undefined) {
      params.status = selectedStatus.value;
    }

    const result = await getExpertOrders(params);

    if (refresh) {
      orderList.value = result.records;
    } else {
      orderList.value.push(...result.records);
    }

    hasMore.value = result.current < result.pages;
    pageParams.current++;

    // 更新状态计数
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

// 判断是否可以拒绝
const canReject = (order: Order): boolean => {
  return order.orderStatus === OrderStatus.PENDING;
};

// 判断是否可以接单
const canAccept = (order: Order): boolean => {
  return order.orderStatus === OrderStatus.PENDING;
};

// 判断是否可以开始服务
const canStart = (order: Order): boolean => {
  return order.orderStatus === OrderStatus.PENDING && order.acceptTime;
};

// 判断是否可以完成
const canComplete = (order: Order): boolean => {
  return order.orderStatus === OrderStatus.IN_SERVICE;
};

// 判断是否可以查看结果
const canViewResult = (order: Order): boolean => {
  return order.orderStatus === OrderStatus.COMPLETED;
};

// 拨打电话
const callCustomer = (userInfo: any) => {
  if (userInfo?.phone) {
    uni.makePhoneCall({
      phoneNumber: userInfo.phone,
    });
  }
};

// 发送消息
const messageCustomer = (userInfo: any) => {
  // TODO: 打开聊天界面
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 拒绝订单
const rejectOrder = async (order: Order) => {
  const confirmed = await showConfirm({
    content: `确定拒绝订单"${order.serviceName}"吗？`,
  });

  if (confirmed) {
    try {
      // TODO: 调用拒绝订单接口
      console.log("拒绝订单:", order.id);

      // 从列表中移除或更新状态
      const index = orderList.value.findIndex((item) => item.id === order.id);
      if (index > -1) {
        orderList.value.splice(index, 1);
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

// 接受订单
const acceptOrder = async (order: Order) => {
  const confirmed = await showConfirm({
    content: `确定接受订单"${order.serviceName}"吗？`,
  });

  if (confirmed) {
    try {
      // TODO: 调用接受订单接口
      console.log("接受订单:", order.id);

      // 更新订单状态
      const index = orderList.value.findIndex((item) => item.id === order.id);
      if (index > -1) {
        orderList.value[index].orderStatus = OrderStatus.IN_SERVICE;
        orderList.value[index].acceptTime = new Date().toISOString();
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

// 开始服务
const startService = async (order: Order) => {
  try {
    // TODO: 调用开始服务接口
    console.log("开始服务:", order.id);

    // 更新订单状态
    const index = orderList.value.findIndex((item) => item.id === order.id);
    if (index > -1) {
      orderList.value[index].orderStatus = OrderStatus.IN_SERVICE;
    }

    uni.showToast({
      title: "已开始服务",
      icon: "success",
    });
  } catch (error) {
    console.error("开始服务失败:", error);
    showError("操作失败");
  }
};

// 完成订单
const completeOrder = (order: Order) => {
  currentOrder.value = order;
  completeData.screenshots = [];
  completeData.description = "";
  showCompletePopup.value = true;
};

// 选择截图
const chooseScreenshots = async () => {
  try {
    const result = await chooseImage({
      count: 6 - completeData.screenshots.length,
      sizeType: ["compressed"],
      sourceType: ["album", "camera"],
    });

    if (result.tempFilePaths && result.tempFilePaths.length > 0) {
      // 上传截图
      for (const filePath of result.tempFilePaths) {
        await uploadScreenshot(filePath);
      }
    }
  } catch (error) {
    console.error("选择截图失败:", error);
  }
};

// 上传截图
const uploadScreenshot = async (filePath: string) => {
  try {
    uni.showLoading({ title: "上传中..." });

    // TODO: 调用截图上传接口
    // const result = await uploadCompletionScreenshot(filePath)

    // 临时处理：直接使用本地路径
    completeData.screenshots.push(filePath);

    uni.hideLoading();
  } catch (error) {
    console.error("上传截图失败:", error);
    uni.hideLoading();
    showError("上传失败");
  }
};

// 移除截图
const removeScreenshot = (index: number) => {
  completeData.screenshots.splice(index, 1);
};

// 提交完成
const submitComplete = async () => {
  if (!currentOrder.value) return;

  try {
    // TODO: 调用完成订单接口
    console.log("完成订单:", currentOrder.value.id, completeData);

    // 更新订单状态
    const index = orderList.value.findIndex(
      (item) => item.id === currentOrder.value!.id
    );
    if (index > -1) {
      orderList.value[index].orderStatus = OrderStatus.COMPLETED;
      orderList.value[index].completeTime = new Date().toISOString();
      orderList.value[index].completionScreenshots = [
        ...completeData.screenshots,
      ];
    }

    showCompletePopup.value = false;
    uni.showToast({
      title: "订单已完成",
      icon: "success",
    });
  } catch (error) {
    console.error("完成订单失败:", error);
    showError("操作失败");
  }
};

// 查看订单结果
const viewOrderResult = (order: Order) => {
  goToOrderDetail(order.id);
};
</script>

<style lang="scss" scoped>
.expert-orders-container {
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

.orders-scroll {
  flex: 1;
}

.orders-list {
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
    align-items: flex-start;
    margin-bottom: $spacing-base;

    .order-info {
      flex: 1;

      .order-title {
        display: block;
        font-size: $font-size-lg;
        font-weight: 500;
        color: $text-color-primary;
        margin-bottom: $spacing-xs;
      }

      .order-no {
        font-size: $font-size-sm;
        color: $text-color-placeholder;
      }
    }

    .order-status {
      font-size: $font-size-base;
      font-weight: 500;
    }
  }

  .customer-info {
    display: flex;
    align-items: center;
    padding: $spacing-base;
    background-color: $bg-color-gray;
    border-radius: $border-radius-base;
    margin-bottom: $spacing-base;

    .customer-avatar {
      width: 80rpx;
      height: 80rpx;
      border-radius: 40rpx;
      margin-right: $spacing-base;
    }

    .customer-details {
      flex: 1;

      .customer-name {
        display: block;
        font-size: $font-size-base;
        font-weight: 500;
        color: $text-color-primary;
        margin-bottom: $spacing-xs;
      }

      .customer-phone {
        font-size: $font-size-sm;
        color: $text-color-secondary;
      }
    }

    .contact-actions {
      display: flex;
      gap: $spacing-base;

      .contact-btn {
        width: 64rpx;
        height: 64rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1rpx solid $border-color;
        border-radius: 32rpx;

        .contact-icon {
          width: 32rpx;
          height: 32rpx;
        }
      }
    }
  }

  .task-description {
    margin-bottom: $spacing-base;

    .task-text {
      font-size: $font-size-base;
      color: $text-color-secondary;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }

  .time-info {
    margin-bottom: $spacing-base;

    .create-time,
    .expected-time,
    .accept-time {
      display: block;
      font-size: $font-size-sm;
      color: $text-color-placeholder;
      margin-bottom: $spacing-xs;
    }
  }

  .price-info {
    display: flex;
    align-items: center;
    margin-bottom: $spacing-base;

    .price-label {
      font-size: $font-size-base;
      color: $text-color-secondary;
    }

    .price-value {
      font-size: $font-size-lg;
      font-weight: bold;
      color: $primary-color;
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
    width: 120rpx;
    height: 120rpx;
    margin-bottom: $spacing-base;
  }
}

.complete-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: $bg-color-mask;
  z-index: $z-index-popup;
  display: flex;
  align-items: center;
  justify-content: center;

  .popup-content {
    background-color: $bg-color-white;
    border-radius: $border-radius-lg;
    margin: $spacing-lg;
    max-height: 80vh;
    width: calc(100% - #{$spacing-lg * 2});

    .popup-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $spacing-lg;
      border-bottom: 1rpx solid $border-color-light;

      .popup-title {
        font-size: $font-size-lg;
        font-weight: bold;
        color: $text-color-primary;
      }

      .popup-close {
        font-size: $font-size-xl;
        color: $text-color-placeholder;
      }
    }

    .popup-body {
      padding: $spacing-lg;
      max-height: 60vh;
      overflow-y: auto;

      .form-item {
        margin-bottom: $spacing-lg;

        .form-label {
          display: block;
          font-size: $font-size-base;
          color: $text-color-primary;
          margin-bottom: $spacing-sm;
        }

        .form-textarea {
          width: 100%;
          height: 120rpx;
          border: 1rpx solid $border-color;
          border-radius: $border-radius-base;
          padding: $spacing-base;
          font-size: $font-size-base;
          color: $text-color-primary;
          background-color: $bg-color-white;
          resize: none;
        }
      }

      .upload-area {
        display: flex;
        flex-wrap: wrap;
        gap: $spacing-base;

        .uploaded-images {
          display: flex;
          flex-wrap: wrap;
          gap: $spacing-base;
        }

        .image-item {
          position: relative;
          width: 120rpx;
          height: 120rpx;

          .screenshot-image {
            width: 100%;
            height: 100%;
            border-radius: $border-radius-base;
          }

          .image-remove {
            position: absolute;
            top: -12rpx;
            right: -12rpx;
            width: 40rpx;
            height: 40rpx;
            background-color: $error-color;
            border-radius: 20rpx;
            display: flex;
            align-items: center;
            justify-content: center;

            .remove-icon {
              width: 20rpx;
              height: 20rpx;
            }
          }
        }

        .upload-btn {
          width: 120rpx;
          height: 120rpx;
          border: 2rpx dashed $border-color;
          border-radius: $border-radius-base;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;

          .upload-icon {
            width: 32rpx;
            height: 32rpx;
            margin-bottom: $spacing-xs;
          }

          .upload-text {
            font-size: $font-size-xs;
            color: $text-color-secondary;
          }
        }
      }
    }

    .popup-actions {
      display: flex;
      gap: $spacing-base;
      padding: $spacing-lg;
      border-top: 1rpx solid $border-color-light;

      .cancel-btn,
      .confirm-btn {
        flex: 1;
        height: 80rpx;
        border-radius: $border-radius-base;
        font-size: $font-size-lg;
      }

      .cancel-btn {
        background-color: $bg-color-gray;
        color: $text-color-secondary;
      }

      .confirm-btn {
        background-color: $primary-color;
        color: $text-color-white;
      }
    }
  }
}
</style>
