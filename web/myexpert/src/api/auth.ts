import request from './request'

// 登录请求参数
export interface LoginParams {
  username: string
  password: string
}

// 登录响应数据
export interface LoginResponse {
  accessToken: string
  tokenType?: string
  expiresIn?: number
  adminInfo: {
    id: number
    username: string
    realName?: string
    email?: string
    phone?: string
    avatar?: string
    role?: string
    roleName?: string
  }
}

// 管理员登录
export const login = (data: LoginParams): Promise<LoginResponse> => {
  return request.post('/admin/auth/login', data)
}

// 管理员登出
export const logout = (): Promise<void> => {
  return request.post('/admin/auth/logout')
}

// 获取当前管理员信息
export const getCurrentAdmin = (): Promise<LoginResponse['adminInfo']> => {
  return request.get('/admin/auth/info')
}
