/**
 * 用户个人资料相关API
 */
import request from './request'

// 用户信息接口（与数据库表字段一致）
export interface UserProfile {
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

// 用户信息更新参数
export interface UserUpdateParams {
  nickname?: string
  avatar?: string
  gender?: number
  phone?: string
  realName?: string
}

// 密码修改参数
export interface ChangePasswordParams {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

// 获取用户个人资料
export const getUserProfile = (): Promise<UserProfile> => {
  return request.get('/user/profile')
}

// 更新用户个人资料
export const updateUserProfile = (params: UserUpdateParams): Promise<void> => {
  return request.put('/user/profile', params)
}

// 修改密码
export const changePassword = (params: ChangePasswordParams): Promise<void> => {
  return request.post('/user/change-password', params)
}

// 绑定手机号
export const bindPhone = (params: {
  encryptedData: string
  iv: string
}): Promise<void> => {
  return request.post('/user/bind-phone', params)
}

// 解绑手机号
export const unbindPhone = (): Promise<void> => {
  return request.post('/user/unbind-phone')
}

// 绑定微信
export const bindWechat = (params: {
  code: string
  encryptedData?: string
  iv?: string
}): Promise<void> => {
  return request.post('/user/bind-wechat', params)
}

// 解绑微信
export const unbindWechat = (): Promise<void> => {
  return request.post('/user/unbind-wechat')
}

// 获取账户余额
export const getAccountBalance = (): Promise<{
  balance: number
  totalRecharge: number
  totalConsume: number
}> => {
  return request.get('/user/balance')
}

// 获取用户统计信息
export const getUserStats = (): Promise<{
  orderCount: number
  favoriteCount: number
  messageCount: number
  reviewCount: number
}> => {
  return request.get('/user/stats')
}

// 实名认证
export const realNameAuth = (params: {
  realName: string
  idCard: string
  idCardFront: string
  idCardBack: string
}): Promise<void> => {
  return request.post('/user/real-name-auth', params)
}

// 获取实名认证状态
export const getRealNameAuthStatus = (): Promise<{
  status: number // 0-未认证，1-认证中，2-已认证，3-认证失败
  realName?: string
  idCard?: string
  reason?: string
}> => {
  return request.get('/user/real-name-auth/status')
}

// 注销账户
export const deleteAccount = (params: {
  password: string
  reason: string
}): Promise<void> => {
  return request.post('/user/delete-account', params)
}

// 获取隐私设置
export const getPrivacySettings = (): Promise<{
  showPhone: boolean
  showRealName: boolean
  allowMessage: boolean
  allowNotification: boolean
}> => {
  return request.get('/user/privacy-settings')
}

// 更新隐私设置
export const updatePrivacySettings = (params: {
  showPhone?: boolean
  showRealName?: boolean
  allowMessage?: boolean
  allowNotification?: boolean
}): Promise<void> => {
  return request.put('/user/privacy-settings', params)
}

// 获取登录历史
export const getLoginHistory = (params: {
  page?: number
  size?: number
}): Promise<{
  records: Array<{
    id: number
    loginTime: string
    loginIp: string
    loginDevice: string
    loginLocation: string
  }>
  total: number
  current: number
  size: number
}> => {
  return request.get('/user/login-history', { params })
}

// 清除登录历史
export const clearLoginHistory = (): Promise<void> => {
  return request.delete('/user/login-history')
}

// 获取安全设置
export const getSecuritySettings = (): Promise<{
  hasPassword: boolean
  phoneBinding: boolean
  wechatBinding: boolean
  realNameAuth: boolean
  lastPasswordChange?: string
}> => {
  return request.get('/user/security-settings')
}

// 发送验证码
export const sendVerificationCode = (params: {
  phone: string
  type: 'bind' | 'unbind' | 'change_password'
}): Promise<void> => {
  return request.post('/user/send-verification-code', params)
}

// 验证验证码
export const verifyCode = (params: {
  phone: string
  code: string
  type: 'bind' | 'unbind' | 'change_password'
}): Promise<void> => {
  return request.post('/user/verify-code', params)
}
