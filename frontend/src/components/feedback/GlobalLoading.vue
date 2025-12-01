<template>
  <div v-if="loading" class="global-loading">
    <div class="loading-content">
      <!-- 加载动画 -->
      <div class="loading-spinner">
        <div class="spinner-ring"></div>
        <div class="spinner-ring"></div>
        <div class="spinner-ring"></div>
        <div class="spinner-ring"></div>
      </div>
      
      <!-- 加载文字 -->
      <div class="loading-text">
        {{ loadingText }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useLoadingStore } from '@/store/app'

const loadingStore = useLoadingStore()

const loading = computed(() => loadingStore.loading)
const loadingText = computed(() => loadingStore.loadingText || '加载中...')
</script>

<style lang="scss" scoped>
.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;

  .loading-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 24px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(20px);
  }

  .loading-spinner {
    position: relative;
    width: 80px;
    height: 80px;

    .spinner-ring {
      position: absolute;
      width: 64px;
      height: 64px;
      margin: 8px;
      border: 8px solid transparent;
      border-radius: 50%;
      animation: spin 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
      border-top-color: #00FFFF;

      &:nth-child(1) {
        animation-delay: -0.45s;
        border-top-color: #FF00FF;
      }

      &:nth-child(2) {
        animation-delay: -0.3s;
        border-top-color: #FFFF00;
      }

      &:nth-child(3) {
        animation-delay: -0.15s;
        border-top-color: #00FF00;
      }

      &:nth-child(4) {
        border-top-color: #00FFFF;
      }
    }
  }

  .loading-text {
    color: white;
    font-size: 16px;
    font-weight: 500;
    text-align: center;
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>