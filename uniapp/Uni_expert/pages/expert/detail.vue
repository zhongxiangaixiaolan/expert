<template>
  <view class="expert-detail-container">
    <scroll-view class="detail-scroll" scroll-y v-if="expertInfo">
      <!-- 达人照片展示 - 移动到页面顶部 -->
      <view
        class="photos-hero-section"
        v-if="expertPhotos && expertPhotos.length > 0"
      >
        <view class="photos-carousel-wrapper">
          <ExpertPhotoCarousel3D
            :photos="expertPhotos"
            :auto-play="true"
            :interval="4000"
            :infinite-loop="true"
            :show-controls="true"
            :show-indicators="true"
            width="100%"
            height="600rpx"
            instance-id="expert-detail"
          />
        </view>
      </view>

      <!-- 达人基本信息 -->
      <view class="expert-header">
        <view class="expert-main">
          <image
            class="expert-avatar"
            :src="expertInfo.avatar"
            mode="aspectFill"
          ></image>
          <view class="expert-info">
            <text class="expert-name">{{ expertInfo.expertName }}</text>
            <view class="expert-rating">
              <view class="rating-stars">
                <text
                  class="star"
                  v-for="n in 5"
                  :key="`expert-rating-star-${n}`"
                  :class="{ filled: n <= Math.floor(expertInfo.rating) }"
                >
                  ⭐
                </text>
              </view>
              <text class="rating-score">{{ expertInfo.rating }}</text>
              <text class="rating-count"
                >({{ expertInfo.reviewCount || 0 }}评价)</text
              >
            </view>
            <view class="expert-meta">
              <text class="order-count">已接{{ expertInfo.orderCount }}单</text>
              <text class="response-time"
                >平均响应时间: {{ expertInfo.responseTime || "1小时" }}</text
              >
            </view>
          </view>
        </view>
        <view class="expert-actions">
          <view class="action-btn favorite" @click="toggleFavorite">
            <image
              class="action-icon"
              :src="
                isFavorite
                  ? '/static/icons/heart-filled.svg'
                  : '/static/icons/heart.svg'
              "
            ></image>
            <text class="action-text">{{
              isFavorite ? "已收藏" : "收藏"
            }}</text>
          </view>
          <view class="action-btn contact" @click="contactExpert">
            <image class="action-icon" src="/static/icons/message.svg"></image>
            <text class="action-text">联系</text>
          </view>
        </view>
      </view>

      <!-- 服务信息 -->
      <view class="service-section">
        <view class="section-title">服务信息</view>
        <view class="service-card">
          <view class="service-header">
            <text class="service-name">{{
              expertInfo.category?.name || "专业服务"
            }}</text>
            <text class="service-price">
              <template
                v-if="
                  expertInfo.priceMin &&
                  expertInfo.priceMax &&
                  expertInfo.priceMin !== expertInfo.priceMax
                "
              >
                ¥{{ expertInfo.priceMin }}-{{ expertInfo.priceMax }}
              </template>
              <template v-else-if="expertInfo.priceMin">
                ¥{{ expertInfo.priceMin }}起
              </template>
              <template v-else> 价格面议 </template>
            </text>
          </view>
          <text class="service-desc">{{ expertInfo.description }}</text>
          <view class="service-tags" v-if="expertInfo.tags">
            <text class="tag" v-for="tag in expertInfo.tags" :key="tag">{{
              tag
            }}</text>
          </view>
        </view>
      </view>

      <!-- 服务详情 -->
      <view class="detail-section" v-if="expertInfo.serviceDetail">
        <view class="section-title">服务详情</view>
        <view class="detail-content">
          <rich-text :nodes="expertInfo.serviceDetail"></rich-text>
        </view>
      </view>

      <!-- 作品展示 -->
      <view class="portfolio-section" v-if="portfolioList.length > 0">
        <view class="section-title">作品展示</view>
        <scroll-view class="portfolio-scroll" scroll-x>
          <view class="portfolio-list">
            <view
              class="portfolio-item"
              v-for="(item, index) in portfolioList"
              :key="`portfolio-item-${index}`"
              @click="previewPortfolio(index)"
            >
              <image
                class="portfolio-image"
                :src="item.image"
                mode="aspectFill"
              ></image>
              <text class="portfolio-title">{{ item.title }}</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 用户评价 -->
      <view class="review-section">
        <view class="section-header">
          <text class="section-title">用户评价</text>
          <text class="view-all" @click="viewAllReviews">查看全部</text>
        </view>
        <view class="review-list">
          <view
            class="review-item"
            v-for="review in reviewList"
            :key="review.id"
          >
            <view class="review-header">
              <image
                class="reviewer-avatar"
                :src="review.userAvatar"
                mode="aspectFill"
              ></image>
              <view class="reviewer-info">
                <text class="reviewer-name">{{ review.userName }}</text>
                <view class="review-rating">
                  <text
                    class="star"
                    v-for="n in 5"
                    :key="`review-${review.id}-star-${n}`"
                    :class="{ filled: n <= review.rating }"
                  >
                    ⭐
                  </text>
                </view>
              </view>
              <text class="review-time">{{
                formatTime(review.createTime)
              }}</text>
            </view>
            <text class="review-content">{{ review.content }}</text>
            <view
              class="review-images"
              v-if="review.images && review.images.length > 0"
            >
              <image
                class="review-image"
                v-for="(img, index) in review.images"
                :key="`review-${review.id}-image-${index}`"
                :src="img"
                mode="aspectFill"
                @click="previewImage(review.images, index)"
              ></image>
            </view>
          </view>
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
      <button class="retry-btn" @click="loadExpertDetail">重试</button>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar" v-if="expertInfo">
      <view class="price-info">
        <text class="price-label">服务价格</text>
        <text class="price-value">
          <template
            v-if="
              expertInfo.priceMin &&
              expertInfo.priceMax &&
              expertInfo.priceMin !== expertInfo.priceMax
            "
          >
            ¥{{ expertInfo.priceMin }}-{{ expertInfo.priceMax }}
          </template>
          <template v-else-if="expertInfo.priceMin">
            ¥{{ expertInfo.priceMin }}起
          </template>
          <template v-else> 价格面议 </template>
        </text>
      </view>
      <button class="order-btn" @click="createOrder">立即下单</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { getExpertDetail, getExpertPhotos, type Expert } from "@/api/home";
