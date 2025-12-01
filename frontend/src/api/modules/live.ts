import { request } from '@/api/index'

// 直播流信息
export interface LiveStream {
  id: string
  name: string
  description?: string
  streamUrl: string
  playbackUrl: string
  status: 'idle' | 'live' | 'stopped' | 'error'
  recordingStatus: 'idle' | 'recording' | 'stopped'
  viewerCount: number
  maxViewers: number
  startTime?: string
  endTime?: string
  createTime: string
  createBy: string
  tags?: string[]
  config: {
    resolution: string
    bitrate: number
    fps: number
    audioCodec: string
    videoCodec: string
  }
  statistics?: {
    bandwidth: number
    cpuUsage: number
    memoryUsage: number
    droppedFrames: number
  }
}

// 直播间信息
export interface LiveRoom {
  id: string
  name: string
  description?: string
  coverImage?: string
  status: 'offline' | 'online' | 'maintenance'
  streamId?: string
  owner: string
  maxViewers: number
  currentViewers: number
  createTime: string
  tags?: string[]
  settings: {
    allowChat: boolean
    allowGifts: boolean
    requirePassword: boolean
    isPrivate: boolean
  }
}

// 直播录制信息
export interface LiveRecording {
  id: string
  streamId: string
  streamName: string
  fileName: string
  fileSize: number
  duration: number
  startTime: string
  endTime: string
  status: 'recording' | 'completed' | 'failed'
  downloadUrl?: string
  thumbnailUrl?: string
}

// 直播统计信息
export interface LiveStats {
  totalViewers: number
  activeStreams: number
  totalBandwidth: number
  cpuUsage: number
  memoryUsage: number
  todayStreams: number
  todayViewers: number
  peakViewers: number
  averageViewTime: number
}

// 直播管理相关API
export const liveApi = {
  // 获取直播流列表
  getLiveStreams: (params: {
    page?: number
    size?: number
    status?: string
    keyword?: string
    sortBy?: string
    sortOrder?: 'asc' | 'desc'
  }) => {
    return request.get<PaginationResponse<LiveStream>>('/live/v1/streams', params)
  },

  // 创建直播流
  createLiveStream: (data: {
    name: string
    description?: string
    config?: {
      resolution?: string
      bitrate?: number
      fps?: number
      audioCodec?: string
      videoCodec?: string
    }
    tags?: string[]
  }) => {
    return request.post<LiveStream>('/live/v1/streams', data)
  },

  // 获取直播流详情
  getLiveStream: (streamId: string) => {
    return request.get<LiveStream>(`/live/v1/streams/${streamId}`)
  },

  // 开始直播
  startLiveStream: (streamId: string) => {
    return request.post(`/live/v1/streams/${streamId}/start`)
  },

  // 停止直播
  stopLiveStream: (streamId: string) => {
    return request.post(`/live/v1/streams/${streamId}/stop`)
  },

  // 删除直播流
  deleteLiveStream: (streamId: string) => {
    return request.delete(`/live/v1/streams/${streamId}`)
  },

  // 更新直播流配置
  updateLiveStream: (streamId: string, data: {
    name?: string
    description?: string
    config?: any
    tags?: string[]
  }) => {
    return request.put(`/live/v1/streams/${streamId}`, data)
  },

  // 获取直播间列表
  getLiveRooms: (params: {
    page?: number
    size?: number
    status?: string
    roomType?: string
    keyword?: string
  }) => {
    return request.get<PaginationResponse<LiveRoom>>('/live/v1/rooms', params)
  },

  // 创建直播间
  createLiveRoom: (data: {
    name: string
    description?: string
    coverImage?: string
    settings?: {
      allowChat?: boolean
      allowGifts?: boolean
      requirePassword?: boolean
      isPrivate?: boolean
    }
    tags?: string[]
  }) => {
    return request.post<LiveRoom>('/live/v1/rooms', data)
  },

  // 获取直播间详情
  getLiveRoom: (roomId: string) => {
    return request.get<LiveRoom>(`/live/v1/rooms/${roomId}`)
  },

  // 更新直播间
  updateLiveRoom: (roomId: string, data: {
    name?: string
    description?: string
    coverImage?: string
    settings?: any
    tags?: string[]
  }) => {
    return request.put(`/live/v1/rooms/${roomId}`, data)
  },

  // 删除直播间
  deleteLiveRoom: (roomId: string) => {
    return request.delete(`/live/v1/rooms/${roomId}`)
  },

  // 绑定直播流到直播间
  bindStreamToRoom: (roomId: string, streamId: string) => {
    return request.post(`/live/v1/rooms/${roomId}/bind-stream`, { streamId })
  },

  // 解绑直播流
  unbindStreamFromRoom: (roomId: string) => {
    return request.post(`/live/v1/rooms/${roomId}/unbind-stream`)
  },

  // 开始录制
  startRecording: (streamId: string, options?: {
    fileName?: string
    segmentDuration?: number
    watermark?: {
      text?: string
      imageUrl?: string
      position?: string
    }
  }) => {
    return request.post(`/live/v1/recordings/${streamId}/start`, options)
  },

  // 停止录制
  stopRecording: (streamId: string) => {
    return request.post(`/live/v1/recordings/${streamId}/stop`)
  },

  // 获取录制列表
  getRecordings: (params: {
    page?: number
    size?: number
    streamId?: string
    startDate?: string
    endDate?: string
  }) => {
    return request.get<PaginationResponse<LiveRecording>>('/live/v1/recordings', params)
  },

  // 获取录制详情
  getRecording: (recordingId: string) => {
    return request.get<LiveRecording>(`/live/v1/recordings/${recordingId}`)
  },

  // 删除录制
  deleteRecording: (recordingId: string) => {
    return request.delete(`/live/v1/recordings/${recordingId}`)
  },

  // 获取直播统计
  getLiveStats: () => {
    return request.get<LiveStats>('/live/v1/stats')
  },

  // 获取流统计详情
  getStreamStats: (streamId: string) => {
    return request.get(`/live/v1/streams/${streamId}/stats`)
  },

  // 获取推流地址
  getPushUrl: (streamId: string) => {
    return request.get<{ pushUrl: string }>(`/live/v1/streams/${streamId}/push-url`)
  },

  // 获取播放地址
  getPlayUrls: (streamId: string) => {
    return request.get<{
      hlsUrl: string
      rtmpUrl: string
      flvUrl: string
      webrtcUrl: string
    }>(`/live/v1/streams/${streamId}/play-urls`)
  },

  // 发送聊天消息
  sendChatMessage: (roomId: string, message: {
    content: string
    type?: 'text' | 'image' | 'gift'
    replyTo?: string
  }) => {
    return request.post(`/live/v1/rooms/${roomId}/chat`, message)
  },

  // 获取聊天历史
  getChatHistory: (roomId: string, params?: {
    page?: number
    size?: number
    startTime?: string
    endTime?: string
  }) => {
    return request.get(`/live/v1/rooms/${roomId}/chat/history`, params)
  },

  // 禁言用户
  muteUser: (roomId: string, userId: string, duration?: number) => {
    return request.post(`/live/v1/rooms/${roomId}/mute`, { userId, duration })
  },

  // 解除禁言
  unmuteUser: (roomId: string, userId: string) => {
    return request.post(`/live/v1/rooms/${roomId}/unmute`, { userId })
  },

  // 踢出用户
  kickUser: (roomId: string, userId: string) => {
    return request.post(`/live/v1/rooms/${roomId}/kick`, { userId })
  }
}