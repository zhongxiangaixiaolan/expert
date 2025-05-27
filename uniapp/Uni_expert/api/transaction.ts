import request from './request'

// 交易记录接口
export interface Transaction {
  id: number
  userId: number
  type: string
  amount: number
  status: number
  description: string
  orderNo?: string
  paymentMethod?: string
  serviceName?: string
  withdrawAccount?: string
  orderId?: number
  createTime: string
  finishTime?: string
}

// 交易查询参数
export interface TransactionQueryParams {
  type?: string
  status?: number
  startTime?: string
  endTime?: string
  current?: number
  size?: number
}

// 用户统计信息
export interface UserStats {
  balance: number
  totalRecharge: number
  totalConsume: number
  totalWithdraw: number
  pendingAmount: number
}

// 获取交易记录列表
export const getTransactionList = (params: TransactionQueryParams): Promise<{
  records: Transaction[]
  total: number
  current: number
  size: number
}> => {
  return request.get('/transaction/list', { params })
}

// 获取交易记录详情
export const getTransactionDetail = (transactionId: number): Promise<Transaction> => {
  return request.get(`/transaction/${transactionId}`)
}

// 获取用户统计信息
export const getUserStats = (): Promise<UserStats> => {
  return request.get('/transaction/statistics')
}
