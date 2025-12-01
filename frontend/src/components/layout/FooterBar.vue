<template>
  <el-footer class="tech-footer">
    <div class="footer-container">
      <!-- 左侧信息 -->
      <div class="footer-left">
        <span class="copyright">
          © 2024 AV Stream Space. All rights reserved.
        </span>
        <span class="version">
          Version 1.0.0
        </span>
      </div>

      <!-- 右侧状态信息 -->
      <div class="footer-right">
        <div class="status-item">
          <el-icon class="status-icon online">
            <CircleCheck />
          </el-icon>
          <span>系统正常</span>
        </div>
        <div class="status-item">
          <el-icon class="status-icon">
            <Clock />
          </el-icon>
          <span>{{ currentTime }}</span>
        </div>
        <div class="status-item">
          <el-icon class="status-icon">
            <User />
          </el-icon>
          <span>在线用户: {{ onlineUsers }}</span>
        </div>
      </div>
    </div>
  </el-footer>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { CircleCheck, Clock, User } from '@element-plus/icons-vue'

// 当前时间
const currentTime = ref('')
const onlineUsers = ref(0)

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 模拟在线用户数
const updateOnlineUsers = () => {
  onlineUsers.value = Math.floor(Math.random() * 100) + 50
}

let timeInterval: number
let userInterval: number

onMounted(() => {
  updateTime()
  updateOnlineUsers()
  
  timeInterval = setInterval(updateTime, 1000)
  userInterval = setInterval(updateOnlineUsers, 30000)
})

onUnmounted(() => {
  clearInterval(timeInterval)
  clearInterval(userInterval)
})
</script>

<style lang="scss" scoped>
.tech-footer {
  height: 48px;
  padding: 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;

  .footer-container {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    max-width: 100%;
  }

  .footer-left {
    display: flex;
    align-items: center;
    gap: 24px;

    .copyright {
      font-weight: 500;
    }

    .version {
      background: rgba(0, 255, 255, 0.1);
      padding: 2px 8px;
      border-radius: 4px;
      border: 1px solid rgba(0, 255, 255, 0.3);
      color: #00FFFF;
    }
  }

  .footer-right {
    display: flex;
    align-items: center;
    gap: 24px;

    .status-item {
      display: flex;
      align-items: center;
      gap: 6px;

      .status-icon {
        font-size: 14px;

        &.online {
          color: #00FF00;
        }
      }

      span {
        font-weight: 500;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .tech-footer {
    .footer-container {
      padding: 0 16px;
      flex-direction: column;
      justify-content: center;
      gap: 8px;
    }

    .footer-left {
      gap: 12px;
    }

    .footer-right {
      gap: 12px;

      .status-item {
        span {
          display: none;
        }
      }
    }
  }
}
</style>