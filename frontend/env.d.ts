/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string
  readonly VITE_APP_VERSION: string
  readonly VITE_API_BASE_URL: string
  readonly VITE_MEDIA_SERVICE_URL: string
  readonly VITE_LIVE_SERVICE_URL: string
  readonly VITE_AI_SERVICE_URL: string
  readonly VITE_AUTH_SERVICE_URL: string
  readonly VITE_MONITOR_SERVICE_URL: string
  
  readonly VITE_WEBRTC_STUN_SERVER: string
  readonly VITE_WEBRTC_TURN_SERVER: string
  
  readonly VITE_FFMPEG_PATH: string
  readonly VITE_MAX_UPLOAD_SIZE: string
  readonly VITE_ALLOWED_FILE_TYPES: string
  
  readonly VITE_WHISPER_API_URL: string
  readonly VITE_STABLE_DIFFUSION_API_URL: string
  readonly VITE_TTS_API_URL: string
  
  readonly VITE_PROMETHEUS_URL: string
  readonly VITE_GRAFANA_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}