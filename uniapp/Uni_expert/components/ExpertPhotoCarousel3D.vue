<template>
  <view class="photo-carousel-3d" v-if="photos && photos.length > 0">
    <view class="carousel-container" :style="containerStyle">
      <!-- 轮播卡片容器 -->
      <view 
        class="carousel-track"
        @touchstart="handleTouchStart"
        @touchmove="handleTouchMove"
        @touchend="handleTouchEnd"
      >
        <view
          v-for="(photo, index) in photos"
          :key="photo.id || index"
          class="carousel-card"
          :class="getCardClass(index)"
          :style="getCardStyle(index)"
          @tap="setActiveIndex(index)"
        >
          <view class="photo-frame">
            <view class="photo-wrapper">
              <image
                :src="photo.photoUrl || getPhotoUrl(photo)"
                :alt="photo.photoTitle || `照片 ${index + 1}`"
                class="photo-image"
                mode="aspectFill"
                @error="handleImageError"
              />
              <view class="photo-overlay" v-if="index === activeIndex && photo.photoTitle">
                <view class="photo-title">
                  {{ photo.photoTitle }}
                </view>
                <view class="photo-description" v-if="photo.photoDescription">
                  {{ photo.photoDescription }}
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 控制按钮 -->
    <view class="carousel-controls" v-if="photos.length > 1 && showControls">
      <view
        class="control-btn prev-btn"
        @tap="prevPhoto"
      >
        <text class="control-icon">‹</text>
      </view>
      <view
        class="control-btn next-btn"
        @tap="nextPhoto"
      >
        <text class="control-icon">›</text>
      </view>
    </view>

    <!-- 指示器 -->
    <view class="carousel-indicators" v-if="photos.length > 1 && showIndicators">
      <view
        v-for="(photo, index) in photos"
        :key="index"
        class="indicator"
        :class="{ active: index === activeIndex }"
        @tap="setActiveIndex(index)"
      ></view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from "vue";

// 照片接口定义
interface Photo {
  id?: number;
  photoName: string;
  photoTitle?: string;
  photoDescription?: string;
  sortOrder?: number;
  photoUrl?: string;
}

// Props
interface Props {
  photos: Photo[];
  autoPlay?: boolean;
  interval?: number;
  width?: string;
  height?: string;
  showControls?: boolean;
  showIndicators?: boolean;
  infiniteLoop?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  photos: () => [],
  autoPlay: true,
  interval: 3000,
  width: "100%",
  height: "400rpx",
  showControls: false,
  showIndicators: true,
  infiniteLoop: true,
});

// 状态
const activeIndex = ref(0);
let autoPlayTimer: number | null = null;

// 触摸相关状态
const touchStartX = ref(0);
const touchStartY = ref(0);
const touchStartTime = ref(0);

// 计算属性
const containerStyle = computed(() => ({
  width: props.width,
  height: props.height,
}));

// 获取照片URL
const getPhotoUrl = (photo: Photo): string => {
  if (!photo.photoName) return "";
  // 构建照片访问URL - 使用正确的后端照片访问路径
  return `http://localhost:8080/api/photos/${photo.photoName}`;
};

// 获取卡片类名
const getCardClass = (index: number) => {
  const diff = index - activeIndex.value;
  const totalPhotos = props.photos.length;
  
  if (totalPhotos === 0) return [];
  
  let position = diff;
  
  // 循环位置处理
  if (props.infiniteLoop) {
    if (position > totalPhotos / 2) {
      position -= totalPhotos;
    } else if (position < -totalPhotos / 2) {
      position += totalPhotos;
    }
  }

  const classes = [];
  if (position === 0) {
    classes.push('card-active');
  } else if (position === -1 || (props.infiniteLoop && position === totalPhotos - 1)) {
    classes.push('card-prev');
  } else if (position === 1 || (props.infiniteLoop && position === -(totalPhotos - 1))) {
    classes.push('card-next');
  } else {
    classes.push('card-hidden');
  }

  return classes;
};

