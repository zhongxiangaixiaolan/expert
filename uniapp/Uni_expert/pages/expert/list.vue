<template>
  <view class="expert-list-container">
    <!-- 搜索栏 -->
    <view class="search-section">
      <view class="search-box">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          placeholder="搜索达人或服务"
          v-model="searchKeyword"
          @confirm="onSearch"
        />
      </view>
      <view class="filter-btn" @click="showFilterPopup = true">
        <text class="filter-icon">⚙️</text>
      </view>
    </view>

    <!-- 分类筛选 -->
    <scroll-view class="category-scroll" scroll-x>
      <view class="category-list">
        <view
          class="category-item"
          :class="{ active: selectedCategory === 0 }"
          @click="selectCategory(0)"
        >
          <text>全部</text>
        </view>
        <view
          class="category-item"
          :class="{ active: selectedCategory === category.id }"
          v-for="category in categoryList"
          :key="category.id"
          @click="selectCategory(category.id)"
        >
          <text>{{ category.name }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 达人列表 -->
    <scroll-view
      class="expert-scroll"
      scroll-y
      @scrolltolower="loadMore"
      refresher-enabled
      @refresherrefresh="onRefresh"
      :refresher-triggered="isRefreshing"
    >
      <view class="expert-list">
        <view
          class="expert-card"
          v-for="expert in expertList"
          :key="expert.id"
          @click="goToExpertDetail(expert.id)"
        >
          <image
            class="expert-avatar"
            :src="expert.avatar"
            mode="aspectFill"
          ></image>
          <view class="expert-info">
            <view class="expert-header">
              <text class="expert-name">{{ expert.name }}</text>
              <view class="expert-rating">
                <text class="rating-star">⭐</text>
                <text class="rating-score">{{ expert.rating }}</text>
              </view>
            </view>
            <text class="expert-desc">{{ expert.description }}</text>
            <view class="expert-meta">
              <text class="expert-category">{{ expert.categoryName }}</text>
              <text class="expert-orders">已接{{ expert.orderCount }}单</text>
            </view>
            <view class="expert-footer">
              <text class="expert-price">¥{{ expert.servicePrice }}/次</text>
              <view class="expert-actions">
                <text
                  class="action-btn favorite"
                  @click.stop="toggleFavorite(expert)"
                >
                  {{ expert.isFavorite ? "❤️" : "🤍" }}
                </text>
                <text
                  class="action-btn contact"
                  @click.stop="contactExpert(expert)"
                >
                  💬
                </text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载状态 -->
      <view class="load-more" v-if="hasMore">
        <text>{{ isLoading ? "加载中..." : "上拉加载更多" }}</text>
      </view>
      <view class="no-more" v-else-if="expertList.length > 0">
        <text>没有更多了</text>
      </view>
      <view class="empty" v-else-if="!isLoading">
        <text class="empty-icon">😔</text>
        <text class="empty-text">暂无达人</text>
      </view>
    </scroll-view>

    <!-- 筛选弹窗 -->
    <view
      class="filter-popup"
      v-if="showFilterPopup"
      @click="showFilterPopup = false"
    >
      <view class="filter-content" @click.stop>
        <view class="filter-header">
          <text class="filter-title">筛选条件</text>
          <text class="filter-close" @click="showFilterPopup = false">✕</text>
        </view>

        <view class="filter-section">
          <text class="filter-label">价格区间</text>
          <view class="price-range">
            <input
              class="price-input"
              placeholder="最低价"
              v-model="filterData.minPrice"
              type="number"
            />
            <text class="price-separator">-</text>
            <input
              class="price-input"
              placeholder="最高价"
              v-model="filterData.maxPrice"
              type="number"
            />
          </view>
        </view>

        <view class="filter-section">
          <text class="filter-label">评分</text>
          <view class="rating-options">
            <view
              class="rating-option"
              :class="{ active: filterData.minRating === rating }"
              v-for="rating in [0, 3, 4, 4.5]"
              :key="rating"
              @click="filterData.minRating = rating"
            >
              <text>{{ rating === 0 ? "不限" : `${rating}分以上` }}</text>
            </view>
          </view>
        </view>

        <view class="filter-section">
          <text class="filter-label">排序方式</text>
          <view class="sort-options">
            <view
              class="sort-option"
              :class="{ active: filterData.sortBy === sort.value }"
              v-for="sort in sortOptions"
              :key="sort.value"
              @click="filterData.sortBy = sort.value"
            >
              <text>{{ sort.label }}</text>
            </view>
          </view>
        </view>

        <view class="filter-actions">
          <button class="reset-btn" @click="resetFilter">重置</button>
          <button class="confirm-btn" @click="applyFilter">确定</button>
        </view>
      </view>
    </view>

    <!-- 自定义TabBar -->
    <CustomTabBar />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue";
import { getExpertList, getCategoryList } from "@/api/home";
import { showError, debounce } from "@/utils/index";
import CustomTabBar from "@/custom-tab-bar/index.vue";

// 状态
const expertList = ref([]);
const categoryList = ref([]);
const searchKeyword = ref("");
const selectedCategory = ref(0);
const isLoading = ref(false);
const isRefreshing = ref(false);
const hasMore = ref(true);
const showFilterPopup = ref(false);

// 分页参数
const pageParams = reactive({
  current: 1,
  size: 10,
});

// 筛选参数
const filterData = reactive({
  minPrice: "",
  maxPrice: "",
  minRating: 0,
  sortBy: "rating",
});

// 排序选项
const sortOptions = [
  { label: "综合排序", value: "rating" },
  { label: "价格从低到高", value: "price_asc" },
  { label: "价格从高到低", value: "price_desc" },
  { label: "接单数量", value: "orders" },
];

// 页面加载
onMounted(() => {
  loadCategoryList();
  loadExpertList();
  setTabBarIndex();
});

// 设置自定义tabbar选中状态
const setTabBarIndex = () => {
  if (typeof getApp().globalData.setTabBarIndex === "function") {
    getApp().globalData.setTabBarIndex(1);
  }
};

// 加载分类列表
const loadCategoryList = async () => {
  try {
    const categories = await getCategoryList();
    categoryList.value = categories;
  } catch (error) {
    console.error("加载分类失败:", error);
  }
};

// 加载达人列表
const loadExpertList = async (refresh = false) => {
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
      keyword: searchKeyword.value,
      categoryId: selectedCategory.value || undefined,
      minPrice: filterData.minPrice || undefined,
      maxPrice: filterData.maxPrice || undefined,
      minRating: filterData.minRating || undefined,
      sortBy: filterData.sortBy,
    };

    const result = await getExpertList(params);

    if (refresh) {
      expertList.value = result.records || result;
    } else {
      expertList.value.push(...(result.records || result));
    }

    hasMore.value = result.records ? result.current < result.pages : false;
    pageParams.current++;
  } catch (error) {
    console.error("加载达人列表失败:", error);
    showError("加载失败");
  } finally {
    isLoading.value = false;
    isRefreshing.value = false;
  }
};

