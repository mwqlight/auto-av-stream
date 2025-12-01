// 主题类型
export type ThemeType = 'light' | 'dark' | 'auto'

// 布局类型
export type LayoutType = 'default' | 'sidebar' | 'top' | 'mix'

// 路由元信息
export interface RouteMeta {
  title: string
  requiresAuth?: boolean
  keepAlive?: boolean
  icon?: string
  hidden?: boolean
  roles?: string[]
  permissions?: string[]
  breadcrumb?: boolean
}

// 菜单项
export interface MenuItem {
  key: string
  label: string
  icon?: string
  path?: string
  children?: MenuItem[]
  type?: 'group' | 'divider'
  hidden?: boolean
  roles?: string[]
  permissions?: string[]
}

// 分页参数
export interface PaginationParams {
  page: number
  size: number
  total?: number
}

// 分页响应
export interface PaginationResponse<T> {
  items: T[]
  total: number
  page: number
  size: number
  totalPages: number
}

// 排序参数
export interface SortParams {
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

// 筛选参数
export interface FilterParams {
  keyword?: string
  status?: string
  type?: string
  startDate?: string
  endDate?: string
  [key: string]: any
}

// 通用响应格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 错误信息
export interface ErrorInfo {
  code: number
  message: string
  details?: any
  timestamp: number
}

// 上传文件信息
export interface UploadFileInfo {
  uid: string
  name: string
  status: 'uploading' | 'done' | 'error' | 'removed'
  percent?: number
  url?: string
  thumbUrl?: string
  size?: number
  type?: string
  response?: any
  error?: any
}

// 系统配置
export interface SystemConfig {
  site: {
    title: string
    logo: string
    favicon: string
    copyright: string
  }
  security: {
    captchaEnabled: boolean
    passwordMinLength: number
    sessionTimeout: number
  }
  upload: {
    maxSize: number
    allowedTypes: string[]
    chunkSize: number
  }
  theme: {
    primaryColor: string
    darkMode: boolean
  }
}

// 用户权限
export interface UserPermission {
  id: string
  name: string
  code: string
  type: 'menu' | 'button' | 'api'
  parentId?: string
  children?: UserPermission[]
}

// 用户角色
export interface UserRole {
  id: string
  name: string
  code: string
  description?: string
  permissions?: UserPermission[]
}

// 通知消息
export interface Notification {
  id: string
  type: 'info' | 'success' | 'warning' | 'error'
  title: string
  content: string
  read: boolean
  createTime: string
  link?: string
}

// 系统监控数据
export interface SystemMonitorData {
  cpu: {
    usage: number
    cores: number
    loadAverage: number[]
  }
  memory: {
    total: number
    used: number
    free: number
    usage: number
  }
  disk: {
    total: number
    used: number
    free: number
    usage: number
  }
  network: {
    upload: number
    download: number
  }
  uptime: number
}

// 操作日志
export interface OperationLog {
  id: string
  userId: string
  username: string
  operation: string
  method: string
  url: string
  params?: string
  ip: string
  location?: string
  userAgent: string
  status: number
  errorMsg?: string
  executeTime: number
  createTime: string
}

// 字典数据
export interface DictData {
  id: string
  dictType: string
  dictLabel: string
  dictValue: string
  sort: number
  status: 'enabled' | 'disabled'
  remark?: string
}

// 文件信息
export interface FileInfo {
  id: string
  name: string
  originalName: string
  path: string
  size: number
  mimeType: string
  extension: string
  md5: string
  createTime: string
  updateTime: string
}