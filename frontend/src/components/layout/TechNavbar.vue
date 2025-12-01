<template>
  <nav class="tech-navbar" :class="{ 'navbar-fixed': fixed, 'navbar-glass': glass }" :style="navbarStyle">
    <!-- 左侧Logo和品牌 -->
    <div class="navbar-left">
      <div class="navbar-logo" @click="$emit('logo-click')">
        <div class="logo-icon">
          <div class="logo-pulse"></div>
          <div class="logo-core"></div>
        </div>
        <span class="logo-text">{{ title }}</span>
      </div>
    </div>

    <!-- 中间导航菜单 -->
    <div class="navbar-center">
      <ul class="nav-menu">
        <li
          v-for="item in menuItems"
          :key="item.path"
          class="nav-item"
          :class="{ 
            'nav-item--active': isActive(item.path),
            'nav-item--disabled': item.disabled 
          }"
          @click="!item.disabled && handleNavClick(item)"
        >
          <div class="nav-item-content">
            <i v-if="item.icon" class="nav-icon" :class="item.icon"></i>
            <span class="nav-text">{{ item.title }}</span>
            <div v-if="item.badge" class="nav-badge" :class="`badge--${item.badge.type}`">
              {{ item.badge.content }}
            </div>
          </div>
          <div class="nav-indicator"></div>
        </li>
      </ul>
    </div>

    <!-- 右侧操作区域 -->
    <div class="navbar-right">
      <!-- 搜索框 -->
      <div v-if="showSearch" class="search-container">
        <div class="search-box" :class="{ 'search-box--focused': searchFocused }">
          <i class="search-icon icon-search"></i>
          <input
            v-model="searchQuery"
            type="text"
            class="search-input"
            :placeholder="searchPlaceholder"
            @focus="searchFocused = true"
            @blur="searchFocused = false"
            @keyup.enter="$emit('search', searchQuery)"
          />
          <button
            v-if="searchQuery"
            class="search-clear"
            @click="searchQuery = ''; $emit('search', '')"
          >
            <i class="icon-close"></i>
          </button>
        </div>
      </div>

      <!-- 主题切换 -->
      <div v-if="showThemeToggle" class="theme-toggle">
        <button
          class="theme-btn"
          :class="{ 'theme-btn--active': theme === 'dark' }"
          @click="toggleTheme"
        >
          <i class="theme-icon" :class="theme === 'dark' ? 'icon-moon' : 'icon-sun'"></i>
        </button>
      </div>

      <!-- 通知 -->
      <div v-if="showNotifications" class="notifications">
        <div class="notifications-dropdown">
          <button
            class="notifications-btn"
            :class="{ 'notifications-btn--active': showNotificationsDropdown }"
            @click="toggleNotifications"
          >
            <i class="icon-bell"></i>
            <span v-if="unreadCount > 0" class="notification-badge">{{ unreadCount }}</span>
          </button>
          
          <transition name="dropdown">
            <div v-show="showNotificationsDropdown" class="notifications-panel">
              <div class="notifications-header">
                <h3>通知</h3>
                <button class="mark-all-read" @click="$emit('mark-all-read')">全部已读</button>
              </div>
              <div class="notifications-list">
                <div
                  v-for="notification in notifications"
                  :key="notification.id"
                  class="notification-item"
                  :class="{ 'notification-item--unread': !notification.read }"
                  @click="$emit('notification-click', notification)"
                >
                  <div class="notification-icon">
                    <i :class="notification.icon"></i>
                  </div>
                  <div class="notification-content">
                    <div class="notification-title">{{ notification.title }}</div>
                    <div class="notification-message">{{ notification.message }}</div>
                    <div class="notification-time">{{ notification.time }}</div>
                  </div>
                </div>
              </div>
              <div v-if="notifications.length === 0" class="notifications-empty">
                暂无通知
              </div>
            </div>
          </transition>
        </div>
      </div>

      <!-- 用户菜单 -->
      <div class="user-menu">
        <div class="user-dropdown">
          <button
            class="user-btn"
            :class="{ 'user-btn--active': showUserDropdown }"
            @click="toggleUserMenu"
          >
            <div class="user-avatar">
              <img v-if="userInfo.avatar" :src="userInfo.avatar" :alt="userInfo.name" />
              <div v-else class="avatar-placeholder">
                {{ userInfo.name?.charAt(0) || 'U' }}
              </div>
            </div>
            <span class="user-name">{{ userInfo.name || '用户' }}</span>
            <i class="user-arrow" :class="{ 'user-arrow--up': showUserDropdown }"></i>
          </button>
          
          <transition name="dropdown">
            <div v-show="showUserDropdown" class="user-panel">
              <div class="user-info">
                <div class="user-avatar-large">
                  <img v-if="userInfo.avatar" :src="userInfo.avatar" :alt="userInfo.name" />
                  <div v-else class="avatar-placeholder-large">
                    {{ userInfo.name?.charAt(0) || 'U' }}
                  </div>
                </div>
                <div class="user-details">
                  <div class="user-name-large">{{ userInfo.name || '用户' }}</div>
                  <div class="user-role">{{ userInfo.role || '普通用户' }}</div>
                </div>
              </div>
              
              <div class="user-actions">
                <button class="user-action" @click="$emit('profile-click')">
                  <i class="icon-user"></i>
                  <span>个人资料</span>
                </button>
                <button class="user-action" @click="$emit('settings-click')">
                  <i class="icon-settings"></i>
                  <span>设置</span>
                </button>
                <div class="user-divider"></div>
                <button class="user-action user-action--logout" @click="$emit('logout-click')">
                  <i class="icon-logout"></i>
                  <span>退出登录</span>
                </button>
              </div>
            </div>
          </transition>
        </div>
      </div>
    </div>

    <!-- 移动端菜单按钮 -->
    <div class="navbar-mobile-toggle" @click="$emit('mobile-toggle')">
      <div class="hamburger" :class="{ 'hamburger--active': mobileMenuActive }">
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface MenuItem {
  path: string
  title: string
  icon?: string
  disabled?: boolean
  badge?: {
    type: 'primary' | 'success' | 'warning' | 'danger' | 'info'
    content: string | number
  }
}

