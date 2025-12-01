<template>
  <div class="media-management-container">
    <!-- 页面标题和操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">媒体管理</h1>
        <p class="page-subtitle">音视频文件上传、转码和管理</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleUpload">
          <el-icon><Upload /></el-icon>
          上传媒体
        </el-button>
        <el-button @click="refreshMediaList">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索媒体文件..."
        prefix-icon="Search"
        style="width: 300px"
        clearable
      />
      <el-select v-model="filterType" placeholder="文件类型" clearable>
        <el-option label="视频" value="video" />
        <el-option label="音频" value="audio" />
        <el-option label="图片" value="image" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="状态" clearable>
        <el-option label="就绪" value="ready" />
        <el-option label="转码中" value="transcoding" />
        <el-option label="失败" value="failed" />
      </el-select>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
      />
    </div>

    <!-- 媒体文件列表 -->
    <div class="media-content">
      <el-table
        :data="filteredMediaList"
        v-loading="loading"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="媒体文件" min-width="300">
          <template #default="{ row }">
            <div class="media-info">
              <div class="media-thumbnail">
                <img 
                  v-if="row.type === 'video'" 
                  src="@/assets/video-icon.png" 
                  :alt="row.name"
                />
                <img 
                  v-else-if="row.type === 'audio'" 
                  src="@/assets/audio-icon.png" 
                  :alt="row.name"
                />
                <img 
                  v-else 
                  src="@/assets/image-icon.png" 
                  :alt="row.name"
                />
              </div>
              <div class="media-details">
                <div class="media-name">{{ row.name }}</div>
                <div class="media-meta">
                  <span class="file-size">{{ formatFileSize(row.size) }}</span>
                  <span class="file-type">{{ row.type.toUpperCase() }}</span>
                  <span class="duration" v-if="row.duration">{{ formatDuration(row.duration) }}</span>
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
        <el-table-column prop="format" label="格式" width="100" />
        <el-table-column prop="resolution" label="分辨率" width="120" />
        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              @click="handlePreview(row)"
              :disabled="row.status !== 'ready'"
            >
              预览
            </el-button>
            <el-button 
              size="small" 
              type="primary" 
              @click="handleTranscode(row)"
              :disabled="row.status === 'transcoding'"
            >
              转码
            </el-button>
            <el-dropdown @command="(command) => handleCommand(command, row)">
              <el-button size="small">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="download">下载</el-dropdown-item>
                  <el-dropdown-item command="info">详细信息</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
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
    <div class="batch-actions" v-show="selectedMedia.length > 0">
      <span class="selected-count">已选择 {{ selectedMedia.length }} 个文件</span>
      <el-button size="small" @click="batchDelete">批量删除</el-button>
      <el-button size="small" type="primary" @click="batchTranscode">批量转码</el-button>
      <el-button size="small" @click="clearSelection">取消选择</el-button>
    </div>

    <!-- 上传对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传媒体文件" width="600px">
      <div class="upload-area">
        <el-upload
          ref="uploadRef"
          class="upload-demo"
          drag
          multiple
          :action="uploadUrl"
          :headers="uploadHeaders"
          :data="uploadData"
          :before-upload="beforeUpload"
          :on-success="onUploadSuccess"
          :on-error="onUploadError"
          :on-progress="onUploadProgress"
          :file-list="fileList"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 mp4, avi, mov, mp3, wav, jpg, png 格式，单个文件不超过 2GB
            </div>
          </template>
        </el-upload>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload">开始上传</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="媒体预览" width="800px">
      <div class="preview-content">
        <video 
          v-if="currentMedia.type === 'video'" 
          :src="currentMedia.url" 
          controls 
          autoplay
          style="width: 100%"
        />
        <audio 
          v-else-if="currentMedia.type === 'audio'" 
          :src="currentMedia.url" 
          controls 
          autoplay
          style="width: 100%"
        />
        <img 
          v-else 
          :src="currentMedia.url" 
          style="max-width: 100%"
        />
      </div>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Upload,
  Refresh,
  Search,
  ArrowDown,
  UploadFilled
} from '@element-plus/icons-vue'

