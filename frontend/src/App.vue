<template>
  <div id="app">
    <!-- 粒子背景 -->
    <ParticlesBackground />
    
    <!-- 主布局 -->
    <div class="app-container">
      <!-- 导航栏 -->
      <NavBar v-if="showNavBar" />
      
      <!-- 主内容区 -->
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
      
      <!-- 底部信息栏 -->
      <FooterBar v-if="showFooter" />
    </div>
    
    <!-- 全局加载状态 -->
    <GlobalLoading />
    
    <!-- 全局消息提示 -->
    <GlobalMessage />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import ParticlesBackground from '@/components/background/ParticlesBackground.vue'
import NavBar from '@/components/layout/NavBar.vue'
import FooterBar from '@/components/layout/FooterBar.vue'
import GlobalLoading from '@/components/feedback/GlobalLoading.vue'
import GlobalMessage from '@/components/feedback/GlobalMessage.vue'

const route = useRoute()

// 根据路由决定是否显示导航栏和底部栏
const showNavBar = computed(() => !route.meta.hideNavBar)
const showFooter = computed(() => !route.meta.hideFooter)
</script>

<style lang="scss" scoped>
#app {
  min-height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #0c0c0c 0%, #1a1a1a 50%, #0c0c0c 100%);
  
  .app-container {
    position: relative;
    z-index: 1;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    
    .main-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
  }
}

// 页面切换动画
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style>