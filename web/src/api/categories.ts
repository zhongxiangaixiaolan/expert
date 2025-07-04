import request from './request'

// 分类信息接口
export interface Category {
  id: number
  name: string
  description?: string
  icon?: string
  iconType?: string
  iconColor?: string
  sortOrder: number
  priceMin?: number
  priceMax?: number
  status: number
  createTime?: string
  updateTime?: string
}

// 分类保存参数
export interface CategorySaveParams {
  id?: number
  name: string
  description?: string
  icon?: string
  iconType?: string
  iconColor?: string
  sortOrder?: number
  status?: number
}

// 获取所有分类列表
export const getAllCategories = (): Promise<Category[]> => {
  return request.get('/admin/category/list')
}

// 获取启用的分类列表
export const getEnabledCategories = (): Promise<Category[]> => {
  return request.get('/admin/category/enabled')
}

// 获取分类详情
export const getCategoryDetail = (categoryId: number): Promise<Category> => {
  return request.get(`/admin/category/${categoryId}`)
}

// 保存分类
export const saveCategory = (params: CategorySaveParams): Promise<void> => {
  return request.post('/admin/category/save', params)
}

// 删除分类
export const deleteCategory = (categoryId: number): Promise<void> => {
  return request.delete(`/admin/category/${categoryId}`)
}

// 批量删除分类
export const batchDeleteCategories = (categoryIds: number[]): Promise<void> => {
  return request.delete('/admin/category/batch', { data: { categoryIds } })
}

// 更新分类状态
export const updateCategoryStatus = (categoryId: number, status: number): Promise<void> => {
  return request.put(`/admin/category/${categoryId}/status`, { status })
}

// 更新分类排序
export const updateCategorySort = (categoryId: number, sortOrder: number): Promise<void> => {
  return request.put(`/admin/category/${categoryId}/sort`, { sortOrder })
}

// 分类状态枚举
export const CategoryStatus = {
  DISABLED: 0,
  ENABLED: 1
} as const

// 获取状态文本
export const getCategoryStatusText = (status: number): string => {
  const statusMap = {
    [CategoryStatus.DISABLED]: '禁用',
    [CategoryStatus.ENABLED]: '启用'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
export const getCategoryStatusTagType = (status: number): string => {
  const tagTypeMap = {
    [CategoryStatus.DISABLED]: 'danger',
    [CategoryStatus.ENABLED]: 'success'
  }
  return tagTypeMap[status] || 'info'
}
