<template>
  <view class="login-container">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <text class="navbar-title">欢迎使用达人接单</text>
      </view>
    </view>

    <!-- 登录内容 -->
    <view class="login-content">
      <!-- Logo和标题 -->
      <view class="logo-section">
        <image
          class="logo"
          src="/static/images/logo.png"
          mode="aspectFit"
        ></image>
        <text class="app-name">达人接单</text>
        <text class="app-desc">专业的达人服务平台</text>
      </view>

      <!-- 登录按钮 -->
      <view class="login-section">
        <button
          class="wechat-login-btn"
          open-type="getUserInfo"
          @getuserinfo="onGetUserInfo"
          :loading="isLoading"
        >
          <text class="btn-icon">👤</text>
          <text class="btn-text">微信一键登录</text>
        </button>

        <view class="login-tips">
          <text>登录即表示同意</text>
          <text class="link" @click="showPrivacy">《隐私政策》</text>
          <text>和</text>
          <text class="link" @click="showTerms">《用户协议》</text>
        </view>
      </view>

      <!-- 角色选择 -->
      <view class="role-section" v-if="showRoleSelect">
        <view class="role-title">请选择您的身份</view>
        <view class="role-options">
          <view
            class="role-option"
            :class="{ active: selectedRole === 1 }"
            @click="selectRole(1)"
          >
            <view class="role-icon">👤</view>
            <view class="role-info">
              <text class="role-name">普通用户</text>
              <text class="role-desc">寻找专业达人服务</text>
            </view>
          </view>
          <view
            class="role-option"
            :class="{ active: selectedRole === 2 }"
            @click="selectRole(2)"
          >
            <view class="role-icon">⭐</view>
            <view class="role-info">
              <text class="role-name">服务达人</text>
              <text class="role-desc">提供专业技能服务</text>
            </view>
          </view>
        </view>
        <button
          class="confirm-role-btn"
          @click="confirmRole"
          :disabled="!selectedRole"
        >
          确认身份
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useUserStore } from "@/store/user";
import { wechatLogin, saveLoginInfo, getUserInfo } from "@/api/auth";
import { showError, showSuccess } from "@/utils/index";

// 状态
const userStore = useUserStore();
const statusBarHeight = ref(0);
const isLoading = ref(false);
const showRoleSelect = ref(false);
const selectedRole = ref(0);
const tempLoginData = ref<any>(null);

// 页面加载
onMounted(() => {
  // 获取状态栏高度
  const systemInfo = uni.getSystemInfoSync();
  statusBarHeight.value = systemInfo.statusBarHeight || 0;

  // 尝试静默登录
  tryAutoLogin();
});

// 尝试自动登录
const tryAutoLogin = async () => {
  try {
    // 检查是否已有token
    const token = uni.getStorageSync('token');
    const userInfo = uni.getStorageSync('userInfo');

    if (token && userInfo) {
      // 验证token是否有效
      const isValid = await checkTokenValid();
      if (isValid) {
        // token有效，直接跳转到首页
        userStore.login(token, userInfo);
        uni.switchTab({
          url: '/pages/index/index'
        });
        return;
      }
    }

    // 尝试静默登录
    await silentLogin();
  } catch (error) {
    console.log('自动登录失败:', error);
    // 静默失败，不显示错误，让用户手动登录
  }
};

// 静默登录
const silentLogin = async () => {
  return new Promise((resolve, reject) => {
    uni.login({
      provider: "weixin",
      success: async (loginRes) => {
        if (loginRes.errMsg !== "login:ok") {
          reject(new Error("微信登录失败"));
          return;
        }

        try {
          // 只使用code进行静默登录，不需要用户信息
          const loginParams = {
            code: loginRes.code,
            nickname: "", // 空字符串表示静默登录
            avatar: "",
            gender: 0
          };

          const loginResult = await wechatLogin(loginParams);

          if (loginResult.isNewUser) {
            // 新用户需要手动登录获取用户信息
            reject(new Error("新用户需要授权"));
          } else {
            // 老用户静默登录成功
            completeLogin(loginResult);
            resolve(loginResult);
          }
        } catch (error) {
          reject(error);
        }
      },
      fail: reject
    });
  });
};

// 检查token是否有效
const checkTokenValid = async () => {
  try {
    await getUserInfo();
    return true;
  } catch (error) {
    return false;
  }
};

// 获取用户信息
const onGetUserInfo = async (e: any) => {
  console.log("获取用户信息:", e);

  // 检查用户是否授权
  if (!e.detail || e.detail.errMsg !== "getUserInfo:ok") {
    showError("需要授权才能登录");
    return;
  }

  try {
    isLoading.value = true;

    // 获取微信登录code
    const loginRes = await new Promise((resolve, reject) => {
      uni.login({
        provider: "weixin",
        success: resolve,
        fail: reject
      });
    });

    if (!loginRes || loginRes.errMsg !== "login:ok") {
      throw new Error("微信登录失败");
    }

    // 准备登录参数
    const loginParams = {
      code: loginRes.code,
      encryptedData: e.detail.encryptedData || "",
      iv: e.detail.iv || "",
      signature: e.detail.signature || "",
      rawData: e.detail.rawData || "",
      nickname: e.detail.userInfo?.nickName || "微信用户",
      avatar: e.detail.userInfo?.avatarUrl || "",
      gender: e.detail.userInfo?.gender || 0
    };

    console.log("登录参数:", loginParams);

    // 调用登录接口
    const loginResult = await wechatLogin(loginParams);

    if (loginResult.isNewUser) {
      // 新用户需要选择角色
      tempLoginData.value = loginResult;
      showRoleSelect.value = true;
    } else {
      // 老用户直接登录
      completeLogin(loginResult);
    }
  } catch (error) {
    console.error("登录失败:", error);
    const errorMsg = error?.message || error?.errMsg || "登录失败，请重试";
    showError(errorMsg);
  } finally {
    isLoading.value = false;
  }
};

