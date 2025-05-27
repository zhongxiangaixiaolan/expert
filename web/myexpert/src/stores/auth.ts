import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginParams, LoginResponse } from '@/api/auth'
import { login as loginApi, logout as logoutApi, getCurrentAdmin } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('admin_token') || '')
  const adminInfo = ref<LoginResponse['adminInfo'] | null>(
    JSON.parse(localStorage.getItem('admin_info') || 'null')
  )
  const isLoggedIn = ref<boolean>(!!token.value)

  // 登录
  const login = async (params: LoginParams) => {
    try {
      const response = await loginApi(params)

      // 保存token和用户信息
      token.value = response.accessToken
      adminInfo.value = response.adminInfo
      isLoggedIn.value = true

      // 持久化存储
      localStorage.setItem('admin_token', response.accessToken)
      localStorage.setItem('admin_info', JSON.stringify(response.adminInfo))

      return response
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  // 登出
  const logout = async () => {
    try {
      await logoutApi()
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      // 清除本地状态
      token.value = ''
      adminInfo.value = null
      isLoggedIn.value = false

      // 清除本地存储
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_info')
    }
  }

  // 获取当前用户信息
  const fetchUserInfo = async () => {
    try {
      const userInfo = await getCurrentAdmin()
      adminInfo.value = userInfo
      localStorage.setItem('admin_info', JSON.stringify(userInfo))
      return userInfo
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 如果获取用户信息失败，可能token已过期，执行登出
      await logout()
      throw error
    }
  }

  // 检查登录状态
  const checkAuth = () => {
    const storedToken = localStorage.getItem('admin_token')
    const storedUserInfo = localStorage.getItem('admin_info')

    if (storedToken && storedUserInfo) {
      token.value = storedToken
      adminInfo.value = JSON.parse(storedUserInfo)
      isLoggedIn.value = true
      return true
    } else {
      token.value = ''
      adminInfo.value = null
      isLoggedIn.value = false
      return false
    }
  }

  return {
    // 状态
    token,
    adminInfo,
    isLoggedIn,

    // 方法
    login,
    logout,
    fetchUserInfo,
    checkAuth
  }
})
