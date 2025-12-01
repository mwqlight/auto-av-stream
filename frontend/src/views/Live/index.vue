<template>
  <div class="live-page">
    <!-- 页面标题和操作栏 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="icon-live"></i>
          直播管理
        </h1>
        <p class="page-subtitle">管理直播流、录制内容和实时监控</p>
      </div>
      <div class="header-actions">
        <button class="btn btn--primary" @click="startNewLive">
          <i class="icon-play"></i>
          开始直播
        </button>
        <button class="btn btn--secondary" @click="refreshLiveList">
          <i class="icon-refresh"></i>
          刷新
        </button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card" v-for="stat in stats" :key="stat.title">
        <div class="stat-icon" :class="stat.iconClass">
          <i :class="stat.icon"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-title">{{ stat.title }}</div>
          <div class="stat-trend" :class="stat.trendClass">
            <i :class="stat.trendIcon"></i>
            <span>{{ stat.trendValue }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 直播状态监控 -->
    <div class="live-monitor">
      <div class="monitor-header">
        <h2>实时监控</h2>
        <div class="monitor-actions">
          <button class="btn btn--text" @click="toggleAutoRefresh">
            <i :class="autoRefresh ? 'icon-pause' : 'icon-play'"></i>
            {{ autoRefresh ? '暂停刷新' : '自动刷新' }}
          </button>
        </div>
      </div>
      
      <div class="monitor-grid">
        <div
          v-for="stream in activeStreams"
          :key="stream.id"
          class="stream-card"
          :class="`stream-status--${stream.status}`"
        >
          <div class="stream-preview">
            <div class="preview-placeholder" v-if="!stream.thumbnail">
              <i class="icon-video"></i>
            </div>
            <img v-else :src="stream.thumbnail" :alt="stream.name" />
            <div class="stream-status-indicator"></div>
            <div class="stream-viewers">
              <i class="icon-eye"></i>
              {{ stream.viewers || 0 }}
            </div>
          </div>
          
          <div class="stream-info">
            <div class="stream-name">{{ stream.name }}</div>
            <div class="stream-meta">
              <span class="meta-item">
                <i class="icon-clock"></i>
                {{ formatDuration(stream.duration) }}
              </span>
              <span class="meta-item">
                <i class="icon-resolution"></i>
                {{ stream.resolution || '--' }}
              </span>
            </div>
            <div class="stream-bitrate">
              码率: {{ stream.bitrate || '--' }}
            </div>
          </div>
          
          <div class="stream-actions">
            <button
              v-if="stream.status === 'live'"
              class="action-btn"
              @click="stopStream(stream)"
              title="停止直播"
            >
              <i class="icon-stop"></i>
            </button>
            <button
              v-if="stream.status === 'live'"
              class="action-btn"
              @click="showStreamDetails(stream)"
              title="详细信息"
            >
              <i class="icon-info"></i>
            </button>
            <button
              class="action-btn"
              @click="copyStreamUrl(stream)"
              title="复制推流地址"
            >
              <i class="icon-copy"></i>
            </button>
            <button
              class="action-btn action-btn--danger"
              @click="deleteStream(stream)"
              title="删除"
            >
              <i class="icon-trash"></i>
            </button>
          </div>
        </div>
        
        <div v-if="activeStreams.length === 0" class="no-active-streams">
          <i class="icon-video-off"></i>
          <h3>暂无活跃直播</h3>
          <p>点击"开始直播"创建新的直播流</p>
        </div>
      </div>
    </div>

    <!-- 录制内容 -->
    <div class="recordings-section">
      <div class="section-header">
        <h2>录制内容</h2>
        <div class="section-actions">
          <div class="filter-group">
            <select v-model="recordingFilters.status" class="filter-select">
              <option value="">全部状态</option>
              <option value="recording">录制中</option>
              <option value="completed">已完成</option>
              <option value="error">错误</option>
            </select>
            <select v-model="recordingFilters.sort" class="filter-select">
              <option value="createdAt_desc">最新录制</option>
              <option value="createdAt_asc">最早录制</option>
              <option value="duration_desc">时长最长</option>
              <option value="size_desc">文件最大</option>
            </select>
          </div>
        </div>
      </div>
      
      <div class="recordings-list">
        <div v-if="loadingRecordings" class="loading-container">
          <div class="loading-spinner">
            <div class="spinner"></div>
            <span>加载中...</span>
          </div>
        </div>
        
        <div v-else-if="filteredRecordings.length === 0" class="empty-state">
          <i class="icon-folder"></i>
          <h3>暂无录制内容</h3>
          <p>直播结束后会自动生成录制文件</p>
        </div>
        
        <div v-else class="recordings-grid">
          <div
            v-for="recording in filteredRecordings"
            :key="recording.id"
            class="recording-card"
          >
            <div class="recording-preview">
              <div class="preview-placeholder" v-if="!recording.thumbnail">
                <i class="icon-video"></i>
              </div>
              <img v-else :src="recording.thumbnail" :alt="recording.name" />
              <div class="recording-duration">
                {{ formatDuration(recording.duration) }}
              </div>
              <div class="recording-status" :class="`status-${recording.status}`">
                {{ getRecordingStatusText(recording.status) }}
              </div>
            </div>
            
            <div class="recording-info">
              <div class="recording-name">{{ recording.name }}</div>
              <div class="recording-meta">
                <div class="meta-item">
                  <i class="icon-calendar"></i>
                  {{ formatTime(recording.createdAt) }}
                </div>
                <div class="meta-item">
                  <i class="icon-file"></i>
                  {{ formatFileSize(recording.size) }}
                </div>
                <div class="meta-item">
                  <i class="icon-resolution"></i>
                  {{ recording.resolution || '--' }}
                </div>
              </div>
            </div>
            
            <div class="recording-actions">
              <button
                v-if="recording.status === 'completed'"
                class="action-btn"
                @click="playRecording(recording)"
                title="播放"
              >
                <i class="icon-play"></i>
              </button>
              <button
                v-if="recording.status === 'completed'"
                class="action-btn"
                @click="downloadRecording(recording)"
                title="下载"
              >
                <i class="icon-download"></i>
              </button>
              <button
                class="action-btn"
                @click="showRecordingInfo(recording)"
                title="详细信息"
              >
                <i class="icon-info"></i>
              </button>
              <button
                class="action-btn action-btn--danger"
                @click="deleteRecording(recording)"
                title="删除"
              >
                <i class="icon-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 开始直播模态框 -->
    <transition name="modal">
      <div v-if="showStartLiveModal" class="modal-overlay" @click="showStartLiveModal = false">
        <div class="modal-content modal-content--large" @click.stop>
          <div class="modal-header">
            <h3>开始新直播</h3>
            <button class="modal-close" @click="showStartLiveModal = false">
              <i class="icon-close"></i>
            </button>
          </div>
          
          <div class="modal-body">
            <form @submit.prevent="createLiveStream" class="live-form">
              <div class="form-group">
                <label for="streamName">直播名称 *</label>
                <input
                  id="streamName"
                  v-model="newStream.name"
                  type="text"
                  required
                  placeholder="请输入直播名称"
                  class="form-input"
                />
              </div>
              
              <div class="form-group">
                <label for="streamDescription">直播描述</label>
                <textarea
                  id="streamDescription"
                  v-model="newStream.description"
                  placeholder="请输入直播描述"
                  rows="3"
                  class="form-textarea"
                ></textarea>
              </div>
              
              <div class="form-row">
                <div class="form-group">
                  <label for="streamResolution">分辨率</label>
                  <select
                    id="streamResolution"
                    v-model="newStream.resolution"
                    class="form-select"
                  >
                    <option value="720p">720p</option>
                    <option value="1080p">1080p</option>
                    <option value="2k">2K</option>
                    <option value="4k">4K</option>
                  </select>
                </div>
                
                <div class="form-group">
                  <label for="streamBitrate">码率 (Mbps)</label>
                  <select
                    id="streamBitrate"
                    v-model="newStream.bitrate"
                    class="form-select"
                  >
                    <option value="1">1 Mbps</option>
                    <option value="2">2 Mbps</option>
                    <option value="3">3 Mbps</option>
                    <option value="5">5 Mbps</option>
                    <option value="8">8 Mbps</option>
                  </select>
                </div>
              </div>
              
              <div class="form-group">
                <label class="checkbox-label">
                  <input
                    type="checkbox"
                    v-model="newStream.record"
                    class="checkbox-input"
                  />
                  <span class="checkbox-custom"></span>
                  自动录制直播内容
                </label>
              </div>
              
              <div class="form-group" v-if="newStream.record">
                <label for="recordFormat">录制格式</label>
                <select
                  id="recordFormat"
                  v-model="newStream.recordFormat"
                  class="form-select"
                >
                  <option value="mp4">MP4</option>
                  <option value="flv">FLV</option>
                  <option value="ts">TS</option>
                </select>
              </div>
            </form>
            
            <div class="stream-info-card" v-if="streamConfig">
              <h4>推流信息</h4>
              <div class="info-item">
                <label>推流地址:</label>
                <div class="info-value">
                  <code>{{ streamConfig.pushUrl }}</code>
                  <button class="copy-btn" @click="copyToClipboard(streamConfig.pushUrl)">
                    <i class="icon-copy"></i>
                  </button>
                </div>
              </div>
              <div class="info-item">
                <label>流密钥:</label>
                <div class="info-value">
                  <code>{{ streamConfig.streamKey }}</code>
                  <button class="copy-btn" @click="copyToClipboard(streamConfig.streamKey)">
                    <i class="icon-copy"></i>
                  </button>
                </div>
              </div>
              <div class="info-item">
                <label>播放地址:</label>
                <div class="info-value">
                  <code>{{ streamConfig.playUrl }}</code>
                  <button class="copy-btn" @click="copyToClipboard(streamConfig.playUrl)">
                    <i class="icon-copy"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <div class="modal-footer">
            <button class="btn btn--secondary" @click="showStartLiveModal = false">
              取消
            </button>
            <button class="btn btn--primary" @click="createLiveStream">
              创建直播
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 直播详情模态框 -->
    <transition name="modal">
      <div v-if="selectedStream" class="modal-overlay" @click="selectedStream = null">
        <div class="modal-content modal-content--large" @click.stop>
          <div class="modal-header">
            <h3>{{ selectedStream.name }}</h3>
            <button class="modal-close" @click="selectedStream = null">
              <i class="icon-close"></i>
            </button>
          </div>
          
          <div class="modal-body">
            <div class="stream-details">
              <div class="detail-section">
                <h4>基本信息</h4>
                <div class="detail-grid">
                  <div class="detail-item">
                    <label>状态:</label>
                    <span class="status-badge" :class="`status-${selectedStream.status}`">
                      {{ getStreamStatusText(selectedStream.status) }}
                    </span>
                  </div>
                  <div class="detail-item">
                    <label>开始时间:</label>
                    <span>{{ formatTime(selectedStream.startTime) }}</span>
                  </div>
                  <div class="detail-item">
                    <label>持续时间:</label>
                    <span>{{ formatDuration(selectedStream.duration) }}</span>
                  </div>
                  <div class="detail-item">
                    <label>观看人数:</label>
                    <span>{{ selectedStream.viewers || 0 }}</span>
                  </div>
                </div>
              </div>
              
              <div class="detail-section">
                <h4>技术信息</h4>
                <div class="detail-grid">
                  <div class="detail-item">
                    <label>分辨率:</label>
                    <span>{{ selectedStream.resolution || '--' }}</span>
                  </div>
                  <div class="detail-item">
                    <label>码率:</label>
                    <span>{{ selectedStream.bitrate || '--' }}</span>
                  </div>
                  <div class="detail-item">
                    <label>帧率:</label>
                    <span>{{ selectedStream.fps || '--' }}</span>
                  </div>
                  <div class="detail-item">
                    <label>延迟:</label>
                    <span>{{ selectedStream.latency || '--' }}</span>
                  </div>
                </div>
              </div>
              
              <div class="detail-section" v-if="selectedStream.qualityMetrics">
                <h4>质量指标</h4>
                <div class="quality-metrics">
                  <div class="metric-item">
                    <label>视频质量:</label>
                    <div class="metric-bar">
                      <div
                        class="metric-fill"
                        :style="{ width: selectedStream.qualityMetrics.videoQuality + '%' }"
                      ></div>
                    </div>
                    <span>{{ selectedStream.qualityMetrics.videoQuality }}%</span>
                  </div>
                  <div class="metric-item">
                    <label>音频质量:</label>
                    <div class="metric-bar">
                      <div
                        class="metric-fill"
                        :style="{ width: selectedStream.qualityMetrics.audioQuality + '%' }"
                      ></div>
                    </div>
                    <span>{{ selectedStream.qualityMetrics.audioQuality }}%</span>
                  </div>
                  <div class="metric-item">
                    <label>网络稳定性:</label>
                    <div class="metric-bar">
                      <div
                        class="metric-fill"
                        :style="{ width: selectedStream.qualityMetrics.networkStability + '%' }"
                      ></div>
                    </div>
                    <span>{{ selectedStream.qualityMetrics.networkStability }}%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="modal-footer">
            <button
              v-if="selectedStream.status === 'live'"
              class="btn btn--danger"
              @click="stopStream(selectedStream)"
            >
              停止直播
            </button>
            <button class="btn btn--secondary" @click="selectedStream = null">
              关闭
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useLiveStore } from '@/store/live'
import { formatFileSize, formatTime, formatDuration } from '@/utils'

