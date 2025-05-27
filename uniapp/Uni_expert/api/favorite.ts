import request from './request'

// 收藏接口
export interface FavoriteItem {
  id: number
  userId: number
  favoriteType: string
  targetId: number
  service?: {
    id: number
    title: string
    description: string
    price: number
    images: string[]
    expertName: string
    expertAvatar: string
    rating: number
    orderCount: number
  }
  expert?: {
    id: number
    name: string
    description: string
    avatar: string
    rating: number
    orderCount: number
    completeCount: number
    categoryName: string
  }
  createTime: string
}

// 收藏创建参数
export interface FavoriteCreateParams {
  favoriteType: string
  targetId: number
}

// 收藏查询参数
export interface FavoriteQueryParams {
  favoriteType?: string
  current?: number
  size?: number
}

// 收藏统计
export interface FavoriteStatistics {
  totalCount: number
  serviceCount: number
  expertCount: number
}

// 添加收藏
export const addFavorite = (params: FavoriteCreateParams): Promise<void> => {
  return request.post('/favorite/add', params)
}

// 取消收藏
export const removeFavorite = (favoriteType: string, targetId: number): Promise<void> => {
  return request.delete('/favorite/remove', {
    params: { favoriteType, targetId }
  })
}

// 删除收藏记录
export const removeFavoriteItem = (favoriteId: number): Promise<void> => {
  return request.delete(`/favorite/${favoriteId}`)
}

// 获取收藏列表
export const getFavoriteList = (params: FavoriteQueryParams): Promise<{
  records: FavoriteItem[]
  total: number
  current: number
  size: number
}> => {
  return request.get('/favorite/list', { params })
}

// 检查是否已收藏
export const checkFavorite = (favoriteType: string, targetId: number): Promise<boolean> => {
  return request.get('/favorite/check', {
    params: { favoriteType, targetId }
  })
}

// 获取收藏统计
export const getFavoriteStatistics = (): Promise<FavoriteStatistics> => {
  return request.get('/favorite/statistics')
}
