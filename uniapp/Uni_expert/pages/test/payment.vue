<template>
  <view class="payment-test">
    <view class="header">
      <text class="title">微信支付测试</text>
    </view>

    <view class="form-section">
      <view class="form-item">
        <text class="label">订单ID:</text>
        <input 
          v-model="paymentForm.orderId" 
          type="number" 
          placeholder="请输入订单ID"
          class="input"
        />
      </view>

      <view class="form-item">
        <text class="label">支付金额:</text>
        <input 
          v-model="paymentForm.paymentAmount" 
          type="digit" 
          placeholder="请输入支付金额"
          class="input"
        />
      </view>

      <view class="form-item">
        <text class="label">支付描述:</text>
        <input 
          v-model="paymentForm.paymentDesc" 
          placeholder="请输入支付描述"
          class="input"
        />
      </view>

      <view class="form-item">
        <text class="label">用户OpenID:</text>
        <input 
          v-model="paymentForm.openid" 
          placeholder="自动获取"
          class="input"
          disabled
        />
      </view>
    </view>

    <view class="button-section">
      <button 
        class="pay-btn" 
        @click="createPayment"
        :disabled="loading"
      >
        {{ loading ? '创建中...' : '创建支付订单' }}
      </button>

      <button 
        class="query-btn" 
        @click="queryPayment"
        :disabled="!paymentNo"
      >
        查询支付状态
      </button>
    </view>

    <view class="result-section" v-if="paymentResult">
      <view class="result-title">支付结果:</view>
      <view class="result-content">
        <text>{{ JSON.stringify(paymentResult, null, 2) }}</text>
      </view>
    </view>

    <view class="log-section">
      <view class="log-title">操作日志:</view>
      <view class="log-content">
        <view 
          v-for="(log, index) in logs" 
          :key="index" 
          class="log-item"
        >
          <text class="log-time">{{ log.time }}</text>
          <text class="log-message">{{ log.message }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { payOrder, queryPaymentStatus } from '@/api/order'

// 状态
const userStore = useUserStore()
const loading = ref(false)
const paymentNo = ref('')
const paymentResult = ref<any>(null)
const logs = ref<Array<{ time: string, message: string }>>([])

// 表单数据
const paymentForm = ref({
  orderId: 1,
  paymentAmount: 0.01,
  paymentDesc: '测试支付订单',
  openid: ''
})

// 添加日志
const addLog = (message: string) => {
  logs.value.unshift({
    time: new Date().toLocaleTimeString(),
    message
  })
}

// 创建支付订单
const createPayment = async () => {
  try {
    loading.value = true
    addLog('开始创建支付订单...')

    // 检查用户登录状态
    if (!userStore.isLoggedIn) {
      uni.showToast({ title: '请先登录', icon: 'none' })
      addLog('用户未登录')
      return
    }

    // 检查openid
    if (!paymentForm.value.openid) {
      uni.showToast({ title: '获取用户openid失败', icon: 'none' })
      addLog('用户openid为空')
      return
    }

    // 构建支付参数
    const paymentData = {
      orderId: Number(paymentForm.value.orderId),
      paymentType: 'WECHAT_PAY',
      paymentAmount: Number(paymentForm.value.paymentAmount),
      paymentDesc: paymentForm.value.paymentDesc,
      openid: paymentForm.value.openid
    }

    addLog(`支付参数: ${JSON.stringify(paymentData)}`)

    // 调用支付接口
    const result = await payOrder(paymentData)
    paymentResult.value = result
    paymentNo.value = result.paymentNo

    addLog(`支付订单创建成功: ${result.paymentNo}`)

    // 如果有支付参数，调用微信支付
    if (result.paymentParams) {
      addLog('调用微信支付...')
      
      uni.requestPayment({
        provider: 'wxpay',
        timeStamp: result.paymentParams.timeStamp,
        nonceStr: result.paymentParams.nonceStr,
        package: result.paymentParams.packageValue,
        signType: result.paymentParams.signType,
        paySign: result.paymentParams.paySign,
        success: (res) => {
          addLog('微信支付成功')
          console.log('支付成功', res)
          uni.showToast({ title: '支付成功', icon: 'success' })
          
          // 延迟查询支付状态
          setTimeout(() => {
            queryPayment()
          }, 2000)
        },
        fail: (err) => {
          addLog(`微信支付失败: ${err.errMsg}`)
          console.error('支付失败', err)
          if (err.errMsg !== 'requestPayment:fail cancel') {
            uni.showToast({ title: '支付失败', icon: 'none' })
          }
        }
      })
    } else {
      addLog('支付参数为空，无法调用微信支付')
    }

  } catch (error: any) {
    addLog(`创建支付订单失败: ${error.message || error}`)
    console.error('创建支付订单失败', error)
    uni.showToast({ title: '创建支付订单失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 查询支付状态
const queryPayment = async () => {
  if (!paymentNo.value) {
    uni.showToast({ title: '请先创建支付订单', icon: 'none' })
    return
  }

  try {
    addLog(`查询支付状态: ${paymentNo.value}`)
    const result = await queryPaymentStatus(paymentNo.value)
    addLog(`支付状态查询结果: ${JSON.stringify(result)}`)
    
    uni.showToast({ 
      title: `支付状态: ${result.paymentStatus}`, 
      icon: 'none' 
    })
  } catch (error: any) {
    addLog(`查询支付状态失败: ${error.message || error}`)
    console.error('查询支付状态失败', error)
    uni.showToast({ title: '查询失败', icon: 'none' })
  }
}

// 初始化
onMounted(() => {
  // 获取用户openid
  if (userStore.userInfo?.openid) {
    paymentForm.value.openid = userStore.userInfo.openid
    addLog(`获取到用户openid: ${userStore.userInfo.openid}`)
  } else {
    addLog('用户openid为空，请确保已登录')
  }
})
</script>

<style scoped>
.payment-test {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  text-align: center;
  margin-bottom: 40rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.form-section {
  background: white;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
}

.label {
  width: 160rpx;
  font-size: 28rpx;
  color: #666;
}

.input {
  flex: 1;
  height: 80rpx;
  padding: 0 20rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.button-section {
  margin-bottom: 30rpx;
}

.pay-btn, .query-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 16rpx;
  font-size: 32rpx;
  margin-bottom: 20rpx;
}

.query-btn {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.result-section, .log-section {
  background: white;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.result-title, .log-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.result-content {
  background: #f8f9fa;
  padding: 20rpx;
  border-radius: 8rpx;
  font-family: monospace;
  font-size: 24rpx;
  word-break: break-all;
}

.log-content {
  max-height: 600rpx;
  overflow-y: auto;
}

.log-item {
  display: flex;
  margin-bottom: 15rpx;
  padding: 15rpx;
  background: #f8f9fa;
  border-radius: 8rpx;
}

.log-time {
  width: 160rpx;
  font-size: 24rpx;
  color: #999;
}

.log-message {
  flex: 1;
  font-size: 26rpx;
  color: #333;
  word-break: break-all;
}
</style>
