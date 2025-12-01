import { request } from '@/api/index'

// AI语音识别结果
export interface SpeechRecognitionResult {
  id: string
  text: string
  confidence: number
  language: string
  duration: number
  timestamps?: Array<{
    word: string
    start: number
    end: number
    confidence: number
  }>
  createTime: string
}

// AI图像生成结果
export interface ImageGenerationResult {
  id: string
  prompt: string
  imageUrl: string
  thumbnailUrl: string
  width: number
  height: number
  model: string
  createTime: string
  metadata?: Record<string, any>
}

// AI文本转语音结果
export interface TextToSpeechResult {
  id: string
  text: string
  audioUrl: string
  duration: number
  voice: string
  language: string
  createTime: string
}

// AI内容分析结果
export interface ContentAnalysisResult {
  id: string
  content: string
  type: 'text' | 'image' | 'audio' | 'video'
  analysis: {
    sentiment?: {
      score: number
      label: 'positive' | 'negative' | 'neutral'
    }
    topics?: Array<{
      topic: string
      confidence: number
    }>
    entities?: Array<{
      entity: string
      type: string
      confidence: number
    }>
    summary?: string
    keywords?: string[]
  }
  createTime: string
}

// AI服务相关API
export const aiApi = {
  // 语音识别
  speechRecognition: (audioFile: File, options?: {
    language?: string
    model?: string
    timestamps?: boolean
  }) => {
    const formData = new FormData()
    formData.append('audio', audioFile)
    
    if (options) {
      formData.append('options', JSON.stringify(options))
    }
    
    return request.post<SpeechRecognitionResult>('/ai/v1/speech/recognition', formData)
  },

  // 批量语音识别
  batchSpeechRecognition: (audioFiles: File[], options?: {
    language?: string
    model?: string
  }) => {
    const formData = new FormData()
    audioFiles.forEach((file, index) => {
      formData.append(`audio_${index}`, file)
    })
    
    if (options) {
      formData.append('options', JSON.stringify(options))
    }
    
    return request.post<SpeechRecognitionResult[]>('/ai/v1/speech/recognition/batch', formData)
  },

  // 获取语音识别结果
  getSpeechRecognitionResult: (taskId: string) => {
    return request.get<SpeechRecognitionResult>(`/ai/v1/speech/recognition/${taskId}`)
  },

  // 图像生成
  generateImage: (prompt: string, options?: {
    model?: string
    width?: number
    height?: number
    numImages?: number
    style?: string
    negativePrompt?: string
    seed?: number
  }) => {
    return request.post<ImageGenerationResult[]>('/ai/v1/image/generation', {
      prompt,
      ...options
    })
  },

  // 获取图像生成结果
  getImageGenerationResult: (taskId: string) => {
    return request.get<ImageGenerationResult>(`/ai/v1/image/generation/${taskId}`)
  },

  // 图像风格转换
  styleTransfer: (contentImage: File, styleImage: File, options?: {
    model?: string
    strength?: number
  }) => {
    const formData = new FormData()
    formData.append('contentImage', contentImage)
    formData.append('styleImage', styleImage)
    
    if (options) {
      formData.append('options', JSON.stringify(options))
    }
    
    return request.post<ImageGenerationResult>('/ai/v1/image/style-transfer', formData)
  },

  // 图像超分辨率
  superResolution: (imageFile: File, options?: {
    scale?: number
    model?: string
  }) => {
    const formData = new FormData()
    formData.append('image', imageFile)
    
    if (options) {
      formData.append('options', JSON.stringify(options))
    }
    
    return request.post<ImageGenerationResult>('/ai/v1/image/super-resolution', formData)
  },

  // 文本转语音
  textToSpeech: (text: string, options?: {
    voice?: string
    language?: string
    speed?: number
    pitch?: number
    model?: string
  }) => {
    return request.post<TextToSpeechResult>('/ai/v1/tts', {
      text,
      ...options
    })
  },

  // 获取文本转语音结果
  getTextToSpeechResult: (taskId: string) => {
    return request.get<TextToSpeechResult>(`/ai/v1/tts/${taskId}`)
  },

  // 内容分析
  analyzeContent: (content: string | File, type: 'text' | 'image' | 'audio' | 'video', options?: {
    analysisType?: string[]
    language?: string
  }) => {
    if (typeof content === 'string') {
      return request.post<ContentAnalysisResult>('/ai/v1/content/analysis', {
        content,
        type,
        ...options
      })
    } else {
      const formData = new FormData()
      formData.append('file', content)
      formData.append('type', type)
      
      if (options) {
        formData.append('options', JSON.stringify(options))
      }
      
      return request.post<ContentAnalysisResult>('/ai/v1/content/analysis', formData)
    }
  },

  // 获取内容分析结果
  getContentAnalysisResult: (taskId: string) => {
    return request.get<ContentAnalysisResult>(`/ai/v1/content/analysis/${taskId}`)
  },

  // 智能摘要
  summarizeText: (text: string, options?: {
    maxLength?: number
    language?: string
    model?: string
  }) => {
    return request.post<{ summary: string }>('/ai/v1/text/summarize', {
      text,
      ...options
    })
  },

  // 关键词提取
  extractKeywords: (text: string, options?: {
    maxKeywords?: number
    language?: string
    model?: string
  }) => {
    return request.post<{ keywords: string[] }>('/ai/v1/text/keywords', {
      text,
      ...options
    })
  },

  // 情感分析
  sentimentAnalysis: (text: string, options?: {
    language?: string
    model?: string
  }) => {
    return request.post<{
      sentiment: {
        score: number
        label: 'positive' | 'negative' | 'neutral'
      }
    }>('/ai/v1/text/sentiment', {
      text,
      ...options
    })
  },

  // 实体识别
  entityRecognition: (text: string, options?: {
    language?: string
    model?: string
  }) => {
    return request.post<{
      entities: Array<{
        entity: string
        type: string
        confidence: number
      }>
    }>('/ai/v1/text/entities', {
      text,
      ...options
    })
  },

  // 获取AI模型列表
  getModels: () => {
    return request.get<Array<{
      id: string
      name: string
      type: 'speech' | 'image' | 'text' | 'multimodal'
      description: string
      supportedLanguages: string[]
      maxInputSize: number
      isAvailable: boolean
    }>>('/ai/v1/models')
  },

  // 获取AI服务状态
  getServiceStatus: () => {
    return request.get<{
      status: 'online' | 'offline' | 'degraded'
      models: Array<{
        id: string
        status: 'available' | 'unavailable' | 'loading'
        load: number
        queueSize: number
      }>
      usage: {
        totalRequests: number
        successfulRequests: number
        failedRequests: number
        averageResponseTime: number
      }
    }>('/ai/v1/status')
  },

  // 获取使用统计
  getUsageStats: (params?: {
    startDate?: string
    endDate?: string
    type?: string
  }) => {
    return request.get<{
      totalRequests: number
      requestsByType: Record<string, number>
      requestsByDate: Record<string, number>
      averageResponseTime: number
      successRate: number
    }>('/ai/v1/usage/stats', params)
  }
}