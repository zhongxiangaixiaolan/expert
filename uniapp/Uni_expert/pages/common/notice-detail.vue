<template>
	<view class="notice-detail-container">
		<view class="notice-content" v-if="noticeInfo">
			<!-- 标题 -->
			<view class="notice-header">
				<text class="notice-title">{{ noticeInfo.title }}</text>
				<text class="notice-time">{{ formatTime(noticeInfo.createTime) }}</text>
			</view>
			
			<!-- 内容 -->
			<view class="notice-body">
				<rich-text class="notice-text" :nodes="noticeInfo.content"></rich-text>
			</view>
		</view>
		
		<!-- 加载状态 -->
		<view class="loading" v-else-if="isLoading">
			<text>加载中...</text>
		</view>
		
		<!-- 错误状态 -->
		<view class="error" v-else>
			<text class="error-icon">😔</text>
			<text class="error-text">加载失败</text>
			<button class="retry-btn" @click="loadNoticeDetail">重试</button>
		</view>
	</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getNoticeDetail, type Notice } from '@/api/home'
import { formatTime, showError } from '@/utils/index'

// 获取页面参数
const pages = getCurrentPages()
const currentPage = pages[pages.length - 1]
const options = currentPage.options || {}

// 状态
const noticeInfo = ref<Notice | null>(null)
const isLoading = ref(false)

// 页面加载
onMounted(() => {
	const id = options.id
	if (id) {
		loadNoticeDetail(Number(id))
	} else {
		showError('缺少公告ID')
		setTimeout(() => {
			uni.navigateBack()
		}, 1500)
	}
})

// 加载公告详情
const loadNoticeDetail = async (id?: number) => {
	const noticeId = id || Number(options.id)
	if (!noticeId) return
	
	try {
		isLoading.value = true
		const result = await getNoticeDetail(noticeId)
		noticeInfo.value = result
	} catch (error) {
		console.error('加载公告详情失败:', error)
		showError('加载失败')
	} finally {
		isLoading.value = false
	}
}
</script>

<style lang="scss" scoped>
.notice-detail-container {
	min-height: 100vh;
	background-color: $bg-color-page;
}

.notice-content {
	background-color: $bg-color-white;
	margin: $spacing-base;
	border-radius: $border-radius-lg;
	padding: $spacing-lg;
	
	.notice-header {
		margin-bottom: $spacing-lg;
		padding-bottom: $spacing-lg;
		border-bottom: 1rpx solid $border-color-light;
		
		.notice-title {
			display: block;
			font-size: $font-size-xl;
			font-weight: bold;
			color: $text-color-primary;
			margin-bottom: $spacing-base;
			line-height: 1.5;
		}
		
		.notice-time {
			display: block;
			font-size: $font-size-sm;
			color: $text-color-placeholder;
		}
	}
	
	.notice-body {
		.notice-text {
			font-size: $font-size-base;
			color: $text-color-primary;
			line-height: 1.6;
		}
	}
}

.loading,
.error {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	height: 60vh;
	color: $text-color-placeholder;
	font-size: $font-size-base;
}

.error {
	.error-icon {
		font-size: 120rpx;
		margin-bottom: $spacing-base;
	}
	
	.error-text {
		margin-bottom: $spacing-lg;
	}
	
	.retry-btn {
		width: 200rpx;
		height: 64rpx;
		background-color: $primary-color;
		color: $text-color-white;
		border-radius: $border-radius-base;
		font-size: $font-size-base;
	}
}
</style>
