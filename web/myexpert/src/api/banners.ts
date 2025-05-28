import request from './request'

// 轮播图信息接口
export interface Banner {
  id: number
  title: string
  imageUrl: string
  linkUrl?: string
  linkType: string
  linkId?: number
  sortOrder: number
  status: number
  startTime?: string
  endTime?: string
  createTime?: string
  updateTime?: string
}

// 轮播图保存参数
export interface BannerSaveParams {
  id?: number
  title: string
  imageUrl: string
  linkUrl?: string
  linkType: string
  linkId?: number
  sortOrder: number
  status: number
  startTime?: string
  endTime?: string
}

// 轮播图查询参数
export interface BannerQueryParams {
  pageNum?: number
  pageSize?: number
  title?: string
  linkType?: string
  status?: number
  startTimeBegin?: string
  startTimeEnd?: string
  endTimeBegin?: string
  endTimeEnd?: string
}

// 分页结果
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 获取轮播图分页列表
export const getBannerPage = (params: BannerQueryParams): Promise<PageResult<Banner>> => {
  return request.get('/admin/banner/page', { params })
}

// 获取轮播图详情
export const getBannerDetail = (bannerId: number): Promise<Banner> => {
  return request.get(`/admin/banner/${bannerId}`)
}

// 保存轮播图（新增或更新）
export const saveBanner = (data: BannerSaveParams): Promise<void> => {
  return request.post('/admin/banner/save', data)
}

// 删除轮播图
export const deleteBanner = (bannerId: number): Promise<void> => {
  return request.delete(`/admin/banner/${bannerId}`)
}

// 更新轮播图状态
export const updateBannerStatus = (bannerId: number, status: number): Promise<void> => {
  return request.post(`/admin/banner/${bannerId}/status`, null, {
    params: { status }
  })
}

// 获取启用的轮播图列表
export const getEnabledBanners = (): Promise<Banner[]> => {
  return request.get('/admin/banner/enabled')
}

// 批量更新排序权重
export const updateBannerSortOrders = (bannerIds: number[], sortOrders: number[]): Promise<void> => {
  return request.post('/admin/banner/sort', null, {
    params: {
      bannerIds,
      sortOrders
    }
  })
}

// 批量删除轮播图
export const batchDeleteBanners = (bannerIds: number[]): Promise<void> => {
  return request.delete('/admin/banner/batch', {
    data: { ids: bannerIds }
  })
}

// 上传轮播图
export const uploadBanner = (file: File): Promise<{
  url: string
  fileName: string
  fileSize: number
}> => {
  const formData = new FormData()
  formData.append('file', file)

  return request.post('/files/upload/banner', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 链接类型枚举
export const LinkTypeEnum = {
  NONE: 'NONE',
  URL: 'URL',
  SERVICE: 'SERVICE',
  CATEGORY: 'CATEGORY'
} as const

// 链接类型选项
export const linkTypeOptions = [
  { label: '无链接', value: LinkTypeEnum.NONE },
  { label: '外部链接', value: LinkTypeEnum.URL },
  { label: '服务详情', value: LinkTypeEnum.SERVICE },
  { label: '分类页面', value: LinkTypeEnum.CATEGORY }
]
