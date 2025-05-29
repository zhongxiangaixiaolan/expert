<template>
  <view class="home-container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 错误状态 -->
    <view v-else-if="error" class="error-container">
      <text class="error-icon">⚠️</text>
      <text class="error-text">{{ error }}</text>
      <button class="retry-btn" @click="loadPageData">重试</button>
    </view>

    <!-- 正常内容 -->
    <view v-else>
      <!-- 轮播图 -->
      <view class="banner-section" v-if="bannerList && bannerList.length > 0">
        <swiper
          class="banner-swiper"
          :indicator-dots="true"
          :autoplay="true"
          :interval="3000"
          :duration="500"
          indicator-color="rgba(255, 255, 255, 0.5)"
          indicator-active-color="rgba(255, 255, 255, 0.9)"
        >
          <swiper-item
            v-for="(banner, index) in bannerList"
            :key="banner.id || index"
            @click="onBannerClick(banner)"
          >
            <image
              class="banner-image"
              :src="banner.imageUrl"
              mode="aspectFill"
              @error="onImageError"
            ></image>
          </swiper-item>
        </swiper>
      </view>

      <!-- 公告通知 -->
      <view class="notice-section" v-if="noticeList && noticeList.length > 0">
        <view class="notice-icon">
          <image class="notice-icon-img" src="/static/icons/info.svg"></image>
        </view>
        <swiper
          class="notice-swiper"
          :vertical="true"
          :autoplay="true"
          :interval="3000"
          :duration="500"
        >
          <swiper-item
            v-for="(notice, index) in noticeList"
            :key="notice.id || index"
          >
            <text class="notice-text" @click="onNoticeClick(notice)">{{
              notice.title
            }}</text>
          </swiper-item>
        </swiper>
      </view>

      <!-- 发现优质达人 -->
      <view
        class="expert-section"
        v-if="hotExpertList && hotExpertList.length > 0"
      >
        <view class="section-title">
          <text>发现优质达人</text>
        </view>

        <!-- 热门达人3D照片轮播展示 -->
        <view class="hot-experts-container">
          <view
            class="hot-expert-card"
            v-for="(expert, index) in hotExpertList"
            :key="expert.id || index"
          >
            <!-- 达人基本信息 -->
            <view class="expert-info-header">
              <view class="expert-name-rating">
                <text class="expert-name">{{
                  expert.expertName || "未知达人"
                }}</text>
                <view class="expert-rating">
                  <text class="rating-star">⭐</text>
                  <text class="rating-score">{{ expert.rating || 5.0 }}</text>
                </view>
              </view>
              <text class="expert-desc">{{
                expert.description || "暂无描述"
              }}</text>
            </view>

            <!-- 3D照片轮播 - 点击跳转详情 -->
            <view
              class="photo-carousel-wrapper"
              v-if="expert.photos && expert.photos.length > 0"
            >
              <ExpertPhotoCarousel3D
                :photos="expert.photos"
                :auto-play="true"
                :interval="4000"
                :infinite-loop="true"
                :show-controls="false"
                :show-indicators="true"
                width="100%"
                height="320rpx"
                @photo-click="() => onExpertPhotoClick(expert)"
              />
            </view>

            <!-- 无照片时显示头像 -->
            <view
              class="fallback-avatar"
              v-else
              @click="onExpertPhotoClick(expert)"
            >
              <image
                class="expert-avatar"
                :src="expert.avatar"
                mode="aspectFill"
              ></image>
              <text class="no-photos-text">点击查看详情</text>
            </view>

            <!-- 立即预约按钮 -->
            <view class="book-now-btn" @click="onExpertPhotoClick(expert)">
              <text class="btn-text">立即预约</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 无热门达人时的提示 -->
      <view class="no-experts-tip" v-else-if="!loading">
        <text class="tip-text">暂无热门达人推荐</text>
      </view>
    </view>

    <!-- 底部导航栏 -->
    <Tabbar />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onShow, reactive } from "vue";