// 获取卡片样式
const getCardStyle = (index: number) => {
  const totalPhotos = props.photos.length;
  if (totalPhotos === 0) return {};

  const diff = index - activeIndex.value;
  let position = diff;
  
  // 循环位置处理
  if (props.infiniteLoop) {
    if (position > totalPhotos / 2) {
      position -= totalPhotos;
    } else if (position < -totalPhotos / 2) {
      position += totalPhotos;
    }
  }

  // 计算变换 - 中间照片1.2倍高度，增加悬浮效果
  const cardSpacing = 120; // 卡片间距 (rpx)
  const translateX = position * cardSpacing;
  const scale = position === 0 ? 1.2 : 0.85;
  const rotateY = position * 8; // 轻微的Y轴旋转
  const translateZ = position === 0 ? 30 : -40;
  const opacity = Math.abs(position) > 2 ? 0 : Math.abs(position) > 1 ? 0.6 : 1;

  return {
    transform: `translateX(${translateX}rpx) scale(${scale}) rotateY(${rotateY}deg) translateZ(${translateZ}rpx)`,
    opacity,
    zIndex: position === 0 ? 10 : 10 - Math.abs(position),
  };
};

// 设置激活索引
const setActiveIndex = (index: number) => {
  if (index >= 0 && index < props.photos.length) {
    activeIndex.value = index;
  }
};

// 上一张照片
const prevPhoto = () => {
  if (props.photos.length <= 1) return;
  
  if (props.infiniteLoop) {
    activeIndex.value = activeIndex.value === 0 ? props.photos.length - 1 : activeIndex.value - 1;
  } else {
    if (activeIndex.value > 0) {
      activeIndex.value--;
    }
  }
};

// 下一张照片
const nextPhoto = () => {
  if (props.photos.length <= 1) return;
  
  if (props.infiniteLoop) {
    activeIndex.value = (activeIndex.value + 1) % props.photos.length;
  } else {
    if (activeIndex.value < props.photos.length - 1) {
      activeIndex.value++;
    }
  }
};

// 自动播放
const startAutoPlay = () => {
  if (props.autoPlay && props.photos.length > 1) {
    autoPlayTimer = setInterval(() => {
      nextPhoto();
    }, props.interval);
  }
};

const stopAutoPlay = () => {
  if (autoPlayTimer) {
    clearInterval(autoPlayTimer);
    autoPlayTimer = null;
  }
};

// 触摸事件处理
const handleTouchStart = (event: TouchEvent) => {
  if (props.photos.length <= 1) return;

  const touch = event.touches[0];
  touchStartX.value = touch.clientX;
  touchStartY.value = touch.clientY;
  touchStartTime.value = Date.now();

  stopAutoPlay();
};

const handleTouchMove = (event: TouchEvent) => {
  // 阻止默认滚动行为
  event.preventDefault();
};

const handleTouchEnd = (event: TouchEvent) => {
  if (props.photos.length <= 1) return;

  const touch = event.changedTouches[0];
  const deltaX = touch.clientX - touchStartX.value;
  const deltaY = touch.clientY - touchStartY.value;
  const deltaTime = Date.now() - touchStartTime.value;

  // 判断是否为有效滑动
  const threshold = 50; // 滑动阈值
  const timeThreshold = 300; // 时间阈值

  if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > threshold && deltaTime < timeThreshold) {
    if (deltaX > 0) {
      prevPhoto();
    } else {
      nextPhoto();
    }
  }

  if (props.autoPlay) {
    startAutoPlay();
  }
};

// 图片加载错误处理
const handleImageError = (e: any) => {
  console.log('照片加载失败:', e);
};

// 监听照片变化
watch(() => props.photos, () => {
  if (activeIndex.value >= props.photos.length) {
    activeIndex.value = 0;
  }
}, { immediate: true });

// 生命周期
onMounted(() => {
  startAutoPlay();
});

onUnmounted(() => {
  stopAutoPlay();
});

// 暴露方法
defineExpose({
  prevPhoto,
  nextPhoto,
  setActiveIndex,
  startAutoPlay,
  stopAutoPlay,
});
</script>

<style lang="scss" scoped>
.photo-carousel-3d {
  position: relative;
  margin: 0 auto;
  perspective: 1200rpx;
  user-select: none;
  overflow: hidden;
}

.carousel-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-track {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  transform-style: preserve-3d;
}

