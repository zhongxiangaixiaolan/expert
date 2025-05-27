<template>
  <view class="category-detail-container">
    <!-- 分类信息头部 -->
    <view class="category-header">
      <view class="category-info">
        <image
          class="category-icon"
          :src="categoryInfo.icon || '/static/icons/default-category.svg'"
          mode="aspectFit"
        ></image>
        <view class="category-content">
          <text class="category-name">{{ categoryInfo.name }}</text>
          <text class="category-desc">{{ categoryInfo.description || '暂无描述' }}</text>
          <view class="category-stats">
            <text class="stats-item">{{ serviceList.length }}个服务</text>
            <text class="stats-item">{{ totalExperts }}位达人</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 筛选栏 -->
    <view class="filter-bar">
      <scroll-view class="filter-scroll" scroll-x>
        <view class="filter-list">
          <view
            class="filter-item"
            :class="{ active: sortType === 'default' }"
            @click="setSortType('default')"
          >
            <text>默认排序</text>
          </view>
          <view
            class="filter-item"
            :class="{ active: sortType === 'price_asc' }"
            @click="setSortType('price_asc')"
          >
            <text>价格升序</text>
          </view>
          <view
            class="filter-item"
            :class="{ active: sortType === 'price_desc' }"
            @click="setSortType('price_desc')"
          >
            <text>价格降序</text>
          </view>
          <view
            class="filter-item"
            :class="{ active: sortType === 'rating' }"
            @click="setSortType('rating')"
          >
            <text>评分最高</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 服务列表 -->
    <scroll-view class="service-scroll" scroll-y @scrolltolower="loadMore">
      <view class="service-list">
        <view
          class="service-item"
          v-for="service in sortedServiceList"
          :key="service.id"
          @click="goToServiceDetail(service)"
        >
          <view class="service-card">
            <image
              class="service-image"
              :src="service.images?.[0] || '/static/images/default-service.jpg'"
              mode="aspectFill"
            ></image>
            <view class="service-content">
              <text class="service-title">{{ service.title }}</text>
              <text class="service-desc">{{ service.description }}</text>
              <view class="service-expert">
                <image
                  class="expert-avatar"
                  :src="service.expertAvatar || '/static/images/default-avatar.png'"
                  mode="aspectFill"
                ></image>
                <text class="expert-name">{{ service.expertName }}</text>
                <view class="expert-rating">
                  <image class="star-icon" src="/static/icons/star-filled.svg"></image>
                  <text class="rating-text">{{ service.rating || 5.0 }}</text>
                </view>
              </view>
              <view class="service-footer">
                <text class="service-price">¥{{ service.price }}</text>
                <text class="service-unit">/次</text>
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
      <view v-if="serviceList.length === 0 && !loading" class="empty-state">
        <image class="empty-icon" src="/static/icons/empty.svg"></image>
        <text class="empty-text">该分类下暂无服务</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { getServiceList } from "@/api/service";
import { getCategoryDetail } from "@/api/home";
import { showError } from "@/utils/index";

// 页面参数
const categoryId = ref(null);
const categoryName = ref("");

// 状态
const categoryInfo = ref({});
const serviceList = ref([]);
const sortType = ref("default");
const loading = ref(false);
const hasMore = ref(true);
const currentPage = ref(1);
const pageSize = ref(10);

// 计算属性
const sortedServiceList = computed(() => {
  const list = [...serviceList.value];
  switch (sortType.value) {
    case "price_asc":
      return list.sort((a, b) => a.price - b.price);
    case "price_desc":
      return list.sort((a, b) => b.price - a.price);
    case "rating":
      return list.sort((a, b) => (b.rating || 0) - (a.rating || 0));
    default:
      return list;
  }
});

const totalExperts = computed(() => {
  const expertIds = new Set(serviceList.value.map(service => service.expertId));
  return expertIds.size;
});

// 页面加载
onMounted(() => {
  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1];
  const options = currentPage.options;
  
  categoryId.value = options.id;
  categoryName.value = decodeURIComponent(options.name || "");
  
  // 设置导航标题
  uni.setNavigationBarTitle({
    title: categoryName.value
  });
  
  loadCategoryDetail();
  loadServiceList();
});

// 加载分类详情
const loadCategoryDetail = async () => {
  try {
    const detail = await getCategoryDetail(categoryId.value);
    categoryInfo.value = detail;
  } catch (error) {
    console.error("加载分类详情失败:", error);
  }
};

