<template>
	<view class="profile-container">
		<!-- 头像设置 -->
		<view class="avatar-section">
			<view class="avatar-wrapper" @click="chooseAvatar">
				<image 
					class="avatar-image" 
					:src="formData.avatar || '/static/images/default-avatar.svg'" 
					mode="aspectFill"
				></image>
				<view class="avatar-overlay">
					<text class="camera-icon">📷</text>
					<text class="avatar-tip">点击更换头像</text>
				</view>
			</view>
		</view>

		<!-- 表单信息 -->
		<view class="form-section">
			<view class="form-item">
				<text class="form-label">昵称</text>
				<input 
					class="form-input" 
					v-model="formData.nickname" 
					placeholder="请输入昵称"
					maxlength="20"
				/>
			</view>

			<view class="form-item">
				<text class="form-label">手机号</text>
				<view class="phone-input-wrapper">
					<input 
						class="form-input phone-input" 
						v-model="formData.phone" 
						placeholder="请输入手机号"
						type="number"
						maxlength="11"
						:disabled="!!formData.phone"
					/>
					<button 
						v-if="!formData.phone"
						class="bind-phone-btn" 
						open-type="getPhoneNumber"
						@getphonenumber="onGetPhoneNumber"
					>
						一键绑定
					</button>
				</view>
			</view>

			<view class="form-item">
				<text class="form-label">微信号</text>
				<input 
					class="form-input" 
					v-model="formData.wechatNo" 
					placeholder="请输入微信号（选填）"
					maxlength="30"
				/>
			</view>

			<!-- 角色选择 -->
			<view class="form-item" v-if="showRoleSelect">
				<text class="form-label">身份类型</text>
				<view class="role-selector">
					<view 
						class="role-option" 
						:class="{ active: formData.roleType === 1 }"
						@click="selectRole(1)"
					>
						<text class="role-icon">👤</text>
						<text class="role-text">普通用户</text>
					</view>
					<view 
						class="role-option" 
						:class="{ active: formData.roleType === 2 }"
						@click="selectRole(2)"
					>
						<text class="role-icon">⭐</text>
						<text class="role-text">服务达人</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 保存按钮 -->
		<view class="save-section">
			<button 
				class="save-btn" 
				:disabled="!canSave || isLoading"
				@click="saveProfile"
			>
				{{ isLoading ? '保存中...' : '保存信息' }}
			</button>
		</view>

		<!-- 跳过按钮 -->
		<view class="skip-section" v-if="canSkip">
			<text class="skip-btn" @click="skipProfile">暂时跳过</text>
		</view>
	</view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { updateUserInfo, uploadAvatar, bindPhone } from '@/api/auth'
import { chooseImage, showError, showSuccess, showLoading, hideLoading } from '@/utils/index'

// 获取页面参数
const pages = getCurrentPages()
const currentPage = pages[pages.length - 1]
const options = currentPage.options || {}

// 状态
const userStore = useUserStore()
const isLoading = ref(false)
const showRoleSelect = ref(false)

// 表单数据
const formData = ref({
	nickname: '',
	avatar: '',
	phone: '',
	wechatNo: '',
	roleType: 1
})

// 计算属性
const canSave = computed(() => {
	return formData.value.nickname.trim().length >= 2
})

const canSkip = computed(() => {
	return options.from !== 'required'
})

// 页面加载
onMounted(() => {
	initFormData()
})

// 初始化表单数据
const initFormData = () => {
	const userInfo = userStore.userInfo
	if (userInfo) {
		formData.value = {
			nickname: userInfo.nickname || '',
			avatar: userInfo.avatar || '',
			phone: userInfo.phone || '',
			wechatNo: userInfo.wechatNo || '',
			roleType: userInfo.roleType || 1
		}
	}
	
	// 如果是新用户，显示角色选择
	if (options.isNewUser === 'true') {
		showRoleSelect.value = true
	}
}

// 选择头像
const chooseAvatar = async () => {
	try {
		const result = await chooseImage({
			count: 1,
			sizeType: ['compressed'],
			sourceType: ['album', 'camera']
		})
		
		if (result.tempFilePaths && result.tempFilePaths.length > 0) {
			await uploadAvatarFile(result.tempFilePaths[0])
		}
	} catch (error) {
		console.error('选择头像失败:', error)
	}
}

// 上传头像文件
const uploadAvatarFile = async (filePath: string) => {
	try {
		showLoading('上传中...')
		
		const uploadResult = await uploadAvatar(filePath)
		formData.value.avatar = uploadResult.url
		
		showSuccess('头像上传成功')
	} catch (error) {
		console.error('上传头像失败:', error)
		showError('头像上传失败')
	} finally {
		hideLoading()
	}
}

