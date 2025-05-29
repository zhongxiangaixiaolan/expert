<template>
  <view class="create-order-container">
    <scroll-view class="form-scroll" scroll-y>
      <!-- 达人信息 -->
      <view class="expert-section" v-if="expertInfo">
        <view class="section-title">服务达人</view>
        <view class="expert-card">
          <image
            class="expert-avatar"
            :src="expertInfo.avatar"
            mode="aspectFill"
          ></image>
          <view class="expert-info">
            <text class="expert-name">{{ expertInfo.expertName }}</text>
            <view class="expert-rating">
              <text class="rating-star">⭐</text>
              <text class="rating-score">{{ expertInfo.rating }}</text>
            </view>
            <text class="expert-category">{{
              expertInfo.category?.name || "专业服务"
            }}</text>
          </view>
          <text class="expert-price">
            <template
              v-if="
                expertInfo.priceMin &&
                expertInfo.priceMax &&
                expertInfo.priceMin !== expertInfo.priceMax
              "
            >
              ¥{{ expertInfo.priceMin }}-{{ expertInfo.priceMax }}
            </template>
            <template v-else-if="expertInfo.priceMin">
              ¥{{ expertInfo.priceMin }}起
            </template>
            <template v-else> 价格面议 </template>
          </text>
        </view>
      </view>

      <!-- 服务信息 -->
      <view class="service-section">
        <view class="section-title">服务信息</view>
        <view class="form-item">
          <text class="form-label">服务名称</text>
          <input
            class="form-input"
            v-model="formData.serviceName"
            placeholder="请输入服务名称"
            maxlength="50"
          />
        </view>
        <view class="form-item">
          <text class="form-label"
            >任务描述 <text class="required">*</text></text
          >
          <textarea
            class="form-textarea"
            v-model="formData.taskDescription"
            placeholder="请详细描述您的需求，包括具体要求、交付标准等"
            maxlength="500"
            :show-count="true"
          ></textarea>
        </view>
        <view class="form-item">
          <text class="form-label">期望完成时间</text>
          <picker
            mode="datetime"
            :value="formData.expectedTime"
            @change="onTimeChange"
          >
            <view class="picker-input">
              <text v-if="formData.expectedTime">{{
                formatTime(formData.expectedTime)
              }}</text>
              <text v-else class="placeholder">请选择期望完成时间</text>
              <image
                class="picker-arrow"
                src="/static/icons/arrow-right.svg"
              ></image>
            </view>
          </picker>
        </view>
      </view>

      <!-- 联系方式 -->
      <view class="contact-section">
        <view class="section-title">联系方式</view>
        <view class="form-item">
          <text class="form-label">联系电话</text>
          <input
            class="form-input"
            v-model="formData.contactPhone"
            placeholder="请输入联系电话"
            type="number"
            maxlength="11"
          />
        </view>
        <view class="form-item">
          <text class="form-label">微信号</text>
          <input
            class="form-input"
            v-model="formData.contactWechat"
            placeholder="请输入微信号（选填）"
            maxlength="30"
          />
        </view>
      </view>

      <!-- 附件上传 -->
      <view class="attachment-section">
        <view class="section-title">
          <text>需求附件</text>
          <text class="section-desc">（可上传相关图片或文档）</text>
        </view>
        <view class="upload-area">
          <view class="uploaded-files" v-if="uploadedFiles.length > 0">
            <view
              class="file-item"
              v-for="(file, index) in uploadedFiles"
              :key="index"
            >
              <image
                class="file-preview"
                :src="file.url"
                mode="aspectFill"
              ></image>
              <view class="file-remove" @click="removeFile(index)">
                <image
                  class="remove-icon"
                  src="/static/icons/close.svg"
                ></image>
              </view>
            </view>
          </view>
          <view
            class="upload-btn"
            @click="chooseFiles"
            v-if="uploadedFiles.length < 6"
          >
            <image class="upload-icon" src="/static/icons/add.svg"></image>
            <text class="upload-text">添加附件</text>
          </view>
        </view>
        <text class="upload-tip">最多可上传6张图片，每张不超过5MB</text>
      </view>

      <!-- 费用明细 -->
      <view class="cost-section">
        <view class="section-title">费用明细</view>
        <view class="cost-list">
          <view class="cost-item">
            <text class="cost-label">服务费用</text>
            <text class="cost-value">¥{{ expertInfo?.priceMin || 0 }}</text>
          </view>
          <view class="cost-item">
            <text class="cost-label">平台服务费</text>
            <text class="cost-value">¥{{ platformFee }}</text>
          </view>
          <view class="cost-divider"></view>
          <view class="cost-item total">
            <text class="cost-label">总计</text>
            <text class="cost-value">¥{{ totalAmount }}</text>
          </view>
        </view>
      </view>

      <!-- 服务协议 -->
      <view class="agreement-section">
        <view class="agreement-item" @click="toggleAgreement">
          <image
            class="checkbox"
            :src="
              agreedToTerms
                ? '/static/icons/check.svg'
                : '/static/icons/close.svg'
            "
          ></image>
          <text class="agreement-text">
            我已阅读并同意
            <text class="link" @click.stop="viewTerms">《服务协议》</text>
            和
            <text class="link" @click.stop="viewPrivacy">《隐私政策》</text>
          </text>
        </view>
      </view>
    </scroll-view>

    <!-- 底部提交栏 -->
    <view class="submit-bar">
      <view class="price-info">
        <text class="price-label">总计</text>
        <text class="price-value">¥{{ totalAmount }}</text>
      </view>
      <button
        class="submit-btn"
        :disabled="!canSubmit || isSubmitting"
        @click="submitOrder"
      >
        {{ isSubmitting ? "提交中..." : "确认下单" }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from "vue";
import { useUserStore } from "@/store/user";
import { getExpertDetail, type Expert } from "@/api/home";
import { createOrder, type CreateOrderParams } from "@/api/order";
import {
  formatTime,
  showError,
  showSuccess,
  chooseImage,
  requireAuth,
} from "@/utils/index";

// 获取页面参数
const pages = getCurrentPages();
const currentPage = pages[pages.length - 1];
const options = currentPage.options || {};

// 状态
const userStore = useUserStore();
const expertInfo = ref<Expert | null>(null);
const isSubmitting = ref(false);
const agreedToTerms = ref(false);
const uploadedFiles = ref([]);

// 表单数据
const formData = reactive({
  serviceName: "",
  taskDescription: "",
  expectedTime: "",
  contactPhone: "",
  contactWechat: "",
});

// 计算属性
const platformFee = computed(() => {
  const servicePrice = expertInfo.value?.priceMin || 0;
  return Math.round(servicePrice * 0.05); // 5%平台服务费
});

const totalAmount = computed(() => {
  const servicePrice = expertInfo.value?.priceMin || 0;
  return servicePrice + platformFee.value;
});

const canSubmit = computed(() => {
  return (
    formData.taskDescription.trim().length >= 10 &&
    agreedToTerms.value &&
    !isSubmitting.value
  );
});

// 页面加载
onMounted(() => {
  if (!requireAuth()) return;

  const expertId = options.expertId;
  if (expertId) {
    loadExpertInfo(Number(expertId));
  } else {
    showError("缺少达人信息");
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }

  // 初始化用户信息
  initUserInfo();
});

// 加载达人信息
const loadExpertInfo = async (expertId: number) => {
  try {
    const result = await getExpertDetail(expertId);
    expertInfo.value = result;

    // 设置默认服务名称
    formData.serviceName = result.category?.name || "专业服务";
  } catch (error) {
    console.error("加载达人信息失败:", error);
    showError("加载达人信息失败");
  }
};

// 初始化用户信息
const initUserInfo = () => {
  const userInfo = userStore.userInfo;
  if (userInfo) {
    formData.contactPhone = userInfo.phone || "";
    formData.contactWechat = userInfo.wechatNo || "";
  }
};

// 时间选择
const onTimeChange = (e: any) => {
  formData.expectedTime = e.detail.value;
};

// 选择文件
const chooseFiles = async () => {
  try {
    const result = await chooseImage({
      count: 6 - uploadedFiles.value.length,
      sizeType: ["compressed"],
      sourceType: ["album", "camera"],
    });

    if (result.tempFilePaths && result.tempFilePaths.length > 0) {
      // 上传文件
      for (const filePath of result.tempFilePaths) {
        await uploadFile(filePath);
      }
    }
  } catch (error) {
    console.error("选择文件失败:", error);
  }
};

// 上传文件
const uploadFile = async (filePath: string) => {
  try {
    uni.showLoading({ title: "上传中..." });

    // TODO: 调用文件上传接口
    // const result = await uploadOrderAttachment(filePath)

    // 临时处理：直接使用本地路径
    uploadedFiles.value.push({
      url: filePath,
      name: `附件${uploadedFiles.value.length + 1}`,
    });

    uni.hideLoading();
  } catch (error) {
    console.error("上传文件失败:", error);
    uni.hideLoading();
    showError("上传失败");
  }
};

// 移除文件
const removeFile = (index: number) => {
  uploadedFiles.value.splice(index, 1);
};

// 切换协议同意状态
const toggleAgreement = () => {
  agreedToTerms.value = !agreedToTerms.value;
};

// 查看服务协议
const viewTerms = () => {
  uni.navigateTo({
    url: "/pages/common/webview?url=https://your-domain.com/terms",
  });
};

// 查看隐私政策
const viewPrivacy = () => {
  uni.navigateTo({
    url: "/pages/common/webview?url=https://your-domain.com/privacy",
  });
};

// 提交订单
const submitOrder = async () => {
  if (!canSubmit.value) {
    showError("请完善订单信息");
    return;
  }

  try {
    isSubmitting.value = true;

    const orderParams: CreateOrderParams = {
      talentId: Number(options.expertId),
      categoryId: expertInfo.value?.categoryId || 0,
      serviceName: formData.serviceName,
      servicePrice: totalAmount.value,
      taskDescription: formData.taskDescription,
      expectedTime: formData.expectedTime || undefined,
    };

    const result = await createOrder(orderParams);

    showSuccess("订单创建成功");

    // 跳转到订单详情页
    setTimeout(() => {
      uni.redirectTo({
        url: `/pages/order/detail?id=${result.orderId}`,
      });
    }, 1000);
  } catch (error) {
    console.error("创建订单失败:", error);
    showError("创建订单失败");
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style lang="scss" scoped>
.create-order-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: $bg-color-page;
}

.form-scroll {
  flex: 1;
  padding-bottom: 120rpx;
}

.expert-section,
.service-section,
.contact-section,
.attachment-section,
.cost-section,
.agreement-section {
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

.expert-card {
  display: flex;
  align-items: center;

  .expert-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    margin-right: $spacing-base;
  }

  .expert-info {
    flex: 1;

    .expert-name {
      display: block;
      font-size: $font-size-lg;
      font-weight: 500;
      color: $text-color-primary;
      margin-bottom: $spacing-xs;
    }

    .expert-rating {
      display: flex;
      align-items: center;
      margin-bottom: $spacing-xs;

      .rating-star {
        font-size: $font-size-base;
        margin-right: 4rpx;
      }

      .rating-score {
        font-size: $font-size-base;
        color: $secondary-color;
      }
    }

    .expert-category {
      font-size: $font-size-sm;
      color: $text-color-secondary;
    }
  }

  .expert-price {
    font-size: $font-size-xl;
    font-weight: bold;
    color: #ff4757; // 红色，与列表页保持一致
  }
}

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

    .required {
      color: $error-color;
    }
  }

  .form-input,
  .form-textarea {
    width: 100%;
    border: 1rpx solid $border-color;
    border-radius: $border-radius-base;
    padding: $spacing-base;
    font-size: $font-size-base;
    color: $text-color-primary;
    background-color: $bg-color-white;

    &:focus {
      border-color: $primary-color;
    }
  }

  .form-input {
    height: 88rpx;
  }

  .form-textarea {
    height: 200rpx;
    resize: none;
  }

  .picker-input {
    height: 88rpx;
    border: 1rpx solid $border-color;
    border-radius: $border-radius-base;
    padding: 0 $spacing-base;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: $bg-color-white;

    .placeholder {
      color: $text-color-placeholder;
    }

    .picker-arrow {
      width: 32rpx;
      height: 32rpx;
    }
  }
}