// 搜索
const onSearch = debounce(() => {
  loadExpertList(true);
}, 500);

// 选择分类
const selectCategory = (categoryId: number) => {
  selectedCategory.value = categoryId;
  loadExpertList(true);
};

// 下拉刷新
const onRefresh = () => {
  isRefreshing.value = true;
  loadExpertList(true);
};

// 加载更多
const loadMore = () => {
  if (hasMore.value && !isLoading.value) {
    loadExpertList();
  }
};

// 应用筛选
const applyFilter = () => {
  showFilterPopup.value = false;
  loadExpertList(true);
};

// 重置筛选
const resetFilter = () => {
  filterData.minPrice = "";
  filterData.maxPrice = "";
  filterData.minRating = 0;
  filterData.sortBy = "rating";
};

// 跳转达人详情
const goToExpertDetail = (expertId: number) => {
  uni.navigateTo({
    url: `/pages/expert/detail?id=${expertId}`,
  });
};

// 切换收藏
const toggleFavorite = (expert: any) => {
  // TODO: 调用收藏/取消收藏接口
  expert.isFavorite = !expert.isFavorite;
};

// 联系达人
const contactExpert = (expert: any) => {
  // TODO: 打开聊天或联系方式
  uni.showToast({
    title: "功能开发中",
    icon: "none",
  });
};
</script>

<style lang="scss" scoped>
@import '@/styles/common.scss';
@import '@/styles/components.scss';

.expert-list-container {
  height: 100vh;
  @extend .flex-col;
  background: linear-gradient(180deg, $bg-color-white 0%, $bg-color-page 100%);
}