const liveStore = useLiveStore()

// 响应式数据
const loadingRecordings = ref(false)
const showStartLiveModal = ref(false)
const selectedStream = ref(null)
const autoRefresh = ref(true)

// 筛选条件
const recordingFilters = ref({
  status: '',
  sort: 'createdAt_desc'
})

// 新直播配置
const newStream = ref({
  name: '',
  description: '',
  resolution: '1080p',
  bitrate: '3',
  record: true,
  recordFormat: 'mp4'
})

const streamConfig = ref({
  pushUrl: 'rtmp://your-server.com/live',
  streamKey: 'stream-' + Math.random().toString(36).substr(2, 8),
  playUrl: 'https://your-server.com/live/stream.m3u8'
})

// 统计信息
const stats = ref([
  {
    title: '活跃直播',
    value: '3',
    icon: 'icon-live',
    iconClass: 'stat-icon--primary',
    trendIcon: 'icon-trend-up',
    trendValue: '+1',
    trendClass: 'trend-up'
  },
  {
    title: '总观看人数',
    value: '1,234',
    icon: 'icon-eye',
    iconClass: 'stat-icon--success',
    trendIcon: 'icon-trend-up',
    trendValue: '+256',
    trendClass: 'trend-up'
  },
  {
    title: '录制内容',
    value: '45',
    icon: 'icon-record',
    iconClass: 'stat-icon--warning',
    trendIcon: 'icon-trend-up',
    trendValue: '+5',
    trendClass: 'trend-up'
  },
  {
    title: '平均延迟',
    value: '2.3s',
    icon: 'icon-clock',
    iconClass: 'stat-icon--info',
    trendIcon: 'icon-trend-down',
    trendValue: '-0.5s',
    trendClass: 'trend-down'
  }
])

