<template>
  <view class="favorites-container">
    <!-- 顶部筛选栏 -->
    <view class="filter-bar">
      <view class="filter-tabs">
        <view
          class="tab-item"
          :class="{ active: activeTab === 'service' }"
          @click="switchTab('service')"
        >
          <text>收藏服务</text>
        </view>
        <view
          class="tab-item"
          :class="{ active: activeTab === 'expert' }"
          @click="switchTab('expert')"
        >
          <text>收藏达人</text>
        </view>
      </view>
      <view class="filter-actions">
        <text class="edit-btn" @click="toggleEditMode">
          {{ editMode ? '完成' : '编辑' }}
        </text>
      </view>
    </view>

    <!-- 收藏列表 -->
    <scroll-view class="favorites-scroll" scroll-y @scrolltolower="loadMore">
      <!-- 服务收藏 -->
      <view v-if="activeTab === 'service'" class="service-list">
        <view
          class="service-item"
          v-for="item in serviceList"
          :key="item.id"
          @click="goToServiceDetail(item)"
        >
          <view class="item-content">
            <image
              class="service-image"
              :src="item.service?.images?.[0] || '/static/images/default-service.jpg'"
              mode="aspectFill"
            ></image>
            <view class="service-info">
              <text class="service-title">{{ item.service?.title }}</text>
              <text class="service-desc">{{ item.service?.description }}</text>
              <view class="service-meta">
                <text class="expert-name">{{ item.service?.expertName }}</text>
                <text class="service-price">¥{{ item.service?.price }}</text>
              </view>
              <text class="favorite-time">收藏于 {{ formatTime(item.createTime) }}</text>
            </view>
          </view>
          <view v-if="editMode" class="item-actions">
            <view
              class="action-btn delete"
              @click.stop="removeFavorite(item.id, 'service')"
            >
              <image class="action-icon" src="/static/icons/delete.svg"></image>
            </view>
          </view>
        </view>
      </view>

      <!-- 达人收藏 -->
      <view v-if="activeTab === 'expert'" class="expert-list">
        <view
          class="expert-item"
          v-for="item in expertList"
          :key="item.id"
          @click="goToExpertDetail(item)"
        >
          <view class="item-content">
            <image
              class="expert-avatar"
              :src="item.expert?.avatar || '/static/images/default-avatar.png'"
              mode="aspectFill"
            ></image>
            <view class="expert-info">
              <text class="expert-name">{{ item.expert?.name }}</text>
              <text class="expert-desc">{{ item.expert?.description }}</text>
              <view class="expert-meta">
                <view class="rating">
                  <image class="star-icon" src="/static/icons/star-filled.svg"></image>
                  <text class="rating-text">{{ item.expert?.rating || 5.0 }}</text>
                </view>
                <text class="order-count">{{ item.expert?.orderCount || 0 }}单</text>
              </view>
              <text class="favorite-time">收藏于 {{ formatTime(item.createTime) }}</text>
            </view>
          </view>
          <view v-if="editMode" class="item-actions">
            <view
              class="action-btn delete"
              @click.stop="removeFavorite(item.id, 'expert')"
            >
              <image class="action-icon" src="/static/icons/delete.svg"></image>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore" class="load-more">
        <text class="load-text">{{ loading ? '加载中...' : '上拉加载更多' }}</text>
      </view>

      <!-- 空状态 -->
      <view v-if="currentList.length === 0 && !loading" class="empty-state">
        <image class="empty-icon" src="/static/icons/heart-empty.svg"></image>
        <text class="empty-text">暂无收藏{{ activeTab === 'service' ? '服务' : '达人' }}</text>
        <text class="empty-desc">去发现更多优质{{ activeTab === 'service' ? '服务' : '达人' }}吧</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { getFavoriteList, removeFavoriteItem } from "@/api/favorite";
import { showError, showSuccess, requireAuth, formatTime } from "@/utils/index";

// 状态
const activeTab = ref("service");
const editMode = ref(false);
const serviceList = ref([]);
const expertList = ref([]);
const loading = ref(false);
const hasMore = ref(true);
const currentPage = ref(1);
const pageSize = ref(10);

// 计算属性
const currentList = computed(() => {
  return activeTab.value === "service" ? serviceList.value : expertList.value;
});

// 页面加载
onMounted(async () => {
  const isAuthenticated = await requireAuth();
  if (!isAuthenticated) return;
  loadFavoriteList();
});

// 切换标签
const switchTab = (tab) => {
  if (activeTab.value === tab) return;
  
  activeTab.value = tab;
  editMode.value = false;
  currentPage.value = 1;
  hasMore.value = true;
  
  if (tab === "service") {
    serviceList.value = [];
  } else {
    expertList.value = [];
  }
  
  loadFavoriteList();
};