.carousel-card {
  position: absolute;
  width: 140rpx;
  height: 186rpx; /* 3:4比例 */
  cursor: pointer;
  transform-style: preserve-3d;
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  will-change: transform, opacity;
  left: 50%;
  top: 50%;
  margin-left: -70rpx; /* 宽度的一半 */
  margin-top: -93rpx; /* 高度的一半 */
}

.carousel-card.card-active {
  z-index: 10;
}

.carousel-card.card-prev,
.carousel-card.card-next {
  z-index: 5;
}

.carousel-card.card-hidden {
  opacity: 0;
  pointer-events: none;
}

.photo-frame {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 16rpx;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.12), 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  border: 2rpx solid rgba(255, 255, 255, 0.8);
}

.carousel-card.card-active .photo-frame {
  box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.25), 0 8rpx 24rpx rgba(0, 0, 0, 0.15);
  border-color: rgba(255, 255, 255, 1);
}

.photo-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  background: #f8f9fa;
  border-radius: 14rpx;
  overflow: hidden;
}

.photo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
  border-radius: 12rpx;
}

.carousel-card:active .photo-image {
  transform: scale(1.02);
}

.photo-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  padding: 16rpx;
  border-radius: 0 0 12rpx 12rpx;
}

.photo-title {
  font-size: 24rpx;
  font-weight: 600;
  margin-bottom: 4rpx;
  line-height: 1.2;
}

.photo-description {
  font-size: 20rpx;
  opacity: 0.9;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 控制按钮 */
.carousel-controls {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  transform: translateY(-50%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20rpx;
  pointer-events: none;
  z-index: 20;
}

.control-btn {
  width: 60rpx;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
  pointer-events: auto;
  transition: all 0.3s ease;
}

.control-btn:active {
  transform: scale(0.95);
  background: rgba(255, 255, 255, 1);
}

.control-icon {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.prev-btn {
  margin-left: -30rpx;
}

.next-btn {
  margin-right: -30rpx;
}

/* 指示器 */
.carousel-indicators {
  position: absolute;
  bottom: -60rpx;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12rpx;
  padding: 8rpx 16rpx;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 20rpx;
  backdrop-filter: blur(10rpx);
}

.indicator {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
  cursor: pointer;
}

.indicator.active {
  background: #007aff;
  width: 24rpx;
  border-radius: 6rpx;
}

/* 响应式设计 */
@media (max-width: 768rpx) {
  .carousel-card {
    width: 120rpx;
    height: 160rpx;
    margin-left: -60rpx;
    margin-top: -80rpx;
  }

  .control-btn {
    width: 50rpx;
    height: 50rpx;
  }

  .control-icon {
    font-size: 28rpx;
  }

  .prev-btn {
    margin-left: -25rpx;
  }

  .next-btn {
    margin-right: -25rpx;
  }

  .photo-overlay {
    padding: 12rpx;
  }

  .photo-title {
    font-size: 22rpx;
  }

  .photo-description {
    font-size: 18rpx;
    -webkit-line-clamp: 1;
  }

  .carousel-indicators {
    bottom: -50rpx;
    gap: 8rpx;
    padding: 6rpx 12rpx;
  }

  .indicator {
    width: 8rpx;
    height: 8rpx;
  }

  .indicator.active {
    width: 16rpx;
  }
}

@media (max-width: 480rpx) {
  .carousel-card {
    width: 100rpx;
    height: 133rpx;
    margin-left: -50rpx;
    margin-top: -66rpx;
  }

  .carousel-controls {
    padding: 0 10rpx;
  }

  .control-btn {
    width: 44rpx;
    height: 44rpx;
  }

  .control-icon {
    font-size: 24rpx;
  }

  .prev-btn {
    margin-left: -22rpx;
  }

  .next-btn {
    margin-right: -22rpx;
  }

  .photo-frame {
    border-radius: 12rpx;
  }

  .photo-wrapper {
    border-radius: 10rpx;
  }

  .photo-image {
    border-radius: 8rpx;
  }
}

/* 加载动画 */
@keyframes shimmer {
  0% {
    background-position: -200rpx 0;
  }
  100% {
    background-position: calc(200rpx + 100%) 0;
  }
}

.photo-image[src=""] {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200rpx 100%;
  animation: shimmer 1.5s infinite;
}
</style>