// 计算属性
const activeStreams = computed(() => {
  return liveStore.liveStreams?.filter(stream => stream.status === 'live') || []
})

const filteredRecordings = computed(() => {
  let recordings = liveStore.recordings || []
  
  // 状态筛选
  if (recordingFilters.value.status) {
    recordings = recordings.filter(rec => rec.status === recordingFilters.value.status)
  }
  
  // 排序
  recordings.sort((a, b) => {
    const [field, order] = recordingFilters.value.sort.split('_')
    const multiplier = order === 'desc' ? -1 : 1
    
    if (field === 'createdAt') {
      return (new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()) * multiplier
    } else if (field === 'duration') {
      return (a.duration - b.duration) * multiplier
    } else if (field === 'size') {
      return (a.size - b.size) * multiplier
    }
    
    return 0
  })
  
  return recordings
})

// 方法
const refreshLiveList = async () => {
  try {
    await liveStore.fetchLiveStreams()
    loadingRecordings.value = true
    await liveStore.fetchRecordings()
  } catch (error) {
    console.error('刷新直播列表失败:', error)
  } finally {
    loadingRecordings.value = false
  }
}

const toggleAutoRefresh = () => {
  autoRefresh.value = !autoRefresh.value
}

const startNewLive = () => {
  showStartLiveModal.value = true
  // 生成新的流密钥
  streamConfig.value.streamKey = 'stream-' + Math.random().toString(36).substr(2, 8)
}

