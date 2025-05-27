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
      <view class="banner-section" v-if="bannerList.length > 0">
        <swiper
          class="banner-swiper"
          :indicator-dots="true"
          :autoplay="true"
          :interval="3000"
          :duration="500"
        >
          <swiper-item
            v-for="(banner, index) in bannerList"
            :key="index"
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
    <view class="notice-section" v-if="noticeList.length > 0">
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
        <swiper-item v-for="(notice, index) in noticeList" :key="index">
          <text class="notice-text" @click="onNoticeClick(notice)">{{
            notice.title
          }}</text>
        </swiper-item>
      </swiper>
    </view>

    <!-- 服务分类 -->
    <view class="category-section">
      <view class="section-title">服务分类</view>
      <view class="category-grid">
        <view
          class="category-item"
          v-for="(category, index) in categoryList"
          :key="index"
          @click="onCategoryClick(category)"
        >
          <!-- 简化图标显示 -->
          <view class="category-icon">
            <text class="category-emoji">{{ getCategoryEmoji(category.name) }}</text>
          </view>
          <text class="category-name">{{ category.name }}</text>
        </view>
      </view>
    </view>

    <!-- 推荐达人 -->
    <view class="expert-section">
      <view class="section-title">
        <text>推荐达人</text>
        <text class="more-btn" @click="goToExpertList">更多</text>
      </view>
      <view class="expert-list">
        <view
          class="expert-item"
          v-for="(expert, index) in expertList"
          :key="index"
          @click="onExpertClick(expert)"
        >
          <image
            class="expert-avatar"
            :src="expert.avatar"
            mode="aspectFill"
          ></image>
          <view class="expert-info">
            <text class="expert-name">{{ expert.name }}</text>
            <text class="expert-desc">{{ expert.description }}</text>
            <view class="expert-meta">
              <text class="expert-rating">⭐ {{ expert.rating }}</text>
              <text class="expert-price">¥{{ expert.servicePrice }}/次</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    </view>

    <!-- 自定义TabBar -->
    <CustomTabBar />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import {
  getBannerList,
  getCategoryList,
  getExpertList,
  getNoticeList,
} from "@/api/home";
import CustomTabBar from "@/custom-tab-bar/index.vue";

// 响应式数据
const bannerList = ref([]);
const noticeList = ref([]);
const categoryList = ref([]);
const expertList = ref([]);
const loading = ref(true);
const error = ref('');

// 页面加载
onMounted(() => {
  console.log('首页组件挂载完成');
  loadPageData();
  setTabBarIndex();
});

// 设置自定义tabbar选中状态
const setTabBarIndex = () => {
  if (typeof getApp().globalData.setTabBarIndex === "function") {
    getApp().globalData.setTabBarIndex(0);
  }
};

// 加载页面数据
const loadPageData = async () => {
  try {
    loading.value = true;
    error.value = '';

    console.log('开始加载页面数据...');

    // 并行加载数据
    const [banners, notices, categories, experts] = await Promise.all([
      getBannerList(),
      getNoticeList(),
      getCategoryList(),
      getExpertList({ pageSize: 6 }),
    ]);

    console.log('数据加载成功:', { banners, notices, categories, experts });

    bannerList.value = banners;
    noticeList.value = notices;
    categoryList.value = categories;
    expertList.value = experts.records || experts;

    loading.value = false;
  } catch (err) {
    console.error("加载页面数据失败:", err);
    loading.value = false;
    error.value = '数据加载失败，请检查网络连接';

    // 不显示toast，改为在页面上显示错误信息
    // uni.showToast({
    //   title: "加载失败",
    //   icon: "none",
    // });
  }
};

// 轮播图点击
const onBannerClick = (banner: any) => {
  if (banner.linkUrl) {
    // 处理跳转逻辑
    console.log("点击轮播图:", banner);
  }
};

// 公告点击
const onNoticeClick = (notice: any) => {
  uni.navigateTo({
    url: `/pages/common/notice-detail?id=${notice.id}`,
  });
};

// 分类点击
const onCategoryClick = (category: any) => {
  uni.navigateTo({
    url: `/pages/category/detail?id=${category.id}&name=${category.name}`,
  });
};

// 达人点击
const onExpertClick = (expert: any) => {
  uni.navigateTo({
    url: `/pages/expert/detail?id=${expert.id}`,
  });
};

// 查看更多达人
const goToExpertList = () => {
  uni.switchTab({
    url: "/pages/expert/list",
  });
};

// 图片加载错误处理
const onImageError = (e: any) => {
  console.log('图片加载失败:', e);
  // 可以在这里设置默认图片
};

// 获取分类emoji图标
const getCategoryEmoji = (categoryName: string) => {
  const emojiMap: Record<string, string> = {
    '设计': '🎨',
    '编程': '💻',
    '写作': '✍️',
    '营销': '📢',
    '咨询': '💡',
    '教育': '🎓',
    '生活服务': '🏠',
    '商务服务': '💼',
    '技术服务': '⚙️',
    '创意服务': '🎭'
  };
  return emojiMap[categoryName] || '📋';
};
</script>

<style lang="scss" scoped>
@import '@/styles/common.scss';
@import '@/styles/components.scss';

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
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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
  background: linear-gradient(135deg, rgba($info-color, 0.05) 0%, rgba($info-color, 0.02) 100%);
  border-left: 6rpx solid $info-color;

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

