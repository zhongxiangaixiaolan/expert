<template>
	<view class="webview-container">
		<web-view :src="webUrl" @message="onMessage"></web-view>
	</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 获取页面参数
const pages = getCurrentPages()
const currentPage = pages[pages.length - 1]
const options = currentPage.options || {}

// 状态
const webUrl = ref('')

// 页面加载
onMounted(() => {
	const url = options.url
	if (url) {
		webUrl.value = decodeURIComponent(url)
	} else {
		uni.showToast({
			title: '缺少URL参数',
			icon: 'none'
		})
		setTimeout(() => {
			uni.navigateBack()
		}, 1500)
	}
})

// 接收网页消息
const onMessage = (e: any) => {
	console.log('收到网页消息:', e.detail.data)
}
</script>

<style lang="scss" scoped>
.webview-container {
	height: 100vh;
}
</style>
