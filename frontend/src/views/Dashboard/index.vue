<template>
  <div class="dashboard">
    <!-- 粒子背景 -->
    <ParticlesBackground 
      :density="0.8" 
      :speed="1.2"
      class="dashboard-bg"
    />
    
    <!-- 网格背景 -->
    <div class="grid-bg"></div>
    
    <!-- 页面标题 -->
    <div class="dashboard-header">
      <h1 class="dashboard-title">
        <span class="title-text">智能音视频流媒体平台</span>
        <span class="title-subtitle">实时监控与智能分析</span>
      </h1>
      <div class="dashboard-actions">
        <button class="btn btn--primary" @click="refreshData">
          <i class="icon-refresh"></i>
          刷新数据
        </button>
        <button class="btn btn--secondary" @click="exportReport">
          <i class="icon-download"></i>
          导出报告
        </button>
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div 
        v-for="stat in stats" 
        :key="stat.id"
        class="stat-card"
        :class="`stat-card--${stat.type}`"
      >
        <div class="stat-icon">
          <i :class="stat.icon"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-trend" :class="`trend--${stat.trend.type}`">
            <i :class="stat.trend.icon"></i>
            <span>{{ stat.trend.value }}</span>
          </div>
        </div>
        <div class="stat-chart">
          <div class="mini-chart">
            <div 
              v-for="(point, index) in stat.chartData" 
              :key="index"
              class="chart-bar"
              :style="{ height: point + '%' }"
            ></div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="dashboard-content">
      <!-- 左侧：实时监控 -->
      <div class="content-left">
        <!-- 实时流监控 -->
        <div class="monitor-panel card card--glass">
          <div class="panel-header">
            <h3 class="panel-title">实时流监控</h3>
            <div class="panel-actions">
              <button class="btn btn--text" @click="toggleAutoRefresh">
                <i :class="autoRefresh ? 'icon-pause' : 'icon-play'"></i>
                {{ autoRefresh ? '暂停' : '开始' }}
              </button>
            </div>
          </div>
          <div class="panel-content">
            <div class="stream-list">
              <div 
                v-for="stream in liveStreams" 
                :key="stream.id"
                class="stream-item"
                :class="{ 'stream-item--active': stream.status === 'live' }"
              >
                <div class="stream-info">
                  <div class="stream-thumbnail">
                    <img :src="stream.thumbnail" :alt="stream.title" />
                    <div class="stream-status" :class="`status--${stream.status}`">
                      {{ stream.status === 'live' ? '直播中' : '离线' }}
                    </div>
                  </div>
                  <div class="stream-details">
                    <div class="stream-title">{{ stream.title }}</div>
                    <div class="stream-meta">
                      <span class="viewers">
                        <i class="icon-eye"></i>
                        {{ stream.viewers }} 观看
                      </span>
                      <span class="duration">
                        <i class="icon-clock"></i>
                        {{ stream.duration }}
                      </span>
                    </div>
                  </div>
                </div>
                <div class="stream-actions">
                  <button class="btn btn--text" @click="viewStream(stream)">
                    <i class="icon-play"></i>
                  </button>
                  <button class="btn btn--text" @click="manageStream(stream)">
                    <i class="icon-settings"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 系统状态 -->
        <div class="system-panel card card--glass">
          <div class="panel-header">
            <h3 class="panel-title">系统状态</h3>
          </div>
          <div class="panel-content">
            <div class="system-metrics">
              <div 
                v-for="metric in systemMetrics" 
                :key="metric.name"
                class="metric-item"
              >
                <div class="metric-info">
                  <div class="metric-name">{{ metric.name }}</div>
                  <div class="metric-value">{{ metric.value }}</div>
                </div>
                <div class="metric-progress">
                  <div 
                    class="progress-bar"
                    :class="`progress--${metric.status}`"
                  >
                    <div 
                      class="progress-fill"
                      :style="{ width: metric.percentage + '%' }"
                    ></div>
                  </div>
                  <div class="progress-label">{{ metric.percentage }}%</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧：图表和分析 -->
      <div class="content-right">
        <!-- 流量统计 -->
        <div class="chart-panel card card--glass">
          <div class="panel-header">
            <h3 class="panel-title">流量统计</h3>
            <div class="chart-controls">
              <button 
                v-for="period in chartPeriods" 
                :key="period"
                class="btn btn--text"
                :class="{ 'btn--primary': activePeriod === period }"
                @click="activePeriod = period"
              >
                {{ period }}
              </button>
            </div>
          </div>
          <div class="panel-content">
            <div class="chart-container">
              <div class="traffic-chart" ref="trafficChart">
                <!-- 这里可以集成 ECharts 或其他图表库 -->
                <div class="chart-placeholder">
                  <i class="icon-chart"></i>
                  <p>流量统计图表</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 最近活动 -->
        <div class="activity-panel card card--glass">
          <div class="panel-header">
            <h3 class="panel-title">最近活动</h3>
            <button class="btn btn--text" @click="viewAllActivities">查看全部</button>
          </div>
          <div class="panel-content">
            <div class="activity-list">
              <div 
                v-for="activity in recentActivities" 
                :key="activity.id"
                class="activity-item"
              >
                <div class="activity-icon" :class="`icon--${activity.type}`">
                  <i :class="activity.icon"></i>
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
        
        <!-- 快速操作 -->
        <div class="quick-actions-panel card card--glass">
          <div class="panel-header">
            <h3 class="panel-title">快速操作</h3>
          </div>
          <div class="panel-content">
            <div class="quick-actions">
              <button 
                v-for="action in quickActions" 
                :key="action.id"
                class="quick-action"
                :class="`quick-action--${action.type}`"
                @click="handleQuickAction(action)"
              >
                <div class="action-icon">
                  <i :class="action.icon"></i>
                </div>
                <div class="action-text">{{ action.text }}</div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 底部信息栏 -->
    <div class="dashboard-footer">
      <div class="footer-info">
        <span class="server-status" :class="`status--${serverStatus}`">
          <i class="icon-server"></i>
          服务器状态: {{ serverStatus === 'online' ? '在线' : '离线' }}
        </span>
        <span class="last-update">
          最后更新: {{ lastUpdate }}
        </span>
      </div>
      <div class="footer-version">
        v{{ appVersion }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import ParticlesBackground from '@/components/ParticlesBackground.vue'

interface StatItem {
  id: string
  type: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  icon: string
  value: string | number
  label: string
  trend: {
    type: 'up' | 'down' | 'stable'
    icon: string
    value: string
  }
  chartData: number[]
}

interface LiveStream {
  id: string
  title: string
  thumbnail: string
  status: 'live' | 'offline'
  viewers: number
  duration: string
}

interface SystemMetric {
  name: string
  value: string
  percentage: number
  status: 'normal' | 'warning' | 'danger'
}

interface Activity {
  id: string
  type: 'upload' | 'transcode' | 'live' | 'ai' | 'system'
  icon: string
  title: string
  description: string
  time: string
}

interface QuickAction {
  id: string
  type: 'primary' | 'success' | 'warning' | 'danger'
  icon: string
  text: string
  route?: string
  action?: () => void
}

const router = useRouter()

// 响应式数据
const autoRefresh = ref(true)
const activePeriod = ref('今日')
const serverStatus = ref<'online' | 'offline'>('online')
const lastUpdate = ref('刚刚')
const appVersion = ref('1.0.0')

// 统计卡片数据
const stats = ref<StatItem[]>([
  {
    id: '1',
    type: 'primary',
    icon: 'icon-live',
    value: '12',
    label: '活跃直播流',
    trend: { type: 'up', icon: 'icon-trend-up', value: '+5.2%' },
    chartData: [30, 45, 60, 75, 80, 70, 65, 75, 85, 90, 85, 80]
  },
  {
    id: '2',
    type: 'success',
    icon: 'icon-media',
    value: '256',
    label: '媒体文件',
    trend: { type: 'up', icon: 'icon-trend-up', value: '+12.3%' },
    chartData: [20, 35, 50, 45, 60, 55, 70, 65, 80, 75, 70, 65]
  },
  {
    id: '3',
    type: 'warning',
    icon: 'icon-users',
    value: '1,234',
    label: '在线用户',
    trend: { type: 'stable', icon: 'icon-trend-stable', value: '+0.5%' },
    chartData: [40, 50, 45, 55, 60, 65, 70, 75, 80, 85, 80, 75]
  },
  {
    id: '4',
    type: 'danger',
    icon: 'icon-storage',
    value: '85%',
    label: '存储使用',
    trend: { type: 'up', icon: 'icon-trend-up', value: '+8.7%' },
    chartData: [30, 40, 50, 60, 70, 75, 80, 85, 90, 85, 80, 75]
  }
])

// 实时流数据
const liveStreams = ref<LiveStream[]>([
  {
    id: '1',
    title: '技术分享直播',
    thumbnail: '/api/placeholder/80/45',
    status: 'live',
    viewers: 156,
    duration: '02:15:30'
  },
  {
    id: '2',
    title: '产品发布会',
    thumbnail: '/api/placeholder/80/45',
    status: 'live',
    viewers: 89,
    duration: '01:45:12'
  },
  {
    id: '3',
    title: '培训课程',
    thumbnail: '/api/placeholder/80/45',
    status: 'offline',
    viewers: 0,
    duration: '00:00:00'
  }
])

// 系统指标
const systemMetrics = ref<SystemMetric[]>([
  { name: 'CPU使用率', value: '45%', percentage: 45, status: 'normal' },
  { name: '内存使用率', value: '68%', percentage: 68, status: 'warning' },
  { name: '磁盘使用率', value: '85%', percentage: 85, status: 'danger' },
  { name: '网络带宽', value: '120 Mbps', percentage: 60, status: 'normal' }
])

// 图表周期选项
const chartPeriods = ref(['今日', '本周', '本月', '今年'])

// 最近活动
const recentActivities = ref<Activity[]>([
  {
    id: '1',
    type: 'upload',
    icon: 'icon-upload',
    title: '文件上传完成',
    description: 'video-presentation.mp4 已成功上传',
    time: '2分钟前'
  },
  {
    id: '2',
    type: 'transcode',
    icon: 'icon-transcode',
    title: '转码任务完成',
    description: '3个视频文件转码成功',
    time: '15分钟前'
  },
  {
    id: '3',
    type: 'live',
    icon: 'icon-live',
    title: '直播流开始',
    description: '技术分享直播已开始',
    time: '1小时前'
  },
  {
    id: '4',
    type: 'ai',
    icon: 'icon-ai',
    title: 'AI分析完成',
    description: '视频内容分析报告已生成',
    time: '3小时前'
  }
])

// 快速操作
const quickActions = ref<QuickAction[]>([
  {
    id: '1',
    type: 'primary',
    icon: 'icon-live',
    text: '开始直播',
    route: '/live/console'
  },
  {
    id: '2',
    type: 'success',
    icon: 'icon-upload',
    text: '上传媒体',
    route: '/media/upload'
  },
  {
    id: '3',
    type: 'warning',
    icon: 'icon-transcode',
    text: '批量转码',
    route: '/media/transcode'
  },
  {
    id: '4',
    type: 'danger',
    icon: 'icon-ai',
    text: 'AI分析',
    route: '/ai/analysis'
  }
])

// 方法
const refreshData = () => {
  // 模拟数据刷新
  console.log('刷新数据...')
  lastUpdate.value = '刚刚'
}

const exportReport = () => {
  // 导出报告逻辑
  console.log('导出报告...')
}

const toggleAutoRefresh = () => {
  autoRefresh.value = !autoRefresh.value
}

const viewStream = (stream: LiveStream) => {
  if (stream.status === 'live') {
    router.push(`/live/player/${stream.id}`)
  }
}

const manageStream = (stream: LiveStream) => {
  router.push(`/live/console/${stream.id}`)
}

const viewAllActivities = () => {
  router.push('/activities')
}

const handleQuickAction = (action: QuickAction) => {
  if (action.route) {
    router.push(action.route)
  } else if (action.action) {
    action.action()
  }
}

// 生命周期
onMounted(() => {
  // 初始化数据
  refreshData()
  
  // 设置自动刷新定时器
  const refreshInterval = setInterval(() => {
    if (autoRefresh.value) {
      refreshData()
    }
  }, 30000) // 30秒刷新一次
  
  onUnmounted(() => {
    clearInterval(refreshInterval)
  })
})
</script>

<style scoped lang="scss">
.dashboard {
  min-height: 100vh;
  background: var(--gradient-bg);
  position: relative;
  overflow-x: hidden;
}

.dashboard-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
}

