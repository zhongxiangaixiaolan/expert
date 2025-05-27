<template>
  <view class="profile-container">
    <scroll-view class="profile-scroll" scroll-y>
      <!-- 用户信息卡片 -->
      <view class="user-card">
        <view class="user-info">
          <view class="avatar-section" @click="changeAvatar">
            <image
              class="user-avatar"
              :src="userInfo.avatar || '/static/images/default-avatar.png'"
              mode="aspectFill"
            ></image>
            <view class="avatar-edit">
              <image class="edit-icon" src="/static/icons/camera.svg"></image>
            </view>
          </view>
          <view class="user-details">
            <text class="user-name">{{
              userInfo.nickname || "未设置昵称"
            }}</text>
            <text class="user-phone">{{
              userInfo.phone || "未绑定手机号"
            }}</text>
            <view class="user-balance">
              <text class="balance-label">余额：</text>
              <text class="balance-amount">¥{{ userInfo.balance || 0 }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 个人信息设置 -->
      <view class="info-section">
        <view class="section-title">个人信息</view>
        <view class="info-list">
          <view class="info-item" @click="editField('nickname')">
            <text class="info-label">昵称</text>
            <view class="info-value">
              <text class="value-text">{{
                userInfo.nickname || "未设置"
              }}</text>
              <image
                class="arrow-icon"
                src="/static/icons/arrow-right.svg"
              ></image>
            </view>
          </view>
          <view class="info-item" @click="editField('phone')">
            <text class="info-label">手机号</text>
            <view class="info-value">
              <text class="value-text">{{ userInfo.phone || "未绑定" }}</text>
              <image
                class="arrow-icon"
                src="/static/icons/arrow-right.svg"
              ></image>
            </view>
          </view>
          <view class="info-item" @click="editField('realName')">
            <text class="info-label">真实姓名</text>
            <view class="info-value">
              <text class="value-text">{{
                userInfo.realName || "未设置"
              }}</text>
              <image
                class="arrow-icon"
                src="/static/icons/arrow-right.svg"
              ></image>
            </view>
          </view>
          <view class="info-item" @click="editField('gender')">
            <text class="info-label">性别</text>
            <view class="info-value">
              <text class="value-text">{{
                getGenderText(userInfo.gender)
              }}</text>
              <image
                class="arrow-icon"
                src="/static/icons/arrow-right.svg"
              ></image>
            </view>
          </view>
        </view>
      </view>

      <!-- 账户安全 -->
      <view class="info-section">
        <view class="section-title">账户安全</view>
        <view class="info-list">
          <view class="info-item" @click="changePassword">
            <text class="info-label">修改密码</text>
            <view class="info-value">
              <image
                class="arrow-icon"
                src="/static/icons/arrow-right.svg"
              ></image>
            </view>
          </view>
          <view class="info-item" @click="bindWechat">
            <text class="info-label">微信绑定</text>
            <view class="info-value">
              <text class="value-text">{{
                userInfo.openid ? "已绑定" : "未绑定"
              }}</text>
              <image
                class="arrow-icon"
                src="/static/icons/arrow-right.svg"
              ></image>
            </view>
          </view>
        </view>
      </view>

      <!-- 其他设置 -->
      <view class="info-section">
        <view class="section-title">其他设置</view>
        <view class="info-list">
          <view class="info-item" @click="clearCache">
            <text class="info-label">清除缓存</text>
            <view class="info-value">
              <text class="value-text">{{ cacheSize }}</text>
              <image
                class="arrow-icon"
                src="/static/icons/arrow-right.svg"
              ></image>
            </view>
          </view>
          <view class="info-item" @click="aboutApp">
            <text class="info-label">关于我们</text>
            <view class="info-value">
              <image
                class="arrow-icon"
                src="/static/icons/arrow-right.svg"
              ></image>
            </view>
          </view>
        </view>
      </view>

      <!-- 退出登录 -->
      <view class="logout-section">
        <button class="logout-btn" @click="logout">退出登录</button>
      </view>
    </scroll-view>

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
          <input
            v-if="editingField !== 'gender'"
            class="modal-input"
            v-model="editValue"
            :placeholder="`请输入${getFieldLabel(editingField)}`"
          />
          <picker
            v-if="editingField === 'gender'"
            :value="editValue"
            :range="genderOptions"
            range-key="label"
            @change="onGenderChange"
          >
            <view class="picker-view">
              <text>{{ genderOptions[editValue]?.label || "请选择性别" }}</text>
              <image
                class="arrow-icon"
                src="/static/icons/arrow-down.svg"
              ></image>
            </view>
          </picker>
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
import { getUserProfile, updateUserProfile } from "@/api/user";
import { uploadAvatar } from "@/api/upload";
import {
  showError,
  showSuccess,
  chooseImage,
  requireAuth,
} from "@/utils/index";

// 状态
const userStore = useUserStore();
const userInfo = ref({});
const showEditModal = ref(false);
const editingField = ref("");
const editValue = ref("");
const cacheSize = ref("0KB");

// 性别选项
const genderOptions = [
  { value: 0, label: "未设置" },
  { value: 1, label: "男" },
  { value: 2, label: "女" },
];

// 页面加载
onMounted(() => {
  if (!requireAuth()) return;
  loadUserProfile();
  calculateCacheSize();
});

// 加载用户信息
const loadUserProfile = async () => {
  try {
    const profile = await getUserProfile();
    userInfo.value = profile;
  } catch (error) {
    console.error("加载用户信息失败:", error);
    showError("加载失败");
  }
};

// 计算缓存大小
const calculateCacheSize = () => {
  // 模拟计算缓存大小
  cacheSize.value = "2.5MB";
};

// 获取性别文本
const getGenderText = (gender) => {
  const option = genderOptions.find((opt) => opt.value === gender);
  return option?.label || "未设置";
};

// 获取字段标签
const getFieldLabel = (field) => {
  const labelMap = {
    nickname: "昵称",
    phone: "手机号",
    realName: "真实姓名",
    gender: "性别",
  };
  return labelMap[field] || "";
};

// 编辑字段
const editField = (field) => {
  editingField.value = field;
  if (field === "gender") {
    editValue.value = userInfo.value.gender || 0;
  } else {
    editValue.value = userInfo.value[field] || "";
  }
  showEditModal.value = true;
};

// 性别选择变化
const onGenderChange = (e) => {
  editValue.value = e.detail.value;
};

// 确认编辑
const confirmEdit = async () => {
  try {
    const updateData = {
      [editingField.value]: editValue.value,
    };

    await updateUserProfile(updateData);
    userInfo.value[editingField.value] = editValue.value;

    showEditModal.value = false;
    showSuccess("修改成功");
  } catch (error) {
    console.error("修改失败:", error);
    showError(error.message || "修改失败");
  }
};

// 更换头像
const changeAvatar = async () => {
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
    await updateUserProfile({ avatar: avatarUrl });

    userInfo.value.avatar = avatarUrl;
    uni.hideLoading();
    showSuccess("头像更新成功");
  } catch (error) {
    console.error("上传头像失败:", error);
    uni.hideLoading();
    showError("头像上传失败");
  }
};

