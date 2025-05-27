/**
 * 用户状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/api/auth'
import { getUserInfo, clearLoginInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref<User | null>(null)
  const token = ref<string>('')
  const isLoggedIn = ref<boolean>(false)

  // 计算属性
  const isExpert = computed(() => {
    return userInfo.value?.isExpert === 1
  })

  const isAdmin = computed(() => {
    return false // 小程序端没有管理员角色
  })

  const nickname = computed(() => {
    return userInfo.value?.nickname || '未设置'
  })

  const avatar = computed(() => {
    return userInfo.value?.avatar || '/static/images/default_avatar_svg.svg'
  })

  const balance = computed(() => {
    return userInfo.value?.balance || 0
  })

  // 方法
  const setUserInfo = (user: User) => {
    userInfo.value = user
    uni.setStorageSync('userInfo', user)
  }

  const setToken = (newToken: string) => {
    token.value = newToken
    uni.setStorageSync('token', newToken)
    isLoggedIn.value = true
  }

  const login = (newToken: string, user: User) => {
    setToken(newToken)
    setUserInfo(user)
  }

  const logout = () => {
    userInfo.value = null
    token.value = ''
    isLoggedIn.value = false
    clearLoginInfo()
  }

  const updateUserInfo = (updates: Partial<User>) => {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...updates }
      uni.setStorageSync('userInfo', userInfo.value)
    }
  }

  const initUserInfo = () => {
    const savedToken = uni.getStorageSync('token')
    const savedUserInfo = uni.getStorageSync('userInfo')

    if (savedToken && savedUserInfo) {
      token.value = savedToken
      userInfo.value = savedUserInfo
      isLoggedIn.value = true
    }
  }

  const refreshUserInfo = async () => {
    try {
      const user = await getUserInfo()
      setUserInfo(user)
      return user
    } catch (error) {
      console.error('刷新用户信息失败:', error)
      logout()
      throw error
    }
  }

  return {
    // 状态
    userInfo,
    token,
    isLoggedIn,

    // 计算属性
    isExpert,
    isAdmin,
    nickname,
    avatar,
    balance,

    // 方法
    setUserInfo,
    setToken,
    login,
    logout,
    updateUserInfo,
    initUserInfo,
    refreshUserInfo
  }
})
