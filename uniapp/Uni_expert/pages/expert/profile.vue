<template>
  <view class="profile-container">
    <scroll-view class="profile-scroll" scroll-y>
      <!-- 审核状态提示 -->
      <view
        class="audit-status"
        v-if="expertInfo"
        :class="getAuditStatusClass(expertInfo.auditStatus)"
      >
        <image
          class="status-icon"
          :src="getAuditStatusIcon(expertInfo.auditStatus)"
        ></image>
        <view class="status-content">
          <text class="status-title">{{
            getAuditStatusTitle(expertInfo.auditStatus)
          }}</text>
          <text class="status-desc">{{
            getAuditStatusDesc(expertInfo.auditStatus)
          }}</text>
          <text v-if="expertInfo.auditRemark" class="status-remark">{{
            expertInfo.auditRemark
          }}</text>
        </view>
      </view>

      <!-- 基本信息 -->
      <view class="info-section">
        <view class="section-title">基本信息</view>

        <view class="info-item">
          <text class="info-label">达人名称</text>
          <view class="info-value editable" @click="editField('expertName')">
            <text>{{ formData.expertName || "未设置" }}</text>
            <image class="edit-icon" src="/static/icons/edit.svg"></image>
          </view>
        </view>

        <view class="info-item">
          <text class="info-label">服务分类</text>
          <view class="info-value editable" @click="editField('category')">
            <text>{{ selectedCategory?.name || "未设置" }}</text>
            <image class="edit-icon" src="/static/icons/edit.svg"></image>
          </view>
        </view>

        <view class="info-item">
          <text class="info-label">个人简介</text>
          <view class="info-value editable" @click="editField('description')">
            <text class="description-text">{{
              formData.description || "未设置"
            }}</text>
            <image class="edit-icon" src="/static/icons/edit.svg"></image>
          </view>
        </view>
      </view>

      <!-- 服务定价 -->
      <view class="info-section">
        <view class="section-title">服务定价</view>

        <view class="price-range">
          <view class="price-item">
            <text class="price-label">最低价格</text>
            <view class="price-value editable" @click="editField('priceMin')">
              <text>¥{{ formData.priceMin || "0" }}</text>
              <image class="edit-icon" src="/static/icons/edit.svg"></image>
            </view>
          </view>

          <view class="price-separator">-</view>

          <view class="price-item">
            <text class="price-label">最高价格</text>
            <view class="price-value editable" @click="editField('priceMax')">
              <text>¥{{ formData.priceMax || "0" }}</text>
              <image class="edit-icon" src="/static/icons/edit.svg"></image>
            </view>
          </view>
        </view>
      </view>

      <!-- 达人头像 -->
      <view class="info-section">
        <view class="section-title">达人头像</view>

        <view class="avatar-section">
          <view class="avatar-preview" @click="changeAvatar">
            <image
              v-if="formData.avatar"
              class="avatar-image"
              :src="formData.avatar"
              mode="aspectFill"
            ></image>
            <view v-else class="avatar-placeholder">
              <image class="upload-icon" src="/static/icons/camera.svg"></image>
              <text class="upload-text">上传头像</text>
            </view>
            <view class="avatar-overlay">
              <image class="camera-icon" src="/static/icons/camera.svg"></image>
            </view>
          </view>
          <text class="avatar-tip">点击更换头像</text>
        </view>
      </view>

      <!-- 统计信息 -->
      <view class="info-section" v-if="expertInfo">
        <view class="section-title">统计信息</view>

        <view class="stats-grid">
          <view class="stat-item">
            <text class="stat-value">{{ expertInfo.rating || 0 }}</text>
            <text class="stat-label">评分</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">{{ expertInfo.orderCount || 0 }}</text>
            <text class="stat-label">接单数</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">{{ expertInfo.completeCount || 0 }}</text>
            <text class="stat-label">完成数</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">{{ getCompleteRate(expertInfo) }}%</text>
            <text class="stat-label">完成率</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部按钮 -->
    <view class="bottom-actions">
      <button
        class="save-btn"
        :class="{ disabled: !hasChanges || isSubmitting }"
        :disabled="!hasChanges || isSubmitting"
        @click="saveProfile"
      >
        {{ isSubmitting ? "保存中..." : "保存修改" }}
      </button>
    </view>

    <!-- 编辑弹窗 -->
    <view
      class="edit-modal"
      v-if="showEditModal"
      @click="showEditModal = false"
    >
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">编辑{{ getFieldLabel(editingField) }}</text>
          <text class="modal-close" @click="showEditModal = false">✕</text>
        </view>

        <view class="modal-body">
          <!-- 文本输入 -->
          <input
            v-if="editingField === 'expertName'"
            class="modal-input"
            v-model="editValue"
            placeholder="请输入达人名称"
            maxlength="50"
          />

          <!-- 文本域输入 -->
          <textarea
            v-if="editingField === 'description'"
            class="modal-textarea"
            v-model="editValue"
            placeholder="请输入个人简介"
            maxlength="500"
          ></textarea>

          <!-- 价格输入 -->
          <view
            v-if="editingField === 'priceMin' || editingField === 'priceMax'"
            class="price-input"
          >
            <text class="price-symbol">¥</text>
            <input
              class="modal-input price"
              v-model="editValue"
              placeholder="0"
              type="digit"
            />
          </view>

          <!-- 分类选择 -->
          <scroll-view
            v-if="editingField === 'category'"
            class="category-list"
            scroll-y
          >
            <view
              class="category-item"
              v-for="category in categoryList"
              :key="category.id"
              :class="{ active: editValue === category.id }"
              @click="editValue = category.id"
            >
              <image
                class="category-icon"
                :src="category.icon"
                mode="aspectFit"
              ></image>
              <text class="category-name">{{ category.name }}</text>
              <image
                v-if="editValue === category.id"
                class="check-icon"
                src="/static/icons/check.svg"
              ></image>
            </view>
          </scroll-view>
        </view>

        <view class="modal-actions">
          <button class="cancel-btn" @click="showEditModal = false">
            取消
          </button>
          <button class="confirm-btn" @click="confirmEdit">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useUserStore } from "@/store/user";
