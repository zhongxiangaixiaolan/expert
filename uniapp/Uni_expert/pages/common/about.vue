<template>
  <view class="about-container">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar">
      <view class="navbar-content">
        <text class="back-btn" @click="goBack">‹</text>
        <text class="navbar-title">关于我们</text>
        <view class="navbar-placeholder"></view>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <!-- 应用信息 -->
      <view class="app-info-section">
        <view class="app-logo">
          <image class="logo-image" src="/static/images/logo_svg.svg" mode="aspectFit"></image>
        </view>
        <text class="app-name">达人接单</text>
        <text class="app-version">版本 {{ appVersion }}</text>
        <text class="app-description">专业的技能服务平台，连接达人与用户</text>
      </view>

      <!-- 功能介绍 -->
      <view class="feature-section">
        <view class="section-title">功能特色</view>
        <view class="feature-list">
          <view class="feature-item">
            <image class="feature-icon" src="/static/icons/star.svg"></image>
            <view class="feature-content">
              <text class="feature-title">专业达人</text>
              <text class="feature-desc">汇聚各行业专业达人，提供优质服务</text>
            </view>
          </view>
          <view class="feature-item">
            <image class="feature-icon" src="/static/icons/check.svg"></image>
            <view class="feature-content">
              <text class="feature-title">安全保障</text>
              <text class="feature-desc">完善的评价体系和资金保障机制</text>
            </view>
          </view>
          <view class="feature-item">
            <image class="feature-icon" src="/static/icons/message.svg"></image>
            <view class="feature-content">
              <text class="feature-title">便捷沟通</text>
              <text class="feature-desc">实时消息通知，高效沟通协作</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 联系我们 -->
      <view class="contact-section">
        <view class="section-title">联系我们</view>
        <view class="contact-list">
          <view class="contact-item" @click="callPhone">
            <image class="contact-icon" src="/static/icons/phone.svg"></image>
            <view class="contact-content">
              <text class="contact-label">客服电话</text>
              <text class="contact-value">400-123-4567</text>
            </view>
            <image class="arrow-icon" src="/static/icons/arrow-right.svg"></image>
          </view>
          <view class="contact-item" @click="copyEmail">
            <image class="contact-icon" src="/static/icons/message.svg"></image>
            <view class="contact-content">
              <text class="contact-label">邮箱地址</text>
              <text class="contact-value">service@expert.com</text>
            </view>
            <image class="arrow-icon" src="/static/icons/arrow-right.svg"></image>
          </view>
          <view class="contact-item" @click="openWebsite">
            <image class="contact-icon" src="/static/icons/info.svg"></image>
            <view class="contact-content">
              <text class="contact-label">官方网站</text>
              <text class="contact-value">www.expert.com</text>
            </view>
            <image class="arrow-icon" src="/static/icons/arrow-right.svg"></image>
          </view>
        </view>
      </view>

      <!-- 法律信息 -->
      <view class="legal-section">
        <view class="section-title">法律信息</view>
        <view class="legal-list">
          <view class="legal-item" @click="openPrivacyPolicy">
            <text class="legal-text">隐私政策</text>
            <image class="arrow-icon" src="/static/icons/arrow-right.svg"></image>
          </view>
          <view class="legal-item" @click="openUserAgreement">
            <text class="legal-text">用户协议</text>
            <image class="arrow-icon" src="/static/icons/arrow-right.svg"></image>
          </view>
          <view class="legal-item" @click="openServiceTerms">
            <text class="legal-text">服务条款</text>
            <image class="arrow-icon" src="/static/icons/arrow-right.svg"></image>
          </view>
        </view>
      </view>

      <!-- 版权信息 -->
      <view class="copyright-section">
        <text class="copyright-text">© 2024 达人接单平台</text>
        <text class="copyright-text">All Rights Reserved</text>
        <text class="build-info">构建时间：{{ buildTime }}</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { showSuccess, showError } from "@/utils/index";

// 应用信息
const appVersion = ref("1.0.0");
const buildTime = ref("");

// 页面加载
onMounted(() => {
  getBuildTime();
});

// 获取构建时间
const getBuildTime = () => {
  const now = new Date();
  buildTime.value = now.toLocaleDateString('zh-CN');
};