.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-base;
  margin-bottom: $spacing-base;

  .uploaded-files {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-base;
  }

  .file-item {
    position: relative;
    width: 160rpx;
    height: 160rpx;

    .file-preview {
      width: 100%;
      height: 100%;
      border-radius: $border-radius-base;
    }

    .file-remove {
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

.upload-tip {
  font-size: $font-size-sm;
  color: $text-color-placeholder;
}

.cost-list {
  .cost-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $spacing-base 0;

    &.total {
      font-weight: bold;
      font-size: $font-size-lg;

      .cost-value {
        color: $primary-color;
      }
    }

    .cost-label {
      font-size: $font-size-base;
      color: $text-color-secondary;
    }

    .cost-value {
      font-size: $font-size-base;
      color: $text-color-primary;
    }
  }

  .cost-divider {
    height: 1rpx;
    background-color: $border-color-light;
    margin: $spacing-base 0;
  }
}

.agreement-section {
  .agreement-item {
    display: flex;
    align-items: flex-start;

    .checkbox {
      width: 40rpx;
      height: 40rpx;
      margin-right: $spacing-base;
      margin-top: 4rpx;
      flex-shrink: 0;
    }

    .agreement-text {
      flex: 1;
      font-size: $font-size-base;
      color: $text-color-secondary;
      line-height: 1.5;

      .link {
        color: $primary-color;
        text-decoration: underline;
      }
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
  display: flex;
  align-items: center;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);

  .price-info {
    flex: 1;

    .price-label {
      display: block;
      font-size: $font-size-sm;
      color: $text-color-secondary;
      margin-bottom: $spacing-xs;
    }

    .price-value {
      display: block;
      font-size: $font-size-xl;
      font-weight: bold;
      color: $primary-color;
    }
  }

  .submit-btn {
    width: 240rpx;
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