.category-section,
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
  @extend .flex, .justify-between, .items-center;
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  color: $text-color-primary;
  margin-bottom: $spacing-xl;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -$spacing-sm;
    left: 0;
    width: 60rpx;
    height: 6rpx;
    background: $primary-gradient;
    border-radius: $border-radius-sm;
  }

  .more-btn {
    font-size: $font-size-base;
    color: $primary-color;
    font-weight: $font-weight-medium;
    padding: $spacing-xs $spacing-sm;
    border-radius: $border-radius-base;
    transition: all $transition-base;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba($primary-color, 0.1);
      border-radius: $border-radius-base;
      opacity: 0;
      transition: opacity $transition-base;
    }

    &:active {
      color: $primary-dark;

      &::before {
        opacity: 1;
      }
    }
  }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;

  .category-item {
    @extend .flex-col, .items-center;
    padding: $spacing-base;
    border-radius: $border-radius-lg;
    transition: all $transition-base;
    cursor: pointer;
    position: relative;

    &:active {
      transform: scale(0.95);
      background-color: rgba($primary-color, 0.05);
    }

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, rgba($primary-color, 0.1) 0%, rgba($secondary-color, 0.05) 100%);
      border-radius: $border-radius-lg;
      opacity: 0;
      transition: opacity $transition-base;
      z-index: -1;
    }

    &:active::before {
      opacity: 1;
    }

    .category-icon {
      width: 100rpx;
      height: 100rpx;
      margin-bottom: $spacing-base;
      @extend .flex, .items-center, .justify-center;
      background: linear-gradient(135deg, rgba($primary-color, 0.1) 0%, rgba($secondary-color, 0.05) 100%);
      border-radius: $border-radius-xl;
      position: relative;
      transition: all $transition-base;

      &::after {
        content: '';
        position: absolute;
        top: -4rpx;
        left: -4rpx;
        right: -4rpx;
        bottom: -4rpx;
        background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
        border-radius: $border-radius-xl;
        opacity: 0;
        z-index: -1;
        transition: opacity $transition-base;
      }

      .category-emoji {
        font-size: 56rpx;
        line-height: 1;
        transition: transform $transition-base;
      }
    }

    &:active .category-icon {
      transform: scale(1.1);

      &::after {
        opacity: 0.1;
      }

      .category-emoji {
        transform: scale(1.1);
      }
    }

    .category-name {
      font-size: $font-size-sm;
      color: $text-color-secondary;
      text-align: center;
      font-weight: $font-weight-medium;
      line-height: $line-height-tight;
      transition: color $transition-base;
    }

    &:active .category-name {
      color: $primary-color;
    }
  }
}

.expert-list {
  .expert-item {
    @extend .flex;
    padding: $spacing-lg 0;
    border-bottom: 1rpx solid $border-color-light;
    border-radius: $border-radius-lg;
    transition: all $transition-base;
    cursor: pointer;
    position: relative;

    &:last-child {
      border-bottom: none;
    }

    &:active {
      transform: translateX(8rpx);
      background-color: rgba($primary-color, 0.02);
    }

    &::before {
      content: '';
      position: absolute;
      left: -$spacing-xl;
      top: 0;
      bottom: 0;
      width: 6rpx;
      background: $primary-gradient;
      border-radius: 0 $border-radius-sm $border-radius-sm 0;
      opacity: 0;
      transition: opacity $transition-base;
    }

    &:active::before {
      opacity: 1;
    }

    .expert-avatar {
      @extend .avatar, .avatar-lg;
      margin-right: $spacing-lg;
      box-shadow: $box-shadow-sm;
      transition: all $transition-base;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        top: -4rpx;
        left: -4rpx;
        right: -4rpx;
        bottom: -4rpx;
        background: $primary-gradient;
        border-radius: $border-radius-full;
        opacity: 0;
        z-index: -1;
        transition: opacity $transition-base;
      }
    }

    &:active .expert-avatar {
      transform: scale(1.05);

      &::after {
        opacity: 0.2;
      }
    }

    .expert-info {
      flex: 1;

      .expert-name {
        font-size: $font-size-lg;
        font-weight: $font-weight-bold;
        color: $text-color-primary;
        display: block;
        margin-bottom: $spacing-xs;
        transition: color $transition-base;
      }

      .expert-desc {
        font-size: $font-size-base;
        color: $text-color-secondary;
        display: block;
        margin-bottom: $spacing-base;
        @extend .text-ellipsis;
        line-height: $line-height-relaxed;
      }

      .expert-meta {
        @extend .flex, .justify-between, .items-center;

        .expert-rating {
          @extend .tag, .tag-warning;
          font-size: $font-size-xs;
          padding: 6rpx $spacing-xs;

          &::before {
            content: '⭐';
            margin-right: 4rpx;
          }
        }

        .expert-price {
          font-size: $font-size-lg;
          color: $primary-color;
          font-weight: $font-weight-bold;

          &::before {
            content: '¥';
            font-size: $font-size-base;
            margin-right: 2rpx;
          }

          &::after {
            content: '/次';
            font-size: $font-size-sm;
            color: $text-color-tertiary;
            font-weight: $font-weight-normal;
            margin-left: 4rpx;
          }
        }
      }
    }

    &:active .expert-info .expert-name {
      color: $primary-color;
    }
  }
}
</style>
