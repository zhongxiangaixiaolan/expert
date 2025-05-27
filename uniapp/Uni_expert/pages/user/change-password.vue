<template>
  <view class="change-password-container">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar">
      <view class="navbar-content">
        <text class="back-btn" @click="goBack">‹</text>
        <text class="navbar-title">修改密码</text>
        <view class="navbar-placeholder"></view>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <view class="form-container">
        <view class="form-section">
          <view class="form-item">
            <text class="form-label">当前密码</text>
            <input
              class="form-input"
              type="password"
              v-model="formData.oldPassword"
              placeholder="请输入当前密码"
              maxlength="20"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">新密码</text>
            <input
              class="form-input"
              type="password"
              v-model="formData.newPassword"
              placeholder="请输入新密码（6-20位）"
              maxlength="20"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">确认新密码</text>
            <input
              class="form-input"
              type="password"
              v-model="formData.confirmPassword"
              placeholder="请再次输入新密码"
              maxlength="20"
            />
          </view>
        </view>

        <view class="password-tips">
          <view class="tips-title">密码要求：</view>
          <view class="tips-list">
            <text class="tip-item">• 长度为6-20位字符</text>
            <text class="tip-item">• 建议包含字母、数字和特殊字符</text>
            <text class="tip-item">• 不能与当前密码相同</text>
          </view>
        </view>

        <view class="form-actions">
          <button class="submit-btn" @click="submitForm" :disabled="!canSubmit">
            确认修改
          </button>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { changePassword } from "@/api/user";
import { showError, showSuccess, requireAuth } from "@/utils/index";

// 表单数据
const formData = ref({
  oldPassword: "",
  newPassword: "",
  confirmPassword: ""
});

// 页面加载
onMounted(() => {
  if (!requireAuth()) return;
});

// 计算属性 - 是否可以提交
const canSubmit = computed(() => {
  return formData.value.oldPassword.length >= 6 &&
         formData.value.newPassword.length >= 6 &&
         formData.value.confirmPassword.length >= 6;
});

// 验证表单
const validateForm = () => {
  const { oldPassword, newPassword, confirmPassword } = formData.value;
  
  if (!oldPassword) {
    showError("请输入当前密码");
    return false;
  }
  
  if (oldPassword.length < 6) {
    showError("当前密码长度不能少于6位");
    return false;
  }
  
  if (!newPassword) {
    showError("请输入新密码");
    return false;
  }
  
  if (newPassword.length < 6 || newPassword.length > 20) {
    showError("新密码长度必须为6-20位");
    return false;
  }
  
  if (!confirmPassword) {
    showError("请确认新密码");
    return false;
  }
  
  if (newPassword !== confirmPassword) {
    showError("两次输入的新密码不一致");
    return false;
  }
  
  if (oldPassword === newPassword) {
    showError("新密码不能与当前密码相同");
    return false;
  }
  
  return true;
};

// 提交表单
const submitForm = async () => {
  if (!validateForm()) return;
  
  try {
    uni.showLoading({ title: "修改中..." });
    
    await changePassword({
      oldPassword: formData.value.oldPassword,
      newPassword: formData.value.newPassword,
      confirmPassword: formData.value.confirmPassword
    });
    
    uni.hideLoading();
    showSuccess("密码修改成功");
    
    // 延迟返回上一页
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
    
  } catch (error) {
    console.error("修改密码失败:", error);
    uni.hideLoading();
    showError(error.message || "修改失败");
  }
};

// 返回上一页
const goBack = () => {
  uni.navigateBack();
};
</script>

<style lang="scss" scoped>
.change-password-container {
  min-height: 100vh;
  background-color: $bg-color-page;
}

.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: $z-index-popup;
  background-color: $bg-color-white;
  border-bottom: 1rpx solid $border-color-light;
  
  .navbar-content {
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 $spacing-base;
    
    .back-btn {
      font-size: 48rpx;
      color: $text-color-primary;
      width: 80rpx;
    }
    
    .navbar-title {
      font-size: $font-size-lg;
      font-weight: bold;
      color: $text-color-primary;
    }
    
    .navbar-placeholder {
      width: 80rpx;
    }
  }
}

.content-scroll {
  height: 100vh;
  padding-top: 88rpx;
}

.form-container {
  padding: $spacing-lg;
}

.form-section {
  background-color: $bg-color-white;
  border-radius: $border-radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-lg;
  
  .form-item {
    margin-bottom: $spacing-lg;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .form-label {
      display: block;
      font-size: $font-size-base;
      color: $text-color-primary;
      margin-bottom: $spacing-sm;
    }
    
    .form-input {
      width: 100%;
      height: 88rpx;
      background-color: $bg-color-gray;
      border-radius: $border-radius-base;
      padding: 0 $spacing-base;
      font-size: $font-size-base;
      color: $text-color-primary;
      border: 1rpx solid transparent;
      
      &:focus {
        border-color: $primary-color;
        background-color: $bg-color-white;
      }
    }
  }
}

.password-tips {
  background-color: $bg-color-white;
  border-radius: $border-radius-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-lg;
  
  .tips-title {
    font-size: $font-size-base;
    font-weight: bold;
    color: $text-color-primary;
    margin-bottom: $spacing-base;
  }
  
  .tips-list {
    .tip-item {
      display: block;
      font-size: $font-size-sm;
      color: $text-color-secondary;
      line-height: 1.6;
      margin-bottom: $spacing-xs;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

.form-actions {
  .submit-btn {
    width: 100%;
    height: 88rpx;
    background-color: $primary-color;
    color: $text-color-white;
    border: none;
    border-radius: $border-radius-lg;
    font-size: $font-size-base;
    font-weight: bold;
    
    &:disabled {
      background-color: $text-color-disabled;
      color: $text-color-white;
    }
  }
}
</style>
