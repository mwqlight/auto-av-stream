<template>
  <div class="live-management-container">
    <!-- 页面标题和操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">直播管理</h1>
        <p class="page-subtitle">实时直播流管理和监控</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleCreateLive">
          <el-icon><VideoCamera /></el-icon>
          创建直播
        </el-button>
        <el-button @click="refreshLiveList">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 直播状态概览 -->
    <div class="live-overview">
      <el-row :gutter="24">
        <el-col :xs="12" :sm="6">
          <div class="overview-item">
            <div class="overview-icon total-live">
              <el-icon><VideoCamera /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ overview.totalLive }}</div>
              <div class="overview-label">总直播数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="overview-item">
            <div class="overview-icon active-live">
              <el-icon><Promotion /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ overview.activeLive }}</div>
              <div class="overview-label">活跃直播</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="overview-item">
            <div class="overview-icon total-viewers">
              <el-icon><User /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ overview.totalViewers }}</div>
              <div class="overview-label">总观众数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="overview-item">
            <div class="overview-icon bandwidth">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ formatBandwidth(overview.bandwidth) }}</div>
              <div class="overview-label">带宽使用</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索直播名称..."
        prefix-icon="Search"
        style="width: 300px"
        clearable
      />
      <el-select v-model="filterStatus" placeholder="直播状态" clearable>
        <el-option label="直播中" value="live" />
        <el-option label="已结束" value="ended" />
        <el-option label="已计划" value="scheduled" />
        <el-option label="异常" value="error" />
      </el-select>
      <el-select v-model="filterType" placeholder="直播类型" clearable>
        <el-option label="实时直播" value="realtime" />
        <el-option label="录播" value="recorded" />
        <el-option label="轮播" value="loop" />
      </el-select>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
      />
    </div>

    <!-- 直播列表 -->
    <div class="live-content">
      <el-table
        :data="filteredLiveList"
        v-loading="loading"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="直播信息" min-width="300">
          <template #default="{ row }">
            <div class="live-info">
              <div class="live-thumbnail">
                <img 
                  :src="row.thumbnail" 
                  :alt="row.title"
                  @error="handleImageError"
                />
                <div class="live-status-indicator" :class="row.status"></div>
              </div>
              <div class="live-details">
                <div class="live-title">{{ row.title }}</div>
                <div class="live-meta">
                  <span class="live-id">ID: {{ row.id }}</span>
                  <span class="live-type">{{ getTypeText(row.type) }}</span>
                  <span class="viewers" v-if="row.status === 'live'">
                    <el-icon><User /></el-icon>
                    {{ row.viewers }}
                  </span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag 
              :type="getStatusType(row.status)"
              size="small"
            >
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="推流地址" width="200">
          <template #default="{ row }">
            <div class="stream-url">
              <span>{{ row.streamUrl }}</span>
              <el-button 
                size="small" 
                type="text" 
                @click="copyStreamUrl(row)"
              >
                复制
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="100">
          <template #default="{ row }">
            {{ formatDuration(row.duration) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary" 
              @click="handleWatch(row)"
              :disabled="row.status !== 'live'"
            >
              观看
            </el-button>
            <el-button 
              size="small" 
              @click="handleEdit(row)"
              :disabled="row.status === 'live'"
            >
              编辑
            </el-button>
            <el-dropdown @command="(command) => handleCommand(command, row)">
              <el-button size="small">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item 
                    command="start" 
                    :disabled="row.status === 'live' || row.status === 'ended'"
                  >
                    开始直播
                  </el-dropdown-item>
                  <el-dropdown-item 
                    command="stop" 
                    :disabled="row.status !== 'live'"
                  >
                    停止直播
                  </el-dropdown-item>
                  <el-dropdown-item command="statistics">统计数据</el-dropdown-item>
                  <el-dropdown-item command="recordings" divided>录制文件</el-dropdown-item>
                  <el-dropdown-item command="delete">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 批量操作栏 -->
    <div class="batch-actions" v-show="selectedLives.length > 0">
      <span class="selected-count">已选择 {{ selectedLives.length }} 个直播</span>
      <el-button size="small" @click="batchDelete">批量删除</el-button>
      <el-button size="small" type="primary" @click="batchStart">批量开始</el-button>
      <el-button size="small" @click="clearSelection">取消选择</el-button>
    </div>

    <!-- 创建/编辑直播对话框 -->
    <el-dialog 
      v-model="liveDialogVisible" 
      :title="isEdit ? '编辑直播' : '创建直播'" 
      width="600px"
    >
      <el-form :model="liveForm" :rules="liveRules" ref="liveFormRef" label-width="100px">
        <el-form-item label="直播标题" prop="title">
          <el-input v-model="liveForm.title" placeholder="请输入直播标题" />
        </el-form-item>
        <el-form-item label="直播描述" prop="description">
          <el-input 
            v-model="liveForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入直播描述"
          />
        </el-form-item>
        <el-form-item label="直播类型" prop="type">
          <el-radio-group v-model="liveForm.type">
            <el-radio label="realtime">实时直播</el-radio>
            <el-radio label="recorded">录播</el-radio>
            <el-radio label="loop">轮播</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="liveForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="推流协议" prop="protocol">
          <el-select v-model="liveForm.protocol" placeholder="请选择推流协议">
            <el-option label="RTMP" value="rtmp" />
            <el-option label="HLS" value="hls" />
            <el-option label="WebRTC" value="webrtc" />
          </el-select>
        </el-form-item>
        <el-form-item label="分辨率" prop="resolution">
          <el-select v-model="liveForm.resolution" placeholder="请选择分辨率">
            <el-option label="720p" value="720p" />
            <el-option label="1080p" value="1080p" />
            <el-option label="4K" value="4k" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否录制" prop="record">
          <el-switch v-model="liveForm.record" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="liveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitLiveForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 观看直播对话框 -->
    <el-dialog v-model="watchDialogVisible" title="观看直播" width="800px" fullscreen>
      <div class="watch-container">
        <div class="video-player">
          <video 
            ref="videoPlayer" 
            :src="currentLive.streamUrl" 
            controls 
            autoplay
            style="width: 100%; height: 400px"
          />
        </div>
        <div class="live-info-panel">
          <h3>{{ currentLive.title }}</h3>
          <div class="live-stats">
            <div class="stat-item">
              <span class="label">观众数:</span>
              <span class="value">{{ currentLive.viewers }}</span>
            </div>
            <div class="stat-item">
              <span class="label">开始时间:</span>
              <span class="value">{{ formatDate(currentLive.startTime) }}</span>
            </div>
            <div class="stat-item">
              <span class="label">时长:</span>
              <span class="value">{{ formatDuration(currentLive.duration) }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="watchDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  VideoCamera,
  Refresh,
  Search,
  ArrowDown,
  Promotion,
  User,
  TrendCharts
} from '@element-plus/icons-vue'

interface LiveItem {
  id: string
  title: string
  description: string
  type: 'realtime' | 'recorded' | 'loop'
  status: 'live' | 'ended' | 'scheduled' | 'error'
  streamUrl: string
  thumbnail: string
  startTime: string
  duration: number
  viewers: number
  protocol: string
  resolution: string
  record: boolean
}

// 响应式数据
const loading = ref(false)
const searchQuery = ref('')
const filterStatus = ref('')
const filterType = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const selectedLives = ref<LiveItem[]>([])
const liveDialogVisible = ref(false)
const watchDialogVisible = ref(false)
const isEdit = ref(false)
const currentLive = ref<LiveItem>({} as LiveItem)

// 概览数据
const overview = reactive({
  totalLive: 0,
  activeLive: 0,
  totalViewers: 0,
  bandwidth: 0
})

// 表单数据
const liveForm = reactive({
  title: '',
  description: '',
  type: 'realtime',
  startTime: '',
  protocol: 'rtmp',
  resolution: '720p',
  record: true
})

const liveRules = {
  title: [
    { required: true, message: '请输入直播标题', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择直播类型', trigger: 'change' }
  ]
}

// 模拟直播数据
const liveList = ref<LiveItem[]>([
  {
    id: 'live-001',
    title: '产品发布会直播',
    description: '新产品发布会的实时直播',
    type: 'realtime',
    status: 'live',
    streamUrl: 'rtmp://live.example.com/live/001',
    thumbnail: '/thumbnails/live1.jpg',
    startTime: '2024-01-15 14:00:00',
    duration: 3600,
    viewers: 1250,
    protocol: 'rtmp',
    resolution: '1080p',
    record: true
  },
  {
    id: 'live-002',
    title: '技术分享会录播',
    description: '上周技术分享会的录播内容',
    type: 'recorded',
    status: 'ended',
    streamUrl: 'hls://vod.example.com/vod/002',
    thumbnail: '/thumbnails/live2.jpg',
    startTime: '2024-01-10 10:00:00',
    duration: 7200,
    viewers: 890,
    protocol: 'hls',
    resolution: '720p',
    record: true
  },
  {
    id: 'live-003',
    title: '公司宣传片轮播',
    description: '公司宣传片的24小时轮播',
    type: 'loop',
    status: 'live',
    streamUrl: 'webrtc://stream.example.com/loop/003',
    thumbnail: '/thumbnails/live3.jpg',
    startTime: '2024-01-01 00:00:00',
    duration: 86400,
    viewers: 45,
    protocol: 'webrtc',
    resolution: '720p',
    record: false
  },
  {
    id: 'live-004',
    title: '下周会议直播',
    description: '下周重要会议的直播计划',
    type: 'realtime',
    status: 'scheduled',
    streamUrl: 'rtmp://live.example.com/live/004',
    thumbnail: '/thumbnails/live4.jpg',
    startTime: '2024-01-20 09:00:00',
    duration: 0,
    viewers: 0,
    protocol: 'rtmp',
    resolution: '1080p',
    record: true
  }
])

// 计算属性：过滤后的直播列表
const filteredLiveList = computed(() => {
  let filtered = liveList.value
  
  if (searchQuery.value) {
    filtered = filtered.filter(item => 
      item.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  if (filterStatus.value) {
    filtered = filtered.filter(item => item.status === filterStatus.value)
  }
  
  if (filterType.value) {
    filtered = filtered.filter(item => item.type === filterType.value)
  }
  
  if (dateRange.value && dateRange.value.length === 2) {
    const [start, end] = dateRange.value
    filtered = filtered.filter(item => {
      const startTime = new Date(item.startTime)
      return startTime >= start && startTime <= end
    })
  }
  
  total.value = filtered.length
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

// 工具函数
const formatBandwidth = (bytes: number): string => {
  if (bytes === 0) return '0 B/s'
  const k = 1024
  const sizes = ['B/s', 'KB/s', 'MB/s', 'GB/s']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDuration = (seconds: number): string => {
  if (seconds === 0) return '--'
  const hours = Math.floor(seconds / 3600)
  const mins = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  return `${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const getStatusType = (status: string): string => {
  const statusMap: { [key: string]: string } = {
    'live': 'success',
    'ended': 'info',
    'scheduled': 'warning',
    'error': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string): string => {
  const statusMap: { [key: string]: string } = {
    'live': '直播中',
    'ended': '已结束',
    'scheduled': '已计划',
    'error': '异常'
  }
  return statusMap[status] || status
}

const getTypeText = (type: string): string => {
  const typeMap: { [key: string]: string } = {
    'realtime': '实时直播',
    'recorded': '录播',
    'loop': '轮播'
  }
  return typeMap[type] || type
}

// 事件处理函数
const handleSelectionChange = (selection: LiveItem[]) => {
  selectedLives.value = selection
}

const handleCreateLive = () => {
  isEdit.value = false
  Object.assign(liveForm, {
    title: '',
    description: '',
    type: 'realtime',
    startTime: '',
    protocol: 'rtmp',
    resolution: '720p',
    record: true
  })
  liveDialogVisible.value = true
}

const refreshLiveList = () => {
  loading.value = true
  // 模拟API调用
  setTimeout(() => {
    loading.value = false
    updateOverview()
    ElMessage.success('直播列表已刷新')
  }, 1000)
}

const handleWatch = (live: LiveItem) => {
  if (live.status !== 'live') {
    ElMessage.warning('该直播暂不可观看')
    return
  }
  currentLive.value = live
  watchDialogVisible.value = true
}

const handleEdit = (live: LiveItem) => {
  if (live.status === 'live') {
    ElMessage.warning('直播中无法编辑')
    return
  }
  
  isEdit.value = true
  Object.assign(liveForm, live)
  liveDialogVisible.value = true
}

const copyStreamUrl = (live: LiveItem) => {
  navigator.clipboard.writeText(live.streamUrl)
  ElMessage.success('推流地址已复制到剪贴板')
}

const handleCommand = (command: string, live: LiveItem) => {
  switch (command) {
    case 'start':
      startLive(live)
      break
    case 'stop':
      stopLive(live)
      break
    case 'statistics':
      showStatistics(live)
      break
    case 'recordings':
      showRecordings(live)
      break
    case 'delete':
      handleDelete(live)
      break
  }
}

const startLive = async (live: LiveItem) => {
  try {
    await ElMessageBox.confirm(
      `确定要开始直播"${live.title}"吗？`,
      '开始直播确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟开始直播
    live.status = 'live'
    live.startTime = new Date().toISOString()
    ElMessage.success('直播已开始')
  } catch {
    // 用户取消
  }
}

const stopLive = async (live: LiveItem) => {
  try {
    await ElMessageBox.confirm(
      `确定要停止直播"${live.title}"吗？`,
      '停止直播确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟停止直播
    live.status = 'ended'
    live.duration = Math.floor((new Date().getTime() - new Date(live.startTime).getTime()) / 1000)
    ElMessage.success('直播已停止')
  } catch {
    // 用户取消
  }
}

const showStatistics = (live: LiveItem) => {
  ElMessageBox.alert(
    `直播标题: ${live.title}\n观众峰值: ${live.viewers}\n开始时间: ${formatDate(live.startTime)}\n直播时长: ${formatDuration(live.duration)}`,
    '直播统计'
  )
}

const showRecordings = (live: LiveItem) => {
  if (!live.record) {
    ElMessage.warning('该直播未开启录制功能')
    return
  }
  ElMessage.info(`查看直播"${live.title}"的录制文件`)
}

const handleDelete = async (live: LiveItem) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除直播"${live.title}"吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const index = liveList.value.findIndex(item => item.id === live.id)
    if (index > -1) {
      liveList.value.splice(index, 1)
      ElMessage.success('直播删除成功')
    }
  } catch {
    // 用户取消
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
}

// 批量操作
const batchDelete = async () => {
  if (selectedLives.value.length === 0) return
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedLives.value.length} 个直播吗？此操作不可恢复。`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    selectedLives.value.forEach(live => {
      const index = liveList.value.findIndex(item => item.id === live.id)
      if (index > -1) {
        liveList.value.splice(index, 1)
      }
    })
    
    selectedLives.value = []
    ElMessage.success(`成功删除 ${selectedLives.value.length} 个直播`)
  } catch {
    // 用户取消
  }
}

const batchStart = () => {
  if (selectedLives.value.length === 0) return
  
  selectedLives.value.forEach(live => {
    if (live.status === 'scheduled') {
      live.status = 'live'
      live.startTime = new Date().toISOString()
    }
  })
  
  ElMessage.success(`已开始 ${selectedLives.value.length} 个直播`)
}

const clearSelection = () => {
  selectedLives.value = []
}

// 表单提交
const liveFormRef = ref()

const submitLiveForm = async () => {
  if (!liveFormRef.value) return
  
  try {
    await liveFormRef.value.validate()
    
    if (isEdit.value) {
      // 编辑逻辑
      const index = liveList.value.findIndex(item => item.id === currentLive.value.id)
      if (index > -1) {
        Object.assign(liveList.value[index], liveForm)
      }
      ElMessage.success('直播信息更新成功')
    } else {
      // 创建逻辑
      const newLive: LiveItem = {
        id: 'live-' + Date.now(),
        ...liveForm,
        status: 'scheduled',
        streamUrl: `${liveForm.protocol}://live.example.com/live/${Date.now()}`,
        thumbnail: '/thumbnails/default.jpg',
        duration: 0,
        viewers: 0
      }
      liveList.value.unshift(newLive)
      ElMessage.success('直播创建成功')
    }
    
    liveDialogVisible.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = '/thumbnails/default.jpg'
}

// 更新概览数据
const updateOverview = () => {
  overview.totalLive = liveList.value.length
  overview.activeLive = liveList.value.filter(item => item.status === 'live').length
  overview.totalViewers = liveList.value.reduce((sum, item) => sum + item.viewers, 0)
  overview.bandwidth = liveList.value.filter(item => item.status === 'live').length * 1024 * 1024 // 模拟带宽计算
}

onMounted(() => {
  refreshLiveList()
  
  // 定时更新数据
  setInterval(() => {
    updateOverview()
    
    // 模拟观众数变化
    liveList.value.forEach(live => {
      if (live.status === 'live') {
        live.viewers += Math.floor(Math.random() * 10) - 5
        live.viewers = Math.max(0, live.viewers)
        live.duration += 1
      }
    })
  }, 5000)
})
</script>

<style lang="scss" scoped>
.live-management-container {
  padding: 24px;
  min-height: calc(100vh - 112px);

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .header-left {
      .page-title {
        margin: 0 0 8px 0;
        font-size: 28px;
        font-weight: 700;
        background: linear-gradient(135deg, #00FFFF 0%, #FF00FF 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      .page-subtitle {
        margin: 0;
        color: rgba(255, 255, 255, 0.7);
        font-size: 14px;
      }
    }
  }

  .live-overview {
    margin-bottom: 24px;

    .overview-item {
      background: rgba(255, 255, 255, 0.05);
      border-radius: 12px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;

      .overview-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 60px;
        height: 60px;
        border-radius: 12px;
        font-size: 24px;
        color: white;

        &.total-live {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.active-live {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.total-viewers {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.bandwidth {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }

      .overview-info {
        .overview-value {
          font-size: 28px;
          font-weight: 700;
          color: white;
          line-height: 1;
          margin-bottom: 4px;
        }

        .overview-label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.7);
        }
      }
    }
  }

  .filter-bar {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
    flex-wrap: wrap;
  }

  .live-content {
    background: rgba(255, 255, 255, 0.05);
    border-radius: 12px;
    padding: 24px;

    .live-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .live-thumbnail {
        position: relative;
        width: 80px;
        height: 60px;
        border-radius: 8px;
        overflow: hidden;
        background: rgba(255, 255, 255, 0.1);

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .live-status-indicator {
          position: absolute;
          top: 4px;
          right: 4px;
          width: 8px;
          height: 8px;
          border-radius: 50%;

          &.live {
            background: #67C23A;
            animation: pulse 2s infinite;
          }

          &.ended {
            background: #909399;
          }

          &.scheduled {
            background: #E6A23C;
          }

          &.error {
            background: #F56C6C;
          }
        }
      }

      .live-details {
        .live-title {
          font-weight: 500;
          color: white;
          margin-bottom: 4px;
        }

        .live-meta {
          display: flex;
          gap: 12px;
          font-size: 12px;
          color: rgba(255, 255, 255, 0.7);

          span {
            padding: 2px 6px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 4px;
          }
        }
      }
    }

    .stream-url {
      display: flex;
      align-items: center;
      gap: 8px;

      span {
        font-family: 'Courier New', monospace;
        font-size: 12px;
        color: rgba(255, 255, 255, 0.7);
        word-break: break-all;
      }
    }

    .pagination-container {
      display: flex;
      justify-content: flex-end;
      margin-top: 24px;
    }
  }

  .batch-actions {
    position: fixed;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    background: rgba(0, 0, 0, 0.9);
    padding: 12px 24px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 16px;
    border: 1px solid rgba(255, 255, 255, 0.2);

    .selected-count {
      color: white;
      font-weight: 500;
    }
  }

  .watch-container {
    .video-player {
      margin-bottom: 20px;
    }

    .live-info-panel {
      background: rgba(255, 255, 255, 0.05);
      border-radius: 8px;
      padding: 16px;

      h3 {
        margin: 0 0 16px 0;
        color: white;
        font-size: 18px;
      }

      .live-stats {
        .stat-item {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;

          .label {
            color: rgba(255, 255, 255, 0.7);
          }

          .value {
            color: white;
            font-weight: 500;
          }
        }
      }
    }
  }
}

// 脉冲动画
@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .live-management-container {
    padding: 16px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;

      .header-right {
        width: 100%;
        display: flex;
        gap: 8px;

        .el-button {
          flex: 1;
        }
      }
    }

    .live-overview {
      .el-col {
        margin-bottom: 16px;
      }
    }

    .filter-bar {
      flex-direction: column;

      .el-input,
      .el-select,
      .el-date-picker {
        width: 100% !important;
      }
    }

    .live-content {
      padding: 16px;

      .live-info {
        .live-thumbnail {
          width: 60px;
          height: 45px;
        }
      }
    }
  }
}