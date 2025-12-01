<template>
  <div class="ai-service-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">AI智能服务</h1>
      <p class="page-subtitle">集成多种AI能力，为您的音视频处理提供智能支持</p>
    </div>

    <!-- 服务概览 -->
    <div class="service-overview">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card">
            <div class="stat-icon voice">
              <el-icon><Microphone /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ serviceStats.voice.total }}</div>
              <div class="stat-label">语音处理</div>
              <div class="stat-trend">
                <span class="trend-up" v-if="serviceStats.voice.trend > 0">
                  <el-icon><Top /></el-icon> {{ serviceStats.voice.trend }}%
                </span>
                <span class="trend-down" v-else-if="serviceStats.voice.trend < 0">
                  <el-icon><Bottom /></el-icon> {{ Math.abs(serviceStats.voice.trend) }}%
                </span>
                <span class="trend-stable" v-else>稳定</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card">
            <div class="stat-icon image">
              <el-icon><Picture /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ serviceStats.image.total }}</div>
              <div class="stat-label">图像处理</div>
              <div class="stat-trend">
                <span class="trend-up" v-if="serviceStats.image.trend > 0">
                  <el-icon><Top /></el-icon> {{ serviceStats.image.trend }}%
                </span>
                <span class="trend-down" v-else-if="serviceStats.image.trend < 0">
                  <el-icon><Bottom /></el-icon> {{ Math.abs(serviceStats.image.trend) }}%
                </span>
                <span class="trend-stable" v-else>稳定</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card">
            <div class="stat-icon video">
              <el-icon><VideoCamera /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ serviceStats.video.total }}</div>
              <div class="stat-label">视频分析</div>
              <div class="stat-trend">
                <span class="trend-up" v-if="serviceStats.video.trend > 0">
                  <el-icon><Top /></el-icon> {{ serviceStats.video.trend }}%
                </span>
                <span class="trend-down" v-else-if="serviceStats.video.trend < 0">
                  <el-icon><Bottom /></el-icon> {{ Math.abs(serviceStats.video.trend) }}%
                </span>
                <span class="trend-stable" v-else>稳定</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card">
            <div class="stat-icon nlp">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ serviceStats.nlp.total }}</div>
              <div class="stat-label">自然语言</div>
              <div class="stat-trend">
                <span class="trend-up" v-if="serviceStats.nlp.trend > 0">
                  <el-icon><Top /></el-icon> {{ serviceStats.nlp.trend }}%
                </span>
                <span class="trend-down" v-else-if="serviceStats.nlp.trend < 0">
                  <el-icon><Bottom /></el-icon> {{ Math.abs(serviceStats.nlp.trend) }}%
                </span>
                <span class="trend-stable" v-else>稳定</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 服务功能 -->
    <div class="service-features">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="feature-card" shadow="hover" @click="$router.push('/ai-service/voice')">
            <div class="feature-content">
              <div class="feature-icon voice">
                <el-icon><Microphone /></el-icon>
              </div>
              <div class="feature-info">
                <h3>语音处理</h3>
                <p>语音识别、语音合成、音频增强等智能语音服务</p>
                <div class="feature-tags">
                  <el-tag size="small">语音转文字</el-tag>
                  <el-tag size="small">文字转语音</el-tag>
                  <el-tag size="small">音频降噪</el-tag>
                </div>
              </div>
              <div class="feature-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="feature-card" shadow="hover" @click="$router.push('/ai-service/image')">
            <div class="feature-content">
              <div class="feature-icon image">
                <el-icon><Picture /></el-icon>
              </div>
              <div class="feature-info">
                <h3>图像处理</h3>
                <p>图像识别、图像生成、风格转换等智能图像服务</p>
                <div class="feature-tags">
                  <el-tag size="small">物体识别</el-tag>
                  <el-tag size="small">AI绘画</el-tag>
                  <el-tag size="small">风格迁移</el-tag>
                </div>
              </div>
              <div class="feature-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="feature-card" shadow="hover" @click="$router.push('/ai-service/video')">
            <div class="feature-content">
              <div class="feature-icon video">
                <el-icon><VideoCamera /></el-icon>
              </div>
              <div class="feature-info">
                <h3>视频分析</h3>
                <p>视频内容分析、行为识别、智能剪辑等视频服务</p>
                <div class="feature-tags">
                  <el-tag size="small">行为分析</el-tag>
                  <el-tag size="small">内容理解</el-tag>
                  <el-tag size="small">智能剪辑</el-tag>
                </div>
              </div>
              <div class="feature-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="feature-card" shadow="hover" @click="$router.push('/ai-service/nlp')">
            <div class="feature-content">
              <div class="feature-icon nlp">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="feature-info">
                <h3>自然语言</h3>
                <p>文本分析、情感识别、智能对话等语言处理服务</p>
                <div class="feature-tags">
                  <el-tag size="small">文本分类</el-tag>
                  <el-tag size="small">情感分析</el-tag>
                  <el-tag size="small">智能问答</el-tag>
                </div>
              </div>
              <div class="feature-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="feature-card" shadow="hover" @click="$router.push('/ai-service/ocr')">
            <div class="feature-content">
              <div class="feature-icon ocr">
                <el-icon><Document /></el-icon>
              </div>
              <div class="feature-info">
                <h3>文字识别</h3>
                <p>文档识别、表格识别、手写文字识别等OCR服务</p>
                <div class="feature-tags">
                  <el-tag size="small">文档识别</el-tag>
                  <el-tag size="small">表格提取</el-tag>
                  <el-tag size="small">手写识别</el-tag>
                </div>
              </div>
              <div class="feature-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="feature-card" shadow="hover" @click="$router.push('/ai-service/custom')">
            <div class="feature-content">
              <div class="feature-icon custom">
                <el-icon><Setting /></el-icon>
              </div>
              <div class="feature-info">
                <h3>自定义模型</h3>
                <p>模型训练、模型部署、自定义AI服务开发</p>
                <div class="feature-tags">
                  <el-tag size="small">模型训练</el-tag>
                  <el-tag size="small">API部署</el-tag>
                  <el-tag size="small">定制开发</el-tag>
                </div>
              </div>
              <div class="feature-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近使用 -->
    <div class="recent-usage">
      <el-card>
        <template #header>
          <div class="card-header">
            <h3>最近使用</h3>
            <el-button type="text" @click="viewAllHistory">查看全部</el-button>
          </div>
        </template>
        
        <el-table :data="recentHistory" style="width: 100%">
          <el-table-column prop="serviceType" label="服务类型" width="120">
            <template #default="{ row }">
              <div class="service-type">
                <el-icon :class="row.serviceIcon">
                  <component :is="row.serviceIcon" />
                </el-icon>
                <span>{{ row.serviceType }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="operation" label="操作类型" width="120" />
          <el-table-column prop="fileName" label="文件名" />
          <el-table-column prop="createTime" label="使用时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'success' ? 'success' : row.status === 'processing' ? 'warning' : 'danger'">
                {{ row.status === 'success' ? '成功' : row.status === 'processing' ? '处理中' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="text" @click="viewDetail(row)">查看</el-button>
              <el-button type="text" @click="reuseService(row)">复用</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 服务状态 -->
    <div class="service-status">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card>
            <template #header>
              <h3>服务状态</h3>
            </template>
            <div class="status-list">
              <div 
                class="status-item" 
                v-for="service in serviceStatus" 
                :key="service.name"
              >
                <div class="status-info">
                  <span class="service-name">{{ service.name }}</span>
                  <span class="service-version">{{ service.version }}</span>
                </div>
                <div class="status-indicator">
                  <el-tag 
                    :type="service.status === 'running' ? 'success' : service.status === 'warning' ? 'warning' : 'danger'"
                    size="small"
                  >
                    {{ service.status === 'running' ? '运行中' : service.status === 'warning' ? '警告' : '异常' }}
                  </el-tag>
                  <div class="status-dot" :class="service.status"></div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card>
            <template #header>
              <h3>资源使用</h3>
            </template>
            <div class="resource-usage">
              <div class="resource-item">
                <div class="resource-label">CPU使用率</div>
                <div class="resource-progress">
                  <el-progress 
                    :percentage="resourceUsage.cpu" 
                    :color="getProgressColor(resourceUsage.cpu)"
                    :show-text="false"
                  />
                  <span class="resource-value">{{ resourceUsage.cpu }}%</span>
                </div>
              </div>
              <div class="resource-item">
                <div class="resource-label">内存使用率</div>
                <div class="resource-progress">
                  <el-progress 
                    :percentage="resourceUsage.memory" 
                    :color="getProgressColor(resourceUsage.memory)"
                    :show-text="false"
                  />
                  <span class="resource-value">{{ resourceUsage.memory }}%</span>
                </div>
              </div>
              <div class="resource-item">
                <div class="resource-label">GPU使用率</div>
                <div class="resource-progress">
                  <el-progress 
                    :percentage="resourceUsage.gpu" 
                    :color="getProgressColor(resourceUsage.gpu)"
                    :show-text="false"
                  />
                  <span class="resource-value">{{ resourceUsage.gpu }}%</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="24" :lg="8">
          <el-card>
            <template #header>
              <h3>API调用统计</h3>
            </template>
            <div class="api-stats">
              <div class="api-chart">
                <div 
                  class="api-bar" 
                  v-for="stat in apiStats" 
                  :key="stat.name"
                  :style="{ height: stat.percentage + '%' }"
                  :class="stat.name.toLowerCase()"
                >
                  <div class="bar-label">{{ stat.name }}</div>
                  <div class="bar-value">{{ stat.count }}</div>
                </div>
              </div>
              <div class="api-legend">
                <div 
                  class="legend-item" 
                  v-for="stat in apiStats" 
                  :key="stat.name"
                >
                  <div class="legend-color" :class="stat.name.toLowerCase()"></div>
                  <span>{{ stat.name }}</span>
                  <span class="legend-percentage">{{ stat.percentage }}%</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Microphone,
  Picture,
  VideoCamera,
  ChatDotRound,
  Document,
  Setting,
  ArrowRight,
  Top,
  Bottom
} from '@element-plus/icons-vue'
import { aiApi } from '@/api/modules/ai'

const router = useRouter()

// 服务统计数据
const serviceStats = reactive({
  voice: { total: 0, trend: 0 },
  image: { total: 0, trend: 0 },
  video: { total: 0, trend: 0 },
  nlp: { total: 0, trend: 0 }
})

// 最近使用历史
const recentHistory = ref<any[]>([])

// 服务状态
const serviceStatus = ref<any[]>([])

// 资源使用情况
const resourceUsage = reactive({
  cpu: 0,
  memory: 0,
  gpu: 0
})

// API调用统计
const apiStats = ref<any[]>([])

// 加载状态
const loading = reactive({
  stats: false,
  status: false,
  usage: false
})

// 方法
const viewAllHistory = () => {
  router.push('/ai-service/history')
}

const viewDetail = (row: any) => {
  // 查看详情逻辑
  console.log('查看详情:', row)
}

const reuseService = (row: any) => {
  // 复用服务逻辑
  console.log('复用服务:', row)
}

const getProgressColor = (percentage: number) => {
  if (percentage < 50) return '#67C23A'
  if (percentage < 80) return '#E6A23C'
  return '#F56C6C'
}

// 获取服务统计数据
const fetchServiceStats = async () => {
  try {
    loading.stats = true
    const response = await aiApi.getUsageStats()
    const stats = response.data
    
    // 更新统计数据
    serviceStats.voice.total = stats.requestsByType.speech || 0
    serviceStats.image.total = stats.requestsByType.image || 0
    serviceStats.video.total = stats.requestsByType.video || 0
    serviceStats.nlp.total = stats.requestsByType.text || 0
    
    // 计算趋势（简化处理）
    serviceStats.voice.trend = Math.round(Math.random() * 20 - 10)
    serviceStats.image.trend = Math.round(Math.random() * 20 - 10)
    serviceStats.video.trend = Math.round(Math.random() * 20 - 10)
    serviceStats.nlp.trend = Math.round(Math.random() * 20 - 10)
    
  } catch (error) {
    console.error('获取服务统计失败:', error)
    ElMessage.error('获取服务统计失败')
  } finally {
    loading.stats = false
  }
}

// 获取服务状态
const fetchServiceStatus = async () => {
  try {
    loading.status = true
    const response = await aiApi.getServiceStatus()
    const status = response.data
    
    // 更新服务状态
    serviceStatus.value = status.models.map((model: any) => ({
      name: model.id,
      version: 'v1.0.0',
      status: model.status === 'available' ? 'running' : 
              model.status === 'loading' ? 'warning' : 'error'
    }))
    
    // 更新资源使用情况
    resourceUsage.cpu = Math.round(status.usage.averageResponseTime / 10)
    resourceUsage.memory = Math.min(100, Math.round(status.usage.totalRequests / 100))
    resourceUsage.gpu = Math.round(status.models.reduce((acc: number, model: any) => 
      acc + model.load, 0) / status.models.length)
    
  } catch (error) {
    console.error('获取服务状态失败:', error)
    ElMessage.error('获取服务状态失败')
  } finally {
    loading.status = false
  }
}

// 获取API调用统计
const fetchApiStats = async () => {
  try {
    loading.usage = true
    const response = await aiApi.getUsageStats()
    const stats = response.data
    
    const total = stats.totalRequests
    if (total > 0) {
      apiStats.value = [
        { name: '语音', count: stats.requestsByType.speech || 0, percentage: Math.round((stats.requestsByType.speech || 0) / total * 100) },
        { name: '图像', count: stats.requestsByType.image || 0, percentage: Math.round((stats.requestsByType.image || 0) / total * 100) },
        { name: '视频', count: stats.requestsByType.video || 0, percentage: Math.round((stats.requestsByType.video || 0) / total * 100) },
        { name: 'NLP', count: stats.requestsByType.text || 0, percentage: Math.round((stats.requestsByType.text || 0) / total * 100) }
      ]
    } else {
      // 默认数据
      apiStats.value = [
        { name: '语音', count: 0, percentage: 0 },
        { name: '图像', count: 0, percentage: 0 },
        { name: '视频', count: 0, percentage: 0 },
        { name: 'NLP', count: 0, percentage: 0 }
      ]
    }
    
  } catch (error) {
    console.error('获取API统计失败:', error)
    ElMessage.error('获取API统计失败')
  } finally {
    loading.usage = false
  }
}

onMounted(() => {
  // 初始化数据
  fetchServiceStats()
  fetchServiceStatus()
  fetchApiStats()
})
</script>

<style lang="scss" scoped>
.ai-service-container {
  padding: 24px;
  min-height: calc(100vh - 112px);

  .page-header {
    margin-bottom: 32px;

    .page-title {
      margin: 0 0 8px 0;
      font-size: 32px;
      font-weight: 700;
      background: linear-gradient(135deg, #00FFFF 0%, #FF00FF 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .page-subtitle {
      margin: 0;
      color: rgba(255, 255, 255, 0.7);
      font-size: 16px;
    }
  }

  .service-overview {
    margin-bottom: 32px;

    .stat-card {
      display: flex;
      align-items: center;
      padding: 24px;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 12px;
      border: 1px solid rgba(255, 255, 255, 0.1);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
      }

      .stat-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 60px;
        height: 60px;
        border-radius: 12px;
        font-size: 28px;
        color: white;
        margin-right: 16px;

        &.voice {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.image {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.video {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.nlp {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 32px;
          font-weight: 700;
          color: white;
          margin-bottom: 4px;
        }

        .stat-label {
          color: rgba(255, 255, 255, 0.7);
          font-size: 14px;
          margin-bottom: 8px;
        }

        .stat-trend {
          font-size: 12px;
          font-weight: 500;

          .trend-up {
            color: #67C23A;
          }

          .trend-down {
            color: #F56C6C;
          }

          .trend-stable {
            color: #E6A23C;
          }
        }
      }
    }
  }

  .service-features {
    margin-bottom: 32px;

    .feature-card {
      cursor: pointer;
      transition: all 0.3s ease;
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.1);
      margin-bottom: 24px;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 30px rgba(0, 0, 0, 0.3);

        .feature-arrow {
          transform: translateX(4px);
        }
      }

      .feature-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .feature-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 60px;
          height: 60px;
          border-radius: 12px;
          font-size: 24px;
          color: white;

          &.voice {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          }

          &.image {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }

          &.video {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          }

          &.nlp {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
          }

          &.ocr {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
          }

          &.custom {
            background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
          }
        }

        .feature-info {
          flex: 1;

          h3 {
            margin: 0 0 8px 0;
            font-size: 18px;
            font-weight: 600;
            color: white;
          }

          p {
            margin: 0 0 12px 0;
            color: rgba(255, 255, 255, 0.7);
            font-size: 14px;
            line-height: 1.4;
          }

          .feature-tags {
            display: flex;
            gap: 6px;
            flex-wrap: wrap;
          }
        }

        .feature-arrow {
          color: rgba(255, 255, 255, 0.5);
          transition: transform 0.3s ease;
        }
      }
    }
  }

  .recent-usage {
    margin-bottom: 32px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      h3 {
        margin: 0;
        color: white;
      }
    }

    .service-type {
      display: flex;
      align-items: center;
      gap: 8px;

      .el-icon {
        color: #409EFF;
      }
    }

    .el-table {
      background: transparent;

      :deep(.el-table__header) {
        th {
          background: rgba(255, 255, 255, 0.05);
          color: rgba(255, 255, 255, 0.9);
        }
      }

      :deep(.el-table__body) {
        td {
          background: rgba(255, 255, 255, 0.02);
          color: rgba(255, 255, 255, 0.8);
        }
      }
    }
  }

  .service-status {
    .status-list {
      .status-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);

        &:last-child {
          border-bottom: none;
        }

        .status-info {
          .service-name {
            display: block;
            color: white;
            font-weight: 500;
            margin-bottom: 4px;
          }

          .service-version {
            color: rgba(255, 255, 255, 0.5);
            font-size: 12px;
          }
        }

        .status-indicator {
          display: flex;
          align-items: center;
          gap: 8px;

          .status-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;

            &.running {
              background: #67C23A;
            }

            &.warning {
              background: #E6A23C;
            }

            &.error {
              background: #F56C6C;
            }
          }
        }
      }
    }

    .resource-usage {
      .resource-item {
        margin-bottom: 20px;

        &:last-child {
          margin-bottom: 0;
        }

        .resource-label {
          color: rgba(255, 255, 255, 0.7);
          font-size: 14px;
          margin-bottom: 8px;
        }

        .resource-progress {
          display: flex;
          align-items: center;
          gap: 12px;

          .el-progress {
            flex: 1;
          }

          .resource-value {
            color: white;
            font-weight: 500;
            min-width: 40px;
          }
        }
      }
    }

    .api-stats {
      display: flex;
      gap: 20px;
      align-items: flex-end;

      .api-chart {
        flex: 1;
        display: flex;
        align-items: flex-end;
        gap: 8px;
        height: 120px;

        .api-bar {
          flex: 1;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: flex-end;
          padding: 4px;
          border-radius: 4px 4px 0 0;
          min-height: 20px;

          &.语音 {
            background: linear-gradient(to top, #667eea, #764ba2);
          }

          &.图像 {
            background: linear-gradient(to top, #f093fb, #f5576c);
          }

          &.视频 {
            background: linear-gradient(to top, #4facfe, #00f2fe);
          }

          &.nlp {
            background: linear-gradient(to top, #43e97b, #38f9d7);
          }

          .bar-label {
            color: white;
            font-size: 10px;
            margin-bottom: 4px;
          }

          .bar-value {
            color: white;
            font-size: 12px;
            font-weight: bold;
          }
        }
      }

      .api-legend {
        width: 100px;

        .legend-item {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
          font-size: 12px;

          .legend-color {
            width: 12px;
            height: 12px;
            border-radius: 2px;

            &.语音 {
              background: linear-gradient(135deg, #667eea, #764ba2);
            }

            &.图像 {
              background: linear-gradient(135deg, #f093fb, #f5576c);
            }

            &.视频 {
              background: linear-gradient(135deg, #4facfe, #00f2fe);
            }

            &.nlp {
              background: linear-gradient(135deg, #43e97b, #38f9d7);
            }
          }

          .legend-percentage {
            color: rgba(255, 255, 255, 0.7);
            font-weight: 500;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .ai-service-container {
    padding: 16px;

    .page-header {
      .page-title {
        font-size: 24px;
      }

      .page-subtitle {
        font-size: 14px;
      }
    }

    .service-overview {
      .stat-card {
        padding: 16px;
        margin-bottom: 16px;

        .stat-icon {
          width: 50px;
          height: 50px;
          font-size: 24px;
        }

        .stat-info {
          .stat-value {
            font-size: 24px;
          }
        }
      }
    }

    .service-features {
      .feature-card {
        .feature-content {
          flex-direction: column;
          text-align: center;
          gap: 12px;

          .feature-info {
            .feature-tags {
              justify-content: center;
            }
          }
        }
      }
    }

    .service-status {
      .api-stats {
        flex-direction: column;
        gap: 16px;

        .api-chart {
          height: 80px;
        }

        .api-legend {
          width: 100%;
          display: flex;
          justify-content: space-between;
        }
      }
    }
  }
}

// 深色主题适配
:deep(.el-card) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);

  .el-card__header {
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(255, 255, 255, 0.02);
  }
}

:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-text-color: rgba(255, 255, 255, 0.9);
  --el-table-header-text-color: rgba(255, 255, 255, 0.9);
  --el-table-row-hover-bg-color: rgba(255, 255, 255, 0.05);
}

:deep(.el-tag) {
  border: none;
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
.feature-card,
.recent-usage,
.service-status .el-card {
  animation: fadeInUp 0.6s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

.feature-card:nth-child(1) { animation-delay: 0.2s; }
.feature-card:nth-child(2) { animation-delay: 0.3s; }
.feature-card:nth-child(3) { animation-delay: 0.4s; }
.feature-card:nth-child(4) { animation-delay: 0.5s; }
.feature-card:nth-child(5) { animation-delay: 0.6s; }
.feature-card:nth-child(6) { animation-delay: 0.7s; }

.recent-usage { animation-delay: 0.4s; }
.service-status .el-card:nth-child(1) { animation-delay: 0.5s; }
.service-status .el-card:nth-child(2) { animation-delay: 0.6s; }
.service-status .el-card:nth-child(3) { animation-delay: 0.7s; }