import {
  formatTime,
  showError,
  previewImage,
  requireAuth,
} from "@/utils/index";
import ExpertPhotoCarousel3D from "@/components/ExpertPhotoCarousel3D.vue";

// 页面参数
let pageOptions: any = {};

// 状态
const expertInfo = ref<Expert | null>(null);
const expertPhotos = ref([]);
const portfolioList = ref([]);
const reviewList = ref([]);
const isLoading = ref(false);
const isFavorite = ref(false);

// 页面加载时获取参数
onLoad((options) => {
  console.log("✅ onLoad获取到页面参数:", options);
  pageOptions = options || {};

  // 获取达人ID
  const id = pageOptions.id;
  console.log("达人ID:", id);

  if (id) {
    console.log("开始加载达人数据:", id);
    loadExpertDetail(Number(id));
    loadExpertPhotos(Number(id));
    loadPortfolio(Number(id));
    loadReviews(Number(id));
  } else {
    console.error("未获取到达人ID");
    showError("缺少达人ID");
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }
});

// 页面挂载完成
onMounted(() => {
  console.log("页面挂载完成");
});

// 加载达人详情
const loadExpertDetail = async (id?: number) => {
  const expertId = id || Number(pageOptions.id);
  if (!expertId) return;

  try {
    isLoading.value = true;
    console.log("开始加载达人详情：expertId=", expertId);
    const result = await getExpertDetail(expertId);
    console.log("达人详情加载成功：", result);
    expertInfo.value = result;

    // 检查收藏状态
    checkFavoriteStatus(expertId);
  } catch (error) {
    console.error("加载达人详情失败:", error);

    // 根据错误类型显示不同的错误信息
    let errorMessage = "加载失败";
    if (error && typeof error === "object") {
      if (error.message) {
        errorMessage = error.message;
      } else if (error.statusCode) {
        errorMessage = `网络错误 (${error.statusCode})`;
      }
    }

    showError(errorMessage);

    // 如果是网络错误，延迟后返回上一页
    if (error && error.message && error.message.includes("网络")) {
      setTimeout(() => {
        uni.navigateBack();
      }, 2000);
    }
  } finally {
    isLoading.value = false;
  }
};