interface UserInfo {
  name?: string
  avatar?: string
  role?: string
}

interface Notification {
  id: string
  title: string
  message: string
  icon: string
  time: string
  read: boolean
}

interface Props {
  title?: string
  fixed?: boolean
  glass?: boolean
  menuItems?: MenuItem[]
  userInfo?: UserInfo
  showSearch?: boolean
  showThemeToggle?: boolean
  showNotifications?: boolean
  searchPlaceholder?: string
  theme?: 'light' | 'dark'
  mobileMenuActive?: boolean
}

interface Emits {
  (e: 'logo-click'): void
  (e: 'nav-click', item: MenuItem): void
  (e: 'search', query: string): void
  (e: 'theme-change', theme: 'light' | 'dark'): void
  (e: 'notification-click', notification: Notification): void
  (e: 'mark-all-read'): void
  (e: 'profile-click'): void
  (e: 'settings-click'): void
  (e: 'logout-click'): void
  (e: 'mobile-toggle'): void
}

const props = withDefaults(defineProps<Props>(), {
  title: 'AV Stream Platform',
  fixed: true,
  glass: true,
  menuItems: () => [
    { path: '/dashboard', title: '仪表盘', icon: 'icon-dashboard' },
    { path: '/media', title: '媒体管理', icon: 'icon-media' },
    { path: '/live', title: '直播管理', icon: 'icon-live' },
    { path: '/ai', title: 'AI服务', icon: 'icon-ai' },
    { path: '/monitor', title: '系统监控', icon: 'icon-monitor' }
  ],
  userInfo: () => ({}),
  showSearch: true,
  showThemeToggle: true,
  showNotifications: true,
  searchPlaceholder: '搜索...',
  theme: 'dark',
  mobileMenuActive: false
})

const emit = defineEmits<Emits>()

const route = useRoute()
const router = useRouter()

const searchQuery = ref('')
const searchFocused = ref(false)
const showNotificationsDropdown = ref(false)
const showUserDropdown = ref(false)

// 模拟通知数据
const notifications = ref<Notification[]>([
  {
    id: '1',
    title: '系统更新',
    message: '系统将在今晚进行维护更新',
    icon: 'icon-info',
    time: '5分钟前',
    read: false
  },
  {
    id: '2',
    title: '直播开始',
    message: '您的直播已开始',
    icon: 'icon-live',
    time: '10分钟前',
    read: true
  }
])

const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

const navbarStyle = computed(() => ({
  '--navbar-height': '60px',
  '--navbar-bg': props.glass ? 'rgba(15, 20, 25, 0.8)' : 'var(--bg-color-light)',
  '--navbar-blur': props.glass ? 'blur(10px)' : 'none'
}))