.search-section {
  @extend .flex, .items-center;
  padding: $spacing-lg;
  background: $bg-color-white;
  box-shadow: $box-shadow-xs;
  position: sticky;
  top: 0;
  z-index: $z-index-sticky;

  .search-box {
    flex: 1;
    @extend .flex, .items-center;
    background: $bg-color-light;
    border-radius: $border-radius-2xl;
    padding: 0 $spacing-lg;
    margin-right: $spacing-base;
    border: 2rpx solid transparent;
    transition: all $transition-base;

    &:focus-within {
      border-color: $primary-color;
      background: $bg-color-white;
      box-shadow: $box-shadow-sm;
    }

    .search-icon {
      font-size: $font-size-lg;
      margin-right: $spacing-sm;
      color: $text-color-placeholder;
      transition: color $transition-base;
    }

    &:focus-within .search-icon {
      color: $primary-color;
    }

    .search-input {
      flex: 1;
      height: 80rpx;
      font-size: $font-size-base;
      color: $text-color-primary;

      &::placeholder {
        color: $text-color-placeholder;
      }
    }
  }

  .filter-btn {
    width: 80rpx;
    height: 80rpx;
    @extend .flex, .items-center, .justify-center;
    background: $primary-gradient;
    border-radius: $border-radius-lg;
    box-shadow: $box-shadow-sm;
    transition: all $transition-base;

    &:active {
      transform: scale(0.95);
      box-shadow: $box-shadow-xs;
    }

    .filter-icon {
      font-size: $font-size-lg;
      color: $text-color-white;
    }
  }
}

.category-scroll {
  background-color: $bg-color-white;
  border-bottom: 1rpx solid $border-color-light;

  .category-list {
    display: flex;
    padding: $spacing-base;
    white-space: nowrap;

    .category-item {
      padding: $spacing-sm $spacing-base;
      margin-right: $spacing-base;
      background-color: $bg-color-gray;
      border-radius: $border-radius-xl;
      font-size: $font-size-sm;
      color: $text-color-secondary;
      white-space: nowrap;

      &.active {
        background-color: $primary-color;
        color: $text-color-white;
      }
    }
  }
}

.expert-scroll {
  flex: 1;
  padding-bottom: calc(100rpx + env(safe-area-inset-bottom));
}

.expert-list {
  padding: $spacing-base;
}

.expert-card {
  display: flex;
  background-color: $bg-color-white;
  border-radius: $border-radius-lg;
  padding: $spacing-base;
  margin-bottom: $spacing-base;
  box-shadow: $box-shadow-sm;

  .expert-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    margin-right: $spacing-base;
  }

  .expert-info {
    flex: 1;

    .expert-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-xs;

      .expert-name {
        font-size: $font-size-lg;
        font-weight: bold;
        color: $text-color-primary;
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
        }
      }
    }

    .expert-desc {
      font-size: $font-size-base;
      color: $text-color-secondary;
      margin-bottom: $spacing-sm;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .expert-meta {
      display: flex;
      justify-content: space-between;
      margin-bottom: $spacing-sm;

      .expert-category,
      .expert-orders {
        font-size: $font-size-sm;
        color: $text-color-placeholder;
      }
    }

    .expert-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .expert-price {
        font-size: $font-size-lg;
        color: $primary-color;
        font-weight: bold;
      }

      .expert-actions {
        display: flex;

        .action-btn {
          width: 60rpx;
          height: 60rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-left: $spacing-sm;
          font-size: $font-size-lg;
        }
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
}

.filter-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: $bg-color-mask;
  z-index: $z-index-popup;
  display: flex;
  align-items: flex-end;

  .filter-content {
    background-color: $bg-color-white;
    border-radius: $border-radius-xl $border-radius-xl 0 0;
    padding: $spacing-lg;
    width: 100%;
    max-height: 80vh;

    .filter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-lg;

      .filter-title {
        font-size: $font-size-xl;
        font-weight: bold;
        color: $text-color-primary;
      }

      .filter-close {
        font-size: $font-size-xl;
        color: $text-color-placeholder;
      }
    }

    .filter-section {
      margin-bottom: $spacing-lg;

      .filter-label {
        display: block;
        font-size: $font-size-lg;
        font-weight: 500;
        color: $text-color-primary;
        margin-bottom: $spacing-base;
      }

      .price-range {
        display: flex;
        align-items: center;

        .price-input {
          flex: 1;
          height: 80rpx;
          border: 1rpx solid $border-color;
          border-radius: $border-radius-base;
          padding: 0 $spacing-base;
          font-size: $font-size-base;
        }

        .price-separator {
          margin: 0 $spacing-base;
          color: $text-color-placeholder;
        }
      }

      .rating-options,
      .sort-options {
        display: flex;
        flex-wrap: wrap;
        gap: $spacing-base;

        .rating-option,
        .sort-option {
          padding: $spacing-sm $spacing-base;
          border: 1rpx solid $border-color;
          border-radius: $border-radius-base;
          font-size: $font-size-base;
          color: $text-color-secondary;

          &.active {
            border-color: $primary-color;
            background-color: rgba($primary-color, 0.1);
            color: $primary-color;
          }
        }
      }
    }

    .filter-actions {
      display: flex;
      gap: $spacing-base;

      .reset-btn,
      .confirm-btn {
        flex: 1;
        height: 88rpx;
        border-radius: $border-radius-base;
        font-size: $font-size-lg;
      }

      .reset-btn {
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