// 返回上一页
const goBack = () => {
  uni.navigateBack();
};

// 拨打电话
const callPhone = () => {
  uni.makePhoneCall({
    phoneNumber: '400-123-4567',
    fail: () => {
      showError('拨打电话失败');
    }
  });
};

// 复制邮箱
const copyEmail = () => {
  uni.setClipboardData({
    data: 'service@expert.com',
    success: () => {
      showSuccess('邮箱地址已复制');
    },
    fail: () => {
      showError('复制失败');
    }
  });
};

// 打开官网
const openWebsite = () => {
  uni.navigateTo({
    url: '/pages/common/webview?url=https://www.expert.com&title=官方网站'
  });
};

// 打开隐私政策
const openPrivacyPolicy = () => {
  uni.navigateTo({
    url: '/pages/common/webview?url=https://www.expert.com/privacy&title=隐私政策'
  });
};

// 打开用户协议
const openUserAgreement = () => {
  uni.navigateTo({
    url: '/pages/common/webview?url=https://www.expert.com/agreement&title=用户协议'
  });
};

// 打开服务条款
const openServiceTerms = () => {
  uni.navigateTo({
    url: '/pages/common/webview?url=https://www.expert.com/terms&title=服务条款'
  });
};
</script>

<style lang="scss" scoped>
.about-container {
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
  padding-bottom: 120rpx;
}

.app-info-section {
  background-color: $bg-color-white;
  padding: $spacing-xl;
  margin-bottom: $spacing-base;
  text-align: center;
  
  .app-logo {
    margin-bottom: $spacing-lg;
    
    .logo-image {
      width: 160rpx;
      height: 160rpx;
    }
  }
  
  .app-name {
    display: block;
    font-size: $font-size-xxl;
    font-weight: bold;
    color: $text-color-primary;
    margin-bottom: $spacing-sm;
  }
  
  .app-version {
    display: block;
    font-size: $font-size-base;
    color: $text-color-secondary;
    margin-bottom: $spacing-lg;
  }
  
  .app-description {
    display: block;
    font-size: $font-size-base;
    color: $text-color-secondary;
    line-height: 1.6;
  }
}

.feature-section,
.contact-section,
.legal-section {
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
}

.feature-list {
  .feature-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: $spacing-lg;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .feature-icon {
      width: 48rpx;
      height: 48rpx;
      margin-right: $spacing-base;
      margin-top: $spacing-xs;
    }
    
    .feature-content {
      flex: 1;
      
      .feature-title {
        display: block;
        font-size: $font-size-base;
        font-weight: bold;
        color: $text-color-primary;
        margin-bottom: $spacing-xs;
      }
      
      .feature-desc {
        display: block;
        font-size: $font-size-sm;
        color: $text-color-secondary;
        line-height: 1.5;
      }
    }
  }
}

.contact-list,
.legal-list {
  .contact-item,
  .legal-item {
    display: flex;
    align-items: center;
    padding: $spacing-base 0;
    border-bottom: 1rpx solid $border-color-light;
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .contact-item {
    .contact-icon {
      width: 40rpx;
      height: 40rpx;
      margin-right: $spacing-base;
    }
    
    .contact-content {
      flex: 1;
      
      .contact-label {
        display: block;
        font-size: $font-size-base;
        color: $text-color-primary;
        margin-bottom: $spacing-xs;
      }
      
      .contact-value {
        display: block;
        font-size: $font-size-sm;
        color: $text-color-secondary;
      }
    }
  }
  
  .legal-item {
    .legal-text {
      flex: 1;
      font-size: $font-size-base;
      color: $text-color-primary;
    }
  }
  
  .arrow-icon {
    width: 24rpx;
    height: 24rpx;
  }
}

.copyright-section {
  text-align: center;
  padding: $spacing-xl;
  
  .copyright-text {
    display: block;
    font-size: $font-size-sm;
    color: $text-color-placeholder;
    margin-bottom: $spacing-xs;
  }
  
  .build-info {
    display: block;
    font-size: $font-size-xs;
    color: $text-color-placeholder;
    margin-top: $spacing-base;
  }
}
</style>
