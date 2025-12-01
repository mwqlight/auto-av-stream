import { request } from '@/api/index'

// 登录请求参数
export interface LoginParams {
  username: string
  password: string
  captcha?: string
  rememberMe?: boolean
}

// 登录响应数据
export interface LoginResponse {
  token: string
  refreshToken: string
  userInfo: {
    id: string
    username: string
    nickname: string
    avatar: string
    email: string
    phone: string
    roles: string[]
    permissions: string[]
    lastLoginTime: string
  }
}

// 注册请求参数
export interface RegisterParams {
  username: string
  password: string
  confirmPassword: string
  email: string
  phone?: string
  nickname?: string
}

// 用户信息更新参数
export interface UpdateUserInfoParams {
  nickname?: string
  avatar?: string
  email?: string
  phone?: string
}

// 密码修改参数
export interface ChangePasswordParams {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

// 认证相关API
export const authApi = {
  // 用户登录
  login: (params: LoginParams) => {
    return request.post<LoginResponse>('/api/v1/auth/login', params)
  },

  // 用户注册
  register: (params: RegisterParams) => {
    return request.post('/api/v1/auth/register', params)
  },

  // 获取验证码
  getCaptcha: () => {
    return request.get('/api/v1/auth/captcha')
  },

  // 刷新token
  refreshToken: (refreshToken: string) => {
    return request.post('/api/v1/auth/refresh', { refreshToken })
  },

  // 退出登录
  logout: () => {
    return request.post('/api/v1/auth/logout')
  },

  // 获取当前用户信息
  getCurrentUser: () => {
    return request.get('/api/v1/auth/userinfo')
  },

  // 更新用户信息
  updateUserInfo: (params: UpdateUserInfoParams) => {
    return request.put('/api/v1/auth/userinfo', params)
  },

  // 修改密码
  changePassword: (params: ChangePasswordParams) => {
    return request.put('/api/v1/auth/password', params)
  },

  // 忘记密码
  forgotPassword: (email: string) => {
    return request.post('/api/v1/auth/forgot-password', { email })
  },

  // 重置密码
  resetPassword: (token: string, newPassword: string) => {
    return request.post('/api/v1/auth/reset-password', { token, newPassword })
  },

  // 验证token有效性
  validateToken: (token: string) => {
    return request.post('/api/v1/auth/validate', { token })
  }
}