// 加载达人照片
const loadExpertPhotos = async (expertId: number) => {
  try {
    console.log("开始加载达人照片：expertId=", expertId);
    const photos = await getExpertPhotos(expertId);
    console.log("达人照片加载成功：", photos);
    expertPhotos.value = Array.isArray(photos) ? photos : [];
  } catch (error) {
    console.error("加载达人照片失败:", error);
    expertPhotos.value = [];
  }
};

// 加载作品展示
const loadPortfolio = async (expertId: number) => {
  try {
    // TODO: 调用作品展示接口
    // const result = await getExpertPortfolio(expertId)
    // portfolioList.value = result

    // 临时数据
    portfolioList.value = [
      { title: "作品1", image: "/static/images/placeholder.svg" },
      { title: "作品2", image: "/static/images/placeholder.svg" },
      { title: "作品3", image: "/static/images/placeholder.svg" },
    ];
  } catch (error) {
    console.error("加载作品展示失败:", error);
  }
};

// 加载用户评价
const loadReviews = async (expertId: number) => {
  try {
    // TODO: 调用评价接口
    // const result = await getExpertReviews(expertId, { pageSize: 3 })
    // reviewList.value = result.records

    // 临时数据
    reviewList.value = [
      {
        id: 1,
        userName: "用户A",
        userAvatar: "/static/images/default-avatar.svg",
        rating: 5,
        content: "服务很专业，效率很高，推荐！",
        createTime: new Date().toISOString(),
        images: [],
      },
    ];
  } catch (error) {
    console.error("加载评价失败:", error);
  }
};

// 检查收藏状态
const checkFavoriteStatus = async (expertId: number) => {
  try {
    // TODO: 调用检查收藏状态接口
    // const result = await checkExpertFavorite(expertId)
    // isFavorite.value = result.isFavorite
    isFavorite.value = false;
  } catch (error) {
    console.error("检查收藏状态失败:", error);
  }
};

// 切换收藏
const toggleFavorite = async () => {
  if (!requireAuth()) return;

  try {
    // TODO: 调用收藏/取消收藏接口
    isFavorite.value = !isFavorite.value;
    uni.showToast({
      title: isFavorite.value ? "收藏成功" : "取消收藏",
      icon: "success",
    });
  } catch (error) {
    console.error("操作失败:", error);
    showError("操作失败");
  }
};

// 联系达人
const contactExpert = () => {
  if (!requireAuth()) return;

  // TODO: 打开聊天或联系方式
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};

// 预览作品
const previewPortfolio = (index: number) => {
  const images = portfolioList.value.map((item) => item.image);
  previewImage(images, index);
};

// 查看全部评价
const viewAllReviews = () => {
  uni.navigateTo({
    url: `/pages/expert/reviews?id=${pageOptions.id}`,
  });
};

// 创建订单
const createOrder = () => {
  if (!requireAuth()) return;

  uni.navigateTo({
    url: `/pages/order/create?expertId=${pageOptions.id}`,
  });
};
</script>

<style lang="scss" scoped>
.expert-detail-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: $bg-color-page;
}