// 修改密码
const changePassword = () => {
  uni.navigateTo({
    url: "/pages/user/change-password",
  });
};

// 绑定微信
const bindWechat = () => {
  if (userInfo.value.openid) {
    showError("已绑定微信");
    return;
  }
  // 实现微信绑定逻辑
  showError("功能开发中");
};

// 清除缓存
const clearCache = () => {
  uni.showModal({
    title: "提示",
    content: "确定要清除缓存吗？",
    success: (res) => {
      if (res.confirm) {
        // 清除缓存逻辑
        cacheSize.value = "0KB";
        showSuccess("缓存清除成功");
      }
    },
  });
};

// 关于我们
const aboutApp = () => {
  uni.navigateTo({
    url: "/pages/common/about",
  });
};

// 退出登录
const logout = () => {
  uni.showModal({
    title: "提示",
    content: "确定要退出登录吗？",
    success: (res) => {
      if (res.confirm) {
        userStore.logout();
        uni.reLaunch({
          url: "/pages/auth/login",
        });
      }
    },
  });
};
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: 100vh;
  background-color: $bg-color-page;
}

.profile-scroll {
  height: 100vh;
  padding-bottom: 120rpx;
}

.user-card {
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;
  padding: $spacing-lg;

  .user-info {
    display: flex;
    align-items: center;

    .avatar-section {
      position: relative;
      margin-right: $spacing-lg;

      .user-avatar {
        width: 120rpx;
        height: 120rpx;
        border-radius: 50%;
      }

      .avatar-edit {
        position: absolute;
        bottom: 0;
        right: 0;
        width: 40rpx;
        height: 40rpx;
        background-color: $primary-color;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;

        .edit-icon {
          width: 24rpx;
          height: 24rpx;
        }
      }
    }

    .user-details {
      flex: 1;

      .user-name {
        display: block;
        font-size: $font-size-xl;
        font-weight: bold;
        color: $text-color-primary;
        margin-bottom: $spacing-xs;
      }

      .user-phone {
        display: block;
        font-size: $font-size-base;
        color: $text-color-secondary;
        margin-bottom: $spacing-base;
      }

      .user-balance {
        display: flex;
        align-items: center;

        .balance-label {
          font-size: $font-size-base;
          color: $text-color-secondary;
        }

        .balance-amount {
          font-size: $font-size-lg;
          font-weight: bold;
          color: $error-color;
        }
      }
    }
  }
}