interface MediaItem {
  id: string
  name: string
  type: 'video' | 'audio' | 'image'
  format: string
  size: number
  duration?: number
  resolution?: string
  status: 'ready' | 'transcoding' | 'failed'
  createdTime: string
  url: string
}

// 响应式数据
const loading = ref(false)
const searchQuery = ref('')
const filterType = ref('')
const filterStatus = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const selectedMedia = ref<MediaItem[]>([])
const uploadDialogVisible = ref(false)
const previewDialogVisible = ref(false)
const currentMedia = ref<MediaItem>({} as MediaItem)

// 模拟媒体数据
const mediaList = ref<MediaItem[]>([
  {
    id: '1',
    name: '宣传视频.mp4',
    type: 'video',
    format: 'MP4',
    size: 1024 * 1024 * 50, // 50MB
    duration: 120,
    resolution: '1920x1080',
    status: 'ready',
    createdTime: '2024-01-15 10:30:00',
    url: '/media/video1.mp4'
  },
  {
    id: '2',
    name: '背景音乐.mp3',
    type: 'audio',
    format: 'MP3',
    size: 1024 * 1024 * 5, // 5MB
    duration: 180,
    status: 'ready',
    createdTime: '2024-01-14 14:20:00',
    url: '/media/audio1.mp3'
  },
  {
    id: '3',
    name: '产品图片.jpg',
    type: 'image',
    format: 'JPG',
    size: 1024 * 1024 * 2, // 2MB
    resolution: '1200x800',
    status: 'ready',
    createdTime: '2024-01-13 09:15:00',
    url: '/media/image1.jpg'
  },
  {
    id: '4',
    name: '会议记录.avi',
    type: 'video',
    format: 'AVI',
    size: 1024 * 1024 * 100, // 100MB
    duration: 600,
    resolution: '1280x720',
    status: 'transcoding',
    createdTime: '2024-01-12 16:45:00',
    url: '/media/video2.avi'
  },
  {
    id: '5',
    name: '音效.wav',
    type: 'audio',
    format: 'WAV',
    size: 1024 * 1024 * 10, // 10MB
    duration: 30,
    status: 'failed',
    createdTime: '2024-01-11 11:20:00',
    url: '/media/audio2.wav'
  }
])

