import request from './request'

// 订单状态枚举
export const OrderStatus = {
  PENDING: 1,      // 待接单
  ACCEPTED: 2,     // 已接单
  IN_PROGRESS: 3,  // 服务中
  COMPLETED: 4,    // 已完成
  CANCELLED: 5,    // 已取消
  AFTER_SALE: 6    // 售后中
} as const

// 支付状态枚举
export const PayStatus = {
  UNPAID: 0,    // 未支付
  PAID: 1,      // 已支付
  REFUNDED: 2   // 已退款
} as const

// 订单信息接口
export interface Order {
  id: number
  orderNo: string
  userId: number
  userNickname?: string
  userAvatar?: string
  expertId?: number
  expertNickname?: string
  expertAvatar?: string
  serviceId: number
  serviceName: string
  servicePrice: number
  serviceCover?: string
  title?: string
  description?: string
  amount: number
  paidAmount?: number
  discountAmount?: number
  status: number
  payStatus: number
  payType?: string
  payTime?: string
  expectedTime?: string
  acceptTime?: string
  startTime?: string
  finishTime?: string
  cancelTime?: string
  cancelReason?: string
  userRemark?: string
  expertRemark?: string
  adminRemark?: string
  contactInfo?: string
  serviceAddress?: string
  appointmentTime?: string
  createTime: string
  updateTime?: string
}

// 订单查询参数
export interface OrderQueryParams {
  current?: number
  size?: number
  orderNo?: string
  userId?: number
  expertId?: number
  serviceId?: number
  categoryId?: number
  status?: number
  payStatus?: number
  title?: string
  userNickname?: string
  expertNickname?: string
  serviceName?: string
  startTime?: string
  endTime?: string
}

// 订单更新参数
export interface OrderUpdateParams {
  id: number
  status?: number
  payStatus?: number
  expertRemark?: string
  adminRemark?: string
}

// 分页查询订单列表
export const getOrderPage = (params: OrderQueryParams): Promise<{
  records: Order[]
  total: number
  current: number
  size: number
}> => {
  return request.get('/admin/orders/page', params)
}

// 获取订单详情
export const getOrderDetail = (orderId: number): Promise<Order> => {
  return request.get(`/admin/orders/${orderId}`)
}

// 更新订单信息
export const updateOrder = (params: OrderUpdateParams): Promise<void> => {
  return request.put('/admin/orders/update', params)
}

// 取消订单
export const cancelOrder = (orderId: number, cancelReason: string): Promise<void> => {
  return request.put(`/admin/orders/${orderId}/cancel`, { cancelReason })
}

// 退款订单
export const refundOrder = (orderId: number, refundReason: string): Promise<void> => {
  return request.put(`/admin/orders/${orderId}/refund`, { refundReason })
}

// 获取订单状态文本
export const getOrderStatusText = (status: number): string => {
  const statusMap = {
    [OrderStatus.PENDING]: '待接单',
    [OrderStatus.ACCEPTED]: '已接单',
    [OrderStatus.IN_PROGRESS]: '服务中',
    [OrderStatus.COMPLETED]: '已完成',
    [OrderStatus.CANCELLED]: '已取消',
    [OrderStatus.AFTER_SALE]: '售后中'
  }
  return statusMap[status] || '未知'
}

// 获取订单状态标签类型
export const getOrderStatusTagType = (status: number): string => {
  const tagTypeMap = {
    [OrderStatus.PENDING]: 'warning',
    [OrderStatus.ACCEPTED]: 'primary',
    [OrderStatus.IN_PROGRESS]: 'info',
    [OrderStatus.COMPLETED]: 'success',
    [OrderStatus.CANCELLED]: 'danger',
    [OrderStatus.AFTER_SALE]: 'warning'
  }
  return tagTypeMap[status] || 'info'
}

// 获取支付状态文本
export const getPayStatusText = (payStatus: number): string => {
  const statusMap = {
    [PayStatus.UNPAID]: '未支付',
    [PayStatus.PAID]: '已支付',
    [PayStatus.REFUNDED]: '已退款'
  }
  return statusMap[payStatus] || '未知'
}

// 获取支付状态标签类型
export const getPayStatusTagType = (payStatus: number): string => {
  const tagTypeMap = {
    [PayStatus.UNPAID]: 'danger',
    [PayStatus.PAID]: 'success',
    [PayStatus.REFUNDED]: 'warning'
  }
  return tagTypeMap[payStatus] || 'info'
}
