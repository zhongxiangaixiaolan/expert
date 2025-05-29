/**
 * 首页相关API
 */
import request from './request'

// 轮播图接口
export interface Banner {
  id: number
  title: string
  imageUrl: string
  linkUrl?: string
  linkType: string  // 链接类型：NONE,URL,SERVICE,CATEGORY
  linkId?: number   // 关联ID（服务ID或分类ID）
  sortOrder: number
  status: number
  startTime?: string
  endTime?: string
  createTime: string
  updateTime?: string
}

// 公告接口
export interface Notice {
  id: number
  title: string
  content: string
  type: string      // 通告类型：NOTICE,ACTIVITY,SYSTEM
  isScroll: number  // 是否滚动显示：0-否，1-是
  sortOrder: number
  status: number
  startTime?: string
  endTime?: string
  createTime: string
  updateTime?: string
}

// 分类接口
export interface Category {
  id: number
  name: string
  description?: string
  icon?: string
  iconType?: string  // 图标类型：iconify,emoji,url
  iconColor?: string // 图标颜色
  sortOrder: number
  status: number
  createTime: string
  updateTime?: string
}

// 达人接口
export interface Expert {
  id: number
  userId: number
  categoryId: number
  expertName: string  // 达人名称
  description: string
  avatar?: string
  priceMin: number    // 最低价格
  priceMax: number    // 最高价格
  rating: number      // 评分（1-5分）
  orderCount: number  // 接单数量
  completeCount: number // 完成数量
  completeRate: number  // 完成率
  status: number      // 状态：0-下线，1-在线，2-忙碌
  auditStatus: number // 审核状态：0-待审核，1-通过，2-拒绝
  auditRemark?: string // 审核备注
  createTime: string
  updateTime?: string
  // 关联信息
  user?: {
    id: number
    nickname: string
    avatar?: string
    phone?: string
  }
  category?: {
    id: number
    name: string
    icon?: string
  }
}

// 获取轮播图列表
export const getBannerList = (): Promise<Banner[]> => {
  return request.get('/banners').then((data: Banner[]) => {
    // 处理图片URL，确保是完整的访问路径
    return data.map(banner => ({
      ...banner,
      imageUrl: getBannerImageUrl(banner.imageUrl)
    }))
  })
}

// 构建轮播图图片URL
const getBannerImageUrl = (imageUrl: string): string => {
  if (!imageUrl) return ''

  // 如果已经是完整URL，直接返回
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }

  // 构建完整的图片访问URL
  return `http://localhost:8080/api/static/banner/${imageUrl}`
}

// 获取公告列表
export const getNoticeList = (): Promise<Notice[]> => {
  return request.get('/announcements')
}

// 获取分类列表
export const getCategoryList = (): Promise<Category[]> => {
  return request.get('/categories')
}

// 获取分类详情
export const getCategoryDetail = (categoryId: number): Promise<Category> => {
  return request.get(`/categories/${categoryId}`)
}

// 获取推荐达人列表
export const getExpertList = (params?: {
  current?: number
  size?: number
  categoryId?: number
  keyword?: string
}): Promise<{ records: Expert[], total: number }> => {
  return request.get('/experts', { params }).then((data: any) => {
    // 处理达人头像URL
    const processedRecords = (data.records || []).map((expert: any) => ({
      ...expert,
      avatar: getAvatarImageUrl(expert.avatar)
    }))

    return {
      records: processedRecords,
      total: data.total || 0
    }
  })
}

// 构建头像图片URL
const getAvatarImageUrl = (avatar?: string): string => {
  if (!avatar) return ''

  // 如果已经是完整URL，直接返回
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }

  // 构建完整的头像访问URL
  return `http://localhost:8080/api/static/avatars/${avatar}`
}

// 获取达人详情
export const getExpertDetail = (id: number): Promise<Expert> => {
  return request.get(`/experts/${id}`)
}

// 获取公告详情
export const getNoticeDetail = (id: number): Promise<Notice> => {
  return request.get(`/announcements/${id}`)
}

// 获取热门达人列表
export const getHotExperts = (limit?: number): Promise<Expert[]> => {
  return request.get('/experts/hot', { params: limit ? { limit } : {} }).then((data: Expert[]) => {
    // 处理达人头像URL
    return data.map(expert => ({
      ...expert,
      avatar: getAvatarImageUrl(expert.avatar)
    }))
  })
}

// 达人照片接口
export interface ExpertPhoto {
  id: number
  expertId: number
  photoName: string
  photoTitle?: string
  photoDescription?: string
  sortOrder: number
  fileSize?: number
  width?: number
  height?: number
  createTime?: string
  updateTime?: string
  photoUrl?: string
}

// 获取达人照片列表
export const getExpertPhotos = (expertId: number): Promise<ExpertPhoto[]> => {
  return request.get(`/experts/${expertId}/photos`).then((data: ExpertPhoto[]) => {
    // 处理照片URL
    return data.map(photo => ({
      ...photo,
      photoUrl: getPhotoImageUrl(photo.photoName)
    }))
  })
}

// 构建照片图片URL
const getPhotoImageUrl = (photoName?: string): string => {
  if (!photoName) return ''

  // 如果已经是完整URL，直接返回
  if (photoName.startsWith('http://') || photoName.startsWith('https://')) {
    return photoName
  }

  // 构建完整的照片访问URL
  return `http://localhost:8080/api/photos/${photoName}`
}
