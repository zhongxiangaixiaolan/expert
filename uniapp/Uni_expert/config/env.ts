/**
 * 环境配置文件
 * 统一管理不同环境下的配置
 */

// 环境配置接口
export interface EnvConfig {
  // 应用信息
  APP_NAME: string
  APP_VERSION: string

  // API配置
  API_BASE_URL: string
  API_TIMEOUT: number

  // 微信小程序配置
  WECHAT_APPID: string

  // 功能开关
  ENABLE_DEBUG: boolean
  ENABLE_CONSOLE_LOG: boolean
  ENABLE_VCONSOLE: boolean

  // 文件上传配置
  UPLOAD_SIZE_LIMIT: number
  UPLOAD_ALLOWED_TYPES: string[]
}

// 统一配置
const config: EnvConfig = {
  APP_NAME: '达人接单',
  APP_VERSION: '1.0.0',

  API_BASE_URL: 'http://localhost:3001/api', // 通过3001端口代理访问后端API
  API_TIMEOUT: 30000,

  WECHAT_APPID: 'wx52c058048a9d15bb',

  ENABLE_DEBUG: true,
  ENABLE_CONSOLE_LOG: true,
  ENABLE_VCONSOLE: true,

  UPLOAD_SIZE_LIMIT: 10 * 1024 * 1024, // 10MB
  UPLOAD_ALLOWED_TYPES: ['jpg', 'jpeg', 'png', 'gif']
}

/**
 * 获取当前环境配置
 */
export const getEnvConfig = (): EnvConfig => {
  return config
}

/**
 * 当前环境配置
 */
export const ENV_CONFIG = getEnvConfig()

/**
 * 环境判断工具
 */
export const ENV_UTILS = {
  // 是否为开发环境
  isDev: process.env.NODE_ENV === 'development',

  // 是否为生产环境
  isProd: process.env.NODE_ENV === 'production',

  // 是否为测试环境
  isTest: process.env.NODE_ENV === 'test',

  // 是否为微信小程序
  isWeixin:
    // #ifdef MP-WEIXIN
    true
    // #endif
    // #ifndef MP-WEIXIN
    false
    // #endif
  ,

  // 是否为H5
  isH5:
    // #ifdef H5
    true
    // #endif
    // #ifndef H5
    false
    // #endif
  ,

  // 是否为App
  isApp:
    // #ifdef APP-PLUS
    true
    // #endif
    // #ifndef APP-PLUS
    false
    // #endif
}

/**
 * 构建完整的API URL
 */
export const buildApiUrl = (path: string): string => {
  const baseUrl = ENV_CONFIG.API_BASE_URL.replace(/\/$/, '')
  const apiPath = path.startsWith('/') ? path : `/${path}`
  return `${baseUrl}${apiPath}`
}

/**
 * 构建完整的静态资源URL
 */
export const buildStaticUrl = (path: string): string => {
  if (path.startsWith('http')) {
    return path
  }
  const baseUrl = ENV_CONFIG.API_BASE_URL.replace(/\/$/, '')
  const staticPath = path.startsWith('/') ? path : `/${path}`
  return `${baseUrl}${staticPath}`
}

/**
 * 日志工具
 */
export const Logger = {
  log: (...args: any[]) => {
    if (ENV_CONFIG.ENABLE_CONSOLE_LOG) {
      console.log('[达人接单]', ...args)
    }
  },

  warn: (...args: any[]) => {
    if (ENV_CONFIG.ENABLE_CONSOLE_LOG) {
      console.warn('[达人接单]', ...args)
    }
  },

  error: (...args: any[]) => {
    if (ENV_CONFIG.ENABLE_CONSOLE_LOG) {
      console.error('[达人接单]', ...args)
    }
  },

  debug: (...args: any[]) => {
    if (ENV_CONFIG.ENABLE_DEBUG && ENV_CONFIG.ENABLE_CONSOLE_LOG) {
      console.debug('[达人接单]', ...args)
    }
  }
}
