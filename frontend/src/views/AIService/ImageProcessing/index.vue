<template>
  <div class="image-processing-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">图像处理</h1>
      <p class="page-subtitle">图像识别、图像生成、风格转换等AI服务</p>
    </div>

    <!-- 功能卡片 -->
    <div class="function-cards">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="function-card" shadow="hover" @click="activeTab = 'imageRecognition'">
            <div class="card-content">
              <div class="card-icon image-recognition">
                <el-icon><Search /></el-icon>
              </div>
              <div class="card-info">
                <h3>图像识别</h3>
                <p>识别图像中的物体、场景、文字等内容</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="function-card" shadow="hover" @click="activeTab = 'imageGeneration'">
            <div class="card-content">
              <div class="card-icon image-generation">
                <el-icon><MagicStick /></el-icon>
              </div>
              <div class="card-info">
                <h3>图像生成</h3>
                <p>根据文本描述生成高质量的图像内容</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="function-card" shadow="hover" @click="activeTab = 'styleTransfer'">
            <div class="card-content">
              <div class="card-icon style-transfer">
                <el-icon><Brush /></el-icon>
              </div>
              <div class="card-info">
                <h3>风格转换</h3>
                <p>将图像转换为不同艺术风格的作品</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 功能内容区域 -->
    <div class="function-content">
      <!-- 图像识别 -->
      <div v-show="activeTab === 'imageRecognition'" class="tab-content">
        <el-card>
          <template #header>
            <div class="tab-header">
              <h3>图像识别</h3>
              <el-button type="primary" @click="startCamera" v-if="!isCameraActive">
                <el-icon><Camera /></el-icon>
                开启摄像头
              </el-button>
              <el-button type="danger" @click="stopCamera" v-else>
                <el-icon><VideoPause /></el-icon>
                关闭摄像头
              </el-button>
            </div>
          </template>
          
          <div class="recognition-area">
            <div class="camera-preview" v-if="isCameraActive">
              <video ref="cameraVideo" autoplay playsinline></video>
              <div class="camera-overlay">
                <div class="recognition-box" 
                     v-for="(box, index) in recognitionBoxes" 
                     :key="index"
                     :style="{
                       left: box.x + 'px',
                       top: box.y + 'px',
                       width: box.width + 'px',
                       height: box.height + 'px'
                     }">
                  <span class="box-label">{{ box.label }}</span>
                </div>
              </div>
              <div class="camera-controls">
                <el-button type="primary" @click="captureImage">
                  <el-icon><Camera /></el-icon>
                  拍照识别
                </el-button>
              </div>
            </div>
            
            <div class="upload-area" v-else>
              <el-upload
                class="image-upload"
                drag
                :action="uploadUrl"
                :before-upload="beforeImageUpload"
                :on-success="onImageUploadSuccess"
                :show-file-list="false"
              >
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">
                  将图像文件拖到此处，或<em>点击上传</em>
                </div>
                <template #tip>
                  <div class="el-upload__tip">
                    支持 jpg, png, gif 格式，单个文件不超过 10MB
                  </div>
                </template>
              </el-upload>
            </div>
          </div>
          
          <div class="recognition-result" v-if="recognitionResult">
            <h4>识别结果</h4>
            <div class="result-grid">
              <div class="result-item" v-for="(item, index) in recognitionResult" :key="index">
                <div class="item-label">{{ item.label }}</div>
                <div class="item-confidence">
                  <el-progress 
                    :percentage="item.confidence * 100" 
                    :show-text="false"
                    :stroke-width="8"
                  />
                  <span class="confidence-value">{{ (item.confidence * 100).toFixed(1) }}%</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="recognition-settings">
            <h4>识别设置</h4>
            <el-form :model="recognitionSettings" label-width="100px">
              <el-form-item label="识别类型">
                <el-select v-model="recognitionSettings.type" multiple>
                  <el-option label="物体识别" value="object" />
                  <el-option label="场景识别" value="scene" />
                  <el-option label="人脸识别" value="face" />
                  <el-option label="文字识别" value="text" />
                </el-select>
              </el-form-item>
              <el-form-item label="置信度阈值">
                <el-slider
                  v-model="recognitionSettings.confidenceThreshold"
                  :min="0.1"
                  :max="1"
                  :step="0.1"
                  show-stops
                />
                <span class="slider-value">{{ recognitionSettings.confidenceThreshold }}</span>
              </el-form-item>
              <el-form-item label="最大识别数">
                <el-input-number 
                  v-model="recognitionSettings.maxResults" 
                  :min="1" 
                  :max="50" 
                />
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </div>

      <!-- 图像生成 -->
      <div v-show="activeTab === 'imageGeneration'" class="tab-content">
        <el-card>
          <template #header>
            <h3>图像生成</h3>
          </template>
          
          <div class="generation-input">
            <el-input
              v-model="generationPrompt"
              type="textarea"
              :rows="3"
              placeholder="请输入图像描述，例如：一只可爱的猫咪在花园里玩耍..."
              maxlength="500"
              show-word-limit
            />
            <div class="prompt-examples">
              <span class="examples-title">示例提示词：</span>
              <el-tag 
                v-for="example in promptExamples" 
                :key="example"
                class="example-tag"
                @click="generationPrompt = example"
              >
                {{ example }}
              </el-tag>
            </div>
          </div>
          
          <div class="generation-settings">
            <el-form :model="generationSettings" label-width="100px">
              <el-form-item label="生成风格">
                <el-select v-model="generationSettings.style">
                  <el-option label="写实风格" value="realistic" />
                  <el-option label="动漫风格" value="anime" />
                  <el-option label="油画风格" value="oil-painting" />
                  <el-option label="水彩风格" value="watercolor" />
                  <el-option label="科幻风格" value="sci-fi" />
                </el-select>
              </el-form-item>
              <el-form-item label="图像尺寸">
                <el-select v-model="generationSettings.size">
                  <el-option label="512x512" value="512x512" />
                  <el-option label="768x768" value="768x768" />
                  <el-option label="1024x1024" value="1024x1024" />
                </el-select>
              </el-form-item>
              <el-form-item label="生成质量">
                <el-slider
                  v-model="generationSettings.quality"
                  :min="1"
                  :max="10"
                  :step="1"
                  show-stops
                />
                <span class="slider-value">{{ generationSettings.quality }}</span>
              </el-form-item>
              <el-form-item label="生成数量">
                <el-input-number 
                  v-model="generationSettings.count" 
                  :min="1" 
                  :max="4" 
                />
              </el-form-item>
            </el-form>
          </div>
          
          <div class="generation-controls">
            <el-button 
              type="primary" 
              @click="startGeneration"
              :disabled="!generationPrompt.trim()"
              :loading="isGenerating"
            >
              <el-icon><MagicStick /></el-icon>
              {{ isGenerating ? '生成中...' : '开始生成' }}
            </el-button>
            <el-button @click="clearGeneration">清空</el-button>
          </div>
          
          <div class="generation-result" v-if="generatedImages.length > 0">
            <h4>生成结果</h4>
            <div class="image-grid">
              <div 
                class="image-item" 
                v-for="(image, index) in generatedImages" 
                :key="index"
                @click="previewImage(image)"
              >
                <img :src="image.url" :alt="image.prompt" />
                <div class="image-overlay">
                  <div class="overlay-actions">
                    <el-button type="text" @click.stop="downloadImage(image)">
                      <el-icon><Download /></el-icon>
                    </el-button>
                    <el-button type="text" @click.stop="shareImage(image)">
                      <el-icon><Share /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 风格转换 -->
      <div v-show="activeTab === 'styleTransfer'" class="tab-content">
        <el-card>
          <template #header>
            <h3>风格转换</h3>
          </template>
          
          <div class="style-transfer-area">
            <div class="upload-section">
              <h4>上传内容图像</h4>
              <el-upload
                class="content-upload"
                drag
                :action="uploadUrl"
                :before-upload="beforeContentUpload"
                :on-success="onContentUploadSuccess"
                :show-file-list="false"
              >
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">
                  上传内容图像
                </div>
              </el-upload>
            </div>
            
            <div class="style-selection">
              <h4>选择风格</h4>
              <div class="style-grid">
                <div 
                  class="style-item" 
                  v-for="style in styleOptions" 
                  :key="style.value"
                  :class="{ active: selectedStyle === style.value }"
                  @click="selectedStyle = style.value"
                >
                  <img :src="style.thumbnail" :alt="style.label" />
                  <div class="style-label">{{ style.label }}</div>
                </div>
              </div>
            </div>
            
            <div class="transfer-controls" v-if="contentImage && selectedStyle">
              <el-button 
                type="primary" 
                @click="startStyleTransfer"
                :loading="isTransferring"
              >
                <el-icon><Brush /></el-icon>
                {{ isTransferring ? '转换中...' : '开始转换' }}
              </el-button>
            </div>
            
            <div class="transfer-result" v-if="transferResult">
              <h4>转换结果</h4>
              <div class="result-comparison">
                <div class="comparison-item">
                  <h5>原图</h5>
                  <img :src="contentImage" alt="原图" />
                </div>
                <div class="comparison-item">
                  <h5>转换后</h5>
                  <img :src="transferResult" alt="转换后" />
                </div>
              </div>
              <div class="result-actions">
                <el-button type="primary" @click="downloadTransferResult">
                  <el-icon><Download /></el-icon>
                  下载结果
                </el-button>
                <el-button @click="resetTransfer">重新选择</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 处理历史 -->
    <div class="processing-history">
      <el-card>
        <template #header>
          <h3>处理历史</h3>
        </template>
        <el-table :data="processingHistory" style="width: 100%">
          <el-table-column prop="type" label="类型" width="100" />
          <el-table-column prop="fileName" label="文件名" />
          <el-table-column prop="prompt" label="描述/提示词" />
          <el-table-column prop="createTime" label="处理时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'success' ? 'success' : 'danger'">
                {{ row.status === 'success' ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="text" @click="viewHistoryDetail(row)">查看</el-button>
              <el-button type="text" @click="deleteHistory(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  MagicStick,
  Brush,
  Camera,
  VideoPause,
  UploadFilled,
  Download,
  Share
} from '@element-plus/icons-vue'
import { aiApi } from '@/api/modules/ai'

// 响应式数据
const activeTab = ref('imageRecognition')

// 加载状态
const loading = ref(false)

// 图像识别相关
const isCameraActive = ref(false)
const cameraVideo = ref<HTMLVideoElement | null>(null)
const recognitionBoxes = ref<any[]>([])
const recognitionResult = ref<any[]>([])
const recognitionSettings = reactive({
  type: ['object', 'scene'],
  confidenceThreshold: 0.5,
  maxResults: 10
})

// 图像生成相关
const generationPrompt = ref('')
const isGenerating = ref(false)
const generatedImages = ref<any[]>([])
const generationSettings = reactive({
  style: 'realistic',
  size: '512x512',
  quality: 7,
  count: 1
})

// 风格转换相关
const contentImage = ref('')
const selectedStyle = ref('')
const isTransferring = ref(false)
const transferResult = ref('')

// 处理历史
const processingHistory = ref<any[]>([])

// 示例数据
const promptExamples = [
  '一只可爱的猫咪在花园里玩耍',
  '未来城市的科幻场景',
  '宁静的山水风景画',
  '现代简约风格的室内设计'
]

const styleOptions = [
  {
    value: 'van-gogh',
    label: '梵高风格',
    thumbnail: '/images/styles/van-gogh.jpg'
  },
  {
    value: 'picasso',
    label: '毕加索风格',
    thumbnail: '/images/styles/picasso.jpg'
  },
  {
    value: 'monet',
    label: '莫奈风格',
    thumbnail: '/images/styles/monet.jpg'
  },
  {
    value: 'ukiyo-e',
    label: '浮世绘风格',
    thumbnail: '/images/styles/ukiyo-e.jpg'
  }
]

// 上传配置
const uploadUrl = '/api/ai/upload'

// 图像识别功能
const startCamera = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ video: true })
    if (cameraVideo.value) {
      cameraVideo.value.srcObject = stream
      isCameraActive.value = true
    }
  } catch (error) {
    ElMessage.error('无法访问摄像头，请检查权限设置')
  }
}

