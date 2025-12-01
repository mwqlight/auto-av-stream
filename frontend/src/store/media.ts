import { defineStore } from 'pinia'
import type { MediaFile, UploadTask, TranscodeTask } from '@/types/media'

export const useMediaStore = defineStore('media', {
  state: () => ({
    // 媒体文件列表
    mediaFiles: [] as MediaFile[],
    totalCount: 0,
    currentPage: 1,
    pageSize: 20,
    
    // 筛选条件
    filters: {
      fileType: '' as string,
      status: '' as string,
      keyword: '' as string,
      startDate: '' as string,
      endDate: '' as string
    },
    
    // 上传任务
    uploadTasks: [] as UploadTask[],
    
    // 转码任务
    transcodeTasks: [] as TranscodeTask[],
    
    // 当前选中的媒体文件
    currentMedia: null as MediaFile | null,
    
    // 存储使用情况
    storageUsage: {
      used: 0,
      total: 10737418240, // 10GB
      percentage: 0
    },
    
    // 加载状态
    loading: {
      list: false,
      upload: false,
      transcode: false
    }
  }),

  getters: {
    // 获取上传中的任务
    uploadingTasks: (state) => state.uploadTasks.filter(task => task.status === 'uploading'),
    
    // 获取转码中的任务
    transcodingTasks: (state) => state.transcodeTasks.filter(task => task.status === 'processing'),
    
    // 获取失败的任务
    failedTasks: (state) => state.uploadTasks.filter(task => task.status === 'failed'),
    
    // 获取视频文件
    videoFiles: (state) => state.mediaFiles.filter(file => file.fileType === 'video'),
    
    // 获取音频文件
    audioFiles: (state) => state.mediaFiles.filter(file => file.fileType === 'audio'),
    
    // 获取图片文件
    imageFiles: (state) => state.mediaFiles.filter(file => file.fileType === 'image'),
    
    // 获取分页信息
    paginationInfo: (state) => ({
      current: state.currentPage,
      pageSize: state.pageSize,
      total: state.totalCount,
      totalPages: Math.ceil(state.totalCount / state.pageSize)
    })
  },

  actions: {
    // 获取媒体文件列表
    async fetchMediaFiles(params?: any) {
      this.loading.list = true
      try {
        const response = await $api.media.getMediaFiles({
          page: this.currentPage,
          size: this.pageSize,
          ...this.filters,
          ...params
        })
        
        this.mediaFiles = response.data.items
        this.totalCount = response.data.total
        
        return response
      } catch (error) {
        throw error
      } finally {
        this.loading.list = false
      }
    },

    // 搜索媒体文件
    async searchMedia(keyword: string) {
      this.filters.keyword = keyword
      return this.fetchMediaFiles()
    },

    // 按类型筛选
    async filterByType(fileType: string) {
      this.filters.fileType = fileType
      return this.fetchMediaFiles()
    },

    // 按状态筛选
    async filterByStatus(status: string) {
      this.filters.status = status
      return this.fetchMediaFiles()
    },

    // 上传文件
    async uploadFile(file: File, options?: any) {
      this.loading.upload = true
      try {
        const taskId = this.createUploadTask(file, options)
        
        const response = await $api.media.uploadFile(file, {
          onUploadProgress: (progressEvent) => {
            this.updateUploadProgress(taskId, progressEvent)
          }
        })
        
        this.completeUploadTask(taskId, response.data)
        return response
      } catch (error) {
        this.failUploadTask(taskId, error)
        throw error
      } finally {
        this.loading.upload = false
      }
    },

    // 分片上传
    async uploadChunk(file: File, chunkIndex: number, totalChunks: number, fileUuid: string) {
      try {
        const response = await $api.media.uploadChunk(file, fileUuid, chunkIndex, totalChunks)
        return response
      } catch (error) {
        throw error
      }
    },

    // 合并分片
    async mergeChunks(fileUuid: string) {
      try {
        const response = await $api.media.mergeChunks(fileUuid)
        return response
      } catch (error) {
        throw error
      }
    },

    // 创建转码任务
    async createTranscodeTask(mediaId: string, options: any) {
      this.loading.transcode = true
      try {
        const response = await $api.media.createTranscodeTask(mediaId, options)
        
        const task: TranscodeTask = {
          id: response.data.id,
          mediaId,
          status: 'pending',
          progress: 0,
          createTime: new Date(),
          ...options
        }
        
        this.transcodeTasks.push(task)
        return response
      } catch (error) {
        throw error
      } finally {
        this.loading.transcode = false
      }
    },

    // 获取转码任务状态
    async fetchTranscodeTasks() {
      try {
        const response = await $api.media.getTranscodeTasks()
        this.transcodeTasks = response.data
        return response
      } catch (error) {
        throw error
      }
    },

    // 删除媒体文件
    async deleteMediaFile(mediaId: string) {
      try {
        const response = await $api.media.deleteMediaFile(mediaId)
        
        // 从列表中移除
        this.mediaFiles = this.mediaFiles.filter(file => file.id !== mediaId)
        this.totalCount -= 1
        
        return response
      } catch (error) {
        throw error
      }
    },

    // 获取存储使用情况
    async fetchStorageUsage() {
      try {
        const response = await $api.media.getStorageUsage()
        this.storageUsage = response.data
        return response
      } catch (error) {
        throw error
      }
    },

    // 创建上传任务
    createUploadTask(file: File, options?: any): string {
      const taskId = `upload-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
      
      const task: UploadTask = {
        id: taskId,
        file,
        fileName: file.name,
        fileSize: file.size,
        status: 'uploading',
        progress: 0,
        startTime: new Date(),
        ...options
      }
      
      this.uploadTasks.push(task)
      return taskId
    },

    // 更新上传进度
    updateUploadProgress(taskId: string, progressEvent: any) {
      const task = this.uploadTasks.find(t => t.id === taskId)
      if (task) {
        const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        task.progress = progress
      }
    },

    // 完成上传任务
    completeUploadTask(taskId: string, mediaFile: MediaFile) {
      const task = this.uploadTasks.find(t => t.id === taskId)
      if (task) {
        task.status = 'completed'
        task.endTime = new Date()
        task.result = mediaFile
        
        // 添加到媒体文件列表
        this.mediaFiles.unshift(mediaFile)
        this.totalCount += 1
      }
    },

    // 上传任务失败
    failUploadTask(taskId: string, error: any) {
      const task = this.uploadTasks.find(t => t.id === taskId)
      if (task) {
        task.status = 'failed'
        task.endTime = new Date()
        task.error = error.message || '上传失败'
      }
    },

    // 清除已完成的上传任务
    clearCompletedTasks() {
      this.uploadTasks = this.uploadTasks.filter(task => task.status !== 'completed')
    },

    // 设置当前媒体文件
    setCurrentMedia(media: MediaFile | null) {
      this.currentMedia = media
    },

    // 重置筛选条件
    resetFilters() {
      this.filters = {
        fileType: '',
        status: '',
        keyword: '',
        startDate: '',
        endDate: ''
      }
    }
  }
})