import { getExpertProfile, updateExpertProfile } from "@/api/expert";
import { getCategoryList } from "@/api/home";
import { uploadAvatar } from "@/api/upload";
import {
  showError,
  showSuccess,
  chooseImage,
  requireAuth,
} from "@/utils/index";

// 状态
const userStore = useUserStore();
const expertInfo = ref(null);
const formData = ref({
  expertName: "",
  categoryId: null,
  description: "",
  priceMin: "",
  priceMax: "",
  avatar: "",
});

const originalData = ref({});
const categoryList = ref([]);
const selectedCategory = ref(null);
const showEditModal = ref(false);
const editingField = ref("");
const editValue = ref("");
const isSubmitting = ref(false);

// 计算属性
const hasChanges = computed(() => {
  return JSON.stringify(formData.value) !== JSON.stringify(originalData.value);
});

// 页面加载
onMounted(() => {
  if (!requireAuth()) return;
  loadExpertProfile();
  loadCategoryList();
});

// 加载达人信息
const loadExpertProfile = async () => {
  try {
    const profile = await getExpertProfile();
    expertInfo.value = profile;

    formData.value = {
      expertName: profile.expertName || "",
      categoryId: profile.categoryId,
      description: profile.description || "",
      priceMin: profile.priceMin?.toString() || "",
      priceMax: profile.priceMax?.toString() || "",
      avatar: profile.avatar || "",
    };

    // 保存原始数据
    originalData.value = JSON.parse(JSON.stringify(formData.value));
  } catch (error) {
    console.error("加载达人信息失败:", error);
    showError("加载失败");
  }
};

// 加载分类列表
const loadCategoryList = async () => {
  try {
    const categories = await getCategoryList();
    categoryList.value = categories;

    // 设置当前选中的分类
    if (formData.value.categoryId) {
      selectedCategory.value = categories.find(
        (cat) => cat.id === formData.value.categoryId
      );
    }
  } catch (error) {
    console.error("加载分类列表失败:", error);
  }
};

// 获取审核状态样式类
const getAuditStatusClass = (status) => {
  const classMap = {
    0: "pending",
    1: "approved",
    2: "rejected",
  };
  return classMap[status] || "pending";
};

// 获取审核状态图标
const getAuditStatusIcon = (status) => {
  const iconMap = {
    0: "/static/icons/clock.svg",
    1: "/static/icons/check-circle.svg",
    2: "/static/icons/close-circle.svg",
  };
  return iconMap[status] || "/static/icons/clock.svg";
};

// 获取审核状态标题
const getAuditStatusTitle = (status) => {
  const titleMap = {
    0: "审核中",
    1: "审核通过",
    2: "审核未通过",
  };
  return titleMap[status] || "审核中";
};

// 获取审核状态描述
const getAuditStatusDesc = (status) => {
  const descMap = {
    0: "您的达人申请正在审核中，请耐心等待",
    1: "恭喜！您的达人申请已通过审核",
    2: "很抱歉，您的达人申请未通过审核",
  };
  return descMap[status] || "";
};

