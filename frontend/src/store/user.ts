import { defineStore } from 'pinia'
import type { UserInfo, LoginParams } from '@/types/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null as UserInfo | null,
    permissions: [] as string[],
    roles: [] as string[]
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userName: (state) => state.userInfo?.username || '',
    userId: (state) => state.userInfo?.id || 0,
    avatar: (state) => state.userInfo?.avatar || ''
  },

  actions: {
    // 用户登录
    async login(loginParams: LoginParams) {
      try {
        // 调用登录API
        const response = await $api.auth.login(loginParams)
        
        this.token = response.data.token
        this.userInfo = response.data.userInfo
        this.permissions = response.data.permissions || []
        this.roles = response.data.roles || []
        
        // 存储token到localStorage
        localStorage.setItem('token', this.token)
        
        return response
      } catch (error) {
        throw error
      }
    },

    // 用户登出
    async logout() {
      try {
        await $api.auth.logout()
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        this.reset()
      }
    },

    // 获取用户信息
    async getUserInfo() {
      try {
        const response = await $api.auth.getUserInfo()
        this.userInfo = response.data
        this.permissions = response.data.permissions || []
        this.roles = response.data.roles || []
        return response
      } catch (error) {
        throw error
      }
    },

    // 更新用户信息
    async updateUserInfo(userInfo: Partial<UserInfo>) {
      try {
        const response = await $api.auth.updateUserInfo(userInfo)
        this.userInfo = { ...this.userInfo, ...response.data }
        return response
      } catch (error) {
        throw error
      }
    },

    // 检查权限
    hasPermission(permission: string): boolean {
      return this.permissions.includes(permission)
    },

    // 检查角色
    hasRole(role: string): boolean {
      return this.roles.includes(role)
    },

    // 重置状态
    reset() {
      this.token = ''
      this.userInfo = null
      this.permissions = []
      this.roles = []
      localStorage.removeItem('token')
    }
  },

  persist: {
    paths: ['token', 'userInfo', 'permissions', 'roles']
  }
})