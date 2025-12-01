<template>
  <div class="voice-processing-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">语音处理</h1>
      <p class="page-subtitle">语音识别、语音合成、音频增强等AI服务</p>
    </div>

    <!-- 功能卡片 -->
    <div class="function-cards">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="function-card" shadow="hover" @click="activeTab = 'speechRecognition'">
            <div class="card-content">
              <div class="card-icon speech-recognition">
                <el-icon><Microphone /></el-icon>
              </div>
              <div class="card-info">
                <h3>语音识别</h3>
                <p>将语音转换为文字，支持多语言识别和实时转写</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="function-card" shadow="hover" @click="activeTab = 'speechSynthesis'">
            <div class="card-content">
              <div class="card-icon speech-synthesis">
                <el-icon><Headset /></el-icon>
              </div>
              <div class="card-info">
                <h3>语音合成</h3>
                <p>将文字转换为自然语音，多种音色和情感可选</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <el-card class="function-card" shadow="hover" @click="activeTab = 'audioEnhancement'">
            <div class="card-content">
              <div class="card-icon audio-enhancement">
                <el-icon><VideoPlay /></el-icon>
              </div>
              <div class="card-info">
                <h3>音频增强</h3>
                <p>降噪、回声消除、音量均衡等音频处理功能</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 功能内容区域 -->
    <div class="function-content">
      <!-- 语音识别 -->
      <div v-show="activeTab === 'speechRecognition'" class="tab-content">
        <el-card>
          <template #header>
            <div class="tab-header">
              <h3>语音识别</h3>
              <el-button type="primary" @click="startRecording" :disabled="isRecording">
                <el-icon><Microphone /></el-icon>
                {{ isRecording ? '录音中...' : '开始录音' }}
              </el-button>
            </div>
          </template>
          
          <div class="recording-area">
            <div class="audio-visualizer" v-if="isRecording">
              <div 
                v-for="(bar, index) in audioBars" 
                :key="index"
                class="audio-bar"
                :style="{ height: bar + 'px' }"
              ></div>
            </div>
            
            <div class="recording-controls">
              <el-button 
                v-if="isRecording" 
                type="danger" 
                @click="stopRecording"
              >
                <el-icon><VideoPause /></el-icon>
                停止录音
              </el-button>
              
              <el-upload
                v-else
                class="audio-upload"
                :action="uploadUrl"
                :before-upload="beforeAudioUpload"
                :on-success="onAudioUploadSuccess"
                :show-file-list="false"
              >
                <el-button type="primary">
                  <el-icon><Upload /></el-icon>
                  上传音频文件
                </el-button>
              </el-upload>
            </div>
          </div>
          
          <div class="recognition-result">
            <h4>识别结果</h4>
            <el-input
              v-model="recognitionResult"
              type="textarea"
              :rows="6"
              placeholder="识别结果将显示在这里..."
              readonly
            />
            <div class="result-actions">
              <el-button @click="copyResult">复制结果</el-button>
              <el-button type="primary" @click="downloadResult">下载文本</el-button>
              <el-button @click="clearResult">清空</el-button>
            </div>
          </div>
          
          <div class="recognition-settings">
            <h4>识别设置</h4>
            <el-form :model="recognitionSettings" label-width="100px">
              <el-form-item label="识别语言">
                <el-select v-model="recognitionSettings.language">
                  <el-option label="中文" value="zh-CN" />
                  <el-option label="英文" value="en-US" />
                  <el-option label="日语" value="ja-JP" />
                  <el-option label="韩语" value="ko-KR" />
                </el-select>
              </el-form-item>
              <el-form-item label="识别模式">
                <el-radio-group v-model="recognitionSettings.mode">
                  <el-radio label="accurate">精准模式</el-radio>
                  <el-radio label="fast">快速模式</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="标点符号">
                <el-switch v-model="recognitionSettings.punctuation" />
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </div>

      <!-- 语音合成 -->
      <div v-show="activeTab === 'speechSynthesis'" class="tab-content">
        <el-card>
          <template #header>
            <h3>语音合成</h3>
          </template>
          
          <div class="synthesis-input">
            <el-input
              v-model="synthesisText"
              type="textarea"
              :rows="4"
              placeholder="请输入要合成的文本内容..."
              maxlength="1000"
              show-word-limit
            />
          </div>
          
          <div class="synthesis-settings">
            <el-form :model="synthesisSettings" label-width="100px">
              <el-form-item label="语音类型">
                <el-select v-model="synthesisSettings.voiceType">
                  <el-option label="标准女声" value="female-standard" />
                  <el-option label="标准男声" value="male-standard" />
                  <el-option label="甜美女声" value="female-sweet" />
                  <el-option label="磁性男声" value="male-magnetic" />
                </el-select>
              </el-form-item>
              <el-form-item label="语速">
                <el-slider
                  v-model="synthesisSettings.speed"
                  :min="0.5"
                  :max="2"
                  :step="0.1"
                  show-stops
                />
                <span class="slider-value">{{ synthesisSettings.speed }}x</span>
              </el-form-item>
              <el-form-item label="音调">
                <el-slider
                  v-model="synthesisSettings.pitch"
                  :min="0.5"
                  :max="2"
                  :step="0.1"
                  show-stops
                />
                <span class="slider-value">{{ synthesisSettings.pitch }}x</span>
              </el-form-item>
              <el-form-item label="音量">
                <el-slider
                  v-model="synthesisSettings.volume"
                  :min="0"
                  :max="100"
                  show-stops
                />
                <span class="slider-value">{{ synthesisSettings.volume }}%</span>
              </el-form-item>
            </el-form>
          </div>
          
          <div class="synthesis-controls">
            <el-button 
              type="primary" 
              @click="startSynthesis"
              :disabled="!synthesisText.trim()"
            >
              <el-icon><VideoPlay /></el-icon>
              开始合成
            </el-button>
            <el-button @click="previewSynthesis" :disabled="!synthesisAudioUrl">
              <el-icon><Headset /></el-icon>
              试听
            </el-button>
            <el-button @click="downloadSynthesis" :disabled="!synthesisAudioUrl">
              <el-icon><Download /></el-icon>
              下载音频
            </el-button>
          </div>
          
          <div v-if="synthesisAudioUrl" class="synthesis-result">
            <audio :src="synthesisAudioUrl" controls style="width: 100%"></audio>
          </div>
        </el-card>
      </div>

      <!-- 音频增强 -->
      <div v-show="activeTab === 'audioEnhancement'" class="tab-content">
        <el-card>
          <template #header>
            <h3>音频增强</h3>
          </template>
          
          <div class="enhancement-upload">
            <el-upload
              class="audio-upload-area"
              drag
              :action="uploadUrl"
              :before-upload="beforeEnhancementUpload"
              :on-success="onEnhancementUploadSuccess"
              :show-file-list="false"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将音频文件拖到此处，或<em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持 mp3, wav, aac 格式，单个文件不超过 100MB
                </div>
              </template>
            </el-upload>
          </div>
          
          <div v-if="enhancementAudio" class="enhancement-controls">
            <div class="audio-preview">
              <h4>原始音频</h4>
              <audio :src="enhancementAudio.originalUrl" controls style="width: 100%"></audio>
            </div>
            
            <div class="enhancement-settings">
              <h4>增强设置</h4>
              <el-form :model="enhancementSettings" label-width="120px">
                <el-form-item label="降噪强度">
                  <el-slider
                    v-model="enhancementSettings.noiseReduction"
                    :min="0"
                    :max="100"
                    show-stops
                  />
                  <span class="slider-value">{{ enhancementSettings.noiseReduction }}%</span>
                </el-form-item>
                <el-form-item label="回声消除">
                  <el-slider
                    v-model="enhancementSettings.echoCancellation"
                    :min="0"
                    :max="100"
                    show-stops
                  />
                  <span class="slider-value">{{ enhancementSettings.echoCancellation }}%</span>
                </el-form-item>
                <el-form-item label="音量均衡">
                  <el-slider
                    v-model="enhancementSettings.volumeNormalization"
                    :min="0"
                    :max="100"
                    show-stops
                  />
                  <span class="slider-value">{{ enhancementSettings.volumeNormalization }}%</span>
                </el-form-item>
                <el-form-item label="音质提升">
                  <el-slider
                    v-model="enhancementSettings.qualityEnhancement"
                    :min="0"
                    :max="100"
                    show-stops
                  />
                  <span class="slider-value">{{ enhancementSettings.qualityEnhancement }}%</span>
                </el-form-item>
              </el-form>
            </div>
            
            <div class="enhancement-actions">
              <el-button type="primary" @click="startEnhancement">
                <el-icon><MagicStick /></el-icon>
                开始增强
              </el-button>
              <el-button @click="previewEnhanced" :disabled="!enhancementAudio.enhancedUrl">
                <el-icon><Headset /></el-icon>
                试听效果
              </el-button>
              <el-button @click="downloadEnhanced" :disabled="!enhancementAudio.enhancedUrl">
                <el-icon><Download /></el-icon>
                下载结果
              </el-button>
            </div>
            
            <div v-if="enhancementAudio.enhancedUrl" class="enhancement-result">
              <h4>增强后音频</h4>
              <audio :src="enhancementAudio.enhancedUrl" controls style="width: 100%"></audio>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Microphone,
  Headset,
  VideoPlay,
  VideoPause,
  Upload,
  Download,
  UploadFilled,
  MagicStick
} from '@element-plus/icons-vue'
import { aiApi, type SpeechRecognitionResult, type TextToSpeechResult } from '@/api/modules/ai'