// 计算属性：过滤后的媒体列表
const filteredMediaList = computed(() => {
  let filtered = mediaList.value
  
  if (searchQuery.value) {
    filtered = filtered.filter(item => 
      item.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  if (filterType.value) {
    filtered = filtered.filter(item => item.type === filterType.value)
  }
  
  if (filterStatus.value) {
    filtered = filtered.filter(item => item.status === filterStatus.value)
  }
  
  if (dateRange.value && dateRange.value.length === 2) {
    const [start, end] = dateRange.value
    filtered = filtered.filter(item => {
      const createTime = new Date(item.createdTime)
      return createTime >= start && createTime <= end
    })
  }
  
  total.value = filtered.length
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

// 工具函数
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDuration = (seconds: number): string => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const getStatusType = (status: string): string => {
  const statusMap: { [key: string]: string } = {
    'ready': 'success',
    'transcoding': 'warning',
    'failed': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string): string => {
  const statusMap: { [key: string]: string } = {
    'ready': '就绪',
    'transcoding': '转码中',
    'failed': '失败'
  }
  return statusMap[status] || status
}

// 事件处理函数
const handleSelectionChange = (selection: MediaItem[]) => {
  selectedMedia.value = selection
}

const handleUpload = () => {
  uploadDialogVisible.value = true
}

const refreshMediaList = () => {
  loading.value = true
  // 模拟API调用
  setTimeout(() => {
    loading.value = false
    ElMessage.success('媒体列表已刷新')
  }, 1000)
}

const handlePreview = (media: MediaItem) => {
  if (media.status !== 'ready') {
    ElMessage.warning('该文件暂不可预览')
    return
  }
  currentMedia.value = media
  previewDialogVisible.value = true
}

const handleTranscode = async (media: MediaItem) => {
  try {
    await ElMessageBox.confirm(
      `确定要对"${media.name}"进行转码吗？`,
      '转码确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟转码操作
    media.status = 'transcoding'
    ElMessage.success('转码任务已开始')
    
    // 模拟转码完成
    setTimeout(() => {
      media.status = 'ready'
      ElMessage.success(`"${media.name}"转码完成`)
    }, 5000)
  } catch {
    // 用户取消
  }
}

const handleCommand = (command: string, media: MediaItem) => {
  switch (command) {
    case 'download':
      handleDownload(media)
      break
    case 'info':
      showMediaInfo(media)
      break
    case 'delete':
      handleDelete(media)
      break
  }
}

const handleDownload = (media: MediaItem) => {
  ElMessage.info(`开始下载: ${media.name}`)
  // 实际下载逻辑
}

const showMediaInfo = (media: MediaItem) => {
  ElMessageBox.alert(
    `文件名: ${media.name}\n文件类型: ${media.type}\n文件大小: ${formatFileSize(media.size)}\n创建时间: ${formatDate(media.createdTime)}`,
    '文件信息'
  )
}

const handleDelete = async (media: MediaItem) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除"${media.name}"吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const index = mediaList.value.findIndex(item => item.id === media.id)
    if (index > -1) {
      mediaList.value.splice(index, 1)
      ElMessage.success('文件删除成功')
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
  if (selectedMedia.value.length === 0) return
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedMedia.value.length} 个文件吗？此操作不可恢复。`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    selectedMedia.value.forEach(media => {
      const index = mediaList.value.findIndex(item => item.id === media.id)
      if (index > -1) {
        mediaList.value.splice(index, 1)
      }
    })
    
    selectedMedia.value = []
    ElMessage.success(`成功删除 ${selectedMedia.value.length} 个文件`)
  } catch {
    // 用户取消
  }
}

const batchTranscode = () => {
  if (selectedMedia.value.length === 0) return
  
  selectedMedia.value.forEach(media => {
    if (media.status !== 'transcoding') {
      media.status = 'transcoding'
    }
  })
  
  ElMessage.success(`已开始对 ${selectedMedia.value.length} 个文件进行转码`)
}

const clearSelection = () => {
  selectedMedia.value = []
}

// 上传相关
const uploadRef = ref()
const fileList = ref([])
const uploadUrl = '/api/media/upload'
const uploadHeaders = {
  'Authorization': 'Bearer ' + localStorage.getItem('token')
}
const uploadData = {
  category: 'media'
}

const beforeUpload = (file: File) => {
  const isLt2G = file.size / 1024 / 1024 / 1024 < 2
  if (!isLt2G) {
    ElMessage.error('文件大小不能超过 2GB!')
    return false
  }
  return true
}

const onUploadSuccess = (response: any, file: File) => {
  ElMessage.success(`${file.name} 上传成功`)
}

const onUploadError = (error: Error, file: File) => {
  ElMessage.error(`${file.name} 上传失败`)
}

const onUploadProgress = (event: any, file: File) => {
  // 上传进度处理
}

const submitUpload = () => {
  uploadRef.value.submit()
}

onMounted(() => {
  refreshMediaList()
})
</script>

<style lang="scss" scoped>
.media-management-container {
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

  .filter-bar {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
    flex-wrap: wrap;
  }

  .media-content {
    background: rgba(255, 255, 255, 0.05);
    border-radius: 12px;
    padding: 24px;

    .media-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .media-thumbnail {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        overflow: hidden;
        background: rgba(255, 255, 255, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;

        img {
          width: 40px;
          height: 40px;
          object-fit: contain;
        }
      }

      .media-details {
        .media-name {
          font-weight: 500;
          color: white;
          margin-bottom: 4px;
        }

        .media-meta {
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

  .upload-area {
    .upload-demo {
      :deep(.el-upload-dragger) {
        background: rgba(255, 255, 255, 0.05);
        border: 2px dashed rgba(255, 255, 255, 0.3);

        &:hover {
          border-color: #409EFF;
        }
      }
    }
  }

  .preview-content {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 400px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .media-management-container {
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

    .filter-bar {
      flex-direction: column;

      .el-input,
      .el-select,
      .el-date-picker {
        width: 100% !important;
      }
    }

    .media-content {
      padding: 16px;
    }
  }
}
</style>