// 加载服务列表
const loadServiceList = async (isLoadMore = false) => {
  if (loading.value) return;
  
  try {
    loading.value = true;
    
    const params = {
      categoryId: categoryId.value,
      current: isLoadMore ? currentPage.value + 1 : 1,
      size: pageSize.value
    };
    
    const result = await getServiceList(params);
    
    if (isLoadMore) {
      serviceList.value.push(...result.records);
      currentPage.value++;
    } else {
      serviceList.value = result.records;
      currentPage.value = 1;
    }
    
    hasMore.value = result.records.length === pageSize.value;
  } catch (error) {
    console.error("加载服务列表失败:", error);
    showError("加载失败");
  } finally {
    loading.value = false;
  }
};

// 设置排序类型
const setSortType = (type) => {
  sortType.value = type;
};

// 加载更多
const loadMore = () => {
  if (hasMore.value && !loading.value) {
    loadServiceList(true);
  }
};

// 跳转到服务详情
const goToServiceDetail = (service) => {
  uni.navigateTo({
    url: `/pages/service/detail?id=${service.id}`
  });
};
</script>

<style lang="scss" scoped>
.category-detail-container {
  min-height: 100vh;
  background-color: $bg-color-page;
}

.category-header {
  background-color: $bg-color-white;
  padding: $spacing-lg;
  margin-bottom: $spacing-base;

  .category-info {
    display: flex;
    align-items: flex-start;

    .category-icon {
      width: 80rpx;
      height: 80rpx;
      margin-right: $spacing-base;
      border-radius: $border-radius-base;
    }

    .category-content {
      flex: 1;

      .category-name {
        display: block;
        font-size: $font-size-xl;
        font-weight: bold;
        color: $text-color-primary;
        margin-bottom: $spacing-xs;
      }

      .category-desc {
        display: block;
        font-size: $font-size-base;
        color: $text-color-secondary;
        margin-bottom: $spacing-base;
        line-height: 1.5;
      }

      .category-stats {
        display: flex;
        gap: $spacing-base;

        .stats-item {
          font-size: $font-size-sm;
          color: $primary-color;
        }
      }
    }
  }
}

.filter-bar {
  background-color: $bg-color-white;
  padding: $spacing-base 0;
  margin-bottom: $spacing-base;

  .filter-scroll {
    white-space: nowrap;
  }

  .filter-list {
    display: flex;
    padding: 0 $spacing-base;
    gap: $spacing-base;

    .filter-item {
      padding: $spacing-sm $spacing-base;
      border-radius: $border-radius-lg;
      background-color: $bg-color-gray;
      white-space: nowrap;

      &.active {
        background-color: $primary-color;
        color: $text-color-white;
      }

      text {
        font-size: $font-size-sm;
      }
    }
  }
}

.service-scroll {
  height: calc(100vh - 300rpx);
  padding: 0 $spacing-base;
}

.service-list {
  .service-item {
    margin-bottom: $spacing-base;

    .service-card {
      background-color: $bg-color-white;
      border-radius: $border-radius-lg;
      overflow: hidden;
      box-shadow: $box-shadow-sm;

      .service-image {
        width: 100%;
        height: 300rpx;
      }

      .service-content {
        padding: $spacing-lg;

        .service-title {
          display: block;
          font-size: $font-size-lg;
          font-weight: bold;
          color: $text-color-primary;
          margin-bottom: $spacing-xs;
        }

        .service-desc {
          display: block;
          font-size: $font-size-base;
          color: $text-color-secondary;
          margin-bottom: $spacing-base;
          line-height: 1.5;
        }

        .service-expert {
          display: flex;
          align-items: center;
          margin-bottom: $spacing-base;

          .expert-avatar {
            width: 40rpx;
            height: 40rpx;
            border-radius: 50%;
            margin-right: $spacing-sm;
          }

          .expert-name {
            font-size: $font-size-sm;
            color: $text-color-secondary;
            margin-right: $spacing-base;
          }

          .expert-rating {
            display: flex;
            align-items: center;

            .star-icon {
              width: 24rpx;
              height: 24rpx;
              margin-right: $spacing-xs;
            }

            .rating-text {
              font-size: $font-size-sm;
              color: $warning-color;
            }
          }
        }

        .service-footer {
          display: flex;
          align-items: baseline;

          .service-price {
            font-size: $font-size-xl;
            font-weight: bold;
            color: $error-color;
          }

          .service-unit {
            font-size: $font-size-sm;
            color: $text-color-secondary;
            margin-left: $spacing-xs;
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
    font-size: $font-size-base;
    color: $text-color-placeholder;
  }
}
</style>
