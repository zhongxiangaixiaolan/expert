import { getToken } from '@/utils/auth'

// API基础URL配置
const API_BASE_URL = 'http://localhost:8080/api'

// 上传文件响应
export interface UploadResponse {
  url: string
  fileName: string
  fileSize: number
}

/**
 * 上传头像
 */
export const uploadAvatar = (filePath: string): Promise<string> => {
  return new Promise((resolve, reject) => {
    const token = getToken()

    uni.uploadFile({
      url: `${API_BASE_URL}/files/upload/avatar`,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': `Bearer ${token}`
      },
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data.url)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        } catch (error) {
          reject(new Error('上传响应解析失败'))
        }
      },
      fail: (error) => {
        reject(new Error(error.errMsg || '上传失败'))
      }
    })
  })
}

/**
 * 上传完成截图
 */
export const uploadCompletionScreenshot = (filePath: string): Promise<string> => {
  return new Promise((resolve, reject) => {
    const token = getToken()

    uni.uploadFile({
      url: `${API_BASE_URL}/files/upload/screenshot`,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': `Bearer ${token}`
      },
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data.url)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        } catch (error) {
          reject(new Error('上传响应解析失败'))
        }
      },
      fail: (error) => {
        reject(new Error(error.errMsg || '上传失败'))
      }
    })
  })
}

/**
 * 上传通用文件
 */
export const uploadFile = (filePath: string, type: string = 'common'): Promise<UploadResponse> => {
  return new Promise((resolve, reject) => {
    const token = getToken()

    uni.uploadFile({
      url: `${API_BASE_URL}/files/upload/${type}`,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': `Bearer ${token}`
      },
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        } catch (error) {
          reject(new Error('上传响应解析失败'))
        }
      },
      fail: (error) => {
        reject(new Error(error.errMsg || '上传失败'))
      }
    })
  })
}

/**
 * 批量上传文件
 */
export const uploadMultipleFiles = async (filePaths: string[], type: string = 'common'): Promise<string[]> => {
  const uploadPromises = filePaths.map(filePath => uploadFile(filePath, type))

  try {
    const results = await Promise.all(uploadPromises)
    return results.map(result => result.url)
  } catch (error) {
    throw new Error('批量上传失败')
  }
}

/**
 * 获取文件上传进度
 */
export const uploadFileWithProgress = (
  filePath: string,
  type: string = 'common',
  onProgress?: (progress: number) => void
): Promise<UploadResponse> => {
  return new Promise((resolve, reject) => {
    const token = getToken()

    const uploadTask = uni.uploadFile({
      url: `${API_BASE_URL}/files/upload/${type}`,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': `Bearer ${token}`
      },
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        } catch (error) {
          reject(new Error('上传响应解析失败'))
        }
      },
      fail: (error) => {
        reject(new Error(error.errMsg || '上传失败'))
      }
    })

    // 监听上传进度
    if (onProgress) {
      uploadTask.onProgressUpdate((res) => {
        onProgress(res.progress)
      })
    }
  })
}

/**
 * 压缩图片并上传
 */
export const compressAndUploadImage = (
  filePath: string,
  type: string = 'avatar',
  quality: number = 80
): Promise<string> => {
  return new Promise((resolve, reject) => {
    // 先压缩图片
    uni.compressImage({
      src: filePath,
      quality: quality,
      success: (compressRes) => {
        // 压缩成功后上传
        uploadAvatar(compressRes.tempFilePath)
          .then(resolve)
          .catch(reject)
      },
      fail: (error) => {
        // 压缩失败，直接上传原图
        console.warn('图片压缩失败，使用原图上传:', error)
        uploadAvatar(filePath)
          .then(resolve)
          .catch(reject)
      }
    })
  })
}

/**
 * 获取图片信息
 */
export const getImageInfo = (src: string): Promise<uni.GetImageInfoSuccessData> => {
  return new Promise((resolve, reject) => {
    uni.getImageInfo({
      src: src,
      success: resolve,
      fail: reject
    })
  })
}

/**
 * 验证图片大小
 */
export const validateImageSize = async (filePath: string, maxSize: number = 5 * 1024 * 1024): Promise<boolean> => {
  try {
    const imageInfo = await getImageInfo(filePath)
    // 注意：uni.getImageInfo 在某些平台可能不返回文件大小
    // 这里主要验证图片是否有效
    return true
  } catch (error) {
    return false
  }
}

/**
 * 验证图片尺寸
 */
export const validateImageDimensions = async (
  filePath: string,
  maxWidth: number = 2000,
  maxHeight: number = 2000
): Promise<boolean> => {
  try {
    const imageInfo = await getImageInfo(filePath)
    return imageInfo.width <= maxWidth && imageInfo.height <= maxHeight
  } catch (error) {
    return false
  }
}

// 支持的图片格式
export const SUPPORTED_IMAGE_TYPES = ['jpg', 'jpeg', 'png', 'gif', 'webp']

/**
 * 验证文件类型
 */
export const validateFileType = (filePath: string, supportedTypes: string[] = SUPPORTED_IMAGE_TYPES): boolean => {
  const extension = filePath.split('.').pop()?.toLowerCase()
  return extension ? supportedTypes.includes(extension) : false
}

/**
 * 格式化文件大小
 */
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'

  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