import {
  getBannerList,
  getExpertList,
  getNoticeList,
  getHotExperts,
  getExpertPhotos,
  type ExpertPhoto,
} from "@/api/home";
import Tabbar from "@/components/Tabbar.vue";
import ExpertPhotoCarousel3D from "@/components/ExpertPhotoCarousel3D.vue";

// 定义数据类型接口
interface Banner {
  id: number;
  imageUrl: string;
  linkUrl?: string;
  title?: string;
}

interface Notice {
  id: number;
  title: string;
  content?: string;
}

interface Expert {
  id: number;
  expertName: string;
  description: string;
  avatar: string;
  rating: number;
  priceMin: number;
  priceMax: number;
  photos?: ExpertPhoto[];
}

// 响应式数据 - 使用明确的类型定义
const bannerList = ref<Banner[]>([]);
const noticeList = ref<Notice[]>([]);
const hotExpertList = ref<Expert[]>([]);
const loading = ref<boolean>(true);
const error = ref<string>("");

// 页面加载
onMounted(() => {
  console.log("首页组件挂载完成");
  loadPageData();
});

// 加载页面数据
const loadPageData = async () => {
  try {
    loading.value = true;
    error.value = "";

    console.log("开始加载页面数据...");

    // 并行加载数据
    const [banners, notices, hotExperts] = await Promise.all([
      getBannerList(),
      getNoticeList(),
      getHotExperts(), // 获取所有热门达人，不限制数量
    ]);

    console.log("数据加载成功:", { banners, notices, hotExperts });

    // 安全地设置数据，确保数据类型正确
    bannerList.value = Array.isArray(banners) ? banners : [];
    noticeList.value = Array.isArray(notices) ? notices : [];

    // 为每个热门达人加载照片
    const hotExpertsWithPhotos = await Promise.all(
      (Array.isArray(hotExperts) ? hotExperts : []).map(async (expert) => {
        try {
          const photos = await getExpertPhotos(expert.id);
          return { ...expert, photos: Array.isArray(photos) ? photos : [] };
        } catch (error) {
          console.error(`获取达人${expert.id}照片失败:`, error);
          return { ...expert, photos: [] };
        }
      })
    );

    hotExpertList.value = hotExpertsWithPhotos;
    loading.value = false;
  } catch (err) {
    console.error("加载页面数据失败:", err);
    loading.value = false;
    error.value = "数据加载失败，请检查网络连接";

    // 设置空数组防止渲染错误
    bannerList.value = [];
    noticeList.value = [];
    hotExpertList.value = [];

    // 不显示toast，改为在页面上显示错误信息
    // uni.showToast({
    //   title: "加载失败",
    //   icon: "none",
    // });
  }
};

// 轮播图点击
const onBannerClick = (banner: Banner) => {
  if (banner && banner.linkUrl) {
    // 处理跳转逻辑
    console.log("点击轮播图:", banner);
  }
};

// 公告点击
const onNoticeClick = (notice: Notice) => {
  if (notice && notice.id) {
    uni.navigateTo({
      url: `/pages/common/notice-detail?id=${notice.id}`,
    });
  }
};

// 达人照片点击
const onExpertPhotoClick = (expert: Expert) => {
  if (expert && expert.id) {
    uni.navigateTo({
      url: `/pages/expert/detail?id=${expert.id}`,
    });
  }
};

// 图片加载错误处理
const onImageError = (e: any) => {
  console.log("图片加载失败:", e);
  // 可以在这里设置默认图片
};
</script>

<style lang="scss" scoped>
@import "@/styles/common.scss";
@import "@/styles/components.scss";

