<template>
  <view class="apply-container">
    <scroll-view class="apply-scroll" scroll-y>
      <!-- 申请说明 -->
      <view class="apply-notice">
        <view class="notice-header">
          <image class="notice-icon" src="/static/icons/info.svg"></image>
          <text class="notice-title">申请成为达人</text>
        </view>
        <text class="notice-content">
          成为达人后，您可以接受用户的服务订单，通过提供专业服务获得收益。请认真填写以下信息，我们将在1-3个工作日内完成审核。
        </text>
      </view>

      <!-- 申请表单 -->
      <view class="apply-form">
        <!-- 基本信息 -->
        <view class="form-section">
          <view class="section-title">基本信息</view>

          <view class="form-item">
            <text class="form-label"
              >达人名称 <text class="required">*</text></text
            >
            <input
              class="form-input"
              v-model="formData.expertName"
              placeholder="请输入您的达人名称"
              maxlength="50"
            />
          </view>

          <view class="form-item">
            <text class="form-label"
              >服务分类 <text class="required">*</text></text
            >
            <view class="form-picker" @click="showCategoryPicker = true">
              <text
                class="picker-text"
                :class="{ placeholder: !selectedCategory }"
              >
                {{
                  selectedCategory ? selectedCategory.name : "请选择服务分类"
                }}
              </text>
              <image
                class="picker-arrow"
                src="/static/icons/arrow-right.svg"
              ></image>
            </view>
          </view>

          <view class="form-item">
            <text class="form-label"
              >个人简介 <text class="required">*</text></text
            >
            <textarea
              class="form-textarea"
              v-model="formData.description"
              placeholder="请简要介绍您的专业技能、经验和服务特色"
              maxlength="500"
            ></textarea>
            <text class="char-count"
              >{{ formData.description.length }}/500</text
            >
          </view>
        </view>

        <!-- 服务定价 -->
        <view class="form-section">
          <view class="section-title">服务定价</view>

          <view class="price-range">
            <view class="form-item half">
              <text class="form-label"
                >最低价格 <text class="required">*</text></text
              >
              <view class="price-input">
                <text class="price-symbol">¥</text>
                <input
                  class="form-input"
                  v-model="formData.priceMin"
                  placeholder="0"
                  type="digit"
                />
              </view>
            </view>

            <view class="price-separator">-</view>

            <view class="form-item half">
              <text class="form-label"
                >最高价格 <text class="required">*</text></text
              >
              <view class="price-input">
                <text class="price-symbol">¥</text>
                <input
                  class="form-input"
                  v-model="formData.priceMax"
                  placeholder="0"
                  type="digit"
                />
              </view>
            </view>
          </view>

          <view class="price-tip">
            <image class="tip-icon" src="/static/icons/info.svg"></image>
            <text class="tip-text">建议根据服务复杂度和市场行情合理定价</text>
          </view>
        </view>

        <!-- 头像上传 -->
        <view class="form-section">
          <view class="section-title">达人头像</view>

          <view class="avatar-upload">
            <view class="avatar-preview" @click="chooseAvatar">
              <image
                v-if="formData.avatar"
                class="avatar-image"
                :src="formData.avatar"
                mode="aspectFill"
              ></image>
              <view v-else class="avatar-placeholder">
                <image
                  class="upload-icon"
                  src="/static/icons/camera.svg"
                ></image>
                <text class="upload-text">上传头像</text>
              </view>
            </view>
            <text class="avatar-tip"
              >建议上传清晰的个人照片，有助于提高接单成功率</text
            >
          </view>
        </view>

        <!-- 协议同意 -->
        <view class="agreement-section">
          <view class="agreement-item" @click="toggleAgreement">
            <image
              class="checkbox"
              :src="
                isAgreed
                  ? '/static/icons/checkbox-checked.svg'
                  : '/static/icons/checkbox.svg'
              "
            ></image>
            <text class="agreement-text">
              我已阅读并同意
              <text class="agreement-link" @click.stop="viewAgreement"
                >《达人服务协议》</text
              >
            </text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部按钮 -->
    <view class="bottom-actions">
      <button
        class="submit-btn"
        :class="{ disabled: !canSubmit }"
        :disabled="!canSubmit"
        @click="submitApplication"
      >
        {{ isSubmitting ? "提交中..." : "提交申请" }}
      </button>
    </view>

    <!-- 分类选择器 -->
    <view
      class="category-picker"
      v-if="showCategoryPicker"
      @click="showCategoryPicker = false"
    >
      <view class="picker-content" @click.stop>
        <view class="picker-header">
          <text class="picker-title">选择服务分类</text>
          <text class="picker-close" @click="showCategoryPicker = false"
            >✕</text
          >
        </view>
        <scroll-view class="picker-list" scroll-y>
          <view
            class="picker-item"
            v-for="category in categoryList"
            :key="category.id"
            @click="selectCategory(category)"
          >
            <image
              class="category-icon"
              :src="category.icon"
              mode="aspectFit"
            ></image>
            <text class="category-name">{{ category.name }}</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useUserStore } from "@/store/user";
