import { request } from '@/api/index'

// 媒体文件信息
export interface MediaFile {
  id: string
  name: string
  originalName: string
  fileType: 'video' | 'audio' | 'image' | 'document'
  mimeType: string
  fileSize: number
  duration?: number
  width?: number
  height?: number
  thumbnailUrl?: string
  previewUrl?: string
  downloadUrl?: string
  status: 'uploading' | 'processing' | 'completed' | 'failed'
  uploadTime: string
  uploadBy: string
  tags?: string[]
  description?: string
  metadata?: Record<string, any>
}

// 上传任务信息
export interface UploadTask {
  id: string
  file: File
  fileName: string
  fileSize: number
  progress: number
  status: 'pending' | 'uploading' | 'completed' | 'failed'
  startTime: Date
  endTime?: Date
  error?: string
  result?: MediaFile
}

// 转码任务信息
export interface TranscodeTask {
  id: string
  mediaId: string
  targetFormat: string
  quality: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  progress: number
  createTime: Date
  startTime?: Date
  endTime?: Date
  error?: string
  outputUrl?: string
}

// 分页响应
export interface PaginationResponse<T> {
  items: T[]
  total: number
  page: number
  size: number
  totalPages: number
}

// 媒体管理相关API
export const mediaApi = {
  // 获取媒体文件列表
  getMediaFiles: (params: {
    page?: number
    size?: number
    fileType?: string
    status?: string
    keyword?: string
    startDate?: string
    endDate?: string
    sortBy?: string
    sortOrder?: 'asc' | 'desc'
  }) => {
    return request.get<PaginationResponse<MediaFile>>('/media/v1/files', params)
  },

  // 上传文件
  uploadFile: (file: File, options?: {
    onUploadProgress?: (progressEvent: any) => void
    tags?: string[]
    description?: string
  }) => {
    const formData = new FormData()
    formData.append('file', file)
    
    if (options?.tags) {
      formData.append('tags', JSON.stringify(options.tags))
    }
    if (options?.description) {
      formData.append('description', options.description)
    }
    
    return request.upload<MediaFile>('/media/v1/upload', formData, options?.onUploadProgress)
  },

  // 分片上传 - 初始化
  initChunkUpload: (fileInfo: {
    fileName: string
    fileSize: number
    chunkSize: number
    totalChunks: number
  }) => {
    return request.post('/media/v1/upload/init', fileInfo)
  },

  // 分片上传 - 上传分片
  uploadChunk: (file: File, fileUuid: string, chunkIndex: number, totalChunks: number) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('fileUuid', fileUuid)
    formData.append('chunkIndex', chunkIndex.toString())
    formData.append('totalChunks', totalChunks.toString())
    
    return request.post(`/media/v1/upload/chunk/${fileUuid}`, formData)
  },

  // 分片上传 - 合并分片
  mergeChunks: (fileUuid: string) => {
    return request.post(`/media/v1/upload/merge/${fileUuid}`)
  },

  // 获取媒体文件详情
  getMediaFile: (mediaId: string) => {
    return request.get<MediaFile>(`/media/v1/files/${mediaId}`)
  },

  // 删除媒体文件
  deleteMediaFile: (mediaId: string) => {
    return request.delete(`/media/v1/files/${mediaId}`)
  },

  // 批量删除媒体文件
  batchDeleteMediaFiles: (mediaIds: string[]) => {
    return request.post('/media/v1/files/batch-delete', { ids: mediaIds })
  },

  // 更新媒体文件信息
  updateMediaFile: (mediaId: string, data: {
    name?: string
    tags?: string[]
    description?: string
  }) => {
    return request.put(`/media/v1/files/${mediaId}`, data)
  },

  // 下载媒体文件
  downloadMediaFile: (mediaId: string) => {
    return request.download(`/media/v1/files/${mediaId}/download`)
  },

  // 获取文件预览URL
  getPreviewUrl: (mediaId: string) => {
    return request.get<{ previewUrl: string }>(`/media/v1/files/${mediaId}/preview`)
  },

  // 创建转码任务
  createTranscodeTask: (mediaId: string, options: {
    targetFormat: string
    quality?: string
    resolution?: string
    bitrate?: number
    watermark?: {
      text?: string
      imageUrl?: string
      position?: string
    }
  }) => {
    return request.post(`/media/v1/transcode/${mediaId}`, options)
  },

  // 获取转码任务列表
  getTranscodeTasks: (params?: {
    page?: number
    size?: number
    status?: string
  }) => {
    return request.get<PaginationResponse<TranscodeTask>>('/media/v1/transcode/tasks', params)
  },

  // 获取转码任务详情
  getTranscodeTask: (taskId: string) => {
    return request.get<TranscodeTask>(`/media/v1/transcode/tasks/${taskId}`)
  },

  // 取消转码任务
  cancelTranscodeTask: (taskId: string) => {
    return request.post(`/media/v1/transcode/tasks/${taskId}/cancel`)
  },

  // 获取存储使用情况
  getStorageUsage: () => {
    return request.get<{
      used: number
      total: number
      percentage: number
      filesCount: number
      lastCleanupTime: string
    }>('/media/v1/storage/usage')
  },

  // 清理过期文件
  cleanupExpiredFiles: () => {
    return request.post('/media/v1/storage/cleanup')
  },

  // 获取媒体统计信息
  getMediaStats: () => {
    return request.get<{
      totalFiles: number
      totalSize: number
      todayUploads: number
      todaySize: number
      fileTypeDistribution: Record<string, number>
    }>('/media/v1/stats')
  },

  // 批量添加标签
  batchAddTags: (mediaIds: string[], tags: string[]) => {
    return request.post('/media/v1/files/batch-tags', { ids: mediaIds, tags })
  },

  // 批量移除标签
  batchRemoveTags: (mediaIds: string[], tags: string[]) => {
    return request.post('/media/v1/files/batch-remove-tags', { ids: mediaIds, tags })
  }
}