<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'

// 应用启动
onLaunch(() => {
	console.log('App Launch')

	try {
		// 初始化用户信息
		const userStore = useUserStore()
		userStore.initUserInfo()
	} catch (error) {
		console.error('初始化用户信息失败:', error)
	}

	// 检查更新
	checkUpdate()

	// 设置状态栏样式
	setStatusBarStyle()
})

// 应用显示
onShow(() => {
	console.log('App Show')
})

// 应用隐藏
onHide(() => {
	console.log('App Hide')
})

// 检查更新
const checkUpdate = () => {
	// #ifdef MP-WEIXIN
	const updateManager = uni.getUpdateManager()

	updateManager.onCheckForUpdate((res) => {
		console.log('检查更新结果:', res.hasUpdate)
	})

	updateManager.onUpdateReady(() => {
		uni.showModal({
			title: '更新提示',
			content: '新版本已经准备好，是否重启应用？',
			success: (res) => {
				if (res.confirm) {
					updateManager.applyUpdate()
				}
			}
		})
	})

	updateManager.onUpdateFailed(() => {
		uni.showToast({
			title: '更新失败',
			icon: 'none'
		})
	})
	// #endif
}

// 设置状态栏样式
const setStatusBarStyle = () => {
	// #ifdef MP-WEIXIN
	uni.setNavigationBarColor({
		frontColor: '#000000',
		backgroundColor: '#ffffff'
	})
	// #endif
}
</script>

<style lang="scss">
/* 全局样式 */
@import '@/uni.scss';
@import '@/styles/common.scss';
@import '@/styles/components.scss';

/* 重置样式 */
view, text, button, input, image {
	box-sizing: border-box;
}

page {
	background-color: $bg-color-page;
	font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
	line-height: 1.4;
}

/* 通用样式类 */
.flex {
	display: flex;
}

.flex-center {
	display: flex;
	align-items: center;
	justify-content: center;
}

.flex-between {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.flex-column {
	display: flex;
	flex-direction: column;
}

.text-center {
	text-align: center;
}

.text-left {
	text-align: left;
}

.text-right {
	text-align: right;
}

.text-primary {
	color: $primary-color;
}

.text-secondary {
	color: $secondary-color;
}

.text-success {
	color: $success-color;
}

.text-warning {
	color: $warning-color;
}

.text-error {
	color: $error-color;
}

.bg-white {
	background-color: $bg-color-white;
}

.bg-gray {
	background-color: $bg-color-gray;
}

.border-radius {
	border-radius: $border-radius-base;
}

.shadow {
	box-shadow: $box-shadow-base;
}

/* 按钮样式重置 */
button {
	border: none;
	outline: none;
	background: none;
	padding: 0;
	margin: 0;
	font-size: inherit;
	color: inherit;
}

/* 按钮伪元素重置 - 微信小程序专用 */
button::after {
	border: none;
}

/* 输入框样式重置 */
input {
	outline: none;
	border: none;
	background: none;
	font-size: inherit;
	color: inherit;
}

/* 图片样式 */
image {
	display: block;
}

/* 滚动条样式 - 微信小程序不支持，注释掉 */
/* ::-webkit-scrollbar {
	width: 0;
	height: 0;
	background: transparent;
} */

/* 安全区域适配 */
.safe-area-inset-top {
	padding-top: constant(safe-area-inset-top);
	padding-top: env(safe-area-inset-top);
}

.safe-area-inset-bottom {
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);
}

/* 一行省略 */
.ellipsis {
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

/* 多行省略 */
.ellipsis-2 {
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.ellipsis-3 {
	display: -webkit-box;
	-webkit-line-clamp: 3;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

/* 动画 */
.fade-in {
	animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
	from {
		opacity: 0;
	}
	to {
		opacity: 1;
	}
}

.slide-up {
	animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
	from {
		transform: translateY(100%);
	}
	to {
		transform: translateY(0);
	}
}
</style>
