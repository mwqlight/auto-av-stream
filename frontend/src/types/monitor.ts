// 监控相关类型定义

// 服务状态枚举
export enum ServiceStatus {
  HEALTHY = 'HEALTHY',
  WARNING = 'WARNING',
  ERROR = 'ERROR',
  UNKNOWN = 'UNKNOWN'
}

// 监控指标响应接口
export interface MonitorMetricsResponse {
  code: number
  message: string
  data: {
    // 服务状态
    serviceStatus: {
      overall: ServiceStatus
      storage: ServiceStatus
      media: ServiceStatus
      transcode: ServiceStatus
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
    
    // 时间戳
    timestamp: string
  }
}

// 监控历史数据接口
export interface MonitorHistoryData {
  code: number
  message: string
  data: {
    timestamp: string
    metrics: Array<{
      timestamp: string
      cpuUsage: number
      memoryUsage: number
      diskUsage: number
      errorRate: number
      throughput: number
    }>
  }
}

// 监控图表数据接口
export interface MonitorChartData {
  labels: string[]
  datasets: Array<{
    label: string
    data: number[]
    backgroundColor: string
    borderColor: string
    borderWidth: number
  }>
}

// 服务健康状态接口
export interface ServiceHealthStatus {
  serviceName: string
  status: ServiceStatus
  lastCheckTime: string
  responseTime: number
  errorMessage?: string
}

// 系统资源使用情况接口
export interface SystemResourceUsage {
  cpu: {
    usage: number
    cores: number
    loadAverage: number
  }
  memory: {
    usage: number
    total: number
    used: number
    free: number
  }
  disk: {
    usage: number
    total: number
    used: number
    free: number
  }
  network: {
    in: number
    out: number
    connections: number
  }
}

// 业务指标统计接口
export interface BusinessMetrics {
  uploads: {
    total: number
    today: number
    failed: number
  }
  downloads: {
    total: number
    today: number
    failed: number
  }
  transcodes: {
    total: number
    today: number
    failed: number
    averageTime: number
  }
  streaming: {
    total: number
    active: number
    failed: number
    averageBitrate: number
  }
}

// 性能指标接口
export interface PerformanceMetrics {
  responseTime: {
    average: number
    p95: number
    p99: number
    max: number
  }
  throughput: {
    requestsPerSecond: number
    dataPerSecond: number
  }
  errorRate: number
  availability: number
}

// 监控配置接口
export interface MonitorConfig {
  refreshInterval: number
  alertThresholds: {
    cpu: number
    memory: number
    disk: number
    errorRate: number
  }
  dataRetentionDays: number
  autoRefresh: boolean
}