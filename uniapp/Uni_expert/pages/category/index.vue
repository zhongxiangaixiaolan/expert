<template>
  <view class="category-container">
    <!-- 顶部搜索栏 -->
    <view class="search-header">
      <view class="search-box">
        <image class="search-icon" src="/static/icons/search.svg"></image>
        <input
          class="search-input"
          v-model="searchKeyword"
          placeholder="搜索服务分类"
          @input="onSearchInput"
        />
        <text v-if="searchKeyword" class="clear-btn" @click="clearSearch">✕</text>
      </view>
    </view>

    <!-- 分类列表 -->
    <scroll-view class="category-scroll" scroll-y>
      <view class="category-grid">
        <view
          class="category-item"
          v-for="category in filteredCategories"
          :key="category.id"
          @click="goToCategoryDetail(category)"
        >
          <view class="category-card">
            <image
              class="category-icon"
              :src="category.icon || '/static/icons/default-category.svg'"
              mode="aspectFit"
            ></image>
            <text class="category-name">{{ category.name }}</text>
            <text class="category-desc">{{ category.description || '暂无描述' }}</text>
            <view class="category-stats">
              <text class="stats-text">{{ category.serviceCount || 0 }}个服务</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="filteredCategories.length === 0" class="empty-state">
        <image class="empty-icon" src="/static/icons/empty.svg"></image>
        <text class="empty-text">暂无相关分类</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { getCategoryList } from "@/api/home";
import { showError } from "@/utils/index";

// 状态
const categoryList = ref([]);
const searchKeyword = ref("");

// 计算属性
const filteredCategories = computed(() => {
  if (!searchKeyword.value) {
    return categoryList.value;
  }
  return categoryList.value.filter((category) =>
    category.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  );
});

// 页面加载
onMounted(() => {
  loadCategoryList();
});

// 加载分类列表
const loadCategoryList = async () => {
  try {
    uni.showLoading({ title: "加载中..." });
    const categories = await getCategoryList();
    categoryList.value = categories;
  } catch (error) {
    console.error("加载分类列表失败:", error);
    showError("加载失败");
  } finally {
    uni.hideLoading();
  }
};

// 搜索输入
const onSearchInput = () => {
  // 实时搜索，无需额外处理
};

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = "";
};

// 跳转到分类详情
const goToCategoryDetail = (category) => {
  uni.navigateTo({
    url: `/pages/category/detail?id=${category.id}&name=${encodeURIComponent(category.name)}`
  });
};
</script>

<style lang="scss" scoped>
.category-container {
  min-height: 100vh;
  background-color: $bg-color-page;
}

.search-header {
  background-color: $bg-color-white;
  padding: $spacing-base;
  border-bottom: 1rpx solid $border-color-light;
}

.search-box {
  display: flex;
  align-items: center;
  background-color: $bg-color-gray;
  border-radius: $border-radius-lg;
  padding: $spacing-sm $spacing-base;

  .search-icon {
    width: 32rpx;
    height: 32rpx;
    margin-right: $spacing-sm;
  }

  .search-input {
    flex: 1;
    font-size: $font-size-base;
    color: $text-color-primary;

    &::placeholder {
      color: $text-color-placeholder;
    }
  }

  .clear-btn {
    font-size: $font-size-lg;
    color: $text-color-placeholder;
    padding: $spacing-xs;
  }
}

.category-scroll {
  height: calc(100vh - 120rpx);
  padding: $spacing-base;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-base;
}

.category-item {
  .category-card {
    background-color: $bg-color-white;
    border-radius: $border-radius-lg;
    padding: $spacing-lg;
    text-align: center;
    box-shadow: $box-shadow-sm;

    .category-icon {
      width: 80rpx;
      height: 80rpx;
      margin-bottom: $spacing-base;
    }

    .category-name {
      display: block;
      font-size: $font-size-lg;
      font-weight: bold;
      color: $text-color-primary;
      margin-bottom: $spacing-xs;
    }

    .category-desc {
      display: block;
      font-size: $font-size-sm;
      color: $text-color-secondary;
      margin-bottom: $spacing-base;
      line-height: 1.4;
    }

    .category-stats {
      .stats-text {
        font-size: $font-size-sm;
        color: $primary-color;
      }
    }
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
