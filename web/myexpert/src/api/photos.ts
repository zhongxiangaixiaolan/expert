import request from '@/api/request'

// 照片接口定义
export interface ExpertPhoto {
  id: number
  expertId: number
  photoName: string
  photoTitle?: string
  photoDescription?: string
  sortOrder: number
  fileSize?: number
  width?: number
  height?: number
  createTime?: string
  updateTime?: string
}

// 上传照片参数
export interface UploadPhotoParams {
  file: File
  photoTitle?: string
  photoDescription?: string
}

// 更新照片信息参数
export interface UpdatePhotoParams {
  photoTitle?: string
  photoDescription?: string
}

// 获取达人照片列表
export const getExpertPhotos = (expertId: number): Promise<ExpertPhoto[]> => {
  return request.get(`/admin/expert/photos/${expertId}`)
}

// 上传达人照片
export const uploadExpertPhoto = (
  expertId: number,
  params: UploadPhotoParams
): Promise<{
  photo: ExpertPhoto
  photoUrl: string
  fileName: string
  fileSize: number
}> => {
  const formData = new FormData()
  formData.append('file', params.file)
  if (params.photoTitle) {
    formData.append('photoTitle', params.photoTitle)
  }
  if (params.photoDescription) {
    formData.append('photoDescription', params.photoDescription)
  }

  return request.post(`/admin/expert/photos/${expertId}/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除达人照片
export const deleteExpertPhoto = (photoId: number): Promise<void> => {
  return request.delete(`/admin/expert/photos/${photoId}`)
}

// 更新照片信息
export const updateExpertPhoto = (
  photoId: number,
  params: UpdatePhotoParams
): Promise<void> => {
  const formData = new FormData()
  if (params.photoTitle !== undefined) {
    formData.append('photoTitle', params.photoTitle)
  }
  if (params.photoDescription !== undefined) {
    formData.append('photoDescription', params.photoDescription)
  }

  return request.put(`/admin/expert/photos/${photoId}`, formData)
}

// 更新照片排序
export const updatePhotosSort = (photoIds: number[]): Promise<void> => {
  return request.put('/admin/expert/photos/sort', photoIds)
}

// 删除达人所有照片
export const deleteAllExpertPhotos = (expertId: number): Promise<void> => {
  return request.delete(`/admin/expert/photos/expert/${expertId}`)
}

// 获取照片访问URL
export const getPhotoUrl = (photoName: string): string => {
  if (!photoName) return ''
  return `/api/photos/${photoName}`
}
