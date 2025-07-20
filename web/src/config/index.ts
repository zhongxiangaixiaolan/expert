/**
 * 应用配置文件
 * 统一管理所有环境变量和配置项
 */

// 统一环境变量配置
export const ENV_CONFIG = {
  // 应用信息
  APP_TITLE: import.meta.env.VITE_APP_TITLE || '达人接单管理系统',
  APP_VERSION: import.meta.env.VITE_APP_VERSION || '1.0.0',
  APP_DESCRIPTION: import.meta.env.VITE_APP_DESCRIPTION || '达人服务接单平台管理系统',

  // API配置
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090',
  API_TIMEOUT: Number(import.meta.env.VITE_API_TIMEOUT) || 10000,

  // 静态资源配置
  STATIC_BASE_URL: import.meta.env.VITE_STATIC_BASE_URL || 'http://localhost:9090',

  // 文件上传配置
  UPLOAD_SIZE_LIMIT: Number(import.meta.env.VITE_UPLOAD_SIZE_LIMIT) || 10485760,
  UPLOAD_ALLOWED_TYPES: import.meta.env.VITE_UPLOAD_ALLOWED_TYPES || 'jpg,jpeg,png,gif,pdf,doc,docx',

  // 功能开关
  ENABLE_MOCK: import.meta.env.VITE_ENABLE_MOCK === 'true',
  ENABLE_DEVTOOLS: import.meta.env.VITE_ENABLE_DEVTOOLS === 'true',
  ENABLE_CONSOLE_LOG: import.meta.env.VITE_ENABLE_CONSOLE_LOG === 'true',
  ENABLE_VCONSOLE: import.meta.env.VITE_ENABLE_VCONSOLE === 'true',

  // 调试配置
  DEBUG_MODE: import.meta.env.VITE_DEBUG_MODE === 'true',
  LOG_LEVEL: import.meta.env.VITE_LOG_LEVEL || 'info',

  // 环境判断
  DEV: import.meta.env.DEV,
  PROD: import.meta.env.PROD,
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
