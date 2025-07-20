/**
 * 网络请求封装
 */
import { ENV_CONFIG, buildApiUrl, Logger } from '../config/env'

// 请求配置
const config = {
  baseURL: buildApiUrl('/api'), // 使用环境配置构建API URL
  timeout: ENV_CONFIG.API_TIMEOUT,
  header: {
    'Content-Type': 'application/json'
  }
}

// 获取当前环境的baseURL
const getBaseURL = () => {
  return config.baseURL
}

// 请求拦截器
const requestInterceptor = (options: any) => {
  // 添加token
  const token = uni.getStorageSync('token')
  if (token) {
    options.header = {
      ...options.header,
      'Authorization': `Bearer ${token}`
    }
  }

  // 添加基础URL
  if (!options.url.startsWith('http')) {
    options.url = getBaseURL() + options.url
  }

  // 添加请求ID用于调试
  const requestId = Date.now() + Math.random().toString(36).substr(2, 9)
  options.header['X-Request-ID'] = requestId

  console.log(`[请求] ${requestId}:`, {
    url: options.url,
    method: options.method,
    headers: options.header,
    data: options.data
  })

  return options
}

// 响应拦截器
const responseInterceptor = (response: any): Promise<any> => {
  return new Promise((resolve, reject) => {
    const requestId = response.header?.['X-Request-ID'] || 'unknown'
    const { statusCode, data } = response

    Logger.debug(`[响应] ${requestId}:`, {
      statusCode,
      data: typeof data === 'string' ? JSON.parse(data || '{}') : data
    })

    // HTTP状态码检查
    if (statusCode !== 200) {
      Logger.error(`[错误] ${requestId}: HTTP请求失败 ${statusCode}`)
      Logger.error(`[错误详情] ${requestId}:`, {
        url: response.config?.url || 'unknown',
        method: response.config?.method || 'unknown',
        headers: response.header,
        data: response.data
      })

      // 特殊处理403错误
      if (statusCode === 403) {
        Logger.error(`[403错误] ${requestId}: 可能的原因:`)
        Logger.error('1. CORS配置问题')
        Logger.error('2. 后端权限配置问题')
        Logger.error('3. 请求头缺失或错误')
      }

      reject({
        statusCode,
        message: getHttpErrorMessage(statusCode),
        data: response
      })
      return
    }

    // 解析响应数据
    let responseData = data
    if (typeof data === 'string') {
      try {
        responseData = JSON.parse(data)
      } catch (e) {
        console.error(`[错误] ${requestId}: 响应数据解析失败`, e)
        reject({
          message: '响应数据格式错误',
          data: response
        })
        return
      }
    }

    // 业务状态码检查
    if (responseData && responseData.code !== undefined && responseData.code !== 200) {
      console.error(`[错误] ${requestId}: 业务请求失败`, responseData)

      // token过期，跳转登录
      if (responseData.code === 401) {
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
        uni.reLaunch({
          url: '/pages/auth/login'
        })
        reject(responseData)
        return
      }

      // 其他业务错误，不显示toast，让调用方处理
      reject(responseData)
      return
    }

    // 返回数据
    resolve(responseData?.data || responseData || response)
  })
}

// HTTP错误信息映射
const getHttpErrorMessage = (statusCode: number): string => {
  const errorMap: { [key: number]: string } = {
    400: '请求参数错误',
    401: '未授权，请重新登录',
    403: '拒绝访问',
    404: '请求的资源不存在',
    405: '请求方法不允许',
    408: '请求超时',
    500: '服务器内部错误',
    502: '网关错误',
    503: '服务不可用',
    504: '网关超时'
  }
  return errorMap[statusCode] || `请求失败 (${statusCode})`
}

// 封装请求方法
const request = {
  get(url: string, params?: any) {
    return new Promise((resolve, reject) => {
      // 处理GET请求参数，将参数转换为查询字符串
      let requestUrl = url
      if (params && Object.keys(params).length > 0) {
        // 过滤掉undefined值
        const filteredParams = Object.keys(params).reduce((acc: any, key) => {
          if (params[key] !== undefined && params[key] !== null) {
            acc[key] = params[key]
          }
          return acc
        }, {})

        if (Object.keys(filteredParams).length > 0) {
          const queryString = Object.keys(filteredParams)
            .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(filteredParams[key])}`)
            .join('&')
          requestUrl = `${url}${url.includes('?') ? '&' : '?'}${queryString}`
        }
      }

      const options = requestInterceptor({
        url: requestUrl,
        method: 'GET',
        header: config.header,
        timeout: config.timeout
      })

      uni.request({
        ...options,
        success: (res) => {
          responseInterceptor(res).then(resolve).catch(reject)
        },
        fail: (err) => {
          console.error('网络请求失败:', err)
          // 不显示toast，让调用方处理
          reject({
            message: '网络连接失败',
            error: err
          })
        }
      })
    })
  },

  post(url: string, data?: any) {
    return new Promise((resolve, reject) => {
      const options = requestInterceptor({
        url,
        method: 'POST',
        data,
        header: config.header,
        timeout: config.timeout
      })

      uni.request({
        ...options,
        success: (res) => {
          responseInterceptor(res).then(resolve).catch(reject)
        },
        fail: (err) => {
          console.error('网络请求失败:', err)
          reject({
            message: '网络连接失败',
            error: err
          })
        }
      })
    })
  },

  put(url: string, data?: any) {
    return new Promise((resolve, reject) => {
      const options = requestInterceptor({
        url,
        method: 'PUT',
        data,
        header: config.header,
        timeout: config.timeout
      })

      uni.request({
        ...options,
        success: (res) => {
          responseInterceptor(res).then(resolve).catch(reject)
        },
        fail: (err) => {
          console.error('网络请求失败:', err)
          reject({
            message: '网络连接失败',
            error: err
          })
        }
      })
    })
  },

  delete(url: string, data?: any) {
    return new Promise((resolve, reject) => {
      const options = requestInterceptor({
        url,
        method: 'DELETE',
        data,
        header: config.header,
        timeout: config.timeout
      })

      uni.request({
        ...options,
        success: (res) => {
          responseInterceptor(res).then(resolve).catch(reject)
        },
        fail: (err) => {
          console.error('网络请求失败:', err)
          reject({
            message: '网络连接失败',
            error: err
          })
        }
      })
    })
  }
}

export default request

// 通用接口类型定义
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface PageResponse<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}
