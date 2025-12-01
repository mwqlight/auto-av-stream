// 监控API模块
import { request } from '@/api'
import type { MonitorMetricsResponse } from '@/types/monitor'

// 监控指标接口
export interface MonitorMetrics {
  // 服务状态
  serviceStatus: {
    overall: string
    storage: string
    media: string
    transcode: string
    lastCheckTime: string
  }
  
  // 系统指标
  systemMetrics: {
    cpuUsage: number
    memoryUsage: number
    memoryTotal: number
    memoryUsed: number
    diskUsage: number
    diskTotal: number
    diskUsed: number
    threadCount: number
    systemLoad: number
    uptime: number
  }
  
  // 业务指标
  businessMetrics: {
    uploadCount: number
    downloadCount: number
    transcodeCount: number
    streamCount: number
    activeUsers: number
    totalFiles: number
    totalSize: number
  }
  
  // 性能指标
  performanceMetrics: {
    avgResponseTime: number
    maxResponseTime: number
    errorRate: number
    throughput: number
    concurrentUsers: number
  }
  
  // 存储指标
  storageMetrics: {
    storageUsage: number
    storageTotal: number
    storageUsed: number
    storageFree: number
    storagePercent: number
  }
  
  // 网络指标
  networkMetrics: {
    networkIn: number
    networkOut: number
    requestsPerSecond: number
    activeConnections: number
  }
}

// 监控历史数据接口
export interface MonitorHistoryData {
  timestamp: string
  metrics: {
    cpuUsage: number
    memoryUsage: number
    diskUsage: number
    errorRate: number
    throughput: number
  }[]
}

// 监控API
export const monitorApi = {
  // 获取健康状态
  getHealthStatus(): Promise<MonitorMetricsResponse> {
    return request.get('/monitor/health')
  },

  // 获取详细健康状态
  getDetailedHealthStatus(): Promise<MonitorMetricsResponse> {
    return request.get('/monitor/health/detailed')
  },

  // 获取监控指标
  getMetrics(): Promise<MonitorMetricsResponse> {
    return request.get('/monitor/metrics')
  },

  // 获取详细监控指标
  getDetailedMetrics(): Promise<MonitorMetricsResponse> {
    return request.get('/monitor/metrics/detailed')
  },

  // 获取监控历史数据
  getHistoryData(params: {
    startTime: string
    endTime: string
    interval?: string
  }): Promise<MonitorHistoryData> {
    return request.get('/monitor/metrics/history', params)
  },

  // 重置监控指标
  resetMetrics(): Promise<void> {
    return request.post('/monitor/metrics/reset')
  },

  // 获取流媒体统计
  getStreamingStats(): Promise<any> {
    return request.get('/monitor/streaming')
  },

  // 获取存储性能
  getStoragePerformance(): Promise<any> {
    return request.get('/storage/performance')
  }
}

// 默认导出
export default monitorApi