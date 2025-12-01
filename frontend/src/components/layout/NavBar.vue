<template>
  <el-header class="tech-navbar">
    <div class="navbar-container">
      <!-- Logo和标题 -->
      <div class="navbar-brand">
        <div class="logo">
          <el-icon size="28">
            <VideoCamera />
          </el-icon>
        </div>
        <h1 class="app-title">AV Stream Space</h1>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        class="navbar-menu"
        mode="horizontal"
        :ellipsis="false"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>全景驾驶舱</span>
        </el-menu-item>
        <el-menu-item index="/media">
          <el-icon><VideoPlay /></el-icon>
          <span>媒体管理</span>
        </el-menu-item>
        <el-menu-item index="/live">
          <el-icon><Promotion /></el-icon>
          <span>直播管理</span>
        </el-menu-item>
        <el-menu-item index="/ai-service">
          <el-icon><Magic /></el-icon>
          <span>AI服务</span>
        </el-menu-item>
        <el-menu-item index="/monitor">
          <el-icon><Monitor /></el-icon>
          <span>系统监控</span>
        </el-menu-item>
      </el-menu>

      <!-- 右侧功能区 -->
      <div class="navbar-actions">
        <!-- 主题切换 -->
        <el-tooltip content="切换主题" placement="bottom">
          <el-button
            circle
            :icon="Sunny"
            @click="toggleTheme"
            class="theme-toggle"
          />
        </el-tooltip>

        <!-- 用户信息 -->
        <el-dropdown @command="handleUserCommand">
          <span class="user-info">
            <el-avatar :size="32" :src="userAvatar" />
            <span class="username">{{ username }}</span>
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人资料
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>
                系统设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  VideoCamera,
  DataBoard,
  VideoPlay,
  Promotion,
  Magic,
  Monitor,
  Sunny,
  User,
  Setting,
  SwitchButton,
  ArrowDown
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 用户信息
const username = ref('管理员')
const userAvatar = ref('')

// 当前激活菜单
const activeMenu = computed(() => route.path)

// 菜单选择处理
const handleMenuSelect = (index: string) => {
  router.push(index)
}

// 主题切换
const toggleTheme = () => {
  const html = document.documentElement
  const isDark = html.classList.contains('dark')
  
  if (isDark) {
    html.classList.remove('dark')
    ElMessage.success('已切换为浅色主题')
  } else {
    html.classList.add('dark')
    ElMessage.success('已切换为深色主题')
  }
}

// 用户操作处理
const handleUserCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      await handleLogout()
      break
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 跳转到登录页
    router.push('/login')
    ElMessage.success('退出登录成功')
  } catch {
    // 用户取消操作
  }
}
</script>

<style lang="scss" scoped>
.tech-navbar {
  height: 64px;
  padding: 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 1000;

  .navbar-container {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    max-width: 100%;
  }

  .navbar-brand {
    display: flex;
    align-items: center;
    gap: 12px;

    .logo {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 8px;
      color: white;
    }

    .app-title {
      margin: 0;
      font-size: 20px;
      font-weight: 700;
      background: linear-gradient(135deg, #00FFFF 0%, #FF00FF 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .navbar-menu {
    flex: 1;
    justify-content: center;
    background: transparent;
    border-bottom: none;

    :deep(.el-menu-item) {
      color: rgba(255, 255, 255, 0.8);
      font-weight: 500;
      margin: 0 8px;
      border-radius: 8px;
      transition: all 0.3s ease;

      &:hover {
        color: #00FFFF;
        background: rgba(0, 255, 255, 0.1);
      }

      &.is-active {
        color: #00FFFF;
        background: rgba(0, 255, 255, 0.2);
      }
    }
  }

  .navbar-actions {
    display: flex;
    align-items: center;
    gap: 16px;

    .theme-toggle {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: rgba(255, 255, 255, 0.8);

      &:hover {
        background: rgba(255, 255, 255, 0.2);
        border-color: rgba(255, 255, 255, 0.3);
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s ease;
      color: rgba(255, 255, 255, 0.8);

      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }

      .username {
        font-weight: 500;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .tech-navbar {
    .navbar-container {
      padding: 0 16px;
    }

    .app-title {
      display: none;
    }

    .navbar-menu {
      :deep(.el-menu-item) {
        margin: 0 4px;
        padding: 0 12px;

        span {
          font-size: 12px;
        }
      }
    }

    .user-info .username {
      display: none;
    }
  }
}
</style>