const isActive = (path: string) => {
  return route.path.startsWith(path)
}

const handleNavClick = (item: MenuItem) => {
  emit('nav-click', item)
  router.push(item.path)
}

const toggleTheme = () => {
  const newTheme = props.theme === 'dark' ? 'light' : 'dark'
  emit('theme-change', newTheme)
}

const toggleNotifications = () => {
  showNotificationsDropdown.value = !showNotificationsDropdown.value
  showUserDropdown.value = false
}

const toggleUserMenu = () => {
  showUserDropdown.value = !showUserDropdown.value
  showNotificationsDropdown.value = false
}

// 点击外部关闭下拉菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (!target.closest('.notifications-dropdown')) {
    showNotificationsDropdown.value = false
  }
  if (!target.closest('.user-dropdown')) {
    showUserDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped lang="scss">
.tech-navbar {
  --navbar-height: 60px;
  --navbar-bg: rgba(15, 20, 25, 0.8);
  --navbar-blur: blur(10px);
  
  height: var(--navbar-height);
  background: var(--navbar-bg);
  backdrop-filter: var(--navbar-blur);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-lg);
  position: relative;
  z-index: 1000;
  transition: all var(--transition-normal);
  
  &.navbar-fixed {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
  }
  
  &.navbar-glass {
    background: rgba(15, 20, 25, 0.8);
    backdrop-filter: blur(10px);
  }
}

.navbar-left {
  display: flex;
  align-items: center;
}

.navbar-logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: var(--transition-fast);
  
  &:hover {
    opacity: 0.8;
  }
}

.logo-icon {
  position: relative;
  width: 32px;
  height: 32px;
  margin-right: var(--spacing-sm);
}

