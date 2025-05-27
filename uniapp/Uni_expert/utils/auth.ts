/**
 * 认证相关工具函数
 */

// 获取token
export const getToken = (): string => {
  return uni.getStorageSync('token') || ''
}

// 设置token
export const setToken = (token: string): void => {
  uni.setStorageSync('token', token)
}

// 移除token
export const removeToken = (): void => {
  uni.removeStorageSync('token')
}

// 检查是否已登录
export const isLoggedIn = (): boolean => {
  const token = getToken()
  const userInfo = uni.getStorageSync('userInfo')
  return !!(token && userInfo)
}

// 清除所有登录信息
export const clearAuth = (): void => {
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')
}

// 获取用户信息
export const getUserInfo = (): any => {
  return uni.getStorageSync('userInfo') || null
}

// 设置用户信息
export const setUserInfo = (userInfo: any): void => {
  uni.setStorageSync('userInfo', userInfo)
}
