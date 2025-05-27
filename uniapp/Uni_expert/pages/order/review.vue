<template>
	<view class="review-container">
		<scroll-view class="review-scroll" scroll-y>
			<!-- 订单信息 -->
			<view class="order-section" v-if="orderInfo">
				<view class="section-title">订单信息</view>
				<view class="order-card">
					<text class="service-name">{{ orderInfo.serviceName }}</text>
					<text class="order-no">订单号：{{ orderInfo.orderNo }}</text>
					<text class="complete-time">完成时间：{{ formatTime(orderInfo.completeTime) }}</text>
				</view>
			</view>

			<!-- 达人信息 -->
			<view class="talent-section" v-if="orderInfo?.talentInfo">
				<view class="section-title">服务达人</view>
				<view class="talent-card">
					<image class="talent-avatar" :src="orderInfo.talentInfo.avatar" mode="aspectFill"></image>
					<view class="talent-info">
						<text class="talent-name">{{ orderInfo.talentInfo.name }}</text>
						<text class="talent-category">{{ orderInfo.categoryInfo?.name }}</text>
					</view>
				</view>
			</view>

			<!-- 评分区域 -->
			<view class="rating-section">
				<view class="section-title">服务评价</view>
				
				<!-- 总体评分 -->
				<view class="rating-item">
					<text class="rating-label">总体评分</text>
					<view class="rating-stars">
						<text 
							class="star" 
							v-for="n in 5" 
							:key="n"
							:class="{ filled: n <= reviewData.rating, active: n === hoverRating }"
							@click="setRating('rating', n)"
							@touchstart="setHoverRating(n)"
							@touchend="clearHoverRating"
						>
							⭐
						</text>
					</view>
					<text class="rating-text">{{ getRatingText(reviewData.rating) }}</text>
				</view>

				<!-- 详细评分 -->
				<view class="detail-ratings">
					<view class="rating-item">
						<text class="rating-label">服务态度</text>
						<view class="rating-stars">
							<text 
								class="star" 
								v-for="n in 5" 
								:key="n"
								:class="{ filled: n <= reviewData.serviceRating }"
								@click="setRating('serviceRating', n)"
							>
								⭐
							</text>
						</view>
					</view>
					<view class="rating-item">
						<text class="rating-label">响应速度</text>
						<view class="rating-stars">
							<text 
								class="star" 
								v-for="n in 5" 
								:key="n"
								:class="{ filled: n <= reviewData.speedRating }"
								@click="setRating('speedRating', n)"
							>
								⭐
							</text>
						</view>
					</view>
					<view class="rating-item">
						<text class="rating-label">服务质量</text>
						<view class="rating-stars">
							<text 
								class="star" 
								v-for="n in 5" 
								:key="n"
								:class="{ filled: n <= reviewData.qualityRating }"
								@click="setRating('qualityRating', n)"
							>
								⭐
							</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 评价内容 -->
			<view class="content-section">
				<view class="section-title">评价内容</view>
				<textarea 
					class="review-textarea" 
					v-model="reviewData.content" 
					placeholder="请分享您的服务体验，帮助其他用户更好地了解这位达人"
					maxlength="500"
					:show-count="true"
				></textarea>
			</view>

			<!-- 图片上传 -->
			<view class="images-section">
				<view class="section-title">
					<text>上传图片</text>
					<text class="section-desc">（选填，最多9张）</text>
				</view>
				<view class="upload-area">
					<view class="uploaded-images" v-if="reviewData.images.length > 0">
						<view 
							class="image-item" 
							v-for="(img, index) in reviewData.images" 
							:key="index"
						>
							<image class="review-image" :src="img" mode="aspectFill"></image>
							<view class="image-remove" @click="removeImage(index)">
								<image class="remove-icon" src="/static/icons/close.svg"></image>
							</view>
						</view>
					</view>
					<view class="upload-btn" @click="chooseImages" v-if="reviewData.images.length < 9">
						<image class="upload-icon" src="/static/icons/camera.svg"></image>
						<text class="upload-text">添加图片</text>
					</view>
				</view>
			</view>

			<!-- 匿名选项 -->
			<view class="anonymous-section">
				<view class="anonymous-item" @click="toggleAnonymous">
					<image 
						class="checkbox" 
						:src="reviewData.isAnonymous ? '/static/icons/check.svg' : '/static/icons/close.svg'"
					></image>
					<text class="anonymous-text">匿名评价</text>
				</view>
			</view>

			<!-- 快捷评价标签 -->
			<view class="tags-section">
				<view class="section-title">快捷评价</view>
				<view class="tags-grid">
					<view 
						class="tag-item" 
						v-for="tag in quickTags" 
						:key="tag"
						:class="{ active: selectedTags.includes(tag) }"
						@click="toggleTag(tag)"
					>
						<text class="tag-text">{{ tag }}</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部提交栏 -->
		<view class="submit-bar">
			<button 
				class="submit-btn" 
				:disabled="!canSubmit || isSubmitting"
				@click="submitReview"
			>
				{{ isSubmitting ? '提交中...' : '提交评价' }}
			</button>
		</view>
	</view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { getOrderDetail, reviewOrder, type Order, type OrderReviewParams } from '@/api/order'