// 获取完成率
const getCompleteRate = (expert) => {
  if (!expert.orderCount || expert.orderCount === 0) {
    return 0;
  }
  return Math.round(((expert.completeCount || 0) / expert.orderCount) * 100);
};

// 编辑字段
const editField = (field) => {
  if (expertInfo.value?.auditStatus === 0) {
    showError("审核期间无法修改资料");
    return;
  }

  editingField.value = field;

  if (field === "category") {
    editValue.value = formData.value.categoryId;
  } else {
    editValue.value = formData.value[field] || "";
  }

  showEditModal.value = true;
};

// 获取字段标签
const getFieldLabel = (field) => {
  const labelMap = {
    expertName: "达人名称",
    category: "服务分类",
    description: "个人简介",
    priceMin: "最低价格",
    priceMax: "最高价格",
  };
  return labelMap[field] || "";
};

// 确认编辑
const confirmEdit = () => {
  if (editingField.value === "category") {
    formData.value.categoryId = editValue.value;
    selectedCategory.value = categoryList.value.find(
      (cat) => cat.id === editValue.value
    );
  } else {
    formData.value[editingField.value] = editValue.value;
  }

  showEditModal.value = false;
};

// 更换头像
const changeAvatar = async () => {
  if (expertInfo.value?.auditStatus === 0) {
    showError("审核期间无法修改头像");
    return;
  }

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
    showSuccess("头像更新成功");
  } catch (error) {
    console.error("上传头像失败:", error);
    uni.hideLoading();
    showError("头像上传失败");
  }
};

// 保存资料
const saveProfile = async () => {
  if (!hasChanges.value || isSubmitting.value) return;

  // 验证价格
  if (formData.value.priceMin && formData.value.priceMax) {
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
  }

  try {
    isSubmitting.value = true;

    const updateData = {
      id: expertInfo.value.id,
      expertName: formData.value.expertName,
      categoryId: formData.value.categoryId,
      description: formData.value.description,
      priceMin: parseFloat(formData.value.priceMin) || 0,
      priceMax: parseFloat(formData.value.priceMax) || 0,
      avatar: formData.value.avatar,
    };

    await updateExpertProfile(updateData);

    // 更新原始数据
    originalData.value = JSON.parse(JSON.stringify(formData.value));

    showSuccess("资料保存成功");
  } catch (error) {
    console.error("保存资料失败:", error);
    showError(error.message || "保存失败");
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: 100vh;
  background-color: $bg-color-page;
  padding-bottom: 120rpx;
}

.profile-scroll {
  height: calc(100vh - 120rpx);
}

.audit-status {
  display: flex;
  align-items: flex-start;
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;
  padding: $spacing-lg;
  border-left: 8rpx solid;

  &.pending {
    border-left-color: $warning-color;
    background-color: #fff7e6;
  }

  &.approved {
    border-left-color: $success-color;
    background-color: #f6ffed;
  }

  &.rejected {
    border-left-color: $error-color;
    background-color: #fff2f0;
  }

  .status-icon {
    width: 48rpx;
    height: 48rpx;
    margin-right: $spacing-base;
    margin-top: 4rpx;
  }

  .status-content {
    flex: 1;

    .status-title {
      display: block;
      font-size: $font-size-lg;
      font-weight: bold;
      margin-bottom: $spacing-xs;

      .pending & {
        color: $warning-color;
      }

      .approved & {
        color: $success-color;
      }

      .rejected & {
        color: $error-color;
      }
    }

    .status-desc {
      display: block;
      font-size: $font-size-base;
      color: $text-color-secondary;
      margin-bottom: $spacing-xs;
    }

    .status-remark {
      display: block;
      font-size: $font-size-sm;
      color: $error-color;
      background-color: rgba(255, 77, 79, 0.1);
      padding: $spacing-sm;
      border-radius: $border-radius-base;
    }
  }
}

.info-section {
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;
  padding: $spacing-lg;
}

.section-title {
  font-size: $font-size-lg;
  font-weight: bold;
  color: $text-color-primary;
  margin-bottom: $spacing-lg;
  padding-bottom: $spacing-sm;
  border-bottom: 2rpx solid $border-color-light;
}

.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: $spacing-lg;

  &:last-child {
    margin-bottom: 0;
  }

  .info-label {
    width: 160rpx;
    font-size: $font-size-base;
    color: $text-color-secondary;
    margin-right: $spacing-base;
    flex-shrink: 0;
  }

  .info-value {
    flex: 1;

    &.editable {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: $spacing-sm $spacing-base;
      border: 1rpx solid $border-color-light;
      border-radius: $border-radius-base;
      background-color: $bg-color-gray;

      .edit-icon {
        width: 32rpx;
        height: 32rpx;
        margin-left: $spacing-sm;
      }
    }

    .description-text {
      line-height: 1.6;
      word-break: break-all;
    }
  }
}