.grid-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 20px 20px;
  z-index: -1;
  pointer-events: none;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-xl) var(--spacing-xl) var(--spacing-lg);
  
  .dashboard-title {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-sm);
    
    .title-text {
      font-size: 28px;
      font-weight: 700;
      background: var(--gradient-primary);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    
    .title-subtitle {
      font-size: 16px;
      color: var(--text-color-light);
      font-weight: 400;
    }
  }
  
  .dashboard-actions {
    display: flex;
    gap: var(--spacing-md);
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-lg);
  padding: 0 var(--spacing-xl) var(--spacing-xl);
}

.stat-card {
  background: var(--bg-color-light);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-large);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  transition: var(--transition-normal);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: var(--gradient-primary);
    transform: scaleX(0);
    transition: var(--transition-normal);
  }
  
  &:hover {
    border-color: var(--border-color-light);
    box-shadow: var(--shadow-medium);
    transform: translateY(-2px);
    
    &::before {
      transform: scaleX(1);
    }
  }
  
  &.stat-card--primary::before {
    background: var(--gradient-primary);
  }
  
  &.stat-card--success::before {
    background: var(--gradient-success);
  }
  
  &.stat-card--warning::before {
    background: var(--gradient-warning);
  }
  
  &.stat-card--danger::before {
    background: var(--gradient-danger);
  }
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--border-radius-base);
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  
  .stat-card--primary & {
    color: var(--primary-color);
  }
  
  .stat-card--success & {
    color: var(--success-color);
  }
  
  .stat-card--warning & {
    color: var(--warning-color);
  }
  
  .stat-card--danger & {
    color: var(--danger-color);
  }
}

