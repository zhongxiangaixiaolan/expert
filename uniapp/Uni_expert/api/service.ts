import request from './request'

// 服务接口
export interface Service {
  id: number
  expertId: number
  expertName: string
  expertAvatar?: string
  categoryId: number
  categoryName: string
  name: string        // 服务名称
  description?: string
  price: number
  images?: string     // 服务图片（JSON字符串）
  tags?: string       // 服务标签
  isHot: number       // 是否热门：0-否，1-是
  isRecommend: number // 是否推荐：0-否，1-是
  sortOrder: number   // 排序权重
  status: number      // 状态：0-下架，1-上架
  statusText?: string // 状态描述
  rating?: number     // 服务评分（1-5分）
  orderCount?: number // 订单数量
  createTime: string
  updateTime?: string
}

// 服务查询参数
export interface ServiceQueryParams {
  expertId?: number
  categoryId?: number
  name?: string       // 服务名称关键词
  status?: number
  isHot?: number
  isRecommend?: number
  current?: number
  size?: number
}

// 获取服务列表
export const getServiceList = (params: ServiceQueryParams): Promise<{
  records: Service[]
  total: number
  current: number
  size: number
}> => {
  return request.get('/services', { params })
}

// 获取服务详情
export const getServiceDetail = (serviceId: number): Promise<Service> => {
  return request.get(`/services/${serviceId}`)
}

// 获取热门服务列表
export const getHotServices = (limit?: number): Promise<Service[]> => {
  return request.get('/services/hot', {
    params: limit ? { limit } : {}
  })
}

// 获取推荐服务列表
export const getRecommendServices = (limit?: number): Promise<Service[]> => {
  return request.get('/services/recommend', {
    params: limit ? { limit } : {}
  })
}

// 获取分类下的服务列表
export const getCategoryServices = (categoryId: number, limit?: number): Promise<Service[]> => {
  return request.get(`/categories/${categoryId}/services`, {
    params: limit ? { limit } : {}
  })
}

// 获取达人的服务列表
export const getExpertServices = (expertId: number): Promise<Service[]> => {
  return request.get(`/experts/${expertId}/services`)
}