import { getCategoryList } from "@/api/home";
import { applyExpert } from "@/api/expert";
import { uploadAvatar } from "@/api/upload";
import {
  showError,
  showSuccess,
  showConfirm,
  chooseImage,
  requireAuth,
} from "@/utils/index";

// 状态
const userStore = useUserStore();
const formData = ref({
  expertName: "",
  categoryId: null,
  description: "",
  priceMin: "",
  priceMax: "",
  avatar: "",
});

const categoryList = ref([]);
const selectedCategory = ref(null);
const showCategoryPicker = ref(false);
const isAgreed = ref(false);
const isSubmitting = ref(false);

// 计算属性
const canSubmit = computed(() => {
  return (
    formData.value.expertName.trim() &&
    formData.value.categoryId &&
    formData.value.description.trim() &&
    formData.value.priceMin &&
    formData.value.priceMax &&
    parseFloat(formData.value.priceMin) <=
      parseFloat(formData.value.priceMax) &&
    isAgreed.value &&
    !isSubmitting.value
  );
});

// 页面加载
onMounted(async () => {
  const isAuthenticated = await requireAuth();
  if (!isAuthenticated) return;
  loadCategoryList();
});

// 加载分类列表
const loadCategoryList = async () => {
  try {
    const categories = await getCategoryList();
    categoryList.value = categories;
  } catch (error) {
    console.error("加载分类列表失败:", error);
    showError("加载分类失败");
  }
};

// 选择分类
const selectCategory = (category) => {
  selectedCategory.value = category;
  formData.value.categoryId = category.id;
  showCategoryPicker.value = false;
};

// 选择头像
const chooseAvatar = async () => {
  try {
    const result = await chooseImage({
      count: 1,
      sizeType: ["compressed"],
      sourceType: ["album", "camera"],
    });

    if (result.tempFilePaths && result.tempFilePaths.length > 0) {
      await uploadAvatarFile(result.tempFilePaths[0]);
    }
  } catch (error) {
    console.error("选择头像失败:", error);
  }
};

// 上传头像
const uploadAvatarFile = async (filePath: string) => {
  try {
    uni.showLoading({ title: "上传中..." });

    const avatarUrl = await uploadAvatar(filePath);
    formData.value.avatar = avatarUrl;

    uni.hideLoading();
    showSuccess("头像上传成功");
  } catch (error) {
    console.error("上传头像失败:", error);
    uni.hideLoading();
    showError("头像上传失败");
  }
};

// 切换协议同意状态
const toggleAgreement = () => {
  isAgreed.value = !isAgreed.value;
};

// 查看协议
const viewAgreement = () => {
  uni.navigateTo({
    url: "/pages/common/webview?url=https://your-domain.com/expert-agreement&title=达人服务协议",
  });
};