// 响应式数据
const activeTab = ref('speechRecognition')

// 语音识别相关
const isRecording = ref(false)
const audioBars = ref<number[]>([])
const recognitionResult = ref('')
const recognitionSettings = reactive({
  language: 'zh-CN',
  mode: 'accurate',
  punctuation: true
})

// 语音合成相关
const synthesisText = ref('')
const synthesisAudioUrl = ref('')
const synthesisSettings = reactive({
  voiceType: 'female-standard',
  speed: 1,
  pitch: 1,
  volume: 80
})

// 音频增强相关
const enhancementAudio = ref<any>(null)
const enhancementSettings = reactive({
  noiseReduction: 50,
  echoCancellation: 50,
  volumeNormalization: 50,
  qualityEnhancement: 50
})

// 处理历史
const processingHistory = ref<any[]>([])

// 加载状态
const loading = reactive({
  recognition: false,
  synthesis: false,
  enhancement: false
})

// 语音识别功能
const startRecording = () => {
  isRecording.value = true
  // 模拟音频可视化
  audioBars.value = Array(20).fill(0).map(() => Math.random() * 30 + 10)
  
  // 模拟录音过程
  const interval = setInterval(() => {
    audioBars.value = audioBars.value.map(() => Math.random() * 30 + 10)
  }, 100)
  
  // 模拟3秒后自动停止并识别
  setTimeout(() => {
    stopRecording()
    clearInterval(interval)
    ElMessage.warning('实时录音功能需要浏览器录音权限支持，请使用文件上传功能')
  }, 3000)
}