// 获取手机号
const onGetPhoneNumber = async (e: any) => {
	console.log('获取手机号:', e)
	
	if (e.detail.errMsg !== 'getPhoneNumber:ok') {
		showError('获取手机号失败')
		return
	}
	
	try {
		showLoading('绑定中...')
		
		await bindPhone({
			encryptedData: e.detail.encryptedData,
			iv: e.detail.iv
		})
		
		// 重新获取用户信息
		await userStore.refreshUserInfo()
		formData.value.phone = userStore.userInfo?.phone || ''
		
		showSuccess('手机号绑定成功')
	} catch (error) {
		console.error('绑定手机号失败:', error)
		showError('绑定手机号失败')
	} finally {
		hideLoading()
	}
}

// 选择角色
const selectRole = (roleType: number) => {
	formData.value.roleType = roleType
}

// 保存资料
const saveProfile = async () => {
	if (!canSave.value) {
		showError('请填写完整信息')
		return
	}
	
	try {
		isLoading.value = true
		
		// 更新用户信息
		await updateUserInfo({
			nickname: formData.value.nickname,
			avatar: formData.value.avatar,
			wechatNo: formData.value.wechatNo,
			roleType: formData.value.roleType
		})
		
		// 更新本地用户信息
		userStore.updateUserInfo(formData.value)
		
		showSuccess('保存成功')
		
		// 跳转到首页
		setTimeout(() => {
			uni.switchTab({
				url: '/pages/index/index'
			})
		}, 1000)
		
	} catch (error) {
		console.error('保存失败:', error)
		showError('保存失败，请重试')
	} finally {
		isLoading.value = false
	}
}

// 跳过设置
const skipProfile = () => {
	uni.switchTab({
		url: '/pages/index/index'
	})
}
</script>

<style lang="scss" scoped>
.profile-container {
	min-height: 100vh;
	background-color: $bg-color-page;
	padding: $spacing-lg;
}

.avatar-section {
	text-align: center;
	margin-bottom: $spacing-xl;
	
	.avatar-wrapper {
		position: relative;
		width: 200rpx;
		height: 200rpx;
		margin: 0 auto;
		border-radius: 50%;
		overflow: hidden;
		
		.avatar-image {
			width: 100%;
			height: 100%;
		}
		
		.avatar-overlay {
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			background-color: rgba(0, 0, 0, 0.5);
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			opacity: 0;
			transition: opacity 0.3s;
			
			.camera-icon {
				font-size: 48rpx;
				color: $text-color-white;
				margin-bottom: $spacing-xs;
			}
			
			.avatar-tip {
				font-size: $font-size-sm;
				color: $text-color-white;
			}
		}
		
		&:active .avatar-overlay {
			opacity: 1;
		}
	}
}

.form-section {
	background-color: $bg-color-white;
	border-radius: $border-radius-lg;
	padding: $spacing-lg;
	margin-bottom: $spacing-xl;
	
	.form-item {
		margin-bottom: $spacing-lg;
		
		&:last-child {
			margin-bottom: 0;
		}
		
		.form-label {
			display: block;
			font-size: $font-size-lg;
			font-weight: 500;
			color: $text-color-primary;
			margin-bottom: $spacing-base;
		}
		
		.form-input {
			width: 100%;
			height: 88rpx;
			border: 1rpx solid $border-color;
			border-radius: $border-radius-base;
			padding: 0 $spacing-base;
			font-size: $font-size-base;
			background-color: $bg-color-white;
			
			&:focus {
				border-color: $primary-color;
			}
			
			&:disabled {
				background-color: $bg-color-gray;
				color: $text-color-disabled;
			}
		}
		
		.phone-input-wrapper {
			display: flex;
			align-items: center;
			gap: $spacing-base;
			
			.phone-input {
				flex: 1;
			}
			
			.bind-phone-btn {
				height: 88rpx;
				padding: 0 $spacing-lg;
				background-color: $primary-color;
				color: $text-color-white;
				border-radius: $border-radius-base;
				font-size: $font-size-base;
				white-space: nowrap;
			}
		}
		
		.role-selector {
			display: flex;
			gap: $spacing-base;
			
			.role-option {
				flex: 1;
				display: flex;
				flex-direction: column;
				align-items: center;
				padding: $spacing-lg;
				border: 2rpx solid $border-color;
				border-radius: $border-radius-base;
				transition: all 0.3s;
				
				&.active {
					border-color: $primary-color;
					background-color: rgba($primary-color, 0.05);
				}
				
				.role-icon {
					font-size: 48rpx;
					margin-bottom: $spacing-sm;
				}
				
				.role-text {
					font-size: $font-size-base;
					color: $text-color-primary;
				}
			}
		}
	}
}

.save-section {
	margin-bottom: $spacing-lg;
	
	.save-btn {
		width: 100%;
		height: 96rpx;
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

.skip-section {
	text-align: center;
	
	.skip-btn {
		font-size: $font-size-base;
		color: $text-color-placeholder;
		text-decoration: underline;
	}
}
</style>
