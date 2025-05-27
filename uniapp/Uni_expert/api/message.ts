import request from './request'

// 消息接口
export interface Message {
  id: number
  messageType: string
  userId: number
  title: string
  content: string
  status: number
  businessId?: number
  businessType?: string
  sendTime: string
  readTime?: string
  expireTime?: string
}

// 消息查询参数
export interface MessageQueryParams {
  messageType?: string
  status?: number
  current?: number
  size?: number
}

// 未读消息数量
export interface UnreadCount {
  all: number
  system: number
  order: number
  service: number
  payment: number
}

// 获取消息列表
export const getMessageList = (params: MessageQueryParams): Promise<{
  records: Message[]
  total: number
  current: number
  size: number
}> => {
  return request.get('/message/list', { params })
}

// 获取消息详情
export const getMessageDetail = (messageId: number): Promise<Message> => {
  return request.get(`/message/${messageId}`)
}

// 标记消息为已读
export const markMessageAsRead = (messageId: number): Promise<void> => {
  return request.put(`/message/${messageId}/read`)
}

// 批量标记已读
export const markAllMessagesAsRead = (messageType?: string): Promise<void> => {
  return request.put('/message/batch-read', {
    params: messageType ? { messageType } : {}
  })
}

// 删除消息
export const deleteMessage = (messageId: number): Promise<void> => {
  return request.delete(`/message/${messageId}`)
}

// 获取未读消息数量
export const getUnreadCount = (): Promise<UnreadCount> => {
  return request.get('/message/unread-count')
}

// 清空消息
export const clearMessages = (messageType?: string): Promise<void> => {
  return request.delete('/message/clear', {
    params: messageType ? { messageType } : {}
  })
}