.stat-content {
  flex: 1;
  
  .stat-value {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: var(--spacing-xs);
  }
  
  .stat-label {
    color: var(--text-color-light);
    font-size: 14px;
    margin-bottom: var(--spacing-xs);
  }
  
  .stat-trend {
    display: flex;
    align-items: center;
    gap: var(--spacing-xs);
    font-size: 12px;
    font-weight: 600;
    
    &.trend--up {
      color: var(--success-color);
    }
    
    &.trend--down {
      color: var(--danger-color);
    }
    
    &.trend--stable {
      color: var(--info-color);
    }
  }
}

.stat-chart {
  width: 60px;
  height: 40px;
  
  .mini-chart {
    display: flex;
    align-items: end;
    gap: 2px;
    height: 100%;
  }
  
  .chart-bar {
    flex: 1;
    background: var(--gradient-primary);
    border-radius: 1px;
    min-height: 2px;
    opacity: 0.6;
    transition: var(--transition-fast);
    
    &:hover {
      opacity: 1;
    }
  }
}

.dashboard-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: var(--spacing-xl);
  padding: 0 var(--spacing-xl) var(--spacing-xl);
}

.content-left {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.content-right {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
  
  .panel-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-color);
  }
}

.stream-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.stream-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md);
  background: var(--bg-color-lighter);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  transition: var(--transition-fast);
  
  &:hover {
    border-color: var(--border-color-light);
    background: var(--bg-color-light);
  }
  
  &.stream-item--active {
    border-left: 3px solid var(--danger-color);
  }
}

