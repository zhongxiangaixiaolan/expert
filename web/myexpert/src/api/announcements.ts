import request from './request'

// 通告信息接口
export interface Announcement {
  id: number
  title: string
  content?: string
  type: string
  isScroll: number
  sortOrder: number
  status: number
  startTime?: string
  endTime?: string
  createTime?: string
  updateTime?: string
}

// 通告保存参数
export interface AnnouncementSaveParams {
  id?: number
  title: string
  content?: string
  type: string
  isScroll: number
  sortOrder: number
  status?: number
  startTime?: string
  endTime?: string
}

// 通告查询参数
export interface AnnouncementQueryParams {
  pageNum?: number
  pageSize?: number
  title?: string
  type?: string
  isScroll?: number
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

// 通告类型枚举
export const AnnouncementTypeOptions = [
  { label: '通知公告', value: 'NOTICE' },
  { label: '活动公告', value: 'ACTIVITY' },
  { label: '系统公告', value: 'SYSTEM' }
]

// 获取通告分页列表
export const getAnnouncementPage = (params: AnnouncementQueryParams): Promise<PageResult<Announcement>> => {
  return request.get('/admin/announcement/page', { params })
}

// 获取通告详情
export const getAnnouncementDetail = (announcementId: number): Promise<Announcement> => {
  return request.get(`/admin/announcement/${announcementId}`)
}

// 保存通告（新增或更新）
export const saveAnnouncement = (data: AnnouncementSaveParams): Promise<void> => {
  return request.post('/admin/announcement/save', data)
}

// 删除通告
export const deleteAnnouncement = (announcementId: number): Promise<void> => {
  return request.delete(`/admin/announcement/${announcementId}`)
}

// 更新通告状态
export const updateAnnouncementStatus = (announcementId: number, status: number): Promise<void> => {
  return request.post(`/admin/announcement/${announcementId}/status?status=${status}`)
}

// 获取启用的通告列表
export const getEnabledAnnouncements = (): Promise<Announcement[]> => {
  return request.get('/admin/announcement/enabled')
}

// 获取滚动显示的通告列表
export const getScrollAnnouncements = (): Promise<Announcement[]> => {
  return request.get('/admin/announcement/scroll')
}

// 批量更新排序权重
export const updateAnnouncementSortOrders = (announcementIds: number[], sortOrders: number[]): Promise<void> => {
  return request.post('/admin/announcement/sort', null, {
    params: {
      announcementIds,
      sortOrders
    }
  })
}