import { formatTime, showError, showSuccess, chooseImage } from '@/utils/index'

// 获取页面参数
const pages = getCurrentPages()
const currentPage = pages[pages.length - 1]
const options = currentPage.options || {}

// 状态
const orderInfo = ref<Order | null>(null)
const isSubmitting = ref(false)
const hoverRating = ref(0)
const selectedTags = ref<string[]>([])

// 评价数据
const reviewData = reactive({
  rating: 5,
  serviceRating: 5,
  speedRating: 5,
  qualityRating: 5,
  content: '',
  images: [] as string[],
  isAnonymous: false
})

// 快捷评价标签
const quickTags = [
  '服务专业', '响应及时', '态度友好', '质量很好',
  '价格合理', '沟通顺畅', '超出预期', '值得推荐'
]

// 计算属性
const canSubmit = computed(() => {
  return reviewData.rating > 0 && !isSubmitting.value
})

// 页面加载
onMounted(() => {
  const orderId = options.id
  if (orderId) {
    loadOrderInfo(Number(orderId))
  } else {
    showError('缺少订单信息')
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  }
})

// 加载订单信息
const loadOrderInfo = async (orderId: number) => {
  try {
    const result = await getOrderDetail(orderId)
    orderInfo.value = result
  } catch (error) {
    console.error('加载订单信息失败:', error)
    showError('加载订单信息失败')
  }
}

// 设置评分
const setRating = (type: string, rating: number) => {
  reviewData[type] = rating
}

// 设置悬停评分
const setHoverRating = (rating: number) => {
  hoverRating.value = rating
}

// 清除悬停评分
const clearHoverRating = () => {
  hoverRating.value = 0
}

// 获取评分文本
const getRatingText = (rating: number) => {
  const textMap = {
    1: '很差',
    2: '较差',
    3: '一般',
    4: '满意',
    5: '非常满意'
  }
  return textMap[rating] || ''
}

// 选择图片
const chooseImages = async () => {
  try {
    const result = await chooseImage({
      count: 9 - reviewData.images.length,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera']
    })
    
    if (result.tempFilePaths && result.tempFilePaths.length > 0) {
      // 上传图片
      for (const filePath of result.tempFilePaths) {
        await uploadImage(filePath)
      }
    }
  } catch (error) {
    console.error('选择图片失败:', error)
  }
}

// 上传图片
const uploadImage = async (filePath: string) => {
  try {
    uni.showLoading({ title: '上传中...' })
    
    // TODO: 调用图片上传接口
    // const result = await uploadReviewImage(filePath)
    
    // 临时处理：直接使用本地路径
    reviewData.images.push(filePath)
    
    uni.hideLoading()
  } catch (error) {
    console.error('上传图片失败:', error)
    uni.hideLoading()
    showError('上传失败')
  }
}

// 移除图片
const removeImage = (index: number) => {
  reviewData.images.splice(index, 1)
}

// 切换匿名状态
const toggleAnonymous = () => {
  reviewData.isAnonymous = !reviewData.isAnonymous
}

// 切换标签
const toggleTag = (tag: string) => {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
  
  // 将选中的标签添加到评价内容中
  updateContentWithTags()
}

// 更新评价内容
const updateContentWithTags = () => {
  if (selectedTags.value.length > 0) {
    const tagsText = selectedTags.value.join('，')
    if (!reviewData.content.includes(tagsText)) {
      reviewData.content = reviewData.content ? 
        `${reviewData.content} ${tagsText}` : tagsText
    }
  }
}

