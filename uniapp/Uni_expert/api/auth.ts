/**
 * 用户认证相关API
 */
import request from './request'

// 用户信息接口（与数据库表字段一致）
export interface User {
  id: number
  openid: string
  unionid?: string
  nickname: string
  avatar?: string
  gender?: number        // 性别：0-未知，1-男，2-女
  phone?: string
  realName?: string      // 真实姓名
  balance: number        // 账户余额
  totalRecharge?: number // 累计充值金额
  totalConsume?: number  // 累计消费金额
  status: number         // 状态：0-禁用，1-正常
  isExpert?: number      // 是否为达人：0-否，1-是
  registerTime?: string  // 注册时间
  lastLoginTime?: string // 最后登录时间
  createTime: string
  updateTime: string
}

// 微信登录参数
export interface WechatLoginParams {
  code: string
  encryptedData?: string
  iv?: string
  signature?: string
  rawData?: string
  nickname?: string
  avatar?: string
  gender?: number
}

// 登录响应
export interface LoginResponse {
  token: string
  tokenType?: string
  expiresIn?: number
  userInfo: User
  isNewUser: boolean
  isExpert?: boolean
}

// 用户信息更新参数
export interface UserUpdateParams {
  nickname?: string
  avatar?: string
  gender?: number
  phone?: string
  realName?: string
}

// 微信登录
export const wechatLogin = (params: WechatLoginParams): Promise<LoginResponse> => {
  return request.post('/user/login', params)
}

// 获取用户信息
export const getUserInfo = (): Promise<User> => {
  return request.get('/user/profile')
}

// 更新用户信息
export const updateUserInfo = (params: UserUpdateParams): Promise<void> => {
  return request.put('/user/profile', params)
}

// 绑定手机号
export const bindPhone = (params: {
  encryptedData: string
  iv: string
}): Promise<void> => {
  return request.post('/user/bind-phone', params)
}

// 上传头像
export const uploadAvatar = (filePath: string): Promise<{ url: string }> => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    const baseURL = 'http://localhost:8080/api'

    uni.uploadFile({
      url: `${baseURL}/user/upload-avatar`,
      filePath,
      name: 'file',
      header: {
        'Authorization': `Bearer ${token}`
      },
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            reject(data)
          }
        } catch (error) {
          reject(error)
        }
      },
      fail: (error) => {
        reject(error)
      }
    })
  })
}

// 退出登录
export const logout = (): Promise<void> => {
  return request.post('/user/logout')
}

// 检查登录状态
export const checkLoginStatus = (): boolean => {
  const token = uni.getStorageSync('token')
  const userInfo = uni.getStorageSync('userInfo')
  return !!(token && userInfo)
}

// 清除登录信息
export const clearLoginInfo = (): void => {
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')
}

// 保存登录信息
export const saveLoginInfo = (token: string, userInfo: User): void => {
  uni.setStorageSync('token', token)
  uni.setStorageSync('userInfo', userInfo)
}

// 获取当前用户信息
export const getCurrentUser = (): User | null => {
  return uni.getStorageSync('userInfo') || null
}
