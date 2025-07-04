import request from './request'
import type { PageResponse } from './request'

// 用户信息接口
export interface User {
  id: string  // 改为string类型避免大整数精度丢失
  openid?: string
  unionid?: string
  nickname: string
  avatar?: string
  gender?: number
  phone?: string
  realName?: string
  balance?: number
  totalRecharge?: number
  totalConsume?: number
  status: number
  isExpert?: number
  registerTime?: string
  lastLoginTime?: string
  createTime?: string
  updateTime?: string
}

// 用户查询参数
export interface UserQueryParams {
  current?: number
  size?: number
  keyword?: string
  status?: number
  isExpert?: number
  gender?: number
  registerStartTime?: string
  registerEndTime?: string
}

// 用户统计信息
export interface UserStatistics {
  totalUsers: number
  onlineUsers: number
  newUsersToday: number
  newUsersThisMonth: number
  expertUsers: number
  normalUsers: number
}

// 获取用户统计信息
export const getUserStatistics = (): Promise<UserStatistics> => {
  return request.get('/admin/user/statistics')
}

// 分页查询用户列表
export const getUserPage = (params: UserQueryParams): Promise<PageResponse<User>> => {
  return request.get('/admin/user/page', { params })
}

// 获取用户详情
export const getUserDetail = (userId: string): Promise<User> => {
  return request.get(`/admin/user/${userId}`)
}

// 更新用户状态
export const updateUserStatus = (userId: string, status: number): Promise<void> => {
  return request.put(`/admin/user/${userId}/status?status=${status}`)
}

// 批量更新用户状态
export const batchUpdateUserStatus = (userIds: string[], status: number): Promise<void> => {
  return request.put('/admin/user/batch/status', { userIds, status })
}

// 删除用户
export const deleteUser = (userId: string): Promise<void> => {
  return request.delete(`/admin/user/${userId}`)
}

// 批量删除用户
export const batchDeleteUsers = (userIds: string[]): Promise<void> => {
  return request.delete('/admin/user/batch', { data: { userIds } })
}

// 重置用户密码
export const resetUserPassword = (userId: string): Promise<void> => {
  return request.put(`/admin/user/${userId}/reset-password`)
}

// 更新用户余额
export const updateUserBalance = (userId: string, amount: number): Promise<void> => {
  return request.put(`/admin/user/${userId}/balance?amount=${amount}`)
}