// 提交评价
const submitReview = async () => {
  if (!canSubmit.value) {
    showError('请完善评价信息')
    return
  }
  
  try {
    isSubmitting.value = true
    
    const reviewParams: OrderReviewParams = {
      orderId: Number(options.id),
      rating: reviewData.rating,
      serviceRating: reviewData.serviceRating,
      speedRating: reviewData.speedRating,
      qualityRating: reviewData.qualityRating,
      content: reviewData.content,
      images: reviewData.images,
      isAnonymous: reviewData.isAnonymous
    }
    
    await reviewOrder(reviewParams)
    
    showSuccess('评价提交成功')
    
    // 返回订单详情页
    setTimeout(() => {
      uni.navigateBack()
    }, 1000)
    
  } catch (error) {
    console.error('提交评价失败:', error)
    showError('提交评价失败')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.review-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: $bg-color-page;
}

.review-scroll {
  flex: 1;
  padding-bottom: 120rpx;
}

.order-section,
.talent-section,
.rating-section,
.content-section,
.images-section,
.anonymous-section,
.tags-section {
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;
  padding: $spacing-lg;
}

.section-title {
  font-size: $font-size-lg;
  font-weight: bold;
  color: $text-color-primary;
  margin-bottom: $spacing-lg;
  
  .section-desc {
    font-size: $font-size-sm;
    font-weight: normal;
    color: $text-color-secondary;
  }
}

.order-card {
  .service-name {
    display: block;
    font-size: $font-size-lg;
    font-weight: 500;
    color: $text-color-primary;
    margin-bottom: $spacing-sm;
  }
  
  .order-no,
  .complete-time {
    display: block;
    font-size: $font-size-sm;
    color: $text-color-secondary;
    margin-bottom: $spacing-xs;
  }
}

.talent-card {
  display: flex;
  align-items: center;
  
  .talent-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    margin-right: $spacing-base;
  }
  
  .talent-info {
    flex: 1;
    
    .talent-name {
      display: block;
      font-size: $font-size-lg;
      font-weight: 500;
      color: $text-color-primary;
      margin-bottom: $spacing-xs;
    }
    
    .talent-category {
      font-size: $font-size-base;
      color: $text-color-secondary;
    }
  }
}

.rating-item {
  display: flex;
  align-items: center;
  margin-bottom: $spacing-lg;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  .rating-label {
    width: 160rpx;
    font-size: $font-size-base;
    color: $text-color-primary;
  }
  
  .rating-stars {
    flex: 1;
    display: flex;
    margin-right: $spacing-base;
    
    .star {
      font-size: 60rpx;
      color: #ddd;
      margin-right: $spacing-sm;
      transition: color 0.2s;
      
      &.filled,
      &.active {
        color: $secondary-color;
      }
    }
  }
  
  .rating-text {
    font-size: $font-size-base;
    color: $secondary-color;
    font-weight: 500;
  }
}

.detail-ratings {
  margin-top: $spacing-lg;
  padding-top: $spacing-lg;
  border-top: 1rpx solid $border-color-light;
  
  .rating-item {
    margin-bottom: $spacing-base;
    
    .rating-stars .star {
      font-size: 48rpx;
    }
  }
}

.review-textarea {
  width: 100%;
  height: 200rpx;
  border: 1rpx solid $border-color;
  border-radius: $border-radius-base;
  padding: $spacing-base;
  font-size: $font-size-base;
  color: $text-color-primary;
  background-color: $bg-color-white;
  resize: none;
  
  &:focus {
    border-color: $primary-color;
  }
}

.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-base;
  
  .uploaded-images {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-base;
  }
  
  .image-item {
    position: relative;
    width: 160rpx;
    height: 160rpx;
    
    .review-image {
      width: 100%;
      height: 100%;
      border-radius: $border-radius-base;
    }
    
    .image-remove {
      position: absolute;
      top: -16rpx;
      right: -16rpx;
      width: 48rpx;
      height: 48rpx;
      background-color: $error-color;
      border-radius: 24rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .remove-icon {
        width: 24rpx;
        height: 24rpx;
      }
    }
  }
  
  .upload-btn {
    width: 160rpx;
    height: 160rpx;
    border: 2rpx dashed $border-color;
    border-radius: $border-radius-base;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    
    .upload-icon {
      width: 48rpx;
      height: 48rpx;
      margin-bottom: $spacing-sm;
    }
    
    .upload-text {
      font-size: $font-size-sm;
      color: $text-color-secondary;
    }
  }
}

.anonymous-item {
  display: flex;
  align-items: center;
  
  .checkbox {
    width: 40rpx;
    height: 40rpx;
    margin-right: $spacing-base;
  }
  
  .anonymous-text {
    font-size: $font-size-base;
    color: $text-color-primary;
  }
}

.tags-grid {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-base;
  
  .tag-item {
    padding: $spacing-sm $spacing-base;
    border: 1rpx solid $border-color;
    border-radius: $border-radius-xl;
    background-color: $bg-color-white;
    
    &.active {
      border-color: $primary-color;
      background-color: rgba($primary-color, 0.1);
      
      .tag-text {
        color: $primary-color;
      }
    }
    
    .tag-text {
      font-size: $font-size-sm;
      color: $text-color-secondary;
    }
  }
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: $bg-color-white;
  padding: $spacing-lg;
  border-top: 1rpx solid $border-color-light;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);
  
  .submit-btn {
    width: 100%;
    height: 88rpx;
    background-color: $primary-color;
    color: $text-color-white;
    border-radius: $border-radius-base;
    font-size: $font-size-lg;
    font-weight: 500;
    
    &[disabled] {
      background-color: $text-color-disabled;
    }
  }
}
</style>