// 提交申请
const submitApplication = async () => {
  if (!canSubmit.value) return;

  // 验证价格
  const minPrice = parseFloat(formData.value.priceMin);
  const maxPrice = parseFloat(formData.value.priceMax);

  if (minPrice <= 0 || maxPrice <= 0) {
    showError("价格必须大于0");
    return;
  }

  if (minPrice > maxPrice) {
    showError("最低价格不能大于最高价格");
    return;
  }

  const confirmed = await showConfirm({
    content: "确定提交达人申请吗？提交后将进入审核流程。",
  });

  if (!confirmed) return;

  try {
    isSubmitting.value = true;

    const submitData = {
      expertName: formData.value.expertName.trim(),
      categoryId: formData.value.categoryId,
      description: formData.value.description.trim(),
      priceMin: minPrice,
      priceMax: maxPrice,
      avatar: formData.value.avatar,
    };

    await applyExpert(submitData);

    showSuccess("申请提交成功，请等待审核");

    // 延迟跳转
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  } catch (error) {
    console.error("提交申请失败:", error);
    showError(error.message || "提交失败");
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style lang="scss" scoped>
.apply-container {
  min-height: 100vh;
  background-color: $bg-color-page;
  padding-bottom: 120rpx;
}

.apply-scroll {
  height: calc(100vh - 120rpx);
}

.apply-notice {
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;
  padding: $spacing-lg;

  .notice-header {
    display: flex;
    align-items: center;
    margin-bottom: $spacing-base;

    .notice-icon {
      width: 40rpx;
      height: 40rpx;
      margin-right: $spacing-sm;
    }

    .notice-title {
      font-size: $font-size-lg;
      font-weight: bold;
      color: $text-color-primary;
    }
  }

  .notice-content {
    font-size: $font-size-base;
    color: $text-color-secondary;
    line-height: 1.6;
  }
}

.apply-form {
  background-color: $bg-color-white;
  padding: $spacing-lg;
}

.form-section {
  margin-bottom: $spacing-xl;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  font-size: $font-size-lg;
  font-weight: bold;
  color: $text-color-primary;
  margin-bottom: $spacing-lg;
  padding-bottom: $spacing-sm;
  border-bottom: 2rpx solid $border-color-light;
}

.form-item {
  margin-bottom: $spacing-lg;

  &.half {
    flex: 1;
  }

  .form-label {
    display: block;
    font-size: $font-size-base;
    color: $text-color-primary;
    margin-bottom: $spacing-sm;

    .required {
      color: $error-color;
    }
  }

  .form-input {
    width: 100%;
    height: 80rpx;
    border: 1rpx solid $border-color;
    border-radius: $border-radius-base;
    padding: 0 $spacing-base;
    font-size: $font-size-base;
    color: $text-color-primary;
    background-color: $bg-color-white;
  }

  .form-textarea {
    width: 100%;
    min-height: 160rpx;
    border: 1rpx solid $border-color;
    border-radius: $border-radius-base;
    padding: $spacing-base;
    font-size: $font-size-base;
    color: $text-color-primary;
    background-color: $bg-color-white;
    resize: none;
  }

  .char-count {
    display: block;
    text-align: right;
    font-size: $font-size-sm;
    color: $text-color-placeholder;
    margin-top: $spacing-xs;
  }
}

.form-picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 80rpx;
  border: 1rpx solid $border-color;
  border-radius: $border-radius-base;
  padding: 0 $spacing-base;
  background-color: $bg-color-white;

  .picker-text {
    font-size: $font-size-base;
    color: $text-color-primary;

    &.placeholder {
      color: $text-color-placeholder;
    }
  }

  .picker-arrow {
    width: 24rpx;
    height: 24rpx;
  }
}

.price-range {
  display: flex;
  align-items: flex-end;
  gap: $spacing-base;
}

.price-separator {
  font-size: $font-size-lg;
  color: $text-color-secondary;
  margin-bottom: 40rpx;
}

.price-input {
  position: relative;

  .price-symbol {
    position: absolute;
    left: $spacing-base;
    top: 50%;
    transform: translateY(-50%);
    font-size: $font-size-base;
    color: $text-color-secondary;
    z-index: $z-index-base;
  }

  .form-input {
    padding-left: 60rpx;
  }
}

.price-tip {
  display: flex;
  align-items: center;
  margin-top: $spacing-base;

  .tip-icon {
    width: 24rpx;
    height: 24rpx;
    margin-right: $spacing-xs;
  }

  .tip-text {
    font-size: $font-size-sm;
    color: $text-color-placeholder;
  }
}

.avatar-upload {
  text-align: center;

  .avatar-preview {
    width: 160rpx;
    height: 160rpx;
    margin: 0 auto $spacing-base;
    border-radius: 80rpx;
    overflow: hidden;
    border: 2rpx dashed $border-color;
    display: flex;
    align-items: center;
    justify-content: center;

    .avatar-image {
      width: 100%;
      height: 100%;
    }

    .avatar-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;

      .upload-icon {
        width: 48rpx;
        height: 48rpx;
        margin-bottom: $spacing-xs;
      }

      .upload-text {
        font-size: $font-size-sm;
        color: $text-color-placeholder;
      }
    }
  }

  .avatar-tip {
    font-size: $font-size-sm;
    color: $text-color-placeholder;
  }
}

.agreement-section {
  margin-top: $spacing-xl;
  padding-top: $spacing-lg;
  border-top: 1rpx solid $border-color-light;
}

.agreement-item {
  display: flex;
  align-items: center;

  .checkbox {
    width: 32rpx;
    height: 32rpx;
    margin-right: $spacing-sm;
  }

  .agreement-text {
    font-size: $font-size-base;
    color: $text-color-secondary;

    .agreement-link {
      color: $primary-color;
    }
  }
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: $bg-color-white;
  padding: $spacing-lg;
  border-top: 1rpx solid $border-color-light;

  .submit-btn {
    width: 100%;
    height: 88rpx;
    background-color: $primary-color;
    color: $text-color-white;
    border-radius: $border-radius-base;
    font-size: $font-size-lg;
    font-weight: bold;

    &.disabled {
      background-color: $bg-color-gray;
      color: $text-color-placeholder;
    }
  }
}

.category-picker {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: $bg-color-mask;
  z-index: $z-index-popup;
  display: flex;
  align-items: center;
  justify-content: center;

  .picker-content {
    background-color: $bg-color-white;
    border-radius: $border-radius-lg;
    margin: $spacing-lg;
    max-height: 80vh;
    width: calc(100% - #{$spacing-lg * 2});

    .picker-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $spacing-lg;
      border-bottom: 1rpx solid $border-color-light;

      .picker-title {
        font-size: $font-size-lg;
        font-weight: bold;
        color: $text-color-primary;
      }

      .picker-close {
        font-size: $font-size-xl;
        color: $text-color-placeholder;
      }
    }

    .picker-list {
      max-height: 60vh;

      .picker-item {
        display: flex;
        align-items: center;
        padding: $spacing-lg;
        border-bottom: 1rpx solid $border-color-light;

        &:last-child {
          border-bottom: none;
        }

        .category-icon {
          width: 48rpx;
          height: 48rpx;
          margin-right: $spacing-base;
        }

        .category-name {
          font-size: $font-size-base;
          color: $text-color-primary;
        }
      }
    }
  }
}
</style>
