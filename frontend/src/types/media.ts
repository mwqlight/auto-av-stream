// 媒体文件类型
export type MediaFileType = 'video' | 'audio' | 'image' | 'document'

// 媒体文件状态
export type MediaFileStatus = 'uploading' | 'processing' | 'completed' | 'failed'

// 媒体文件信息
export interface MediaFile {
  id: string
  name: string
  originalName: string
  fileType: MediaFileType
  mimeType: string
  fileSize: number
  duration?: number
  width?: number
  height?: number
  thumbnailUrl?: string
  previewUrl?: string
  downloadUrl?: string
  status: MediaFileStatus
  uploadTime: string
  uploadBy: string
  tags?: string[]
  description?: string
  metadata?: {
    codec?: string
    bitrate?: number
    fps?: number
    sampleRate?: number
    channels?: number
    duration?: number
    resolution?: string
    orientation?: number
    [key: string]: any
  }
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
  chunked?: boolean
  totalChunks?: number
  currentChunk?: number
}

// 转码任务状态
export type TranscodeTaskStatus = 'pending' | 'processing' | 'completed' | 'failed' | 'cancelled'

// 转码任务信息
export interface TranscodeTask {
  id: string
  mediaId: string
  mediaName: string
  targetFormat: string
  quality: string
  resolution?: string
  bitrate?: number
  status: TranscodeTaskStatus
  progress: number
  createTime: Date
  startTime?: Date
  endTime?: Date
  error?: string
  outputUrl?: string
  outputSize?: number
  duration?: number
}

// 转码配置
export interface TranscodeConfig {
  targetFormat: string
  quality?: 'low' | 'medium' | 'high' | 'original'
  resolution?: string
  bitrate?: number
  fps?: number
  audioCodec?: string
  videoCodec?: string
  watermark?: {
    enabled: boolean
    text?: string
    imageUrl?: string
    position?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
    opacity?: number
  }
  crop?: {
    enabled: boolean
    x?: number
    y?: number
    width?: number
    height?: number
  }
}

// 媒体库统计信息
export interface MediaLibraryStats {
  totalFiles: number
  totalSize: number
  todayUploads: number
  todaySize: number
  fileTypeDistribution: Record<MediaFileType, number>
  storageUsage: {
    used: number
    total: number
    percentage: number
  }
}

// 媒体文件筛选参数
export interface MediaFilterParams {
  fileType?: MediaFileType | ''
  status?: MediaFileStatus | ''
  keyword?: string
  startDate?: string
  endDate?: string
  tags?: string[]
  sortBy?: 'uploadTime' | 'fileSize' | 'name'
  sortOrder?: 'asc' | 'desc'
}

// 分片上传信息
export interface ChunkUploadInfo {
  fileUuid: string
  fileName: string
  fileSize: number
  chunkSize: number
  totalChunks: number
  uploadedChunks: number[]
}

// 媒体文件操作记录
export interface MediaOperationLog {
  id: string
  mediaId: string
  operation: 'upload' | 'delete' | 'update' | 'transcode' | 'download'
  operator: string
  operatorName: string
  details?: string
  ip: string
  createTime: string
}

// 媒体文件标签
export interface MediaTag {
  id: string
  name: string
  color?: string
  count: number
  createTime: string
}

// 媒体文件批量操作参数
export interface BatchOperationParams {
  mediaIds: string[]
  operation: 'delete' | 'addTags' | 'removeTags' | 'update'
  data?: any
}

// 存储空间信息
export interface StorageSpaceInfo {
  used: number
  total: number
  percentage: number
  filesCount: number
  lastCleanupTime: string
  cleanupThreshold: number
  autoCleanup: boolean
}

// 媒体文件预览信息
export interface MediaPreviewInfo {
  id: string
  name: string
  fileType: MediaFileType
  previewUrl: string
  thumbnailUrl: string
  duration?: number
  width?: number
  height?: number
  fileSize: number
  uploadTime: string
}

// 媒体文件分享信息
export interface MediaShareInfo {
  id: string
  mediaId: string
  shareUrl: string
  password?: string
  expireTime?: string
  maxDownloads?: number
  downloadCount: number
  createTime: string
  createBy: string
}

// 媒体文件水印配置
export interface WatermarkConfig {
  enabled: boolean
  type: 'text' | 'image'
  text?: {
    content: string
    fontFamily: string
    fontSize: number
    color: string
    opacity: number
    position: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
    margin: number
  }
  image?: {
    url: string
    width: number
    height: number
    opacity: number
    position: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
    margin: number
  }
}

// 媒体处理配置
export interface MediaProcessingConfig {
  maxUploadSize: number
  allowedFileTypes: string[]
  autoTranscode: boolean
  transcodeConfigs: Record<MediaFileType, TranscodeConfig>
  watermark: WatermarkConfig
  storage: {
    maxStorage: number
    cleanupThreshold: number
    autoCleanup: boolean
  }
}

// 媒体文件元数据
export interface MediaMetadata {
  // 通用元数据
  format: string
  size: number
  duration?: number
  bitrate?: number
  
  // 视频元数据
  video?: {
    codec: string
    width: number
    height: number
    fps: number
    aspectRatio: string
    rotation?: number
  }
  
  // 音频元数据
  audio?: {
    codec: string
    sampleRate: number
    channels: number
    bitDepth: number
  }
  
  // 图片元数据
  image?: {
    format: string
    width: number
    height: number
    colorDepth: number
    dpi?: number
    orientation?: number
  }
  
  // 其他元数据
  creationTime?: string
  modificationTime?: string
  gps?: {
    latitude: number
    longitude: number
    altitude?: number
  }
}