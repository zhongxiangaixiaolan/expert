import request from './request'

// 系统配置项
export interface SystemConfig {
  id: number
  configKey: string
  configValue: string
  configType: string
  description: string
  createTime: string
  updateTime: string
}

// 配置分组数据
export interface ConfigGroups {
  wechat: Record<string, string>
  storage: Record<string, string>
  system: Record<string, string>
  business: Record<string, string>
}

// 配置更新参数
export interface ConfigUpdateParams {
  configKey: string
  configValue: string
}

// 获取所有配置分组
export const getAllConfigGroups = (): Promise<ConfigGroups> => {
  return request.get('/admin/config/groups')
}

// 根据分组获取配置
export const getConfigsByGroup = (group: string): Promise<Record<string, string>> => {
  return request.get(`/admin/config/group/${group}`)
}

// 获取微信配置
export const getWeChatConfigs = (): Promise<Record<string, string>> => {
  return request.get('/admin/config/wechat')
}

// 获取存储配置
export const getStorageConfigs = (): Promise<Record<string, string>> => {
  return request.get('/admin/config/storage')
}

// 获取系统配置
export const getSystemConfigs = (): Promise<Record<string, string>> => {
  return request.get('/admin/config/system')
}

// 获取业务配置
export const getBusinessConfigs = (): Promise<Record<string, string>> => {
  return request.get('/admin/config/business')
}

// 更新配置
export const updateConfig = (data: ConfigUpdateParams): Promise<void> => {
  return request.put(`/admin/config/${data.configKey}?configValue=${encodeURIComponent(data.configValue)}`)
}

// 批量更新配置
export const batchUpdateConfigs = (data: ConfigUpdateParams[]): Promise<void> => {
  return request.put('/admin/config/batch', data)
}

// 刷新配置缓存
export const refreshCache = (): Promise<void> => {
  return request.post('/admin/config/refresh')
}

// 获取配置详情
export const getConfigDetail = (configKey: string): Promise<SystemConfig> => {
  return request.get(`/admin/config/detail/${configKey}`)
}

// 趋势数据接口
export interface TrendData {
  date: string
  value: number
  label?: string
}

// 分布数据接口
export interface DistributionData {
  name: string
  value: number
  percentage: number
  color?: string
}

// Dashboard统计数据接口
export interface DashboardStatistics {
  userCount: number
  expertCount: number
  orderCount: number
  totalRevenue: number
  todayNewUsers: number
  todayNewExperts: number
  todayNewOrders: number
  todayRevenue: number
  recentOrders: RecentOrder[]
  systemNotices: SystemNotice[]
  userRegistrationTrend: TrendData[]
  expertRegistrationTrend: TrendData[]
  orderCreationTrend: TrendData[]
  revenueTrend: TrendData[]
  orderStatusDistribution: DistributionData[]
  userTypeDistribution: DistributionData[]
  expertStatusDistribution: DistributionData[]
  revenueSourceDistribution: DistributionData[]
}

export interface RecentOrder {
  id: number
  userName: string
  expertName: string
  amount: number
  status: number
  createTime: string
}

export interface SystemNotice {
  id: number
  content: string
  time: string
  type: string
}

// 获取Dashboard统计数据
export const getDashboardStatistics = (): Promise<DashboardStatistics> => {
  return request.get('/admin/dashboard/statistics')
}

// 获取用户注册趋势
export const getUserRegistrationTrend = (days: number = 7): Promise<TrendData[]> => {
  return request.get('/admin/dashboard/trend/user-registration', { params: { days } })
}

// 获取达人注册趋势
export const getExpertRegistrationTrend = (days: number = 7): Promise<TrendData[]> => {
  return request.get('/admin/dashboard/trend/expert-registration', { params: { days } })
}

// 获取订单创建趋势
export const getOrderCreationTrend = (days: number = 7): Promise<TrendData[]> => {
  return request.get('/admin/dashboard/trend/order-creation', { params: { days } })
}

// 获取收入趋势
export const getRevenueTrend = (days: number = 7): Promise<TrendData[]> => {
  return request.get('/admin/dashboard/trend/revenue', { params: { days } })
}

// 获取订单状态分布
export const getOrderStatusDistribution = (): Promise<DistributionData[]> => {
  return request.get('/admin/dashboard/distribution/order-status')
}

// 获取用户类型分布
export const getUserTypeDistribution = (): Promise<DistributionData[]> => {
  return request.get('/admin/dashboard/distribution/user-type')
}

// 获取达人状态分布
export const getExpertStatusDistribution = (): Promise<DistributionData[]> => {
  return request.get('/admin/dashboard/distribution/expert-status')
}

// 获取收入来源分布
export const getRevenueSourceDistribution = (): Promise<DistributionData[]> => {
  return request.get('/admin/dashboard/distribution/revenue-source')
}