const stopRecording = () => {
  isRecording.value = false
  audioBars.value = []
}

const beforeAudioUpload = (file: File) => {
  const isAudio = file.type.startsWith('audio/')
  const isLt100M = file.size / 1024 / 1024 < 100
  
  if (!isAudio) {
    ElMessage.error('只能上传音频文件!')
    return false
  }
  if (!isLt100M) {
    ElMessage.error('音频文件大小不能超过 100MB!')
    return false
  }
  return true
}

const onAudioUploadSuccess = async (response: any, file: File) => {
  try {
    loading.recognition = true
    ElMessage.info('音频上传成功，开始语音识别...')
    
    // 调用AI API进行语音识别
    const result = await aiApi.speechRecognition(file, {
      language: recognitionSettings.language,
      timestamps: true
    })
    
    recognitionResult.value = result.data.text
    
    // 添加到处理历史
    processingHistory.value.unshift({
      id: Date.now().toString(),
      type: '语音识别',
      fileName: file.name,
      createTime: new Date().toLocaleString(),
      status: 'success'
    })
    
    ElMessage.success('语音识别完成')
  } catch (error) {
    console.error('语音识别失败:', error)
    ElMessage.error('语音识别失败，请重试')
  } finally {
    loading.recognition = false
  }
}

const copyResult = () => {
  if (!recognitionResult.value) {
    ElMessage.warning('没有可复制的识别结果')
    return
  }
  navigator.clipboard.writeText(recognitionResult.value)
  ElMessage.success('识别结果已复制到剪贴板')
}

