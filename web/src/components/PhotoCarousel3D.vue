<template>
  <div class="photo-carousel-3d" ref="carouselRef">
    <div class="carousel-container" :style="containerStyle">
      <!-- 轮播卡片容器 -->
      <div
        class="carousel-track"
        :style="trackStyle"
        @mousedown="handleMouseDown"
        @touchstart="handleTouchStart"
      >
        <div
          v-for="(photo, index) in photos"
          :key="photo.id || index"
          class="carousel-card"
          :class="getCardClass(index)"
          :style="getCardStyle(index)"
          @click="setActiveIndex(index)"
        >
          <div class="photo-frame">
            <div class="photo-wrapper">
              <img
                :src="getPhotoUrl(photo)"
                :alt="photo.photoTitle || `照片 ${index + 1}`"
                class="photo-image"
                @error="handleImageError"
                draggable="false"
              />
              <div class="photo-overlay" v-if="index === activeIndex">
                <div class="photo-title" v-if="photo.photoTitle">
                  {{ photo.photoTitle }}
                </div>
                <div class="photo-description" v-if="photo.photoDescription">
                  {{ photo.photoDescription }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 控制按钮 -->
    <div class="carousel-controls" v-if="photos.length > 1">
      <button
        class="control-btn prev-btn"
        @click="prevPhoto"
        :disabled="activeIndex === 0"
      >
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <button
        class="control-btn next-btn"
        @click="nextPhoto"
        :disabled="activeIndex === photos.length - 1"
      >
        <el-icon><ArrowRight /></el-icon>
      </button>
    </div>

    <!-- 指示器 -->
    <div class="carousel-indicators" v-if="photos.length > 1">
      <span
        v-for="(photo, index) in photos"
        :key="index"
        class="indicator"
        :class="{ active: index === activeIndex }"
        @click="setActiveIndex(index)"
      ></span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from "vue";
import { ArrowLeft, ArrowRight } from "@element-plus/icons-vue";

// 照片接口定义
interface Photo {
  id?: number;
  photoName: string;
  photoTitle?: string;
  photoDescription?: string;
  sortOrder?: number;
}

// Props
interface Props {
  photos: Photo[];
  autoPlay?: boolean;
  interval?: number;
  width?: string;
  height?: string;
}

const props = withDefaults(defineProps<Props>(), {
  photos: () => [],
  autoPlay: false,
  interval: 3000,
  width: "600px",
  height: "400px",
});

// 状态
const carouselRef = ref<HTMLElement>();
const activeIndex = ref(0);
let autoPlayTimer: number | null = null;

// 拖拽相关状态
const isDragging = ref(false);
const startX = ref(0);
const currentX = ref(0);
const dragOffset = ref(0);

// 计算属性
const containerStyle = computed(() => ({
  width: props.width,
  height: props.height,
}));

// 轨道样式 - 移除整体轨道移动，改为单独控制每张照片
const trackStyle = computed(() => {
  return {
    // 移除轨道的整体变换，让每张照片独立定位
  };
});

// 获取照片URL
const getPhotoUrl = (photo: Photo): string => {
  if (!photo.photoName) return "";
  // 构建照片访问URL
  return `/api/photos/${photo.photoName}`;
};

// 获取卡片类名 - 非循环模式
const getCardClass = (index: number) => {
  const diff = index - activeIndex.value;
  return {
    "card-active": diff === 0,
    "card-prev": diff === -1,
    "card-next": diff === 1,
    "card-hidden": Math.abs(diff) > 1,
  };
};

// 获取卡片样式
const getCardStyle = (index: number) => {
  const totalPhotos = props.photos.length;
  if (totalPhotos === 0) return {};

  const diff = index - activeIndex.value;
  const position = diff; // 移除循环位置处理，使用直接位置差

  // 计算变换 - 中间照片1.2倍高度，增加悬浮效果
  const cardSpacing = 160; // 卡片间距
  const translateX = position * cardSpacing; // 使用固定像素间距
  const scale = position === 0 ? 1.2 : 0.85; // 中间照片1.2倍大小
  const rotateY = position * 10; // 轻微的Y轴旋转
  const translateZ = position === 0 ? 40 : -60; // 中间照片向前突出
  const opacity = Math.abs(position) > 2 ? 0 : Math.abs(position) > 1 ? 0.6 : 1;

  return {
    transform: `translateX(${translateX}px) scale(${scale}) rotateY(${rotateY}deg) translateZ(${translateZ}px)`,
    opacity,
    zIndex: position === 0 ? 10 : 10 - Math.abs(position),
  };
};

// 设置活动索引
const setActiveIndex = (index: number) => {
  if (index >= 0 && index < props.photos.length) {
    activeIndex.value = index;
  }
};

// 上一张照片 - 非循环模式
const prevPhoto = () => {
  if (activeIndex.value > 0) {
    setActiveIndex(activeIndex.value - 1);
  }
};

// 下一张照片 - 非循环模式
const nextPhoto = () => {
  if (activeIndex.value < props.photos.length - 1) {
    setActiveIndex(activeIndex.value + 1);
  }
};

// 处理图片加载错误
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  // 使用一个简单的SVG占位符
  img.src =
    "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjQwMCIgdmlld0JveD0iMCAwIDMwMCA0MDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIzMDAiIGhlaWdodD0iNDAwIiBmaWxsPSIjRjVGNUY1Ii8+CjxwYXRoIGQ9Ik0xMjUgMTUwSDEyNVYyNTBIMTc1VjE1MEgxMjVaIiBmaWxsPSIjQ0NDIi8+CjxjaXJjbGUgY3g9IjE1MCIgY3k9IjIwMCIgcj0iMjAiIGZpbGw9IiNDQ0MiLz4KPHR5cGUgeD0iMTIwIiB5PSIzMDAiIGZpbGw9IiM5OTkiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgdGV4dC1hbmNob3I9Im1pZGRsZSI+5peg5rOV5Yqg6L295Zu+54mHPC90ZXh0Pgo8L3N2Zz4K";
};