const createLiveStream = async () => {
  try {
    await liveStore.createLiveStream({
      ...newStream.value,
      streamKey: streamConfig.value.streamKey
    })
    showStartLiveModal.value = false
    await refreshLiveList()
  } catch (error) {
    console.error('创建直播失败:', error)
  }
}

const stopStream = async (stream: any) => {
  if (confirm(`确定要停止直播 "${stream.name}" 吗？`)) {
    try {
      await liveStore.stopLiveStream(stream.id)
      await refreshLiveList()
      if (selectedStream.value?.id === stream.id) {
        selectedStream.value = null
      }
    } catch (error) {
      console.error('停止直播失败:', error)
    }
  }
}

const showStreamDetails = (stream: any) => {
  selectedStream.value = stream
}

const copyStreamUrl = (stream: any) => {
  const url = `${streamConfig.value.pushUrl}/${stream.streamKey}`
  copyToClipboard(url)
}

const deleteStream = async (stream: any) => {
  if (confirm(`确定要删除直播流 "${stream.name}" 吗？`)) {
    try {
      await liveStore.deleteLiveStream(stream.id)
      await refreshLiveList()
    } catch (error) {
      console.error('删除直播流失败:', error)
    }
  }
}

const playRecording = (recording: any) => {
  // 播放录制内容
  console.log('播放录制:', recording.name)
}

