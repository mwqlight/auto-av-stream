import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useAppStore } from '@/store/app'

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const userStore = useUserStore()
    const appStore = useAppStore()
    
    // 设置加载状态
    appStore.setLoading(true)
    
    // 添加token到请求头
    if (userStore.token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    
    // 添加请求时间戳
    if (config.params) {
      config.params._t = Date.now()
    } else {
      config.params = { _t: Date.now() }
    }
    
    return config
  },
  (error) => {
    const appStore = useAppStore()
    appStore.setLoading(false)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const appStore = useAppStore()
    appStore.setLoading(false)
    
    const { data } = response
    
    // 如果响应数据是二进制流（如下载文件），直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    // 统一处理响应格式
    if (data.code === 200) {
      return data
    } else {
      // 业务错误处理
      handleBusinessError(data.code, data.message)
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error) => {
    const appStore = useAppStore()
    appStore.setLoading(false)
    
    // 网络错误处理
    handleNetworkError(error)
    return Promise.reject(error)
  }
)

// 业务错误处理
function handleBusinessError(code: number, message: string) {
  const userStore = useUserStore()
  
  switch (code) {
    case 401:
      // 未授权，清除token并跳转到登录页
      userStore.logout()
      ElMessageBox.alert('登录已过期，请重新登录', '提示', {
        confirmButtonText: '重新登录',
        callback: () => {
          window.location.href = '/login'
        }
      })
      break
    case 403:
      ElMessage.warning('没有权限访问该资源')
      break
    case 404:
      ElMessage.warning('请求的资源不存在')
      break
    case 500:
      ElMessage.error('服务器内部错误')
      break
    default:
      ElMessage.error(message || '请求失败')
  }
}

// 网络错误处理
function handleNetworkError(error: any) {
  const appStore = useAppStore()
  
  if (error.response) {
    // 服务器返回错误状态码
    const { status, data } = error.response
    
    switch (status) {
      case 400:
        ElMessage.error(data.message || '请求参数错误')
        break
      case 401:
        ElMessage.error('未授权，请重新登录')
        break
      case 403:
        ElMessage.error('禁止访问')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 500:
        ElMessage.error('服务器内部错误')
        break
      case 502:
        ElMessage.error('网关错误')
        break
      case 503:
        ElMessage.error('服务不可用')
        break
      case 504:
        ElMessage.error('网关超时')
        break
      default:
        ElMessage.error(`请求失败: ${status}`)
    }
    
    // 记录错误日志
    appStore.addErrorLog(`HTTP ${status}: ${data.message || error.message}`)
  } else if (error.request) {
    // 请求未收到响应
    ElMessage.error('网络连接失败，请检查网络设置')
    appStore.addErrorLog('Network Error: No response received')
  } else {
    // 其他错误
    ElMessage.error('请求配置错误')
    appStore.addErrorLog(`Request Error: ${error.message}`)
  }
}

// 通用请求方法
export const request = {
  get<T = any>(url: string, params?: any): Promise<T> {
    return service.get(url, { params })
  },

  post<T = any>(url: string, data?: any): Promise<T> {
    return service.post(url, data)
  },

  put<T = any>(url: string, data?: any): Promise<T> {
    return service.put(url, data)
  },

  delete<T = any>(url: string, params?: any): Promise<T> {
    return service.delete(url, { params })
  },

  patch<T = any>(url: string, data?: any): Promise<T> {
    return service.patch(url, data)
  },

  // 文件上传
  upload<T = any>(url: string, formData: FormData, onProgress?: (progressEvent: any) => void): Promise<T> {
    return service.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: onProgress
    })
  },

  // 文件下载
  download(url: string, params?: any): Promise<Blob> {
    return service.get(url, {
      params,
      responseType: 'blob'
    })
  }
}

// 导出axios实例
export default service