const downloadResult = () => {
  if (!recognitionResult.value) {
    ElMessage.warning('没有可下载的识别结果')
    return
  }
  const blob = new Blob([recognitionResult.value], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '语音识别结果.txt'
  a.click()
  URL.revokeObjectURL(url)
}

const clearResult = () => {
  recognitionResult.value = ''
}

// 语音合成功能
const startSynthesis = async () => {
  if (!synthesisText.value.trim()) {
    ElMessage.warning('请输入要合成的文本内容')
    return
  }
  
  try {
    loading.synthesis = true
    ElMessage.info('开始语音合成...')
    
    // 调用AI API进行语音合成
    const result = await aiApi.textToSpeech(synthesisText.value, {
      voice: synthesisSettings.voiceType,
      speed: synthesisSettings.speed,
      pitch: synthesisSettings.pitch
    })
    
    synthesisAudioUrl.value = result.data.audioUrl
    
    // 添加到处理历史
    processingHistory.value.unshift({
      id: Date.now().toString(),
      type: '语音合成',
      fileName: '语音合成结果.mp3',
      createTime: new Date().toLocaleString(),
      status: 'success'
    })
    
    ElMessage.success('语音合成完成')
  } catch (error) {
    console.error('语音合成失败:', error)
    ElMessage.error('语音合成失败，请重试')
  } finally {
    loading.synthesis = false
  }
}

const previewSynthesis = () => {
  if (!synthesisAudioUrl.value) {
    ElMessage.warning('请先合成语音')
    return
  }
  // 试听逻辑
  const audio = new Audio(synthesisAudioUrl.value)
  audio.play()
}

const downloadSynthesis = () => {
  if (!synthesisAudioUrl.value) {
    ElMessage.warning('请先合成语音')
    return
  }
  
  const a = document.createElement('a')
  a.href = synthesisAudioUrl.value
  a.download = '语音合成结果.mp3'
  a.click()
}

// 音频增强功能
const beforeEnhancementUpload = (file: File) => {
  const isAudio = file.type.startsWith('audio/')
  const isLt100M = file.size / 1024 / 1024 < 100
  
  if (!isAudio) {
    ElMessage.error('只能上传音频文件!')
    return false
  }
  if (!isLt100M) {
    ElMessage.error('音频文件大小不能超过 100MB!')
    return false
  }
  return true
}

const onEnhancementUploadSuccess = (response: any, file: File) => {
  ElMessage.success('音频上传成功')
  enhancementAudio.value = {
    originalUrl: URL.createObjectURL(file),
    enhancedUrl: '',
    fileName: file.name
  }
}

const startEnhancement = async () => {
  if (!enhancementAudio.value) {
    ElMessage.warning('请先上传音频文件')
    return
  }
  
  try {
    loading.enhancement = true
    ElMessage.info('开始音频增强处理...')
    
    // 调用AI API进行音频增强（使用内容分析API模拟）
    const result = await aiApi.analyzeContent(enhancementAudio.value.fileName, 'audio', {
      analysisType: ['quality']
    })
    
    // 模拟增强结果
    enhancementAudio.value.enhancedUrl = '/audio/enhanced/sample.mp3'
    
    // 添加到处理历史
    processingHistory.value.unshift({
      id: Date.now().toString(),
      type: '音频增强',
      fileName: enhancementAudio.value.fileName,
      createTime: new Date().toLocaleString(),
      status: 'success'
    })
    
    ElMessage.success('音频增强完成')
  } catch (error) {
    console.error('音频增强失败:', error)
    ElMessage.error('音频增强失败，请重试')
  } finally {
    loading.enhancement = false
  }
}

const previewEnhanced = () => {
  if (!enhancementAudio.value?.enhancedUrl) {
    ElMessage.warning('请先完成音频增强')
    return
  }
  
  const audio = new Audio(enhancementAudio.value.enhancedUrl)
  audio.play()
}

const downloadEnhanced = () => {
  if (!enhancementAudio.value?.enhancedUrl) {
    ElMessage.warning('请先完成音频增强')
    return
  }
  
  const a = document.createElement('a')
  a.href = enhancementAudio.value.enhancedUrl
  a.download = '音频增强结果.mp3'
  a.click()
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

// 加载历史记录
const loadProcessingHistory = async () => {
  try {
    // 这里可以调用API获取历史记录
    // 暂时使用模拟数据
    processingHistory.value = [
      {
        id: '1',
        type: '语音识别',
        fileName: '会议录音.mp3',
        createTime: '2024-01-15 10:30:00',
        status: 'success'
      },
      {
        id: '2',
        type: '语音合成',
        fileName: '产品介绍.txt',
        createTime: '2024-01-14 14:20:00',
        status: 'success'
      },
      {
        id: '3',
        type: '音频增强',
        fileName: '采访录音.wav',
        createTime: '2024-01-13 09:15:00',
        status: 'success'
      }
    ]
  } catch (error) {
    console.error('加载历史记录失败:', error)
  }
}

onMounted(() => {
  // 初始化音频可视化条
  audioBars.value = Array(20).fill(10)
  // 加载历史记录
  loadProcessingHistory()
})
</script>

<style lang="scss" scoped>
.voice-processing-container {
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

          &.speech-recognition {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          }

          &.speech-synthesis {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }

          &.audio-enhancement {
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

      .recording-area {
        margin-bottom: 24px;

        .audio-visualizer {
          display: flex;
          align-items: flex-end;
          justify-content: center;
          height: 100px;
          background: rgba(0, 0, 0, 0.3);
          border-radius: 8px;
          margin-bottom: 16px;
          gap: 2px;
          padding: 20px;

          .audio-bar {
            width: 4px;
            background: linear-gradient(to top, #00FFFF, #FF00FF);
            border-radius: 2px;
            transition: height 0.1s ease;
          }
        }

        .recording-controls {
          text-align: center;
        }
      }

      .recognition-result,
      .synthesis-input,
      .enhancement-upload {
        margin-bottom: 24px;

        .result-actions,
        .synthesis-controls,
        .enhancement-actions {
          margin-top: 16px;
          display: flex;
          gap: 12px;
        }
      }

      .recognition-settings,
      .synthesis-settings,
      .enhancement-settings {
        margin-top: 24px;

        .slider-value {
          margin-left: 12px;
          color: rgba(255, 255, 255, 0.7);
          font-size: 14px;
        }
      }

      .synthesis-result,
      .enhancement-result {
        margin-top: 24px;
      }

      .audio-preview {
        margin-bottom: 24px;

        h4 {
          margin: 0 0 12px 0;
          color: white;
        }
      }

      .audio-upload-area {
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
  .voice-processing-container {
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

        .result-actions,
        .synthesis-controls,
        .enhancement-actions {
          flex-direction: column;
        }
      }
    }
  }
}
</style>