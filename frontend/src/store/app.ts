import { defineStore } from 'pinia'
import type { ThemeType, LayoutType } from '@/types/app'

export const useAppStore = defineStore('app', {
  state: () => ({
    // 主题相关
    theme: 'dark' as ThemeType,
    primaryColor: '#409EFF',
    
    // 布局相关
    layout: 'default' as LayoutType,
    sidebarCollapsed: false,
    navbarFixed: true,
    footerVisible: true,
    
    // 全局状态
    loading: false,
    loadingText: '加载中...',
    
    // 系统设置
    language: 'zh-CN',
    pageSize: 20,
    
    // 错误处理
    errorLogs: [] as string[],
    
    // 系统信息
    systemInfo: {
      version: import.meta.env.VITE_APP_VERSION,
      buildTime: new Date().toISOString()
    }
  }),

  getters: {
    isDarkTheme: (state) => state.theme === 'dark',
    isLightTheme: (state) => state.theme === 'light',
    sidebarWidth: (state) => state.sidebarCollapsed ? '64px' : '240px',
    currentLanguage: (state) => state.language
  },

  actions: {
    // 切换主题
    toggleTheme() {
      this.theme = this.theme === 'dark' ? 'light' : 'dark'
      this.applyTheme()
    },

    // 设置主题
    setTheme(theme: ThemeType) {
      this.theme = theme
      this.applyTheme()
    },

    // 应用主题
    applyTheme() {
      const html = document.documentElement
      if (this.theme === 'dark') {
        html.classList.add('dark')
      } else {
        html.classList.remove('dark')
      }
    },

    // 切换侧边栏
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },

    // 设置侧边栏状态
    setSidebarCollapsed(collapsed: boolean) {
      this.sidebarCollapsed = collapsed
    },

    // 设置布局
    setLayout(layout: LayoutType) {
      this.layout = layout
    },

    // 设置语言
    setLanguage(language: string) {
      this.language = language
    },

    // 设置加载状态
    setLoading(loading: boolean, text?: string) {
      this.loading = loading
      if (text) {
        this.loadingText = text
      }
    },

    // 添加错误日志
    addErrorLog(error: string) {
      this.errorLogs.push(`${new Date().toLocaleString()}: ${error}`)
      // 限制日志数量
      if (this.errorLogs.length > 100) {
        this.errorLogs.shift()
      }
    },

    // 清除错误日志
    clearErrorLogs() {
      this.errorLogs = []
    },

    // 初始化应用设置
    initAppSettings() {
      // 从localStorage恢复设置
      const savedTheme = localStorage.getItem('app-theme')
      const savedLayout = localStorage.getItem('app-layout')
      const savedLanguage = localStorage.getItem('app-language')
      
      if (savedTheme) this.theme = savedTheme as ThemeType
      if (savedLayout) this.layout = savedLayout as LayoutType
      if (savedLanguage) this.language = savedLanguage
      
      this.applyTheme()
    },

    // 保存设置到localStorage
    saveSettings() {
      localStorage.setItem('app-theme', this.theme)
      localStorage.setItem('app-layout', this.layout)
      localStorage.setItem('app-language', this.language)
    }
  },

  persist: {
    paths: ['theme', 'layout', 'language', 'sidebarCollapsed', 'navbarFixed', 'footerVisible', 'pageSize']
  }
})