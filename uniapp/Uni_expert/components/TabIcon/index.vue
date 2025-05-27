<template>
  <view class="tab-icon-wrapper">
    <!-- 首页图标 -->
    <view v-if="name === 'home'" class="icon-svg" :style="{ color: color }">
      <text class="iconfont">🏠</text>
    </view>
    
    <!-- 达人图标 -->
    <view v-else-if="name === 'expert'" class="icon-svg" :style="{ color: color }">
      <text class="iconfont">👨‍💼</text>
    </view>
    
    <!-- 订单图标 -->
    <view v-else-if="name === 'order'" class="icon-svg" :style="{ color: color }">
      <text class="iconfont">📋</text>
    </view>
    
    <!-- 用户图标 -->
    <view v-else-if="name === 'user'" class="icon-svg" :style="{ color: color }">
      <text class="iconfont">👤</text>
    </view>
    
    <!-- 默认图标 -->
    <view v-else class="icon-svg" :style="{ color: color }">
      <text class="iconfont">⭐</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'TabIcon',
  props: {
    name: {
      type: String,
      required: true
    },
    color: {
      type: String,
      default: '#999999'
    },
    size: {
      type: [String, Number],
      default: 24
    }
  }
}
</script>

<style lang="scss" scoped>
.tab-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  
  .icon-svg {
    display: flex;
    align-items: center;
    justify-content: center;
    
    .iconfont {
      font-size: 48rpx;
      line-height: 1;
    }
  }
}
</style>
