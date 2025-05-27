import request from './request'

// 达人申请参数
export interface ExpertApplyParams {
  expertName: string
  categoryId: number
  description: string
  priceMin: number
  priceMax: number
  avatar?: string
}

// 达人资料更新参数
export interface ExpertUpdateParams {
  id: number
  expertName: string
  categoryId: number
  description: string
  priceMin: number
  priceMax: number
  avatar?: string
}

// 达人详情信息
export interface ExpertDetail {
  id: number
  userId: number
  categoryId: number
  expertName: string
  description: string
  avatar: string
  priceMin: number
  priceMax: number
  rating: number
  orderCount: number
  completeCount: number
  completeRate: number
  status: number
  auditStatus: number
  auditRemark?: string
  createTime: string
  updateTime: string
  // 关联信息
  userInfo?: {
    id: number
    nickname: string
    avatar: string
    phone: string
  }
  categoryInfo?: {
    id: number
    name: string
    icon: string
  }
}

// 达人工作台数据
export interface ExpertWorkspace {
  expertId: number
  expertName: string
  avatar: string
  status: number
  auditStatus: number
  rating: number
  orderCount: number
  completeCount: number
  completeRate: number
  pendingOrderCount: number
  todayStats: {
    newOrders: number
    completedOrders: number
    earnings: number
    responseTime: number
  }
  statusDesc: string
  auditStatusDesc: string
}

// 收益统计数据
export interface EarningsStats {
  totalEarnings: number
  orderCount: number
  avgOrderAmount: number
}

/**
 * 申请成为达人
 */
export const applyExpert = (params: ExpertApplyParams): Promise<void> => {
  return request.post('/expert/apply', params)
}

/**
 * 获取当前达人信息
 */
export const getExpertProfile = (): Promise<ExpertDetail> => {
  return request.get('/expert/profile')
}

/**
 * 更新达人资料
 */
export const updateExpertProfile = (params: ExpertUpdateParams): Promise<void> => {
  return request.put('/expert/profile', params)
}

/**
 * 检查用户是否为达人
 */
export const checkIsExpert = (): Promise<boolean> => {
  return request.get('/expert/check')
}

/**
 * 更新达人在线状态
 */
export const updateExpertStatus = (status: number): Promise<void> => {
  return request.put('/expert/status', null, { params: { status } })
}

/**
 * 获取达人工作台数据
 */
export const getExpertWorkspace = (): Promise<ExpertWorkspace> => {
  return request.get('/expert/workspace/data')
}

/**
 * 获取今日统计数据
 */
export const getTodayStats = (): Promise<{
  newOrders: number
  completedOrders: number
  earnings: number
  responseTime: number
}> => {
  return request.get('/expert/workspace/today-stats')
}

/**
 * 获取收益统计数据
 */
export const getEarningsStats = (type: 'today' | 'week' | 'month' = 'today'): Promise<EarningsStats> => {
  return request.get('/expert/workspace/earnings', { params: { type } })
}

// 达人状态枚举
export enum ExpertStatus {
  OFFLINE = 0,    // 下线
  ONLINE = 1,     // 在线
  BUSY = 2        // 忙碌
}

// 达人审核状态枚举
export enum ExpertAuditStatus {
  PENDING = 0,    // 待审核
  APPROVED = 1,   // 审核通过
  REJECTED = 2    // 审核拒绝
}

/**
 * 获取达人状态文本
 */
export const getExpertStatusText = (status: number): string => {
  const statusMap = {
    [ExpertStatus.OFFLINE]: '下线',
    [ExpertStatus.ONLINE]: '在线',
    [ExpertStatus.BUSY]: '忙碌'
  }
  return statusMap[status] || '未知'
}

/**
 * 获取达人状态颜色
 */
export const getExpertStatusColor = (status: number): string => {
  const colorMap = {
    [ExpertStatus.OFFLINE]: '#999999',
    [ExpertStatus.ONLINE]: '#52c41a',
    [ExpertStatus.BUSY]: '#faad14'
  }
  return colorMap[status] || '#999999'
}

/**
 * 获取审核状态文本
 */
export const getAuditStatusText = (auditStatus: number): string => {
  const statusMap = {
    [ExpertAuditStatus.PENDING]: '待审核',
    [ExpertAuditStatus.APPROVED]: '审核通过',
    [ExpertAuditStatus.REJECTED]: '审核拒绝'
  }
  return statusMap[auditStatus] || '未知'
}

/**
 * 获取审核状态颜色
 */
export const getAuditStatusColor = (auditStatus: number): string => {
  const colorMap = {
    [ExpertAuditStatus.PENDING]: '#faad14',
    [ExpertAuditStatus.APPROVED]: '#52c41a',
    [ExpertAuditStatus.REJECTED]: '#ff4d4f'
  }
  return colorMap[auditStatus] || '#999999'
}

/**
 * 计算完成率
 */
export const calculateCompleteRate = (completeCount: number, orderCount: number): number => {
  if (!orderCount || orderCount === 0) {
    return 0
  }
  return Math.round((completeCount || 0) / orderCount * 100)
}

/**
 * 格式化价格范围
 */
export const formatPriceRange = (priceMin: number, priceMax: number): string => {
  if (!priceMin && !priceMax) {
    return '价格面议'
  }
  if (priceMin === priceMax) {
    return `¥${priceMin}`
  }
  return `¥${priceMin || 0} - ¥${priceMax || 0}`
}

/**
 * 格式化收益金额
 */
export const formatEarnings = (amount: number): string => {
  if (amount >= 10000) {
    return `¥${(amount / 10000).toFixed(1)}万`
  }
  return `¥${amount.toFixed(2)}`
}
