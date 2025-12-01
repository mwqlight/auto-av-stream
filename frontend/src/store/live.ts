import { defineStore } from 'pinia'
import type { LiveStream, LiveRoom, LiveRecording, LiveStats } from '@/types/live'

export const useLiveStore = defineStore('live', {
  state: () => ({
    // 直播流列表
    liveStreams: [] as LiveStream[],
    totalStreams: 0,
    
    // 直播间列表
    liveRooms: [] as LiveRoom[],
    totalRooms: 0,
    
    // 录制列表
    recordings: [] as LiveRecording[],
    totalRecordings: 0,
    
    // 当前直播状态
    currentStream: null as LiveStream | null,
    currentRoom: null as LiveRoom | null,
    
    // 直播统计
    liveStats: {
      totalViewers: 0,
      activeStreams: 0,
      totalBandwidth: 0,
      cpuUsage: 0,
      memoryUsage: 0
    } as LiveStats,
    
    // WebRTC连接
    webrtcConnections: new Map<string, RTCPeerConnection>(),
    
    // 筛选条件
    filters: {
      status: '' as string,
      roomType: '' as string,
      keyword: '' as string
    },
    
    // 分页
    currentPage: 1,
    pageSize: 20,
    
    // 加载状态
    loading: {
      streams: false,
      rooms: false,
      recordings: false,
      stats: false
    }
  }),

  getters: {
    // 获取活跃的直播流
    activeStreams: (state) => state.liveStreams.filter(stream => stream.status === 'live'),
    
    // 获取正在录制的流
    recordingStreams: (state) => state.liveStreams.filter(stream => stream.recordingStatus === 'recording'),
    
    // 获取在线房间
    onlineRooms: (state) => state.liveRooms.filter(room => room.status === 'online'),
    
    // 获取分页信息
    paginationInfo: (state) => ({
      current: state.currentPage,
      pageSize: state.pageSize,
      total: state.totalStreams,
      totalPages: Math.ceil(state.totalStreams / state.pageSize)
    })
  },

  actions: {
    // 获取直播流列表
    async fetchLiveStreams(params?: any) {
      this.loading.streams = true
      try {
        const response = await $api.live.getLiveStreams({
          page: this.currentPage,
          size: this.pageSize,
          ...this.filters,
          ...params
        })
        
        this.liveStreams = response.data.items
        this.totalStreams = response.data.total
        
        return response
      } catch (error) {
        throw error
      } finally {
        this.loading.streams = false
      }
    },

    // 获取直播间列表
    async fetchLiveRooms(params?: any) {
      this.loading.rooms = true
      try {
        const response = await $api.live.getLiveRooms({
          page: this.currentPage,
          size: this.pageSize,
          ...this.filters,
          ...params
        })
        
        this.liveRooms = response.data.items
        this.totalRooms = response.data.total
        
        return response
      } catch (error) {
        throw error
      } finally {
        this.loading.rooms = false
      }
    },

    // 获取录制列表
    async fetchRecordings(params?: any) {
      this.loading.recordings = true
      try {
        const response = await $api.live.getRecordings({
          page: this.currentPage,
          size: this.pageSize,
          ...params
        })
        
        this.recordings = response.data.items
        this.totalRecordings = response.data.total
        
        return response
      } catch (error) {
        throw error
      } finally {
        this.loading.recordings = false
      }
    },

    // 获取直播统计
    async fetchLiveStats() {
      this.loading.stats = true
      try {
        const response = await $api.live.getLiveStats()
        this.liveStats = response.data
        return response
      } catch (error) {
        throw error
      } finally {
        this.loading.stats = false
      }
    },

    // 创建直播流
    async createLiveStream(streamData: any) {
      try {
        const response = await $api.live.createLiveStream(streamData)
        
        const newStream: LiveStream = {
          id: response.data.id,
          name: streamData.name,
          status: 'idle',
          streamUrl: response.data.streamUrl,
          playbackUrl: response.data.playbackUrl,
          createTime: new Date(),
          ...streamData
        }
        
        this.liveStreams.unshift(newStream)
        this.totalStreams += 1
        
        return response
      } catch (error) {
        throw error
      }
    },

    // 开始直播
    async startLiveStream(streamId: string) {
      try {
        const response = await $api.live.startLiveStream(streamId)
        
        // 更新流状态
        const stream = this.liveStreams.find(s => s.id === streamId)
        if (stream) {
          stream.status = 'live'
          stream.startTime = new Date()
        }
        
        return response
      } catch (error) {
        throw error
      }
    },

    // 停止直播
    async stopLiveStream(streamId: string) {
      try {
        const response = await $api.live.stopLiveStream(streamId)
        
        // 更新流状态
        const stream = this.liveStreams.find(s => s.id === streamId)
        if (stream) {
          stream.status = 'stopped'
          stream.endTime = new Date()
        }
        
        return response
      } catch (error) {
        throw error
      }
    },

    // 删除直播流
    async deleteLiveStream(streamId: string) {
      try {
        const response = await $api.live.deleteLiveStream(streamId)
        
        // 从列表中移除
        this.liveStreams = this.liveStreams.filter(stream => stream.id !== streamId)
        this.totalStreams -= 1
        
        return response
      } catch (error) {
        throw error
      }
    },

    // 开始录制
    async startRecording(streamId: string) {
      try {
        const response = await $api.live.startRecording(streamId)
        
        // 更新流录制状态
        const stream = this.liveStreams.find(s => s.id === streamId)
        if (stream) {
          stream.recordingStatus = 'recording'
        }
        
        return response
      } catch (error) {
        throw error
      }
    },

    // 停止录制
    async stopRecording(streamId: string) {
      try {
        const response = await $api.live.stopRecording(streamId)
        
        // 更新流录制状态
        const stream = this.liveStreams.find(s => s.id === streamId)
        if (stream) {
          stream.recordingStatus = 'stopped'
        }
        
        return response
      } catch (error) {
        throw error
      }
    },

    // 创建WebRTC连接
    createWebRTCConnection(streamId: string): RTCPeerConnection {
      const config: RTCConfiguration = {
        iceServers: [
          { urls: 'stun:stun.l.google.com:19302' }
        ]
      }
      
      const connection = new RTCPeerConnection(config)
      this.webrtcConnections.set(streamId, connection)
      
      return connection
    },

    // 关闭WebRTC连接
    closeWebRTCConnection(streamId: string) {
      const connection = this.webrtcConnections.get(streamId)
      if (connection) {
        connection.close()
        this.webrtcConnections.delete(streamId)
      }
    },

    // 设置当前直播流
    setCurrentStream(stream: LiveStream | null) {
      this.currentStream = stream
    },

    // 设置当前直播间
    setCurrentRoom(room: LiveRoom | null) {
      this.currentRoom = room
    },

    // 更新流状态（用于实时更新）
    updateStreamStatus(streamId: string, status: string) {
      const stream = this.liveStreams.find(s => s.id === streamId)
      if (stream) {
        stream.status = status
      }
    },

    // 更新观看人数
    updateViewerCount(streamId: string, viewerCount: number) {
      const stream = this.liveStreams.find(s => s.id === streamId)
      if (stream) {
        stream.viewerCount = viewerCount
      }
    },

    // 重置筛选条件
    resetFilters() {
      this.filters = {
        status: '',
        roomType: '',
        keyword: ''
      }
    }
  }
})