const downloadRecording = async (recording: any) => {
  try {
    // 调用下载API
    console.log('下载录制:', recording.name)
  } catch (error) {
    console.error('下载失败:', error)
  }
}

const showRecordingInfo = (recording: any) => {
  // 显示录制详细信息
  console.log('录制信息:', recording)
}

const deleteRecording = async (recording: any) => {
  if (confirm(`确定要删除录制内容 "${recording.name}" 吗？`)) {
    try {
      await liveStore.deleteRecording(recording.id)
      await refreshLiveList()
    } catch (error) {
      console.error('删除录制失败:', error)
    }
  }
}

const copyToClipboard = (text: string) => {
  navigator.clipboard.writeText(text).then(() => {
    // 可以添加成功提示
    console.log('复制成功:', text)
  }).catch(err => {
    console.error('复制失败:', err)
  })
}

const getStreamStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    live: '直播中',
    stopped: '已停止',
    error: '错误'
  }
  return statusMap[status] || status
}

const getRecordingStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    recording: '录制中',
    completed: '已完成',
    error: '错误'
  }
  return statusMap[status] || status
}

// 自动刷新定时器
let refreshTimer: NodeJS.Timeout

onMounted(() => {
  refreshLiveList()
  
  // 设置自动刷新
  refreshTimer = setInterval(() => {
    if (autoRefresh.value) {
      refreshLiveList()
    }
  }, 5000) // 每5秒刷新一次
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped lang="scss">
.live-page {
  padding: var(--spacing-xl);
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-xl);
}

.header-content {
  .page-title {
    font-size: 32px;
    font-weight: 700;
    color: var(--text-color);
    margin-bottom: var(--spacing-sm);
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
  }
  
  .page-subtitle {
    color: var(--text-color-light);
    font-size: 16px;
  }
}

.header-actions {
  display: flex;
  gap: var(--spacing-md);
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  background: var(--bg-color-light);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  transition: var(--transition-fast);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-medium);
  }
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  
  &.stat-icon--primary {
    background: rgba(64, 158, 255, 0.1);
    color: var(--primary-color);
  }
  
  &.stat-icon--success {
    background: rgba(103, 194, 58, 0.1);
    color: var(--success-color);
  }
  
  &.stat-icon--warning {
    background: rgba(230, 162, 60, 0.1);
    color: var(--warning-color);
  }
  
  &.stat-icon--info {
    background: rgba(144, 147, 153, 0.1);
    color: var(--info-color);
  }
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: var(--spacing-xs);
}

