/**
 * 订单相关API
 */
import request from './request'
import type { PageResponse } from './request'

// 订单状态枚举（与数据库表一致）
export enum OrderStatus {
  PENDING = 1,     // 待接单
  ACCEPTED = 2,    // 已接单
  IN_SERVICE = 3,  // 服务中
  COMPLETED = 4,   // 已完成
  CANCELLED = 5,   // 已取消
  AFTER_SALE = 6   // 售后中
}

// 支付状态枚举
export enum PaymentStatus {
  UNPAID = 0,    // 未支付
  PAID = 1,      // 已支付
  REFUNDED = 2   // 已退款
}

// 订单接口
export interface Order {
  id: number
  orderNo: string
  userId: number
  expertId?: number  // 修正：talentId -> expertId
  serviceId: number  // 添加：服务ID
  serviceName: string
  servicePrice: number
  title?: string     // 添加：订单标题
  description: string // 修正：taskDescription -> description
  amount: number     // 添加：订单金额
  expectedTime?: string
  status: OrderStatus    // 修正：orderStatus -> status
  payStatus: PaymentStatus // 修正：paymentStatus -> payStatus
  payType?: string   // 添加：支付方式
  payTime?: string   // 添加：支付时间
  acceptTime?: string
  startTime?: string // 添加：开始服务时间
  finishTime?: string // 修正：completeTime -> finishTime
  cancelTime?: string
  cancelReason?: string // 添加：取消原因
  userRemark?: string   // 添加：用户备注
  expertRemark?: string // 添加：达人备注
  contactInfo?: string  // 添加：联系方式
  serviceAddress?: string // 添加：服务地址
  appointmentTime?: string // 添加：预约时间
  createTime: string
  updateTime?: string

  // 关联信息
  userInfo?: {
    id: number
    nickname: string
    avatar?: string
    phone?: string
  }
  expertInfo?: {  // 修正：talentInfo -> expertInfo
    id: number
    expertName: string // 修正：name -> expertName
    avatar?: string
    phone?: string
    rating: number
  }
  categoryInfo?: {
    id: number
    name: string
    icon?: string
  }
}

// 创建订单参数
export interface CreateOrderParams {
  serviceId: number     // 修正：使用serviceId而不是talentId
  title: string         // 添加：订单标题
  description: string   // 修正：taskDescription -> description
  amount: number        // 添加：订单金额
  userRemark?: string   // 添加：用户备注
  contactInfo?: string  // 添加：联系方式
  serviceAddress?: string // 添加：服务地址
  appointmentTime?: string // 添加：预约时间
}

// 订单查询参数
export interface OrderQueryParams {
  current?: number
  size?: number
  status?: OrderStatus
  keyword?: string
  startTime?: string
  endTime?: string
}

// 订单评价参数
export interface OrderReviewParams {
  orderId: number
  rating: number
  serviceRating?: number
  speedRating?: number
  qualityRating?: number
  content?: string
  images?: string[]
  isAnonymous?: boolean
}

// 创建订单
export const createOrder = (params: CreateOrderParams): Promise<{ orderId: number }> => {
  return request.post('/orders', params)
}

// 获取订单列表
export const getOrderList = (params: OrderQueryParams): Promise<PageResponse<Order>> => {
  return request.get('/orders', params)
}

// 获取用户订单列表
export const getUserOrders = (params: OrderQueryParams): Promise<PageResponse<Order>> => {
  return request.get('/user/orders', params)
}

// 获取达人订单列表
export const getExpertOrders = (params: OrderQueryParams): Promise<PageResponse<Order>> => {
  return request.get('/expert/orders', params)
}

// 获取订单详情
export const getOrderDetail = (orderId: number): Promise<Order> => {
  return request.get(`/orders/${orderId}`)
}

// 取消订单
export const cancelOrder = (orderId: number, reason?: string): Promise<void> => {
  return request.put(`/orders/${orderId}/cancel`, { reason })
}

// 确认订单
export const confirmOrder = (orderId: number): Promise<void> => {
  return request.put(`/orders/${orderId}/confirm`)
}

// 达人接单
export const acceptOrder = (orderId: number): Promise<void> => {
  return request.put(`/orders/${orderId}/accept`)
}

// 开始服务
export const startService = (orderId: number): Promise<void> => {
  return request.put(`/orders/${orderId}/start`)
}

// 完成订单
export const completeOrder = (orderId: number, data: {
  completionScreenshots?: string[]
  talentSignature?: string
}): Promise<void> => {
  return request.put(`/orders/${orderId}/complete`, data)
}

// 评价订单
export const reviewOrder = (params: OrderReviewParams): Promise<void> => {
  return request.post('/orders/review', params)
}

// 申请售后
export const applyAfterSale = (orderId: number, reason: string): Promise<void> => {
  return request.post(`/orders/${orderId}/after-sale`, { reason })
}

// 获取订单状态文本
export const getOrderStatusText = (status: OrderStatus): string => {
  const statusMap = {
    [OrderStatus.PENDING]: '待接单',
    [OrderStatus.ACCEPTED]: '已接单',
    [OrderStatus.IN_SERVICE]: '服务中',
    [OrderStatus.COMPLETED]: '已完成',
    [OrderStatus.CANCELLED]: '已取消',
    [OrderStatus.AFTER_SALE]: '售后中'
  }
  return statusMap[status] || '未知状态'
}

// 获取支付状态文本
export const getPaymentStatusText = (status: PaymentStatus): string => {
  const statusMap = {
    [PaymentStatus.UNPAID]: '未支付',
    [PaymentStatus.PAID]: '已支付',
    [PaymentStatus.REFUNDED]: '已退款'
  }
  return statusMap[status] || '未知状态'
}

// 获取订单状态颜色
export const getOrderStatusColor = (status: OrderStatus): string => {
  const colorMap = {
    [OrderStatus.PENDING]: '#f0ad4e',
    [OrderStatus.ACCEPTED]: '#5bc0de',
    [OrderStatus.IN_SERVICE]: '#007aff',
    [OrderStatus.COMPLETED]: '#4cd964',
    [OrderStatus.CANCELLED]: '#999999',
    [OrderStatus.AFTER_SALE]: '#ff6b35'
  }
  return colorMap[status] || '#999999'
}

// 支付订单
export const payOrder = (orderId: number, paymentMethod: string = 'wechat'): Promise<{
  paymentParams: any
}> => {
  return request.post(`/orders/${orderId}/pay`, { paymentMethod })
}