.info-section {
  background-color: $bg-color-white;
  margin-bottom: $spacing-base;
  padding: $spacing-lg;

  .section-title {
    font-size: $font-size-lg;
    font-weight: bold;
    color: $text-color-primary;
    margin-bottom: $spacing-lg;
    padding-bottom: $spacing-sm;
    border-bottom: 1rpx solid $border-color-light;
  }

  .info-list {
    .info-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: $spacing-base 0;
      border-bottom: 1rpx solid $border-color-light;

      &:last-child {
        border-bottom: none;
      }

      .info-label {
        font-size: $font-size-base;
        color: $text-color-primary;
      }

      .info-value {
        display: flex;
        align-items: center;

        .value-text {
          font-size: $font-size-base;
          color: $text-color-secondary;
          margin-right: $spacing-sm;
        }

        .arrow-icon {
          width: 24rpx;
          height: 24rpx;
        }
      }
    }
  }
}

.logout-section {
  padding: $spacing-lg;

  .logout-btn {
    width: 100%;
    background-color: $error-color;
    color: $text-color-white;
    border: none;
    border-radius: $border-radius-lg;
    padding: $spacing-lg;
    font-size: $font-size-base;
  }
}

.edit-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: $bg-color-mask;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: $z-index-popup;

  .modal-content {
    background-color: $bg-color-white;
    border-radius: $border-radius-lg;
    width: 600rpx;
    max-width: 90%;

    .modal-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
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

      .modal-input {
        width: 100%;
        padding: $spacing-base;
        border: 1rpx solid $border-color;
        border-radius: $border-radius-base;
        font-size: $font-size-base;
      }

      .picker-view {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: $spacing-base;
        border: 1rpx solid $border-color;
        border-radius: $border-radius-base;

        .arrow-icon {
          width: 24rpx;
          height: 24rpx;
        }
      }
    }

    .modal-actions {
      display: flex;
      gap: $spacing-base;
      padding: $spacing-lg;

      .cancel-btn,
      .confirm-btn {
        flex: 1;
        padding: $spacing-base;
        border-radius: $border-radius-base;
        font-size: $font-size-base;
      }

      .cancel-btn {
        background-color: $bg-color-gray;
        color: $text-color-primary;
        border: none;
      }

      .confirm-btn {
        background-color: $primary-color;
        color: $text-color-white;
        border: none;
      }
    }
  }
}
</style>