const stopCamera = () => {
  if (cameraVideo.value && cameraVideo.value.srcObject) {
    const stream = cameraVideo.value.srcObject as MediaStream
    stream.getTracks().forEach(track => track.stop())
    cameraVideo.value.srcObject = null
  }
  isCameraActive.value = false
  recognitionBoxes.value = []
}

const captureImage = async () => {
  if (!cameraVideo.value) return
  
  try {
    const canvas = document.createElement('canvas')
    canvas.width = cameraVideo.value.videoWidth
    canvas.height = cameraVideo.value.videoHeight
    const ctx = canvas.getContext('2d')
    
    if (ctx) {
      ctx.drawImage(cameraVideo.value, 0, 0)
      
      // 将canvas转换为Blob并上传识别
      canvas.toBlob(async (blob) => {
        if (!blob) return
        
        const formData = new FormData()
        formData.append('file', blob, 'captured-image.jpg')
        formData.append('types', recognitionSettings.type.join(','))
        formData.append('confidenceThreshold', recognitionSettings.confidenceThreshold.toString())
        formData.append('maxResults', recognitionSettings.maxResults.toString())
        
        try {
          loading.value = true
          const result = await aiApi.imageRecognition(formData)
          recognitionResult.value = result.data || []
          
          // 添加处理历史
          processingHistory.value.unshift({
            id: Date.now().toString(),
            type: '图像识别',
            fileName: '摄像头图像.jpg',
            prompt: '',
            createTime: new Date().toLocaleString(),
            status: 'success'
          })
          
          ElMessage.success('图像识别完成')
        } catch (error) {
          ElMessage.error('图像识别失败')
        } finally {
          loading.value = false
        }
      }, 'image/jpeg')
    }
  } catch (error) {
    ElMessage.error('图像捕获失败')
  }
}

const beforeImageUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  
  if (!isImage) {
    ElMessage.error('只能上传图像文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图像文件大小不能超过 10MB!')
    return false
  }
  return true
}

const onImageUploadSuccess = async (response: any, file: File) => {
  ElMessage.success('图像上传成功，开始识别...')
  
  try {
    loading.value = true
    const formData = new FormData()
    formData.append('file', file)
    formData.append('types', recognitionSettings.type.join(','))
    formData.append('confidenceThreshold', recognitionSettings.confidenceThreshold.toString())
    formData.append('maxResults', recognitionSettings.maxResults.toString())
    
    const result = await aiApi.imageRecognition(formData)
    recognitionResult.value = result.data || []
    
    // 添加处理历史
    processingHistory.value.unshift({
      id: Date.now().toString(),
      type: '图像识别',
      fileName: file.name,
      prompt: '',
      createTime: new Date().toLocaleString(),
      status: 'success'
    })
    
    ElMessage.success('图像识别完成')
  } catch (error) {
    ElMessage.error('图像识别失败')
  } finally {
    loading.value = false
  }
}

// 图像生成功能
const startGeneration = async () => {
  if (!generationPrompt.value.trim()) {
    ElMessage.warning('请输入图像描述')
    return
  }
  
  try {
    isGenerating.value = true
    ElMessage.info('开始图像生成...')
    
    const requestData = {
      prompt: generationPrompt.value,
      style: generationSettings.style,
      size: generationSettings.size,
      quality: generationSettings.quality,
      count: generationSettings.count
    }
    
    const result = await aiApi.generateImage(requestData)
    
    if (result.data && result.data.images) {
      generatedImages.value = result.data.images.map((image: any, index: number) => ({
        id: Date.now() + index,
        url: image.url,
        prompt: generationPrompt.value,
        createTime: new Date().toLocaleString()
      }))
      
      // 添加处理历史
      processingHistory.value.unshift({
        id: Date.now().toString(),
        type: '图像生成',
        fileName: `生成图像-${Date.now()}.png`,
        prompt: generationPrompt.value,
        createTime: new Date().toLocaleString(),
        status: 'success'
      })
      
      ElMessage.success('图像生成完成')
    } else {
      ElMessage.error('图像生成失败')
    }
  } catch (error) {
    ElMessage.error('图像生成失败')
  } finally {
    isGenerating.value = false
  }
}