.stat-title {
  color: var(--text-color-light);
  font-size: 14px;
  margin-bottom: var(--spacing-xs);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 12px;
  
  &.trend-up {
    color: var(--success-color);
  }
  
  &.trend-down {
    color: var(--danger-color);
  }
}

.live-monitor {
  background: var(--bg-color-light);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  margin-bottom: var(--spacing-xl);
  overflow: hidden;
}

.monitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
  
  h2 {
    margin: 0;
    color: var(--text-color);
  }
}

.monitor-grid {
  padding: var(--spacing-lg);
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

.stream-card {
  background: var(--bg-color-lighter);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  overflow: hidden;
  transition: var(--transition-fast);
  
  &.stream-status--live {
    border-left: 4px solid var(--success-color);
  }
  
  &.stream-status--stopped {
    border-left: 4px solid var(--info-color);
  }
  
  &.stream-status--error {
    border-left: 4px solid var(--danger-color);
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-medium);
  }
}

.stream-preview {
  position: relative;
  height: 160px;
  background: var(--bg-color);
  overflow: hidden;
  
  .preview-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-color-light);
    font-size: 48px;
  }
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.stream-status-indicator {
  position: absolute;
  top: var(--spacing-sm);
  right: var(--spacing-sm);
  width: 12px;
  height: 12px;
  border-radius: 50%;
  
  .stream-status--live & {
    background: var(--success-color);
    box-shadow: 0 0 10px var(--success-color);
  }
  
  .stream-status--stopped & {
    background: var(--info-color);
  }
  
  .stream-status--error & {
    background: var(--danger-color);
  }
}

.stream-viewers {
  position: absolute;
  bottom: var(--spacing-sm);
  left: var(--spacing-sm);
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.stream-info {
  padding: var(--spacing-md);
  
  .stream-name {
    font-weight: 600;
    color: var(--text-color);
    margin-bottom: var(--spacing-xs);
    word-break: break-all;
  }
  
  .stream-meta {
    display: flex;
    gap: var(--spacing-md);
    font-size: 12px;
    color: var(--text-color-light);
    margin-bottom: var(--spacing-xs);
    
    .meta-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-xs);
    }
  }
  
  .stream-bitrate {
    font-size: 12px;
    color: var(--text-color-light);
  }
}

.stream-actions {
  padding: var(--spacing-md);
  border-top: 1px solid var(--border-color);
  display: flex;
  gap: var(--spacing-xs);
  
  .action-btn {
    width: 32px;
    height: 32px;
    border: none;
    background: transparent;
    border-radius: var(--border-radius-base);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-color-light);
    cursor: pointer;
    transition: var(--transition-fast);
    
    &:hover {
      background: var(--bg-color-light);
      color: var(--text-color);
    }
    
    &.action-btn--danger:hover {
      background: rgba(245, 108, 108, 0.1);
      color: var(--danger-color);
    }
  }
}

