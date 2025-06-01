<template>
  <view class="profile-container">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar">
      <view class="navbar-content">
        <text class="back-btn" @click="goBack">‹</text>
        <text class="navbar-title">个人信息</text>
        <view class="navbar-placeholder"></view>
      </view>
    </view>

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

// 返回上一页
const goBack = () => {
  uni.navigateBack();
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
  background: linear-gradient(
    135deg,
    $primary-color 0%,
    $primary-light 50%,
    $bg-color-page 100%
  );
  position: relative;
}

.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: $z-index-popup;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.9),
    rgba(255, 255, 255, 0.7)
  );
  -webkit-backdrop-filter: blur(24rpx);
  backdrop-filter: blur(24rpx);
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.3);

  .navbar-content {
    height: 60rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 $spacing-base;

    .back-btn {
      font-size: 48rpx;
      color: $primary-color;
      width: 80rpx;
      font-weight: bold;
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

.profile-scroll {
  height: 100vh;
  padding-top: 60rpx;
  padding-bottom: 80rpx;
}

.user-card {
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.9),
    rgba(255, 255, 255, 0.7)
  );
  -webkit-backdrop-filter: blur(24rpx);
  backdrop-filter: blur(24rpx);
  border-radius: $border-radius-2xl;
  margin: $spacing-base $spacing-lg;
  margin-bottom: $spacing-lg;
  padding: $spacing-lg;
  box-shadow: $box-shadow-glass, inset 0 1rpx 0 rgba(255, 255, 255, 0.4);
  border: 1rpx solid rgba(255, 255, 255, 0.3);

  .user-info {
    display: flex;
    align-items: center;

    .avatar-section {
      position: relative;
      margin-right: $spacing-lg;

      .user-avatar {
        width: 100rpx;
        height: 100rpx;
        border-radius: 50%;
        border: 3rpx solid rgba(255, 255, 255, 0.6);
        box-shadow: $box-shadow-lg, 0 0 20rpx rgba(0, 122, 255, 0.3);
        transition: all $transition-base $ease-spring;
      }

      .avatar-edit {
        position: absolute;
        bottom: 0;
        right: 0;
        width: 32rpx;
        height: 32rpx;
        background: linear-gradient(
          135deg,
          $primary-color 0%,
          $primary-light 100%
        );
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: $box-shadow-sm;
        border: 2rpx solid rgba(255, 255, 255, 0.8);
        transition: all $transition-base $ease-spring;

        &:active {
          transform: scale(0.95);
          box-shadow: $box-shadow-xs;
        }

        .edit-icon {
          width: 20rpx;
          height: 20rpx;
          filter: brightness(0) invert(1);
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
      }
    }
  }
}

.info-section {
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.9),
    rgba(255, 255, 255, 0.7)
  );
  -webkit-backdrop-filter: blur(24rpx);
  backdrop-filter: blur(24rpx);
  border-radius: $border-radius-2xl;
  margin: 0 $spacing-lg $spacing-lg;
  padding: $spacing-lg;
  box-shadow: $box-shadow-glass, inset 0 1rpx 0 rgba(255, 255, 255, 0.4);
  border: 1rpx solid rgba(255, 255, 255, 0.3);

  .section-title {
    font-size: $font-size-lg;
    font-weight: bold;
    color: $text-color-primary;
    margin-bottom: $spacing-base;
    padding-bottom: $spacing-xs;
    border-bottom: 1rpx solid rgba(255, 255, 255, 0.3);
    position: relative;

    &::after {
      content: "";
      position: absolute;
      bottom: -1rpx;
      left: 0;
      width: 60rpx;
      height: 3rpx;
      background: linear-gradient(
        90deg,
        $primary-color 0%,
        $primary-light 100%
      );
      border-radius: 2rpx;
    }
  }

  .info-list {
    .info-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: $spacing-base 0;
      border-bottom: 1rpx solid rgba(255, 255, 255, 0.3);
      transition: all $transition-base $ease-spring;
      border-radius: $border-radius-lg;
      margin: 0 -$spacing-sm;
      padding-left: $spacing-sm;
      padding-right: $spacing-sm;

      &:last-child {
        border-bottom: none;
      }

      &:active {
        background: rgba(0, 122, 255, 0.1);
        transform: scale(0.98);
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
  padding: 0 $spacing-lg;
  margin-bottom: $spacing-lg;

  .logout-btn {
    width: 100%;
    height: 80rpx;
    background: linear-gradient(135deg, $error-color 0%, $error-light 100%);
    color: $text-color-white;
    border: none;
    border-radius: $border-radius-2xl;
    font-size: $font-size-lg;
    font-weight: $font-weight-semibold;
    box-shadow: $box-shadow-lg;
    transition: all $transition-base $ease-spring;

    &:active {
      transform: scale(0.98);
      box-shadow: $box-shadow-base;
    }
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
    background: linear-gradient(
      145deg,
      rgba(255, 255, 255, 0.95),
      rgba(255, 255, 255, 0.85)
    );
    -webkit-backdrop-filter: blur(24rpx);
    backdrop-filter: blur(24rpx);
    border-radius: $border-radius-2xl;
    width: 600rpx;
    max-width: 90%;
    box-shadow: $box-shadow-glass, inset 0 1rpx 0 rgba(255, 255, 255, 0.6);
    border: 1rpx solid rgba(255, 255, 255, 0.4);

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
        padding: $spacing-lg;
        border: 1rpx solid rgba(255, 255, 255, 0.4);
        border-radius: $border-radius-lg;
        font-size: $font-size-base;
        background: rgba(255, 255, 255, 0.6);
        -webkit-backdrop-filter: blur(12rpx);
        backdrop-filter: blur(12rpx);
        transition: all $transition-base;

        &:focus {
          border-color: $primary-color;
          background: rgba(255, 255, 255, 0.8);
          box-shadow: 0 0 0 4rpx rgba(0, 122, 255, 0.1);
        }
      }

      .picker-view {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: $spacing-lg;
        border: 1rpx solid rgba(255, 255, 255, 0.4);
        border-radius: $border-radius-lg;
        background: rgba(255, 255, 255, 0.6);
        -webkit-backdrop-filter: blur(12rpx);
        backdrop-filter: blur(12rpx);
        transition: all $transition-base;

        &:active {
          background: rgba(255, 255, 255, 0.8);
          border-color: $primary-color;
        }

        .arrow-icon {
          width: 24rpx;
          height: 24rpx;
          transition: transform $transition-base;
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
        height: 80rpx;
        border-radius: $border-radius-xl;
        font-size: $font-size-base;
        font-weight: $font-weight-medium;
        transition: all $transition-base $ease-spring;
        border: none;
      }

      .cancel-btn {
        background: rgba(255, 255, 255, 0.6);
        color: $text-color-primary;
        -webkit-backdrop-filter: blur(12rpx);
        backdrop-filter: blur(12rpx);
        border: 1rpx solid rgba(255, 255, 255, 0.4);

        &:active {
          transform: scale(0.98);
          background: rgba(255, 255, 255, 0.8);
        }
      }

      .confirm-btn {
        background: linear-gradient(
          135deg,
          $primary-color 0%,
          $primary-light 100%
        );
        color: $text-color-white;
        border: none;
        box-shadow: $box-shadow-sm;
        transition: all $transition-base $ease-spring;

        &:active {
          transform: scale(0.98);
          box-shadow: $box-shadow-xs;
        }
      }
    }
  }
}
</style>