.home-container {
  background: linear-gradient(180deg, $bg-color-page 0%, $bg-color-white 100%);
  min-height: 100vh;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

// 加载状态样式
.loading-container {
  @extend .flex-col, .items-center, .justify-center;
  min-height: 60vh;
  padding: $spacing-2xl;

  .loading-spinner {
    width: 80rpx;
    height: 80rpx;
    border: 6rpx solid $neutral-200;
    border-top: 6rpx solid $primary-color;
    border-radius: $border-radius-full;
    animation: spin 1s linear infinite;
    margin-bottom: $spacing-lg;
  }

  .loading-text {
    font-size: $font-size-base;
    color: $text-color-secondary;
    font-weight: $font-weight-medium;
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// 错误状态样式
.error-container {
  @extend .flex-col, .items-center, .justify-center;
  min-height: 60vh;
  padding: $spacing-2xl;

  .error-icon {
    font-size: 120rpx;
    margin-bottom: $spacing-lg;
    opacity: 0.8;
  }

  .error-text {
    font-size: $font-size-base;
    color: $text-color-secondary;
    text-align: center;
    margin-bottom: $spacing-xl;
    line-height: $line-height-relaxed;
    max-width: 500rpx;
  }

  .retry-btn {
    @extend .btn, .btn-primary;
    border-radius: $border-radius-xl;
    padding: $spacing-base $spacing-2xl;
    font-size: $font-size-base;
    font-weight: $font-weight-semibold;
    box-shadow: $box-shadow-base;
  }
}

.banner-section {
  margin: $spacing-base;
  border-radius: $border-radius-xl;
  overflow: hidden;
  box-shadow: $box-shadow-card;

  .banner-swiper {
    height: 400rpx;
    border-radius: $border-radius-xl;

    .banner-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform $transition-slow;

      &:active {
        transform: scale(1.02);
      }
    }
  }

  // 轮播指示器美化
  :deep(.uni-swiper-dots) {
    bottom: $spacing-base;
  }

  :deep(.uni-swiper-dot) {
    background-color: rgba(255, 255, 255, 0.5);
    width: 16rpx;
    height: 16rpx;
    border-radius: $border-radius-full;
    margin: 0 6rpx;
    transition: all $transition-base;
  }

  :deep(.uni-swiper-dot-active) {
    background-color: $primary-color;
    width: 32rpx;
    border-radius: 8rpx;
  }
}

.notice-section {
  @extend .flex, .items-center, .card;
  margin: $spacing-base;
  padding: $spacing-lg $spacing-xl;
  background: linear-gradient(
    135deg,
    rgba($info-color, 0.15) 0%,
    rgba($info-color, 0.08) 100%
  );
  border-left: 6rpx solid $info-color;
  border-radius: $border-radius-lg;
  box-shadow: 0 4rpx 12rpx rgba($info-color, 0.1);

  .notice-icon {
    margin-right: $spacing-base;
    width: 48rpx;
    height: 48rpx;
    @extend .flex, .items-center, .justify-center;
    background: linear-gradient(135deg, $info-color 0%, $info-light 100%);
    border-radius: $border-radius-full;

    .notice-icon-img {
      width: 24rpx;
      height: 24rpx;
      filter: brightness(0) invert(1);
    }
  }

  .notice-swiper {
    flex: 1;
    height: 60rpx;

    .notice-text {
      font-size: $font-size-base;
      color: $text-color-secondary;
      line-height: 60rpx;
      font-weight: $font-weight-medium;
      transition: color $transition-base;

      &:active {
        color: $info-color;
      }
    }
  }
}

.expert-section {
  @extend .card;
  margin: $spacing-base;
  padding: $spacing-xl;
  background: $bg-color-white;
  border-radius: $border-radius-xl;
  box-shadow: $box-shadow-card;
  transition: all $transition-base;

  &:active {
    transform: translateY(2rpx);
    box-shadow: $box-shadow-sm;
  }
}

.section-title {
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  color: $text-color-primary;
  margin-bottom: $spacing-xl;
  position: relative;

  &::after {
    content: "";
    position: absolute;
    bottom: -$spacing-sm;
    left: 0;
    width: 60rpx;
    height: 6rpx;
    background: $primary-gradient;
    border-radius: $border-radius-sm;
  }
}

// 热门达人容器样式
.hot-experts-container {
  display: flex;
  flex-direction: column;
  gap: $spacing-xl;
}

.hot-expert-card {
  @extend .card;
  padding: $spacing-xl;
  background: linear-gradient(
    135deg,
    rgba($primary-color, 0.05) 0%,
    rgba($primary-color, 0.02) 100%
  );
  border-radius: $border-radius-xl;
  box-shadow: $box-shadow-card;
  border: 1rpx solid rgba($primary-color, 0.1);
  transition: all $transition-base;

  &:active {
    transform: translateY(-4rpx);
    box-shadow: 0 8rpx 24rpx rgba($primary-color, 0.15);
  }
}

.expert-info-header {
  margin-bottom: $spacing-lg;

  .expert-name-rating {
    @extend .flex, .justify-between, .items-center;
    margin-bottom: $spacing-sm;

    .expert-name {
      font-size: $font-size-lg;
      font-weight: $font-weight-bold;
      color: $text-color-primary;
      flex: 1;
    }

    .expert-rating {
      @extend .flex, .items-center;
      background: linear-gradient(
        135deg,
        $warning-color 0%,
        $warning-light 100%
      );
      color: white;
      padding: 6rpx $spacing-sm;
      border-radius: $border-radius-full;
      font-size: $font-size-xs;
      font-weight: $font-weight-semibold;
      box-shadow: 0 2rpx 8rpx rgba($warning-color, 0.3);

      .rating-star {
        margin-right: 4rpx;
      }
    }
  }

  .expert-desc {
    font-size: $font-size-base;
    color: $text-color-secondary;
    line-height: $line-height-relaxed;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.photo-carousel-wrapper {
  margin-bottom: $spacing-lg;
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: $box-shadow-base;
}

.fallback-avatar {
  @extend .flex-col, .items-center, .justify-center;
  padding: $spacing-2xl;
  background: linear-gradient(
    135deg,
    rgba($neutral-100, 0.8) 0%,
    rgba($neutral-50, 0.9) 100%
  );
  border-radius: $border-radius-lg;
  margin-bottom: $spacing-lg;
  transition: all $transition-base;

  &:active {
    transform: scale(0.98);
    background: linear-gradient(
      135deg,
      rgba($primary-color, 0.1) 0%,
      rgba($primary-color, 0.05) 100%
    );
  }

  .expert-avatar {
    @extend .avatar;
    width: 120rpx;
    height: 120rpx;
    margin-bottom: $spacing-base;
    box-shadow: $box-shadow-base;
  }

  .no-photos-text {
    font-size: $font-size-sm;
    color: $text-color-secondary;
    font-weight: $font-weight-medium;
  }
}

.book-now-btn {
  @extend .btn, .btn-primary;
  width: 100%;
  padding: $spacing-base;
  border-radius: $border-radius-lg;
  background: $primary-gradient;
  box-shadow: 0 4rpx 12rpx rgba($primary-color, 0.3);
  transition: all $transition-base;

  &:active {
    transform: translateY(2rpx);
    box-shadow: 0 2rpx 8rpx rgba($primary-color, 0.4);
  }

  .btn-text {
    font-size: $font-size-base;
    font-weight: $font-weight-semibold;
    color: white;
  }
}

.no-experts-tip {
  @extend .flex, .items-center, .justify-center;
  padding: $spacing-2xl;
  margin: $spacing-base;
  background: rgba($neutral-100, 0.5);
  border-radius: $border-radius-lg;
  border: 2rpx dashed $border-color-light;

  .tip-text {
    font-size: $font-size-base;
    color: $text-color-secondary;
    font-weight: $font-weight-medium;
  }
}
</style>