// 自动播放
const startAutoPlay = () => {
  if (props.autoPlay && props.photos.length > 1) {
    autoPlayTimer = window.setInterval(() => {
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

// 鼠标拖拽事件
const handleMouseDown = (event: MouseEvent) => {
  if (props.photos.length <= 1) return;

  isDragging.value = true;
  startX.value = event.clientX;
  currentX.value = event.clientX;
  dragOffset.value = 0;

  stopAutoPlay();

  document.addEventListener("mousemove", handleMouseMove);
  document.addEventListener("mouseup", handleMouseUp);
  event.preventDefault();
};

const handleMouseMove = (event: MouseEvent) => {
  if (!isDragging.value) return;

  currentX.value = event.clientX;
  const deltaX = currentX.value - startX.value;
  // 移除拖拽偏移，因为我们不再使用轨道整体移动
  // dragOffset.value = (deltaX / containerWidth) * 100;
};

const handleMouseUp = () => {
  if (!isDragging.value) return;

  const deltaX = currentX.value - startX.value;
  const threshold = 50; // 拖拽阈值

  if (Math.abs(deltaX) > threshold) {
    if (deltaX > 0) {
      prevPhoto();
    } else {
      nextPhoto();
    }
  }

  isDragging.value = false;
  dragOffset.value = 0;

  document.removeEventListener("mousemove", handleMouseMove);
  document.removeEventListener("mouseup", handleMouseUp);

  if (props.autoPlay) {
    startAutoPlay();
  }
};

// 触摸事件
const handleTouchStart = (event: TouchEvent) => {
  if (props.photos.length <= 1) return;

  isDragging.value = true;
  startX.value = event.touches[0].clientX;
  currentX.value = event.touches[0].clientX;
  dragOffset.value = 0;

  stopAutoPlay();

  document.addEventListener("touchmove", handleTouchMove, { passive: false });
  document.addEventListener("touchend", handleTouchEnd);
};

const handleTouchMove = (event: TouchEvent) => {
  if (!isDragging.value) return;

  currentX.value = event.touches[0].clientX;
  const deltaX = currentX.value - startX.value;
  // 移除拖拽偏移，因为我们不再使用轨道整体移动
  // dragOffset.value = (deltaX / containerWidth) * 100;

  event.preventDefault();
};

const handleTouchEnd = () => {
  if (!isDragging.value) return;

  const deltaX = currentX.value - startX.value;
  const threshold = 50; // 拖拽阈值

  if (Math.abs(deltaX) > threshold) {
    if (deltaX > 0) {
      prevPhoto();
    } else {
      nextPhoto();
    }
  }

  isDragging.value = false;
  dragOffset.value = 0;

  document.removeEventListener("touchmove", handleTouchMove);
  document.removeEventListener("touchend", handleTouchEnd);

  if (props.autoPlay) {
    startAutoPlay();
  }
};

// 键盘事件
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === "ArrowLeft") {
    prevPhoto();
  } else if (event.key === "ArrowRight") {
    nextPhoto();
  }
};

// 生命周期
onMounted(() => {
  startAutoPlay();
  document.addEventListener("keydown", handleKeydown);
});

onUnmounted(() => {
  stopAutoPlay();
  document.removeEventListener("keydown", handleKeydown);
  document.removeEventListener("mousemove", handleMouseMove);
  document.removeEventListener("mouseup", handleMouseUp);
  document.removeEventListener("touchmove", handleTouchMove);
  document.removeEventListener("touchend", handleTouchEnd);
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

<style scoped>
.photo-carousel-3d {
  position: relative;
  margin: 0 auto;
  perspective: 1200px;
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
  width: 180px; /* 固定宽度，避免百分比导致的问题 */
  height: 240px; /* 固定高度，3:4比例 */
  cursor: pointer;
  transform-style: preserve-3d;
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  will-change: transform, opacity;
  left: 50%; /* 居中定位 */
  top: 50%;
  margin-left: -90px; /* 宽度的一半 */
  margin-top: -120px; /* 高度的一半 */
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
  border-radius: 16px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12), 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 2px solid rgba(255, 255, 255, 0.8);
}

.carousel-card.card-active .photo-frame {
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25), 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: rgba(255, 255, 255, 1);
}

.photo-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  background: #f8f9fa;
  border-radius: 14px;
  overflow: hidden;
}

.photo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
  border-radius: 12px;
}