// 选择角色
const selectRole = (role: number) => {
  selectedRole.value = role;
};

// 确认角色
const confirmRole = async () => {
  if (!selectedRole.value || !tempLoginData.value) return;

  try {
    isLoading.value = true;

    // 更新用户角色
    const updatedUser = {
      ...tempLoginData.value.userInfo,
      roleType: selectedRole.value,
    };

    // 这里应该调用更新用户角色的接口
    // await updateUserRole(selectedRole.value)

    const loginResult = {
      ...tempLoginData.value,
      userInfo: updatedUser,
    };

    completeLogin(loginResult);
  } catch (error) {
    console.error("设置角色失败:", error);
    showError("设置角色失败，请重试");
  } finally {
    isLoading.value = false;
  }
};

// 完成登录
const completeLogin = (loginResult: any) => {
  console.log("完成登录，登录结果：", loginResult);

  // 保存登录信息
  saveLoginInfo(loginResult.token, loginResult.userInfo);
  userStore.login(loginResult.token, loginResult.userInfo);

  showSuccess("登录成功");

  // 跳转到首页
  setTimeout(() => {
    console.log("准备跳转到首页");

    // 使用reLaunch确保清除登录页面栈
    uni.reLaunch({
      url: "/pages/index/index",
      success: () => {
        console.log("跳转首页成功");
      },
      fail: (err) => {
        console.error('跳转首页失败:', err);
        // 如果reLaunch失败，尝试使用switchTab
        uni.switchTab({
          url: "/pages/index/index",
          success: () => {
            console.log("switchTab跳转首页成功");
          },
          fail: (switchErr) => {
            console.error('switchTab跳转首页失败:', switchErr);
            // 最后尝试使用redirectTo
            uni.redirectTo({
              url: "/pages/index/index",
              success: () => {
                console.log("redirectTo跳转首页成功");
              },
              fail: (redirectErr) => {
                console.error('redirectTo跳转首页失败:', redirectErr);
              }
            });
          }
        });
      }
    });
  }, 1500); // 增加延迟时间，确保登录信息保存完成
};

// 显示隐私政策
const showPrivacy = () => {
  uni.navigateTo({
    url: "/pages/common/webview?url=https://your-domain.com/privacy",
  });
};

// 显示用户协议
const showTerms = () => {
  uni.navigateTo({
    url: "/pages/common/webview?url=https://your-domain.com/terms",
  });
};
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: $z-index-popup;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);

  .navbar-content {
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: center;

    .navbar-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #ffffff;
    }
  }
}

.login-content {
  padding-top: 200rpx;
  padding: 200rpx 60rpx 60rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-section {
  text-align: center;
  margin-bottom: 120rpx;

  .logo {
    width: 160rpx;
    height: 160rpx;
    margin-bottom: 40rpx;
  }

  .app-name {
    display: block;
    font-size: 48rpx;
    font-weight: bold;
    color: #ffffff;
    margin-bottom: 16rpx;
  }

  .app-desc {
    display: block;
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.8);
  }
}

.login-section {
  width: 100%;

  .wechat-login-btn {
    width: 100%;
    height: 96rpx;
    background: #ffffff;
    border-radius: 48rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 40rpx;
    box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);

    .btn-icon {
      font-size: 36rpx;
      margin-right: 16rpx;
    }

    .btn-text {
      font-size: 32rpx;
      color: #333333;
      font-weight: 500;
    }
  }

  .login-tips {
    text-align: center;
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.7);

    .link {
      color: #ffffff;
      text-decoration: underline;
    }
  }
}

.role-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  border-radius: 32rpx 32rpx 0 0;
  padding: 60rpx 40rpx 40rpx;

  .role-title {
    text-align: center;
    font-size: 36rpx;
    font-weight: bold;
    color: #333333;
    margin-bottom: 60rpx;
  }

  .role-options {
    margin-bottom: 60rpx;

    .role-option {
      display: flex;
      align-items: center;
      padding: 32rpx;
      border: 2rpx solid #e5e5e5;
      border-radius: 16rpx;
      margin-bottom: 24rpx;
      transition: all 0.3s;

      &.active {
        border-color: $primary-color;
        background: rgba(0, 122, 255, 0.05);
      }

      .role-icon {
        font-size: 48rpx;
        margin-right: 24rpx;
      }

      .role-info {
        flex: 1;

        .role-name {
          display: block;
          font-size: 32rpx;
          font-weight: 500;
          color: #333333;
          margin-bottom: 8rpx;
        }

        .role-desc {
          display: block;
          font-size: 24rpx;
          color: #666666;
        }
      }
    }
  }

  .confirm-role-btn {
    width: 100%;
    height: 88rpx;
    background: $primary-color;
    color: #ffffff;
    border-radius: 44rpx;
    font-size: 32rpx;
    font-weight: 500;

    &[disabled] {
      background: #cccccc;
    }
  }
}
</style>
