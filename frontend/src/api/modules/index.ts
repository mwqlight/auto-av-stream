// API模块统一导出
export * from './auth'
export * from './media'
export * from './live'
export * from './ai'
export * from './monitor'

// 全局API对象，方便使用
import { authApi } from './auth'
import { mediaApi } from './media'
import { liveApi } from './live'
import { aiApi } from './ai'
import { monitorApi } from './monitor'

export const $api = {
  auth: authApi,
  media: mediaApi,
  live: liveApi,
  ai: aiApi,
  monitor: monitorApi
}

// 默认导出全局API对象
export default $api