// 切换编辑模式
const toggleEditMode = () => {
  editMode.value = !editMode.value;
};

// 加载收藏列表
const loadFavoriteList = async (isLoadMore = false) => {
  if (loading.value) return;
  
  try {
    loading.value = true;
    
    const params = {
      type: activeTab.value,
      current: isLoadMore ? currentPage.value + 1 : 1,
      size: pageSize.value
    };
    
    const result = await getFavoriteList(params);
    
    if (isLoadMore) {
      if (activeTab.value === "service") {
        serviceList.value.push(...result.records);
      } else {
        expertList.value.push(...result.records);
      }
      currentPage.value++;
    } else {
      if (activeTab.value === "service") {
        serviceList.value = result.records;
      } else {
        expertList.value = result.records;
      }
      currentPage.value = 1;
    }
    
    hasMore.value = result.records.length === pageSize.value;
  } catch (error) {
    console.error("加载收藏列表失败:", error);
    showError("加载失败");
  } finally {
    loading.value = false;
  }
};

// 加载更多
const loadMore = () => {
  if (hasMore.value && !loading.value) {
    loadFavoriteList(true);
  }
};

// 移除收藏
const removeFavorite = async (favoriteId, type) => {
  try {
    await removeFavoriteItem(favoriteId);
    
    if (type === "service") {
      const index = serviceList.value.findIndex(item => item.id === favoriteId);
      if (index > -1) {
        serviceList.value.splice(index, 1);
      }
    } else {
      const index = expertList.value.findIndex(item => item.id === favoriteId);
      if (index > -1) {
        expertList.value.splice(index, 1);
      }
    }
    
    showSuccess("取消收藏成功");
  } catch (error) {
    console.error("取消收藏失败:", error);
    showError("操作失败");
  }
};

// 跳转到服务详情
const goToServiceDetail = (item) => {
  if (editMode.value) return;
  
  uni.navigateTo({
    url: `/pages/service/detail?id=${item.service?.id}`
  });
};

// 跳转到达人详情
const goToExpertDetail = (item) => {
  if (editMode.value) return;
  
  uni.navigateTo({
    url: `/pages/expert/detail?id=${item.expert?.id}`
  });
};
</script>

<style lang="scss" scoped>
.favorites-container {
  min-height: 100vh;
  background-color: $bg-color-page;
}

.filter-bar {
  background-color: $bg-color-white;
  padding: $spacing-base;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1rpx solid $border-color-light;

  .filter-tabs {
    display: flex;

    .tab-item {
      padding: $spacing-sm $spacing-lg;
      margin-right: $spacing-base;
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

  .filter-actions {
    .edit-btn {
      font-size: $font-size-base;
      color: $primary-color;
    }
  }
}

.favorites-scroll {
  height: calc(100vh - 120rpx);
  padding: $spacing-base;
}

.service-list,
.expert-list {
  .service-item,
  .expert-item {
    background-color: $bg-color-white;
    border-radius: $border-radius-lg;
    margin-bottom: $spacing-base;
    overflow: hidden;
    box-shadow: $box-shadow-sm;
    display: flex;
    align-items: center;

    .item-content {
      flex: 1;
      display: flex;
      padding: $spacing-lg;

      .service-image,
      .expert-avatar {
        width: 120rpx;
        height: 120rpx;
        border-radius: $border-radius-base;
        margin-right: $spacing-base;
      }

      .expert-avatar {
        border-radius: 50%;
      }

      .service-info,
      .expert-info {
        flex: 1;

        .service-title,
        .expert-name {
          display: block;
          font-size: $font-size-lg;
          font-weight: bold;
          color: $text-color-primary;
          margin-bottom: $spacing-xs;
        }

        .service-desc,
        .expert-desc {
          display: block;
          font-size: $font-size-base;
          color: $text-color-secondary;
          margin-bottom: $spacing-base;
          line-height: 1.5;
        }

        .service-meta,
        .expert-meta {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: $spacing-base;

          .expert-name {
            font-size: $font-size-sm;
            color: $text-color-secondary;
            margin: 0;
          }

          .service-price {
            font-size: $font-size-lg;
            font-weight: bold;
            color: $error-color;
          }

          .rating {
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

          .order-count {
            font-size: $font-size-sm;
            color: $text-color-secondary;
          }
        }

        .favorite-time {
          font-size: $font-size-sm;
          color: $text-color-placeholder;
        }
      }
    }

    .item-actions {
      padding: $spacing-base;

      .action-btn {
        width: 60rpx;
        height: 60rpx;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;

        &.delete {
          background-color: $error-color;
        }

        .action-icon {
          width: 32rpx;
          height: 32rpx;
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
