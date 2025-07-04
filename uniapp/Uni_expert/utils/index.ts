/**
 * 工具函数
 */

// 格式化时间
export const formatTime = (timestamp: string | number | Date, format: string = 'YYYY-MM-DD HH:mm:ss'): string => {
  const date = new Date(timestamp)

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

// 格式化相对时间
export const formatRelativeTime = (timestamp: string | number | Date): string => {
  const now = new Date().getTime()
  const time = new Date(timestamp).getTime()
  const diff = now - time

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const month = 30 * day
  const year = 12 * month

  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < month) {
    return `${Math.floor(diff / day)}天前`
  } else if (diff < year) {
    return `${Math.floor(diff / month)}个月前`
  } else {
    return `${Math.floor(diff / year)}年前`
  }
}

// 格式化金额
export const formatMoney = (amount: number | string, decimals: number = 2): string => {
  const num = Number(amount)
  if (isNaN(num)) return '0.00'
  return num.toFixed(decimals)
}

// 格式化手机号
export const formatPhone = (phone: string): string => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 防抖函数
export const debounce = (func: Function, wait: number) => {
  let timeout: NodeJS.Timeout
  return function executedFunction(...args: any[]) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

// 节流函数
export const throttle = (func: Function, limit: number) => {
  let inThrottle: boolean
  return function executedFunction(...args: any[]) {
    if (!inThrottle) {
      func.apply(this, args)
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}

// 深拷贝
export const deepClone = (obj: any): any => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime())
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (typeof obj === 'object') {
    const clonedObj: any = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
}

// 生成唯一ID
export const generateId = (): string => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

// 验证手机号
export const validatePhone = (phone: string): boolean => {
  const phoneReg = /^1[3-9]\d{9}$/
  return phoneReg.test(phone)
}

// 验证邮箱
export const validateEmail = (email: string): boolean => {
  const emailReg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailReg.test(email)
}

// 获取文件扩展名
export const getFileExtension = (filename: string): string => {
  return filename.slice((filename.lastIndexOf('.') - 1 >>> 0) + 2)
}

// 获取图片信息
export const getImageInfo = (src: string): Promise<any> => {
  return new Promise((resolve, reject) => {
    uni.getImageInfo({
      src,
      success: resolve,
      fail: reject
    })
  })
}

// 选择图片
export const chooseImage = (options: any = {}): Promise<any> => {
  return new Promise((resolve, reject) => {
    uni.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      ...options,
      success: resolve,
      fail: reject
    })
  })
}

// 预览图片
export const previewImage = (urls: string[], current: number = 0): void => {
  uni.previewImage({
    urls,
    current
  })
}

// 复制到剪贴板
export const copyToClipboard = (text: string): Promise<void> => {
  return new Promise((resolve, reject) => {
    uni.setClipboardData({
      data: text,
      success: () => {
        uni.showToast({
          title: '复制成功',
          icon: 'success'
        })
        resolve()
      },
      fail: reject
    })
  })
}

// 拨打电话
export const makePhoneCall = (phoneNumber: string): void => {
  uni.makePhoneCall({
    phoneNumber
  })
}

// 显示加载提示
export const showLoading = (title: string = '加载中...'): void => {
  uni.showLoading({
    title,
    mask: true
  })
}

// 隐藏加载提示
export const hideLoading = (): void => {
  uni.hideLoading()
}

// 显示成功提示
export const showSuccess = (title: string): void => {
  uni.showToast({
    title,
    icon: 'success'
  })
}

// 显示错误提示
export const showError = (title: string): void => {
  uni.showToast({
    title,
    icon: 'none'
  })
}

// 确认对话框
export const showConfirm = (options: {
  title?: string
  content: string
  confirmText?: string
  cancelText?: string
}): Promise<boolean> => {
  return new Promise((resolve) => {
    uni.showModal({
      title: options.title || '提示',
      content: options.content,
      confirmText: options.confirmText || '确定',
      cancelText: options.cancelText || '取消',
      success: (res) => {
        resolve(res.confirm)
      }
    })
  })
}

// 检查登录状态
export const checkLogin = (): boolean => {
  const token = uni.getStorageSync('token')
  const userInfo = uni.getStorageSync('userInfo')
  return !!(token && userInfo)
}

// 跳转登录页面
export const goToLogin = (): void => {
  uni.reLaunch({
    url: '/pages/auth/login'
  })
}

// 路由守卫 - 需要登录的页面
export const requireAuth = async (): Promise<boolean> => {
  if (!checkLogin()) {
    console.log('本地登录检查失败，跳转登录页面')
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    setTimeout(() => {
      goToLogin()
    }, 1500)
    return false
  }

  // 验证token是否有效
  try {
    const { getUserInfo } = await import('@/api/auth')
    await getUserInfo()
    console.log('token验证成功')
    return true
  } catch (error) {
    console.log('token验证失败，清除本地数据并跳转登录页面', error)
    // token无效，清除本地数据
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')

    uni.showToast({
      title: '登录已过期，请重新登录',
      icon: 'none'
    })
    setTimeout(() => {
      goToLogin()
    }, 1500)
    return false
  }
}

// 获取当前页面路径
export const getCurrentPagePath = (): string => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  return currentPage.route || ''
}

// 获取页面参数
export const getPageParams = (): any => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  return currentPage.options || {}
}