.stream-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex: 1;
}

.stream-thumbnail {
  position: relative;
  width: 80px;
  height: 45px;
  border-radius: var(--border-radius-small);
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.stream-status {
  position: absolute;
  top: 4px;
  right: 4px;
  padding: 2px 6px;
  border-radius: var(--border-radius-small);
  font-size: 10px;
  font-weight: 600;
  
  &.status--live {
    background: var(--danger-color);
    color: white;
  }
  
  &.status--offline {
    background: var(--info-color);
    color: white;
  }
}

.stream-details {
  flex: 1;
  
  .stream-title {
    font-weight: 600;
    margin-bottom: var(--spacing-xs);
  }
  
  .stream-meta {
    display: flex;
    gap: var(--spacing-md);
    font-size: 12px;
    color: var(--text-color-light);
  }
}

.stream-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.system-metrics {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.metric-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .metric-info {
    .metric-name {
      font-size: 14px;
      color: var(--text-color-light);
      margin-bottom: var(--spacing-xs);
    }
    
    .metric-value {
      font-weight: 600;
    }
  }
  
  .metric-progress {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    width: 120px;
  }
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: var(--bg-color-lighter);
  border-radius: 3px;
  overflow: hidden;
  
  .progress-fill {
    height: 100%;
    border-radius: 3px;
    transition: var(--transition-normal);
  }
  
  &.progress--normal .progress-fill {
    background: var(--gradient-success);
  }
  
  &.progress--warning .progress-fill {
    background: var(--gradient-warning);
  }
  
  &.progress--danger .progress-fill {
    background: var(--gradient-danger);
  }
}

.progress-label {
  font-size: 12px;
  color: var(--text-color-light);
  min-width: 30px;
}

.chart-container {
  height: 200px;
  
  .traffic-chart {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--bg-color-lighter);
    border-radius: var(--border-radius-base);
    
    .chart-placeholder {
      text-align: center;
      color: var(--text-color-light);
      
      i {
        font-size: 48px;
        margin-bottom: var(--spacing-sm);
      }
    }
  }
}