.carousel-card:hover .photo-image {
  transform: scale(1.02);
}

.carousel-card.card-active:hover .photo-image {
  transform: scale(1.05);
}

.photo-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(
    transparent,
    rgba(0, 0, 0, 0.4),
    rgba(0, 0, 0, 0.8)
  );
  color: white;
  padding: 16px;
  transform: translateY(100%);
  transition: transform 0.4s ease;
  border-radius: 0 0 12px 12px;
}

.carousel-card.card-active .photo-overlay {
  transform: translateY(0);
}

.photo-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.photo-description {
  font-size: 13px;
  opacity: 0.95;
  line-height: 1.4;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.carousel-controls {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  transform: translateY(-50%);
  display: flex;
  justify-content: space-between;
  pointer-events: none;
  z-index: 30;
  padding: 0 20px;
}

.control-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.95);
  color: #333;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  pointer-events: auto;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12), 0 2px 8px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.control-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.1);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15), 0 4px 12px rgba(0, 0, 0, 0.1);
}

.control-btn:active:not(:disabled) {
  transform: scale(0.95);
}

.control-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: rgba(255, 255, 255, 0.6);
}

.prev-btn {
  margin-left: -22px;
}

.next-btn {
  margin-right: -22px;
}

.carousel-indicators {
  position: absolute;
  bottom: -50px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 30;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  backdrop-filter: blur(8px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.3);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
}

.indicator.active {
  background: #409eff;
  transform: scale(1.3);
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.3);
}

.indicator:hover:not(.active) {
  background: rgba(64, 158, 255, 0.6);
  transform: scale(1.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .carousel-card {
    width: 150px;
    height: 200px;
    margin-left: -75px;
    margin-top: -100px;
  }

  .control-btn {
    width: 40px;
    height: 40px;
  }

  .prev-btn {
    margin-left: -20px;
  }

  .next-btn {
    margin-right: -20px;
  }

  .photo-overlay {
    padding: 12px;
  }

  .photo-title {
    font-size: 14px;
  }

  .photo-description {
    font-size: 12px;
    -webkit-line-clamp: 1;
  }

  .carousel-indicators {
    bottom: -45px;
    gap: 8px;
    padding: 6px 12px;
  }

  .indicator {
    width: 8px;
    height: 8px;
  }
}

@media (max-width: 480px) {
  .carousel-card {
    width: 120px;
    height: 160px;
    margin-left: -60px;
    margin-top: -80px;
  }

  .carousel-controls {
    padding: 0 10px;
  }

  .control-btn {
    width: 36px;
    height: 36px;
  }

  .prev-btn {
    margin-left: -18px;
  }

  .next-btn {
    margin-right: -18px;
  }

  .photo-frame {
    border-radius: 12px;
  }

  .photo-wrapper {
    border-radius: 10px;
  }

  .photo-image {
    border-radius: 8px;
  }
}

/* 拖拽时的样式 */
.carousel-track {
  cursor: grab;
}

.carousel-track:active {
  cursor: grabbing;
}

/* 加载动画 */
@keyframes shimmer {
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: calc(200px + 100%) 0;
  }
}

.photo-wrapper:empty::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, #f0f0f0 0px, #e0e0e0 40px, #f0f0f0 80px);
  background-size: 200px;
  animation: shimmer 1.5s infinite;
}
</style>
