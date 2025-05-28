/**
 * 应用配置文件
 * 统一管理所有环境变量和配置项
 */

// 环境变量配置
export const ENV_CONFIG = {
  // API基础URL
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',

  // 静态资源基础URL
  STATIC_BASE_URL: import.meta.env.VITE_STATIC_BASE_URL || import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',

  // 开发模式
  DEV: import.meta.env.DEV,

  // 生产模式
  PROD: import.meta.env.PROD,

  // 应用模式
  MODE: import.meta.env.MODE
}

// API路径配置
export const API_PATHS = {
  // 认证相关
  AUTH: {
    LOGIN: '/admin/auth/login',
    LOGOUT: '/admin/auth/logout',
    REFRESH: '/admin/auth/refresh'
  },

  // 个人中心
  PROFILE: {
    GET: '/admin/profile',
    UPDATE: '/admin/profile',
    PASSWORD: '/admin/profile/password',
    AVATAR: '/admin/profile/avatar'
  },

  // 静态资源
  STATIC: {
    AVATARS: '/api/avatars'
  }
}

// 文件上传配置
export const UPLOAD_CONFIG = {
  // 头像上传
  AVATAR: {
    MAX_SIZE: 2 * 1024 * 1024, // 2MB
    ALLOWED_TYPES: ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'],
    ALLOWED_EXTENSIONS: ['jpg', 'jpeg', 'png', 'webp']
  },
  // 轮播图上传
  BANNER: {
    MAX_SIZE: 5 * 1024 * 1024, // 5MB
    ALLOWED_TYPES: ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'],
    ALLOWED_EXTENSIONS: ['jpg', 'jpeg', 'png', 'webp'],
    ASPECT_RATIO: 16 / 9 // 16:9 比例
  }
}

// 应用常量
export const APP_CONFIG = {
  // 应用名称
  NAME: '达人接单管理系统',

  // 版本号
  VERSION: '1.0.0',

  // 分页配置
  PAGINATION: {
    DEFAULT_SIZE: 10,
    SIZE_OPTIONS: [10, 20, 50, 100]
  },

  // 本地存储键名
  STORAGE_KEYS: {
    TOKEN: 'admin_token',
    USER_INFO: 'admin_info',
    THEME: 'theme',
    LANGUAGE: 'language'
  }
}

// URL构建工具函数
export const buildUrl = {
  // 构建API URL
  api: (path: string): string => {
    return path.startsWith('http') ? path : `${ENV_CONFIG.API_BASE_URL}${path}`
  },

  // 构建静态资源URL
  static: (path: string): string => {
    return path.startsWith('http') ? path : `${ENV_CONFIG.STATIC_BASE_URL}${path}`
  },

  // 构建头像URL
  avatar: (fileName?: string): string => {
    if (!fileName) return ''
    if (fileName.startsWith('http')) return fileName
    return `${ENV_CONFIG.STATIC_BASE_URL}${API_PATHS.STATIC.AVATARS}/${fileName}`
  }
}

// 导出默认配置
export default {
  ENV_CONFIG,
  API_PATHS,
  UPLOAD_CONFIG,
  APP_CONFIG,
  buildUrl
}
