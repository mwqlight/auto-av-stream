// 直播流状态
export type LiveStreamStatus = 'idle' | 'live' | 'stopped' | 'error'

// 录制状态
export type RecordingStatus = 'idle' | 'recording' | 'stopped' | 'error'

// 直播间状态
export type LiveRoomStatus = 'offline' | 'online' | 'maintenance'

// 直播流信息
export interface LiveStream {
  id: string
  name: string
  description?: string
  streamUrl: string
  playbackUrl: string
  status: LiveStreamStatus
  recordingStatus: RecordingStatus
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
    keyframeInterval: number
    bufferSize: number
  }
  statistics?: {
    bandwidth: number
    cpuUsage: number
    memoryUsage: number
    droppedFrames: number
    bitrate: number
    fps: number
    resolution: string
    uptime: number
  }
}

// 直播间信息
export interface LiveRoom {
  id: string
  name: string
  description?: string
  coverImage?: string
  status: LiveRoomStatus
  streamId?: string
  owner: string
  ownerName: string
  maxViewers: number
  currentViewers: number
  createTime: string
  tags?: string[]
  settings: {
    allowChat: boolean
    allowGifts: boolean
    requirePassword: boolean
    isPrivate: boolean
    recordingEnabled: boolean
    autoStart: boolean
    qualityOptions: string[]
  }
  streamInfo?: LiveStream
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
  status: RecordingStatus
  downloadUrl?: string
  thumbnailUrl?: string
  resolution: string
  bitrate: number
  createTime: string
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
  streamDistribution: Record<LiveStreamStatus, number>
  viewerTrend: Array<{
    time: string
    viewers: number
  }>
}

// 聊天消息
export interface ChatMessage {
  id: string
  roomId: string
  userId: string
  username: string
  avatar?: string
  content: string
  type: 'text' | 'image' | 'gift' | 'system'
  timestamp: string
  replyTo?: string
  giftInfo?: {
    giftId: string
    giftName: string
    giftValue: number
    giftImage: string
  }
}

// 礼物信息
export interface Gift {
  id: string
  name: string
  image: string
  value: number
  animation?: string
  description?: string
}

// 观众信息
export interface Viewer {
  id: string
  username: string
  avatar?: string
  level: number
  joinTime: string
  lastActive: string
  isMuted: boolean
  isKicked: boolean
}

// 直播质量选项
export interface QualityOption {
  name: string
  value: string
  resolution: string
  bitrate: number
  fps: number
  description: string
}

// 推流配置
export interface PushConfig {
  server: string
  streamKey: string
  protocol: 'rtmp' | 'srt' | 'rtsp'
  bitrate: number
  resolution: string
  fps: number
  audioCodec: string
  videoCodec: string
}

// 播放配置
export interface PlayConfig {
  protocol: 'hls' | 'rtmp' | 'flv' | 'webrtc'
  quality: string
  autoplay: boolean
  muted: boolean
  controls: boolean
}

// 录制配置
export interface RecordingConfig {
  enabled: boolean
  format: 'mp4' | 'flv' | 'ts'
  segmentDuration: number
  watermark: {
    enabled: boolean
    text?: string
    imageUrl?: string
    position: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
  }
  storage: {
    maxSize: number
    retentionDays: number
  }
}

// 直播事件
export interface LiveEvent {
  id: string
  type: 'stream_start' | 'stream_stop' | 'recording_start' | 'recording_stop' | 'viewer_join' | 'viewer_leave' | 'chat_message' | 'gift_send'
  streamId: string
  roomId: string
  userId?: string
  username?: string
  data?: any
  timestamp: string
}

// 直播分析数据
export interface LiveAnalytics {
  streamId: string
  period: 'today' | 'week' | 'month' | 'custom'
  metrics: {
    totalViewers: number
    averageViewTime: number
    peakViewers: number
    totalMessages: number
    totalGifts: number
    revenue: number
  }
  trends: {
    viewerTrend: Array<{ time: string; viewers: number }>
    messageTrend: Array<{ time: string; messages: number }>
    giftTrend: Array<{ time: string; gifts: number; revenue: number }>
  }
  audience: {
    geographic: Record<string, number>
    device: Record<string, number>
    age: Record<string, number>
    gender: Record<string, number>
  }
}

// WebRTC连接信息
export interface WebRTCConnection {
  id: string
  streamId: string
  peerConnection: RTCPeerConnection
  localStream?: MediaStream
  remoteStream?: MediaStream
  status: 'connecting' | 'connected' | 'disconnected' | 'failed'
  stats?: RTCStatsReport
}

// 直播控制命令
export interface LiveControlCommand {
  type: 'start' | 'stop' | 'pause' | 'resume' | 'restart' | 'quality_change'
  streamId: string
  data?: any
  timestamp: string
}

// 直播通知设置
export interface LiveNotificationSettings {
  email: boolean
  push: boolean
  sms: boolean
  events: {
    streamStart: boolean
    streamStop: boolean
    recordingStart: boolean
    recordingStop: boolean
    viewerThreshold: boolean
    error: boolean
  }
  thresholds: {
    viewerCount: number
    errorRate: number
    bandwidth: number
  }
}

// 直播模板
export interface LiveTemplate {
  id: string
  name: string
  description?: string
  config: {
    resolution: string
    bitrate: number
    fps: number
    audioCodec: string
    videoCodec: string
  }
  recording: RecordingConfig
  qualityOptions: QualityOption[]
  createTime: string
}

// 直播批量操作参数
export interface BatchLiveOperationParams {
  streamIds: string[]
  operation: 'start' | 'stop' | 'delete' | 'record_start' | 'record_stop'
  data?: any
}