.no-active-streams {
  grid-column: 1 / -1;
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-color-light);
  
  .icon-video-off {
    font-size: 64px;
    margin-bottom: var(--spacing-md);
    opacity: 0.5;
  }
  
  h3 {
    margin-bottom: var(--spacing-sm);
    color: var(--text-color);
  }
}

.recordings-section {
  background: var(--bg-color-light);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
  
  h2 {
    margin: 0;
    color: var(--text-color);
  }
}

.section-actions {
  display: flex;
  gap: var(--spacing-md);
}

.filter-group {
  display: flex;
  gap: var(--spacing-md);
}

.filter-select {
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-color-lighter);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  color: var(--text-color);
  font-size: 14px;
  outline: none;
  
  &:focus {
    border-color: var(--primary-color);
  }
}

.recordings-list {
  padding: var(--spacing-lg);
}

.loading-container {
  padding: var(--spacing-xl);
  text-align: center;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  color: var(--text-color-light);
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(64, 158, 255, 0.3);
  border-top: 2px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.empty-state {
  padding: var(--spacing-xl);
  text-align: center;
  color: var(--text-color-light);
  
  .icon-folder {
    font-size: 64px;
    margin-bottom: var(--spacing-md);
    opacity: 0.5;
  }
  
  h3 {
    margin-bottom: var(--spacing-sm);
    color: var(--text-color);
  }
  
  p {
    margin-bottom: var(--spacing-lg);
  }
}

.recordings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-lg);
}

.recording-card {
  background: var(--bg-color-lighter);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  overflow: hidden;
  transition: var(--transition-fast);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-medium);
  }
}

.recording-preview {
  position: relative;
  height: 160px;
  background: var(--bg-color);
  overflow: hidden;
  
  .preview-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-color-light);
    font-size: 48px;
  }
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.recording-duration {
  position: absolute;
  bottom: var(--spacing-sm);
  right: var(--spacing-sm);
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.recording-status {
  position: absolute;
  top: var(--spacing-sm);
  right: var(--spacing-sm);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  
  &.status-recording {
    background: rgba(230, 162, 60, 0.1);
    color: var(--warning-color);
  }
  
  &.status-completed {
    background: rgba(103, 194, 58, 0.1);
    color: var(--success-color);
  }
  
  &.status-error {
    background: rgba(245, 108, 108, 0.1);
    color: var(--danger-color);
  }
}

.recording-info {
  padding: var(--spacing-md);
  
  .recording-name {
    font-weight: 600;
    color: var(--text-color);
    margin-bottom: var(--spacing-xs);
    word-break: break-all;
  }
  
  .recording-meta {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-xs);
    font-size: 12px;
    color: var(--text-color-light);
    
    .meta-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-xs);
    }
  }
}

.recording-actions {
  padding: var(--spacing-md);
  border-top: 1px solid var(--border-color);
  display: flex;
  gap: var(--spacing-xs);
  
  .action-btn {
    width: 32px;
    height: 32px;
    border: none;
    background: transparent;
    border-radius: var(--border-radius-base);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-color-light);
    cursor: pointer;
    transition: var(--transition-fast);
    
    &:hover {
      background: var(--bg-color-light);
      color: var(--text-color);
    }
    
    &.action-btn--danger:hover {
      background: rgba(245, 108, 108, 0.1);
      color: var(--danger-color);
    }
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  background: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-large);
  max-width: 90vw;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  
  &.modal-content--large {
    width: 600px;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
  
  h3 {
    margin: 0;
    color: var(--text-color);
  }
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-light);
  cursor: pointer;
  transition: var(--transition-fast);
  
  &:hover {
    background: var(--bg-color-lighter);
    color: var(--text-color);
  }
}

.modal-body {
  padding: var(--spacing-lg);
  flex: 1;
  overflow-y: auto;
}

.modal-footer {
  padding: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
}