.logo-pulse {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: 2px solid var(--primary-color);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.logo-core {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 16px;
  height: 16px;
  background: var(--gradient-primary);
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.navbar-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu {
  display: flex;
  list-style: none;
  gap: var(--spacing-md);
}

.nav-item {
  position: relative;
  cursor: pointer;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-base);
  transition: var(--transition-fast);
  
  &:hover:not(.nav-item--disabled) {
    background: rgba(255, 255, 255, 0.05);
  }
  
  &.nav-item--active {
    color: var(--primary-color);
    
    .nav-indicator {
      opacity: 1;
      transform: scaleX(1);
    }
  }
  
  &.nav-item--disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.nav-item-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.nav-icon {
  font-size: 16px;
}

.nav-text {
  font-weight: 500;
}

.nav-badge {
  padding: 2px 6px;
  border-radius: var(--border-radius-round);
  font-size: 12px;
  font-weight: 600;
}

.nav-indicator {
  position: absolute;
  bottom: -1px;
  left: 50%;
  width: 20px;
  height: 2px;
  background: var(--gradient-primary);
  border-radius: 1px;
  transform: translateX(-50%) scaleX(0);
  opacity: 0;
  transition: var(--transition-fast);
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.search-container {
  .search-box {
    position: relative;
    display: flex;
    align-items: center;
    background: var(--bg-color-lighter);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-base);
    padding: var(--spacing-sm) var(--spacing-md);
    transition: var(--transition-fast);
    
    &.search-box--focused {
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
  }
  
  .search-icon {
    color: var(--text-color-light);
    margin-right: var(--spacing-sm);
  }
  
  .search-input {
    background: transparent;
    border: none;
    outline: none;
    color: var(--text-color);
    width: 200px;
    
    &::placeholder {
      color: var(--text-color-light);
    }
  }
  
  .search-clear {
    background: none;
    border: none;
    color: var(--text-color-light);
    cursor: pointer;
    padding: 2px;
    
    &:hover {
      color: var(--text-color);
    }
  }
}

.theme-toggle {
  .theme-btn {
    background: var(--bg-color-lighter);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-base);
    padding: var(--spacing-sm);
    cursor: pointer;
    transition: var(--transition-fast);
    
    &:hover {
      border-color: var(--primary-color);
    }
    
    &.theme-btn--active {
      background: var(--primary-color);
      color: white;
    }
  }
}

.notifications {
  position: relative;
  
  .notifications-btn {
    position: relative;
    background: var(--bg-color-lighter);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-base);
    padding: var(--spacing-sm);
    cursor: pointer;
    transition: var(--transition-fast);
    
    &:hover {
      border-color: var(--primary-color);
    }
    
    &.notifications-btn--active {
      border-color: var(--primary-color);
    }
  }
  
  .notification-badge {
    position: absolute;
    top: -4px;
    right: -4px;
    background: var(--danger-color);
    color: white;
    border-radius: 50%;
    width: 16px;
    height: 16px;
    font-size: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .notifications-panel {
    position: absolute;
    top: 100%;
    right: 0;
    width: 320px;
    background: var(--bg-color-light);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-dark);
    margin-top: var(--spacing-sm);
    z-index: 1001;
  }
  
  .notifications-header {
    padding: var(--spacing-md);
    border-bottom: 1px solid var(--border-color);
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .notifications-list {
    max-height: 400px;
    overflow-y: auto;
  }
  
  .notification-item {
    padding: var(--spacing-md);
    border-bottom: 1px solid var(--border-color);
    cursor: pointer;
    transition: var(--transition-fast);
    
    &:hover {
      background: var(--bg-color-lighter);
    }
    
    &.notification-item--unread {
      background: rgba(64, 158, 255, 0.05);
    }
  }
  
  .notifications-empty {
    padding: var(--spacing-xl);
    text-align: center;
    color: var(--text-color-light);
  }
}

.user-menu {
  position: relative;
  
  .user-btn {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    background: var(--bg-color-lighter);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-base);
    padding: var(--spacing-sm);
    cursor: pointer;
    transition: var(--transition-fast);
    
    &:hover {
      border-color: var(--primary-color);
    }
    
    &.user-btn--active {
      border-color: var(--primary-color);
    }
  }
  
  .user-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .avatar-placeholder {
      width: 100%;
      height: 100%;
      background: var(--gradient-primary);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-weight: 600;
    }
  }
  
  .user-panel {
    position: absolute;
    top: 100%;
    right: 0;
    width: 280px;
    background: var(--bg-color-light);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-dark);
    margin-top: var(--spacing-sm);
    z-index: 1001;
  }
  
  .user-info {
    padding: var(--spacing-md);
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    border-bottom: 1px solid var(--border-color);
  }
  
  .user-avatar-large {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    overflow: hidden;
    
    .avatar-placeholder-large {
      width: 100%;
      height: 100%;
      background: var(--gradient-primary);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  .user-actions {
    padding: var(--spacing-sm);
  }
  
  .user-action {
    width: 100%;
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    padding: var(--spacing-sm) var(--spacing-md);
    background: none;
    border: none;
    color: var(--text-color);
    cursor: pointer;
    border-radius: var(--border-radius-base);
    transition: var(--transition-fast);
    
    &:hover {
      background: var(--bg-color-lighter);
    }
    
    &.user-action--logout {
      color: var(--danger-color);
      
      &:hover {
        background: rgba(245, 108, 108, 0.1);
      }
    }
  }
  
  .user-divider {
    height: 1px;
    background: var(--border-color);
    margin: var(--spacing-sm) 0;
  }
}

.navbar-mobile-toggle {
  display: none;
  
  .hamburger {
    width: 24px;
    height: 18px;
    position: relative;
    cursor: pointer;
    
    span {
      display: block;
      height: 2px;
      background: var(--text-color);
      border-radius: 1px;
      transition: var(--transition-fast);
      
      &:nth-child(1) {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
      }
      
      &:nth-child(2) {
        position: absolute;
        top: 8px;
        left: 0;
        right: 0;
      }
      
      &:nth-child(3) {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
      }
    }
    
    &.hamburger--active {
      span:nth-child(1) {
        transform: rotate(45deg);
        top: 8px;
      }
      
      span:nth-child(2) {
        opacity: 0;
      }
      
      span:nth-child(3) {
        transform: rotate(-45deg);
        bottom: 8px;
      }
    }
  }
}

// 动画
@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.7;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all var(--transition-fast);
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

// 响应式设计
@media (max-width: 1024px) {
  .navbar-center {
    display: none;
  }
  
  .search-container {
    .search-input {
      width: 150px;
    }
  }
}

@media (max-width: 768px) {
  .tech-navbar {
    padding: 0 var(--spacing-md);
  }
  
  .search-container {
    display: none;
  }
  
  .notifications,
  .theme-toggle {
    display: none;
  }
  
  .navbar-mobile-toggle {
    display: block;
  }
}
</style>