const clearGeneration = () => {
  generationPrompt.value = ''
  generatedImages.value = []
}

const previewImage = (image: any) => {
  ElMessage.info(`预览图像: ${image.prompt}`)
}

const downloadImage = async (image: any) => {
  try {
    const response = await fetch(image.url)
    const blob = await response.blob()
    const url = URL.createObjectURL(blob)
    
    const a = document.createElement('a')
    a.href = url
    a.download = `生成图像-${Date.now()}.png`
    a.click()
    
    URL.revokeObjectURL(url)
    ElMessage.success('图像下载成功')
  } catch (error) {
    ElMessage.error('图像下载失败')
  }
}

const shareImage = (image: any) => {
  ElMessage.info('分享功能开发中...')
}

// 风格转换功能
const beforeContentUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  
  if (!isImage) {
    ElMessage.error('只能上传图像文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图像文件大小不能超过 10MB!')
    return false
  }
  return true
}

const onContentUploadSuccess = (response: any, file: File) => {
  contentImage.value = URL.createObjectURL(file)
  ElMessage.success('内容图像上传成功')
}

const startStyleTransfer = async () => {
  if (!contentImage.value || !selectedStyle.value) {
    ElMessage.warning('请上传内容图像并选择风格')
    return
  }
  
  try {
    isTransferring.value = true
    ElMessage.info('开始风格转换...')
    
    // 获取上传的文件（这里需要从contentImage中获取文件对象）
    // 在实际应用中，需要将文件上传到服务器进行风格转换
    const response = await fetch(contentImage.value)
    const blob = await response.blob()
    
    const formData = new FormData()
    formData.append('contentImage', blob, 'content.jpg')
    formData.append('style', selectedStyle.value)
    
    const result = await aiApi.styleTransfer(formData)
    
    if (result.data && result.data.resultUrl) {
      transferResult.value = result.data.resultUrl
      
      // 添加处理历史
      processingHistory.value.unshift({
        id: Date.now().toString(),
        type: '风格转换',
        fileName: '风格转换结果.jpg',
        prompt: selectedStyle.value,
        createTime: new Date().toLocaleString(),
        status: 'success'
      })
      
      ElMessage.success('风格转换完成')
    } else {
      ElMessage.error('风格转换失败')
    }
  } catch (error) {
    ElMessage.error('风格转换失败')
  } finally {
    isTransferring.value = false
  }
}