.live-form {
  .form-group {
    margin-bottom: var(--spacing-lg);
  }
  
  label {
    display: block;
    margin-bottom: var(--spacing-sm);
    color: var(--text-color);
    font-weight: 500;
  }
  
  .form-input,
  .form-textarea,
  .form-select {
    width: 100%;
    padding: var(--spacing-md);
    background: var(--bg-color-lighter);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-base);
    color: var(--text-color);
    font-size: 14px;
    outline: none;
    
    &:focus {
      border-color: var(--primary-color);
    }
  }
  
  .form-textarea {
    resize: vertical;
    min-height: 80px;
  }
  
  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--spacing-lg);
  }
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  
  .checkbox-input {
    display: none;
  }
  
  .checkbox-custom {
    width: 18px;
    height: 18px;
    border: 2px solid var(--border-color);
    border-radius: 3px;
    position: relative;
    transition: var(--transition-fast);
    
    .checkbox-input:checked + & {
      background: var(--primary-color);
      border-color: var(--primary-color);
    }
    
    .checkbox-input:checked + &::after {
      content: '✓';
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      color: white;
      font-size: 12px;
      font-weight: bold;
    }
  }
}

.stream-info-card {
  background: var(--bg-color-light);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  padding: var(--spacing-lg);
  margin-top: var(--spacing-lg);
  
  h4 {
    margin-bottom: var(--spacing-md);
    color: var(--text-color);
  }
  
  .info-item {
    display: flex;
    align-items: center;
    margin-bottom: var(--spacing-md);
    
    &:last-child {
      margin-bottom: 0;
    }
    
    label {
      width: 80px;
      color: var(--text-color-light);
      font-size: 14px;
    }
    
    .info-value {
      flex: 1;
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      
      code {
        background: var(--bg-color-lighter);
        padding: var(--spacing-sm) var(--spacing-md);
        border-radius: var(--border-radius-base);
        font-family: monospace;
        font-size: 12px;
        word-break: break-all;
        flex: 1;
      }
    }
  }
}

.copy-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: var(--border-radius-base);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-light);
  cursor: pointer;
  transition: var(--transition-fast);
  
  &:hover {
    background: var(--bg-color-light);
    color: var(--text-color);
  }
}

.stream-details {
  .detail-section {
    margin-bottom: var(--spacing-lg);
    
    h4 {
      margin-bottom: var(--spacing-md);
      color: var(--text-color);
    }
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .detail-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: var(--spacing-md);
  }
  
  .detail-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    
    label {
      color: var(--text-color-light);
      font-size: 14px;
      min-width: 80px;
    }
    
    span {
      color: var(--text-color);
      font-weight: 500;
    }
  }
  
  .status-badge {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
    
    &.status-live {
      background: rgba(103, 194, 58, 0.1);
      color: var(--success-color);
    }
    
    &.status-stopped {
      background: rgba(144, 147, 153, 0.1);
      color: var(--info-color);
    }
    
    &.status-error {
      background: rgba(245, 108, 108, 0.1);
      color: var(--danger-color);
    }
  }
}

.quality-metrics {
  .metric-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);
    
    &:last-child {
      margin-bottom: 0;
    }
    
    label {
      color: var(--text-color-light);
      font-size: 14px;
      min-width: 100px;
    }
    
    .metric-bar {
      flex: 1;
      height: 8px;
      background: var(--bg-color-lighter);
      border-radius: 4px;
      overflow: hidden;
    }
    
    .metric-fill {
      height: 100%;
      background: var(--primary-color);
      border-radius: 4px;
      transition: width 0.3s ease;
    }
    
    span {
      color: var(--text-color);
      font-weight: 500;
      min-width: 40px;
      text-align: right;
    }
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

// 响应式设计
@media (max-width: 1200px) {
  .live-page {
    padding: var(--spacing-lg);
  }
  
  .page-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .monitor-grid {
    grid-template-columns: 1fr;
  }
  
  .recordings-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .live-page {
    padding: var(--spacing-md);
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }
  
  .filter-group {
    flex-direction: column;
    width: 100%;
  }
  
  .recordings-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-content--large {
    width: 95vw;
    margin: var(--spacing-md);
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
