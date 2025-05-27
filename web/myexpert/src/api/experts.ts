import request from './request'
import type { PageResponse } from './request'

// 达人信息接口
export interface Expert {
  id: number
  userId: number
  categoryId: number
  expertName: string
  description?: string
  avatar?: string
  priceMin?: number
  priceMax?: number
  rating?: number
  orderCount?: number
  completeCount?: number
  completeRate?: number
  status: number
  auditStatus: number
  auditRemark?: string
  createTime?: string
  updateTime?: string
  // 关联信息
  user?: {
    id: number
    nickname: string
    avatar?: string
    gender?: number
    phone?: string
    realName?: string
    status: number
  }
  category?: {
    id: number
    name: string
    description?: string
    icon?: string
  }
}

// 达人查询参数
export interface ExpertQueryParams {
  current?: number
  size?: number
  keyword?: string
  categoryId?: number
  status?: number
  auditStatus?: number
  minRating?: number
  maxRating?: number
  minPrice?: number
  maxPrice?: number
  createStartTime?: string
  createEndTime?: string
  sortField?: string
  sortOrder?: string
}

// 达人保存参数
export interface ExpertSaveParams {
  id?: number
  userId: number
  categoryId: number
  expertName: string
  description?: string
  avatar?: string
  priceMin?: number
  priceMax?: number
}

// 达人审核参数
export interface ExpertAuditParams {
  expertId: number
  auditStatus: number
  auditRemark: string
}

// 达人统计信息
export interface ExpertStatistics {
  totalCount: number
  onlineCount: number
  busyCount: number
  offlineCount: number
  pendingCount: number
  approvedCount: number
  rejectedCount: number
  todayNewCount: number
  weekNewCount: number
  monthNewCount: number
  avgRating: number
  totalOrderCount: number
  totalCompleteCount: number
  avgCompleteRate: number
}

// 获取达人统计信息
export const getExpertStatistics = (): Promise<ExpertStatistics> => {
  return request.get('/admin/expert/statistics')
}

// 分页查询达人列表
export const getExpertPage = (params: ExpertQueryParams): Promise<PageResponse<Expert>> => {
  return request.get('/admin/expert/page', { params })
}

// 获取达人详情
export const getExpertDetail = (expertId: number): Promise<Expert> => {
  return request.get(`/admin/expert/${expertId}`)
}

// 保存达人信息
export const saveExpert = (data: ExpertSaveParams): Promise<void> => {
  return request.post('/admin/expert/save', data)
}

// 删除达人
export const deleteExpert = (expertId: number): Promise<void> => {
  return request.delete(`/admin/expert/${expertId}`)
}

// 批量删除达人
export const batchDeleteExperts = (expertIds: number[]): Promise<void> => {
  return request.delete('/admin/expert/batch', { data: { expertIds } })
}

// 更新达人状态
export const updateExpertStatus = (expertId: number, status: number): Promise<void> => {
  return request.put(`/admin/expert/${expertId}/status?status=${status}`)
}

// 批量更新达人状态
export const batchUpdateExpertStatus = (expertIds: number[], status: number): Promise<void> => {
  return request.put('/admin/expert/batch/status', { expertIds, status })
}

// 达人审核
export const auditExpert = (data: ExpertAuditParams): Promise<void> => {
  return request.put('/admin/expert/audit', data)
}

// 批量达人审核
export const batchAuditExperts = (expertIds: number[], auditStatus: number, auditRemark?: string): Promise<void> => {
  return request.put('/admin/expert/batch/audit', { expertIds, auditStatus, auditRemark })
}

// 根据分类获取达人列表
export const getExpertsByCategory = (categoryId: number): Promise<Expert[]> => {
  return request.get(`/admin/expert/category/${categoryId}`)
}

// 获取热门达人列表
export const getHotExperts = (limit?: number): Promise<Expert[]> => {
  return request.get('/admin/expert/hot', { params: { limit } })
}

// 达人状态枚举
export const ExpertStatus = {
  OFFLINE: 0,
  ONLINE: 1,
  BUSY: 2
} as const

// 达人审核状态枚举
export const ExpertAuditStatus = {
  PENDING: 0,
  APPROVED: 1,
  REJECTED: 2
} as const

// 获取状态文本
export const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    [ExpertStatus.OFFLINE]: '下线',
    [ExpertStatus.ONLINE]: '在线',
    [ExpertStatus.BUSY]: '忙碌'
  }
  return statusMap[status] || '未知'
}

// 获取审核状态文本
export const getAuditStatusText = (auditStatus: number): string => {
  const auditStatusMap: Record<number, string> = {
    [ExpertAuditStatus.PENDING]: '待审核',
    [ExpertAuditStatus.APPROVED]: '已通过',
    [ExpertAuditStatus.REJECTED]: '已拒绝'
  }
  return auditStatusMap[auditStatus] || '未知'
}

// 获取状态标签类型
export const getStatusTagType = (status: number): string => {
  const tagTypeMap: Record<number, string> = {
    [ExpertStatus.OFFLINE]: 'info',
    [ExpertStatus.ONLINE]: 'success',
    [ExpertStatus.BUSY]: 'warning'
  }
  return tagTypeMap[status] || 'info'
}

// 获取审核状态标签类型
export const getAuditStatusTagType = (auditStatus: number): string => {
  const tagTypeMap: Record<number, string> = {
    [ExpertAuditStatus.PENDING]: 'warning',
    [ExpertAuditStatus.APPROVED]: 'success',
    [ExpertAuditStatus.REJECTED]: 'danger'
  }
  return tagTypeMap[auditStatus] || 'info'
}