.chart-controls {
  display: flex;
  gap: var(--spacing-sm);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--bg-color-lighter);
  border-radius: var(--border-radius-base);
  transition: var(--transition-fast);
  
  &:hover {
    background: var(--bg-color-light);
  }
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  
  &.icon--upload {
    background: rgba(64, 158, 255, 0.2);
    color: var(--primary-color);
  }
  
  &.icon--transcode {
    background: rgba(103, 194, 58, 0.2);
    color: var(--success-color);
  }
  
  &.icon--live {
    background: rgba(245, 108, 108, 0.2);
    color: var(--danger-color);
  }
  
  &.icon--ai {
    background: rgba(230, 162, 60, 0.2);
    color: var(--warning-color);
  }
}

.activity-content {
  flex: 1;
  
  .activity-title {
    font-weight: 600;
    margin-bottom: var(--spacing-xs);
  }
  
  .activity-description {
    font-size: 12px;
    color: var(--text-color-light);
    margin-bottom: var(--spacing-xs);
  }
  
  .activity-time {
    font-size: 11px;
    color: var(--text-color-dark);
  }
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg);
  background: var(--bg-color-lighter);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  cursor: pointer;
  transition: var(--transition-fast);
  
  &:hover {
    border-color: var(--border-color-light);
    transform: translateY(-2px);
    box-shadow: var(--shadow-light);
  }
  
  &.quick-action--primary {
    border-color: var(--primary-color);
    
    &:hover {
      background: rgba(64, 158, 255, 0.1);
    }
  }
  
  &.quick-action--success {
    border-color: var(--success-color);
    
    &:hover {
      background: rgba(103, 194, 58, 0.1);
    }
  }
  
  &.quick-action--warning {
    border-color: var(--warning-color);
    
    &:hover {
      background: rgba(230, 162, 60, 0.1);
    }
  }
  
  &.quick-action--danger {
    border-color: var(--danger-color);
    
    &:hover {
      background: rgba(245, 108, 108, 0.1);
    }
  }
  
  .action-icon {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    
    .quick-action--primary & {
      color: var(--primary-color);
    }
    
    .quick-action--success & {
      color: var(--success-color);
    }
    
    .quick-action--warning & {
      color: var(--warning-color);
    }
    
    .quick-action--danger & {
      color: var(--danger-color);
    }
  }
  
  .action-text {
    font-weight: 600;
    font-size: 14px;
  }
}

.dashboard-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-xl);
  background: var(--bg-color-light);
  border-top: 1px solid var(--border-color);
  font-size: 12px;
  color: var(--text-color-light);
  
  .server-status {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    
    &.status--online {
      color: var(--success-color);
    }
    
    &.status--offline {
      color: var(--danger-color);
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }
  
  .content-right {
    grid-template-columns: 1fr 1fr;
    
    .chart-panel {
      grid-column: 1 / -1;
    }
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
    padding: var(--spacing-lg);
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    padding: 0 var(--spacing-lg) var(--spacing-lg);
  }
  
  .dashboard-content {
    padding: 0 var(--spacing-lg) var(--spacing-lg);
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
  
  .dashboard-footer {
    flex-direction: column;
    gap: var(--spacing-sm);
    text-align: center;
  }
}

// 动画效果
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card,
.monitor-panel,
.system-panel,
.chart-panel,
.activity-panel,
.quick-actions-panel {
  animation: fadeInUp 0.6s ease-out;
}

// 延迟动画
.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

.monitor-panel { animation-delay: 0.5s; }
.system-panel { animation-delay: 0.6s; }
.chart-panel { animation-delay: 0.7s; }
.activity-panel { animation-delay: 0.8s; }
.quick-actions-panel { animation-delay: 0.9s; }

// 加载状态
.loading {
  opacity: 0.6;
  pointer-events: none;
}

// 错误状态
.error {
  border-color: var(--danger-color) !important;
  
  &::before {
    background: var(--gradient-danger) !important;
  }
}

// 成功状态
.success {
  border-color: var(--success-color) !important;
  
  &::before {
    background: var(--gradient-success) !important;
  }
}