<template>
  <div class="monitor-container">
    <!-- 页面标题和操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">系统监控</h1>
        <p class="page-subtitle">实时监控媒体服务状态和性能指标</p>
      </div>
      <div class="header-right">
        <el-button @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button type="primary" @click="exportMetrics">
          <el-icon><Download /></el-icon>
          导出指标
        </el-button>
      </div>
    </div>

    <!-- 服务健康状态 -->
    <div class="health-status-section">
      <h2 class="section-title">服务健康状态</h2>
      <div class="health-cards">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="6">
            <div class="health-card" :class="getHealthClass(healthStatus.overall)">
              <div class="health-icon">
                <el-icon><Monitor /></el-icon>
              </div>
              <div class="health-info">
                <div class="health-value">{{ healthStatus.overall || '--' }}</div>
                <div class="health-label">总体状态</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="health-card" :class="getHealthClass(healthStatus.storage)">
              <div class="health-icon">
                <el-icon><DataBoard /></el-icon>
              </div>
              <div class="health-info">
                <div class="health-value">{{ healthStatus.storage || '--' }}</div>
                <div class="health-label">存储服务</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="health-card" :class="getHealthClass(healthStatus.media)">
              <div class="health-icon">
                <el-icon><VideoPlay /></el-icon>
              </div>
              <div class="health-info">
                <div class="health-value">{{ healthStatus.media || '--' }}</div>
                <div class="health-label">媒体服务</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="health-card" :class="getHealthClass(healthStatus.transcode)">
              <div class="health-icon">
                <el-icon><Setting /></el-icon>
              </div>
              <div class="health-info">
                <div class="health-value">{{ healthStatus.transcode || '--' }}</div>
                <div class="health-label">转码服务</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 性能指标 -->
    <div class="metrics-section">
      <h2 class="section-title">性能指标</h2>
      <div class="metrics-grid">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="6">
            <div class="metric-card">
              <div class="metric-icon upload">
                <el-icon><Upload /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-value">{{ metrics.uploadCount || 0 }}</div>
                <div class="metric-label">上传次数</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="metric-card">
              <div class="metric-icon download">
                <el-icon><Download /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-value">{{ metrics.downloadCount || 0 }}</div>
                <div class="metric-label">下载次数</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="metric-card">
              <div class="metric-icon transcode">
                <el-icon><MagicStick /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-value">{{ metrics.transcodeCount || 0 }}</div>
                <div class="metric-label">转码次数</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="metric-card">
              <div class="metric-icon stream">
                <el-icon><VideoCamera /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-value">{{ metrics.streamCount || 0 }}</div>
                <div class="metric-label">流媒体次数</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 实时监控图表 -->
    <div class="charts-section">
      <h2 class="section-title">实时监控</h2>
      <div class="charts-grid">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="chart-card">
              <div class="chart-header">
                <h3>CPU使用率</h3>
                <span class="chart-value">{{ systemMetrics.cpuUsage || 0 }}%</span>
              </div>
              <div class="chart-container">
                <div class="progress-chart">
                  <div 
                    class="progress-bar" 
                    :style="{ width: (systemMetrics.cpuUsage || 0) + '%' }"
                    :class="getProgressClass(systemMetrics.cpuUsage)"
                  ></div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="chart-card">
              <div class="chart-header">
                <h3>内存使用</h3>
                <span class="chart-value">{{ systemMetrics.memoryUsage || 0 }}%</span>
              </div>
              <div class="chart-container">
                <div class="progress-chart">
                  <div 
                    class="progress-bar" 
                    :style="{ width: (systemMetrics.memoryUsage || 0) + '%' }"
                    :class="getProgressClass(systemMetrics.memoryUsage)"
                  ></div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <div class="chart-card">
              <div class="chart-header">
                <h3>存储使用</h3>
                <span class="chart-value">{{ formatStorage(metrics.storageUsage) }}</span>
              </div>
              <div class="chart-container">
                <div class="progress-chart">
                  <div 
                    class="progress-bar" 
                    :style="{ width: (storageUsagePercent || 0) + '%' }"
                    :class="getProgressClass(storageUsagePercent)"
                  ></div>
                </div>
                <div class="chart-info">
                  <span>{{ formatStorage(metrics.storageUsage) }} / 10GB</span>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="chart-card">
              <div class="chart-header">
                <h3>错误率</h3>
                <span class="chart-value">{{ errorRate }}%</span>
              </div>
              <div class="chart-container">
                <div class="progress-chart">
                  <div 
                    class="progress-bar" 
                    :style="{ width: errorRate + '%' }"
                    :class="getProgressClass(errorRate)"
                  ></div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 流媒体统计 -->
    <div class="streaming-section">
      <h2 class="section-title">流媒体统计</h2>
      <div class="streaming-cards">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="8">
            <div class="streaming-card">
              <div class="streaming-icon">
                <el-icon><VideoPlay /></el-icon>
              </div>
              <div class="streaming-info">
                <div class="streaming-value">{{ streamingStats.activeStreams || 0 }}</div>
                <div class="streaming-label">活跃流</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="8">
            <div class="streaming-card">
              <div class="streaming-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="streaming-info">
                <div class="streaming-value">{{ formatBandwidth(streamingStats.bandwidth?.upload) }}</div>
                <div class="streaming-label">上传带宽</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="8">
            <div class="streaming-card">
              <div class="streaming-icon">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="streaming-info">
                <div class="streaming-value">{{ streamingStats.latency?.average || 0 }}ms</div>
                <div class="streaming-label">平均延迟</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 最近活动 -->
    <div class="activity-section">
      <h2 class="section-title">最近活动</h2>
      <div class="activity-list">
        <div 
          v-for="activity in recentActivities" 
          :key="activity.id"
          class="activity-item"
        >
          <div class="activity-icon" :class="`icon--${activity.type}`">
            <el-icon>
              <component :is="activity.icon" />
            </el-icon>
          </div>
          <div class="activity-content">
            <div class="activity-title">{{ activity.title }}</div>
            <div class="activity-description">{{ activity.description }}</div>
            <div class="activity-time">{{ activity.time }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { monitorApi } from '@/api/modules/monitor'
import type { MonitorMetricsResponse, ServiceStatus } from '@/types/monitor'
import {
  Monitor,
  DataBoard,
  VideoPlay,
  Setting,
  Upload,
  Download,
  MagicStick,
  VideoCamera,
  Refresh,
  TrendCharts,
  Clock
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const healthStatus = reactive({
  overall: '',
  storage: '',
  media: '',
  transcode: ''
})

const metrics = reactive({
  uploadCount: 0,
  downloadCount: 0,
  transcodeCount: 0,
  streamCount: 0,
  errorCount: 0,
  activeUploads: 0,
  activeTranscodes: 0,
  activeStreams: 0,
  storageUsage: 0,
  queueLength: 0
})

const systemMetrics = reactive({
  cpuUsage: 0,
  memoryUsage: 0,
  memoryTotal: 0,
  memoryUsed: 0,
  diskUsage: 0,
  diskTotal: 0,
  diskUsed: 0,
  threadCount: 0,
  systemLoad: 0,
  uptime: 0
})

const streamingStats = reactive({
  activeStreams: 0,
  bandwidth: {
    upload: 0,
    download: 0,
    total: 0
  },
  latency: {
    average: 0,
    max: 0,
    min: 0
  }
})

const recentActivities = ref([
  {
    id: 1,
    type: 'upload',
    icon: 'Upload',
    title: '文件上传完成',
    description: 'video.mp4 已成功上传',
    time: '2分钟前'
  },
  {
    id: 2,
    type: 'transcode',
    icon: 'MagicStick',
    title: '转码任务完成',
    description: 'video.mp4 转码为H.264格式',
    time: '5分钟前'
  },
  {
    id: 3,
    type: 'stream',
    icon: 'VideoPlay',
    title: '直播流开始',
    description: '直播房间123开始推流',
    time: '10分钟前'
  }
])

// 计算属性
const errorRate = computed(() => {
  const total = metrics.uploadCount + metrics.downloadCount + metrics.transcodeCount + metrics.streamCount
  return total > 0 ? Math.round((metrics.errorCount / total) * 100) : 0
})

const storageUsagePercent = computed(() => {
  const maxStorage = 10 * 1024 * 1024 * 1024 // 10GB
  return Math.round((metrics.storageUsage / maxStorage) * 100)
})

// 方法
const getHealthClass = (status: string) => {
  if (status === 'UP' || status === 'HEALTHY' || status === ServiceStatus.HEALTHY) return 'health-healthy'
  if (status === 'DOWN' || status === 'UNHEALTHY' || status === ServiceStatus.ERROR) return 'health-unhealthy'
  if (status === ServiceStatus.WARNING) return 'health-warning'
  return 'health-unknown'
}

const getProgressClass = (value: number) => {
  if (value < 50) return 'progress-low'
  if (value < 80) return 'progress-medium'
  return 'progress-high'
}

const formatStorage = (bytes: number) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
  return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
}

const formatBandwidth = (bytes: number) => {
  if (!bytes) return '0 B/s'
  if (bytes < 1024) return bytes + ' B/s'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB/s'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB/s'
}

const formatTime = (timestamp: string) => {
  const now = new Date()
  const time = new Date(timestamp)
  const diff = now.getTime() - time.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return Math.floor(diff / 86400000) + '天前'
}

// API调用
const fetchHealthStatus = async () => {
  try {
    const response = await monitorApi.getDetailedHealthStatus()
    if (response.code === 200 && response.data) {
      const { serviceStatus } = response.data
      Object.assign(healthStatus, {
        overall: serviceStatus.overall,
        storage: serviceStatus.storage,
        media: serviceStatus.media,
        transcode: serviceStatus.transcode
      })
    }
  } catch (error) {
    console.error('获取健康状态失败:', error)
    ElMessage.error('获取健康状态失败')
  }
}

const fetchMetrics = async () => {
  try {
    const response = await monitorApi.getDetailedMetrics()
    if (response.code === 200 && response.data) {
      const { businessMetrics, systemMetrics: sysMetrics, performanceMetrics, storageMetrics } = response.data
      
      // 更新业务指标
      Object.assign(metrics, {
        uploadCount: businessMetrics.uploadCount,
        downloadCount: businessMetrics.downloadCount,
        transcodeCount: businessMetrics.transcodeCount,
        streamCount: businessMetrics.streamCount,
        storageUsage: storageMetrics.storageUsed
      })
      
      // 更新系统指标
      Object.assign(systemMetrics, {
        cpuUsage: sysMetrics.cpuUsage,
        memoryUsage: sysMetrics.memoryUsage,
        memoryTotal: sysMetrics.memoryTotal,
        memoryUsed: sysMetrics.memoryUsed,
        diskUsage: sysMetrics.diskUsage,
        diskTotal: sysMetrics.diskTotal,
        diskUsed: sysMetrics.diskUsed,
        threadCount: sysMetrics.threadCount,
        systemLoad: sysMetrics.systemLoad,
        uptime: sysMetrics.uptime
      })
    }
  } catch (error) {
    console.error('获取指标数据失败:', error)
    ElMessage.error('获取指标数据失败')
  }
}

const fetchStreamingStats = async () => {
  try {
    const response = await monitorApi.getStreamingStats()
    if (response.code === 200 && response.data) {
      Object.assign(streamingStats, response.data)
    }
  } catch (error) {
    console.error('获取流媒体统计失败:', error)
    ElMessage.error('获取流媒体统计失败')
  }
}

const refreshData = async () => {
  loading.value = true
  try {
    await Promise.all([
      fetchHealthStatus(),
      fetchMetrics(),
      fetchStreamingStats()
    ])
    ElMessage.success('数据刷新成功')
  } catch (error) {
    ElMessage.error('数据刷新失败')
  } finally {
    loading.value = false
  }
}

const exportMetrics = () => {
  const data = {
    healthStatus,
    metrics,
    systemMetrics,
    streamingStats,
    timestamp: new Date().toISOString()
  }
  
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `media-metrics-${new Date().getTime()}.json`
  a.click()
  URL.revokeObjectURL(url)
  
  ElMessage.success('指标数据导出成功')
}

// 初始化数据
onMounted(() => {
  refreshData()
  
  // 定时刷新数据（每30秒）
  setInterval(refreshData, 30000)
})
</script>

<style scoped lang="scss">
.monitor-container {
  padding: 20px;
  background: var(--bg-color);
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  
  .header-left {
    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0 0 8px 0;
    }
    
    .page-subtitle {
      font-size: 14px;
      color: var(--text-secondary);
      margin: 0;
    }
  }
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

// 健康状态卡片
.health-cards {
  margin-bottom: 30px;
  
  .health-card {
    background: var(--card-bg);
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    border: 1px solid var(--border-color);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    &.health-healthy {
      border-left: 4px solid #52c41a;
    }
    
    &.health-unhealthy {
      border-left: 4px solid #f5222d;
    }
    
    &.health-warning {
      border-left: 4px solid #faad14;
    }
    
    &.health-unknown {
      border-left: 4px solid #d9d9d9;
    }
    
    .health-icon {
      margin-right: 15px;
      
      .el-icon {
        font-size: 24px;
      }
    }
    
    .health-info {
      .health-value {
        font-size: 20px;
        font-weight: 600;
        margin-bottom: 4px;
      }
      
      .health-label {
        font-size: 14px;
        color: var(--text-secondary);
      }
    }
  }
}

// 指标卡片
.metrics-grid {
  margin-bottom: 30px;
  
  .metric-card {
    background: var(--card-bg);
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    border: 1px solid var(--border-color);
    
    .metric-icon {
      margin-right: 15px;
      width: 48px;
      height: 48px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      
      &.upload { background: #e6f7ff; }
      &.download { background: #f6ffed; }
      &.transcode { background: #fff7e6; }
      &.stream { background: #f9f0ff; }
      
      .el-icon {
        font-size: 24px;
        color: var(--primary-color);
      }
    }
    
    .metric-info {
      .metric-value {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 4px;
      }
      
      .metric-label {
        font-size: 14px;
        color: var(--text-secondary);
      }
    }
  }
}

// 图表卡片
.charts-section {
  margin-bottom: 30px;
  
  .chart-card {
    background: var(--card-bg);
    border-radius: 8px;
    padding: 20px;
    border: 1px solid var(--border-color);
    
    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
      
      h3 {
        font-size: 16px;
        font-weight: 600;
        margin: 0;
      }
      
      .chart-value {
        font-size: 18px;
        font-weight: 600;
        color: var(--primary-color);
      }
    }
    
    .progress-chart {
      height: 8px;
      background: var(--border-color);
      border-radius: 4px;
      overflow: hidden;
      
      .progress-bar {
        height: 100%;
        transition: width 0.3s ease;
        
        &.progress-low { background: #52c41a; }
        &.progress-medium { background: #faad14; }
        &.progress-high { background: #f5222d; }
      }
    }
    
    .chart-info {
      margin-top: 8px;
      font-size: 12px;
      color: var(--text-secondary);
    }
  }
}

// 流媒体统计
.streaming-cards {
  margin-bottom: 30px;
  
  .streaming-card {
    background: var(--card-bg);
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    border: 1px solid var(--border-color);
    
    .streaming-icon {
      margin-right: 15px;
      
      .el-icon {
        font-size: 24px;
        color: var(--primary-color);
      }
    }
    
    .streaming-info {
      .streaming-value {
        font-size: 20px;
        font-weight: 600;
        margin-bottom: 4px;
      }
      
      .streaming-label {
        font-size: 14px;
        color: var(--text-secondary);
      }
    }
  }
}

// 活动列表
.activity-section {
  .activity-list {
    .activity-item {
      display: flex;
      align-items: center;
      padding: 15px;
      background: var(--card-bg);
      border-radius: 8px;
      margin-bottom: 10px;
      border: 1px solid var(--border-color);
      
      .activity-icon {
        margin-right: 15px;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        
        &.icon-upload { background: #e6f7ff; }
        &.icon-transcode { background: #fff7e6; }
        &.icon-stream { background: #f9f0ff; }
        
        .el-icon {
          font-size: 20px;
          color: var(--primary-color);
        }
      }
      
      .activity-content {
        flex: 1;
        
        .activity-title {
          font-weight: 600;
          margin-bottom: 4px;
        }
        
        .activity-description {
          font-size: 14px;
          color: var(--text-secondary);
          margin-bottom: 4px;
        }
        
        .activity-time {
          font-size: 12px;
          color: var(--text-tertiary);
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .health-cards,
  .metrics-grid,
  .streaming-cards {
    .el-col {
      margin-bottom: 15px;
    }
  }
  
  .charts-section {
    .el-col {
      margin-bottom: 15px;
    }
  }
}
</style>