import request from './request'
import { buildUrl } from '@/config'

// 管理员个人信息
export interface AdminProfile {
  id: number
  username: string
  realName?: string
  phone?: string
  email?: string
  avatar?: string
  role?: string
  roleName?: string
  status?: number
  lastLoginTime?: string
  lastLoginIp?: string
  createTime?: string
  updateTime?: string
}

// 更新个人信息请求参数
export interface UpdateProfileParams {
  realName?: string
  phone?: string
  email?: string
  avatar?: string
}

// 修改密码请求参数
export interface UpdatePasswordParams {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

// 头像上传响应
export interface UploadAvatarResponse {
  url: string
  fileName: string
  fileSize: number
}

// 获取个人信息
export const getProfile = (): Promise<AdminProfile> => {
  return request.get('/admin/profile')
}

// 更新个人信息
export const updateProfile = (data: UpdateProfileParams): Promise<void> => {
  return request.put('/admin/profile', data)
}

// 修改密码
export const updatePassword = (data: UpdatePasswordParams): Promise<void> => {
  return request.put('/admin/profile/password', data)
}

// 上传头像
export const uploadAvatar = (file: File): Promise<UploadAvatarResponse> => {
  const formData = new FormData()
  formData.append('file', file)

  return request.post('/admin/profile/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