.detail-scroll {
  flex: 1;
  padding-bottom: 120rpx;
}

/* 顶部照片轮播区域 - 英雄区域样式 */
.photos-hero-section {
  position: relative;
  width: 100%;
  height: 600rpx; /* 占屏幕高度约35-40% */
  margin-bottom: 0;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .photos-carousel-wrapper {
    width: 100%;
    height: 100%;
    position: relative;

    /* 移除原有的圆角和阴影，让轮播充满整个区域 */
    border-radius: 0;
    box-shadow: none;
    overflow: visible;
  }
}

.expert-header {
  background-color: $bg-color-white;
  padding: $spacing-lg;
  margin-bottom: $spacing-base;
  margin-top: 0; /* 紧贴照片轮播区域 */

  /* 添加轻微的上边距和圆角，营造层次感 */
  border-top-left-radius: 24rpx;
  border-top-right-radius: 24rpx;
  position: relative;
  z-index: 2;

  /* 添加轻微阴影，增强层次感 */
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.08);

  .expert-main {
    display: flex;
    margin-bottom: $spacing-lg;

    .expert-avatar {
      width: 160rpx;
      height: 160rpx;
      border-radius: 80rpx;
      margin-right: $spacing-lg;

      /* 增强头像视觉效果 */
      border: 4rpx solid #ffffff;
      box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);

      /* 添加轻微的上移效果，营造从照片区域"浮出"的感觉 */
      margin-top: -40rpx;
      position: relative;
      z-index: 3;
    }

    .expert-info {
      flex: 1;

      /* 调整信息区域的上边距，与头像上移效果协调 */
      margin-top: -20rpx;
      padding-top: 20rpx;

      .expert-name {
        display: block;
        font-size: $font-size-xl;
        font-weight: bold;
        color: $text-color-primary;
        margin-bottom: $spacing-sm;

        /* 增强名字的视觉效果 */
        font-size: 36rpx;
        letter-spacing: 1rpx;
      }

      .expert-rating {
        display: flex;
        align-items: center;
        margin-bottom: $spacing-sm;

        .rating-stars {
          margin-right: $spacing-sm;

          .star {
            font-size: $font-size-base;
            color: #ddd;

            &.filled {
              color: $secondary-color;
            }
          }
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

      .expert-meta {
        .order-count,
        .response-time {
          display: block;
          font-size: $font-size-sm;
          color: $text-color-secondary;
          margin-bottom: $spacing-xs;
        }
      }
    }
  }

  .expert-actions {
    display: flex;
    gap: $spacing-base;

    .action-btn {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: $spacing-base;
      border: 1rpx solid $border-color;
      border-radius: $border-radius-base;

      &.favorite.active {
        border-color: $secondary-color;
        background-color: rgba($secondary-color, 0.1);
      }

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

/* 移除原来的photos-section样式，现在使用photos-hero-section */
.service-section,
.detail-section,
.portfolio-section,
.review-section {
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;
  padding: $spacing-lg;
  border-radius: $border-radius-lg;

  /* 增强卡片阴影效果 */
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);

  /* 优化section间距 */
  margin-top: $spacing-lg;
}

.section-title {
  font-size: $font-size-lg;
  font-weight: bold;
  color: $text-color-primary;
  margin-bottom: $spacing-lg;

  /* 增强标题视觉效果 */
  position: relative;
  padding-left: 16rpx;

  &::before {
    content: "";
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 6rpx;
    height: 32rpx;
    background: linear-gradient(135deg, #007aff 0%, #5856d6 100%);
    border-radius: 3rpx;
  }
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
      color: #ff4757; // 红色，与列表页保持一致
    }
  }

  .service-desc {
    font-size: $font-size-base;
    color: $text-color-secondary;
    line-height: 1.6;
    margin-bottom: $spacing-base;
  }

  .service-tags {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-sm;

    .tag {
      padding: $spacing-xs $spacing-sm;
      background-color: $bg-color-gray;
      color: $text-color-secondary;
      font-size: $font-size-sm;
      border-radius: $border-radius-base;
    }
  }
}

.detail-content {
  font-size: $font-size-base;
  color: $text-color-primary;
  line-height: 1.6;
}

.portfolio-scroll {
  .portfolio-list {
    display: flex;
    gap: $spacing-base;

    .portfolio-item {
      width: 200rpx;

      .portfolio-image {
        width: 200rpx;
        height: 150rpx;
        border-radius: $border-radius-base;
        margin-bottom: $spacing-sm;
      }

      .portfolio-title {
        font-size: $font-size-sm;
        color: $text-color-secondary;
        text-align: center;
      }
    }
  }
}

.review-list {
  .review-item {
    margin-bottom: $spacing-lg;

    &:last-child {
      margin-bottom: 0;
    }

    .review-header {
      display: flex;
      align-items: center;
      margin-bottom: $spacing-base;

      .reviewer-avatar {
        width: 80rpx;
        height: 80rpx;
        border-radius: 40rpx;
        margin-right: $spacing-base;
      }

      .reviewer-info {
        flex: 1;

        .reviewer-name {
          display: block;
          font-size: $font-size-base;
          color: $text-color-primary;
          margin-bottom: $spacing-xs;
        }

        .review-rating {
          .star {
            font-size: $font-size-sm;
            color: #ddd;

            &.filled {
              color: $secondary-color;
            }
          }
        }
      }

      .review-time {
        font-size: $font-size-sm;
        color: $text-color-placeholder;
      }
    }

    .review-content {
      font-size: $font-size-base;
      color: $text-color-primary;
      line-height: 1.6;
      margin-bottom: $spacing-base;
    }

    .review-images {
      display: flex;
      gap: $spacing-sm;

      .review-image {
        width: 120rpx;
        height: 120rpx;
        border-radius: $border-radius-base;
      }
    }
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
  align-items: center;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);

  .price-info {
    flex: 1;

    .price-label {
      display: block;
      font-size: $font-size-sm;
      color: $text-color-secondary;
      margin-bottom: $spacing-xs;
    }

    .price-value {
      display: block;
      font-size: $font-size-xl;
      font-weight: bold;
      color: #ff4757; // 红色，与列表页保持一致
    }
  }

  .order-btn {
    width: 240rpx;
    height: 88rpx;
    background-color: $primary-color;
    color: $text-color-white;
    border-radius: $border-radius-base;
    font-size: $font-size-lg;
    font-weight: 500;
  }
}

/* 响应式设计优化 */
@media (max-width: 750rpx) {
  .photos-hero-section {
    height: 500rpx; /* 小屏幕适当减小高度 */
  }

  .expert-header {
    .expert-main {
      .expert-avatar {
        width: 140rpx;
        height: 140rpx;
        border-radius: 70rpx;
        margin-top: -30rpx; /* 调整上移距离 */
      }

      .expert-info {
        margin-top: -15rpx;

        .expert-name {
          font-size: 32rpx; /* 适当减小字体 */
        }
      }
    }
  }
}

@media (max-width: 600rpx) {
  .photos-hero-section {
    height: 450rpx; /* 更小屏幕进一步减小高度 */
  }

  .expert-header {
    padding: $spacing-base; /* 减小内边距 */

    .expert-main {
      .expert-avatar {
        width: 120rpx;
        height: 120rpx;
        border-radius: 60rpx;
        margin-top: -25rpx;
        margin-right: $spacing-base;
      }

      .expert-info {
        margin-top: -10rpx;

        .expert-name {
          font-size: 28rpx;
        }
      }
    }
  }

  .service-section,
  .detail-section,
  .portfolio-section,
  .review-section {
    padding: $spacing-base; /* 减小section内边距 */
    margin-top: $spacing-base;
  }
}
</style>
