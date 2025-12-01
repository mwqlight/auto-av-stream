import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login/index.vue'),
    meta: {
      title: '登录',
      hideNavBar: true,
      hideFooter: true
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard/index.vue'),
    meta: {
      title: '全景驾驶舱',
      requiresAuth: true
    }
  },
  {
    path: '/media',
    name: 'Media',
    component: () => import('@/views/Media/index.vue'),
    meta: {
      title: '媒体管理',
      requiresAuth: true
    }
  },
  {
    path: '/live',
    name: 'Live',
    component: () => import('@/views/Live/index.vue'),
    meta: {
      title: '直播管理',
      requiresAuth: true
    }
  },
  {
    path: '/ai-service',
    name: 'AIService',
    component: () => import('@/views/AIService/index.vue'),
    meta: {
      title: 'AI智能服务',
      requiresAuth: true
    }
  },
  {
    path: '/ai-service/voice',
    name: 'VoiceProcessing',
    component: () => import('@/views/AIService/VoiceProcessing/index.vue'),
    meta: {
      title: '语音处理',
      requiresAuth: true
    }
  },
  {
    path: '/ai-service/image',
    name: 'ImageProcessing',
    component: () => import('@/views/AIService/ImageProcessing/index.vue'),
    meta: {
      title: '图像处理',
      requiresAuth: true
    }
  },
  {
    path: '/monitor',
    name: 'Monitor',
    component: () => import('@/views/monitor/Monitor.vue'),
    meta: {
      title: '系统监控',
      requiresAuth: true
    }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/settings/Settings.vue'),
    meta: {
      title: '系统设置',
      requiresAuth: true
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面未找到',
      hideNavBar: true,
      hideFooter: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - ${import.meta.env.VITE_APP_TITLE}`
  }
  
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/login')
      return
    }
  }
  
  next()
})

export default router