const downloadTransferResult = async () => {
  if (!transferResult.value) {
    ElMessage.warning('请先完成风格转换')
    return
  }
  
  try {
    const response = await fetch(transferResult.value)
    const blob = await response.blob()
    const url = URL.createObjectURL(blob)
    
    const a = document.createElement('a')
    a.href = url
    a.download = `风格转换-${Date.now()}.jpg`
    a.click()
    
    URL.revokeObjectURL(url)
    ElMessage.success('转换结果下载成功')
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

const resetTransfer = () => {
  contentImage.value = ''
  selectedStyle.value = ''
  transferResult.value = ''
}

// 历史记录功能
const viewHistoryDetail = (history: any) => {
  ElMessage.info(`查看历史记录: ${history.fileName}`)
}

const deleteHistory = (history: any) => {
  const index = processingHistory.value.findIndex(item => item.id === history.id)
  if (index > -1) {
    processingHistory.value.splice(index, 1)
    ElMessage.success('历史记录删除成功')
  }
}

onMounted(async () => {
  // 加载处理历史
  try {
    loading.value = true
    const result = await aiApi.getProcessingHistory({ type: 'image' })
    if (result.data && result.data.history) {
      processingHistory.value = result.data.history
    }
  } catch (error) {
    console.error('加载处理历史失败:', error)
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  stopCamera()
})
</script>

<style lang="scss" scoped>
.image-processing-container {
  padding: 24px;
  min-height: calc(100vh - 112px);

  .page-header {
    margin-bottom: 32px;

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

  .function-cards {
    margin-bottom: 32px;

    .function-card {
      cursor: pointer;
      transition: all 0.3s ease;
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.1);

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
      }

      .card-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .card-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 60px;
          height: 60px;
          border-radius: 12px;
          font-size: 24px;
          color: white;

          &.image-recognition {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          }

          &.image-generation {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }

          &.style-transfer {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          }
        }

        .card-info {
          h3 {
            margin: 0 0 8px 0;
            font-size: 18px;
            font-weight: 600;
            color: white;
          }

          p {
            margin: 0;
            color: rgba(255, 255, 255, 0.7);
            font-size: 14px;
            line-height: 1.4;
          }
        }
      }
    }
  }

  .function-content {
    margin-bottom: 32px;

    .tab-content {
      .tab-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        h3 {
          margin: 0;
          color: white;
        }
      }

      .recognition-area {
        margin-bottom: 24px;

        .camera-preview {
          position: relative;
          width: 100%;
          max-width: 640px;
          margin: 0 auto;

          video {
            width: 100%;
            height: 360px;
            background: #000;
            border-radius: 8px;
          }

          .camera-overlay {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;

            .recognition-box {
              position: absolute;
              border: 2px solid #00FFFF;
              background: rgba(0, 255, 255, 0.1);

              .box-label {
                position: absolute;
                top: -25px;
                left: 0;
                background: #00FFFF;
                color: #000;
                padding: 2px 8px;
                border-radius: 4px;
                font-size: 12px;
                font-weight: bold;
              }
            }
          }

          .camera-controls {
            margin-top: 16px;
            text-align: center;
          }
        }

        .upload-area {
          .image-upload {
            :deep(.el-upload-dragger) {
              background: rgba(255, 255, 255, 0.05);
              border: 2px dashed rgba(255, 255, 255, 0.3);

              &:hover {
                border-color: #409EFF;
              }
            }
          }
        }
      }

      .recognition-result {
        margin-bottom: 24px;

        .result-grid {
          display: grid;
          gap: 16px;

          .result-item {
            display: flex;
            align-items: center;
            gap: 16px;
            padding: 12px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 8px;

            .item-label {
              min-width: 80px;
              color: white;
              font-weight: 500;
            }

            .item-confidence {
              flex: 1;
              display: flex;
              align-items: center;
              gap: 12px;

              .confidence-value {
                min-width: 50px;
                color: rgba(255, 255, 255, 0.7);
                font-size: 14px;
              }
            }
          }
        }
      }

      .generation-input {
        margin-bottom: 24px;

        .prompt-examples {
          margin-top: 16px;

          .examples-title {
            color: rgba(255, 255, 255, 0.7);
            font-size: 14px;
            margin-right: 12px;
          }

          .example-tag {
            margin: 4px;
            cursor: pointer;
            transition: all 0.3s ease;

            &:hover {
              transform: translateY(-2px);
            }
          }
        }
      }

      .generation-result {
        margin-top: 24px;

        .image-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 16px;

          .image-item {
            position: relative;
            aspect-ratio: 1;
            border-radius: 8px;
            overflow: hidden;
            cursor: pointer;
            transition: all 0.3s ease;

            &:hover {
              transform: translateY(-4px);
              box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);

              .image-overlay {
                opacity: 1;
              }
            }

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }

            .image-overlay {
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              background: rgba(0, 0, 0, 0.7);
              display: flex;
              align-items: center;
              justify-content: center;
              opacity: 0;
              transition: opacity 0.3s ease;

              .overlay-actions {
                display: flex;
                gap: 12px;

                .el-button {
                  color: white;
                  background: rgba(255, 255, 255, 0.2);

                  &:hover {
                    background: rgba(255, 255, 255, 0.3);
                  }
                }
              }
            }
          }
        }
      }

      .style-transfer-area {
        .upload-section,
        .style-selection {
          margin-bottom: 24px;

          h4 {
            margin: 0 0 16px 0;
            color: white;
          }

          .content-upload {
            :deep(.el-upload-dragger) {
              background: rgba(255, 255, 255, 0.05);
              border: 2px dashed rgba(255, 255, 255, 0.3);
              height: 200px;

              &:hover {
                border-color: #409EFF;
              }
            }
          }

          .style-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 16px;

            .style-item {
              cursor: pointer;
              border-radius: 8px;
              overflow: hidden;
              transition: all 0.3s ease;
              border: 2px solid transparent;

              &.active {
                border-color: #409EFF;
                transform: scale(1.05);
              }

              &:hover {
                transform: scale(1.02);
              }

              img {
                width: 100%;
                height: 120px;
                object-fit: cover;
              }

              .style-label {
                padding: 8px;
                text-align: center;
                color: white;
                background: rgba(255, 255, 255, 0.1);
              }
            }
          }
        }

        .transfer-controls {
          margin-bottom: 24px;
          text-align: center;
        }

        .transfer-result {
          .result-comparison {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 24px;
            margin-bottom: 24px;

            .comparison-item {
              h5 {
                margin: 0 0 12px 0;
                color: white;
                text-align: center;
              }

              img {
                width: 100%;
                max-height: 300px;
                object-fit: contain;
                border-radius: 8px;
                background: rgba(255, 255, 255, 0.05);
              }
            }
          }

          .result-actions {
            text-align: center;
          }
        }
      }

      .recognition-settings,
      .generation-settings {
        margin-top: 24px;

        .slider-value {
          margin-left: 12px;
          color: rgba(255, 255, 255, 0.7);
          font-size: 14px;
        }
      }
    }
  }

  .processing-history {
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
}

// 响应式设计
@media (max-width: 768px) {
  .image-processing-container {
    padding: 16px;

    .function-cards {
      .el-col {
        margin-bottom: 16px;
      }

      .function-card {
        .card-content {
          flex-direction: column;
          text-align: center;
          gap: 12px;
        }
      }
    }

    .function-content {
      .tab-content {
        .tab-header {
          flex-direction: column;
          gap: 16px;
          align-items: flex-start;
        }

        .recognition-area {
          .camera-preview {
            video {
              height: 240px;
            }
          }
        }

        .generation-result {
          .image-grid {
            grid-template-columns: 1fr;
          }
        }

        .style-transfer-area {
          .result-comparison {
            grid-template-columns: 1fr;
            gap: 16px;
          }
        }
      }
    }
  }
}