.price-range {
  display: flex;
  align-items: center;
  gap: $spacing-base;

  .price-item {
    flex: 1;

    .price-label {
      display: block;
      font-size: $font-size-base;
      color: $text-color-secondary;
      margin-bottom: $spacing-sm;
    }

    .price-value {
      &.editable {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: $spacing-sm $spacing-base;
        border: 1rpx solid $border-color-light;
        border-radius: $border-radius-base;
        background-color: $bg-color-gray;

        .edit-icon {
          width: 32rpx;
          height: 32rpx;
          margin-left: $spacing-sm;
        }
      }
    }
  }

  .price-separator {
    font-size: $font-size-lg;
    color: $text-color-secondary;
    margin-top: 60rpx;
  }
}

.avatar-section {
  text-align: center;

  .avatar-preview {
    position: relative;
    width: 160rpx;
    height: 160rpx;
    margin: 0 auto $spacing-base;
    border-radius: 80rpx;
    overflow: hidden;
    border: 2rpx solid $border-color-light;

    .avatar-image {
      width: 100%;
      height: 100%;
    }

    .avatar-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background-color: $bg-color-gray;

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

    .avatar-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 60rpx;
      background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
      display: flex;
      align-items: center;
      justify-content: center;

      .camera-icon {
        width: 32rpx;
        height: 32rpx;
      }
    }
  }

  .avatar-tip {
    font-size: $font-size-sm;
    color: $text-color-placeholder;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-base;

  .stat-item {
    text-align: center;

    .stat-value {
      display: block;
      font-size: $font-size-xl;
      font-weight: bold;
      color: $primary-color;
      margin-bottom: $spacing-xs;
    }

    .stat-label {
      display: block;
      font-size: $font-size-sm;
      color: $text-color-secondary;
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

  .save-btn {
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

.edit-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;

  .modal-content {
    background-color: $bg-color-white;
    border-radius: $border-radius-lg;
    margin: $spacing-lg;
    max-height: 80vh;
    width: calc(100% - #{$spacing-lg * 2});

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $spacing-lg;
      border-bottom: 1rpx solid $border-color-light;

      .modal-title {
        font-size: $font-size-lg;
        font-weight: bold;
        color: $text-color-primary;
      }

      .modal-close {
        font-size: $font-size-xl;
        color: $text-color-placeholder;
      }
    }

    .modal-body {
      padding: $spacing-lg;
      max-height: 60vh;
      overflow-y: auto;

      .modal-input {
        width: 100%;
        height: 80rpx;
        border: 1rpx solid $border-color;
        border-radius: $border-radius-base;
        padding: 0 $spacing-base;
        font-size: $font-size-base;
        color: $text-color-primary;

        &.price {
          padding-left: 60rpx;
        }
      }

      .modal-textarea {
        width: 100%;
        min-height: 160rpx;
        border: 1rpx solid $border-color;
        border-radius: $border-radius-base;
        padding: $spacing-base;
        font-size: $font-size-base;
        color: $text-color-primary;
        resize: none;
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
          z-index: 1;
        }
      }

      .category-list {
        max-height: 400rpx;

        .category-item {
          display: flex;
          align-items: center;
          padding: $spacing-lg;
          border-bottom: 1rpx solid $border-color-light;

          &:last-child {
            border-bottom: none;
          }

          &.active {
            background-color: $bg-color-gray;
          }

          .category-icon {
            width: 48rpx;
            height: 48rpx;
            margin-right: $spacing-base;
          }

          .category-name {
            flex: 1;
            font-size: $font-size-base;
            color: $text-color-primary;
          }

          .check-icon {
            width: 32rpx;
            height: 32rpx;
          }
        }
      }
    }

    .modal-actions {
      display: flex;
      gap: $spacing-base;
      padding: $spacing-lg;
      border-top: 1rpx solid $border-color-light;

      .cancel-btn,
      .confirm-btn {
        flex: 1;
        height: 80rpx;
        border-radius: $border-radius-base;
        font-size: $font-size-lg;
      }

      .cancel-btn {
        background-color: $bg-color-gray;
        color: $text-color-secondary;
      }

      .confirm-btn {
        background-color: $primary-color;
        color: $text-color-white;
      }
    }
  }
}
</style>
