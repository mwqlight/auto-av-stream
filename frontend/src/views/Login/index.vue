<template>
  <div class="login-page">
    <ParticlesBackground 
      :density="3" 
      :speed="1" 
      :color="'#409EFF'"
      :interactive="true"
    />
    
    <div class="login-container">
      <div class="login-background">
        <div class="grid-background"></div>
        <div class="glow-effect"></div>
      </div>
      
      <div class="login-content">
        <div class="brand-section">
          <div class="logo-container">
            <div class="logo-icon">
              <i class="icon-video"></i>
            </div>
            <h1 class="brand-name">AV Stream Space</h1>
            <p class="brand-tagline">智能音视频流媒体平台</p>
          </div>
          
          <div class="feature-list">
            <div class="feature-item">
              <i class="icon-check"></i>
              <span>实时音视频处理</span>
            </div>
            <div class="feature-item">
              <i class="icon-check"></i>
              <span>AI智能分析</span>
            </div>
            <div class="feature-item">
              <i class="icon-check"></i>
              <span>多平台直播推流</span>
            </div>
            <div class="feature-item">
              <i class="icon-check"></i>
              <span>云端录制存储</span>
            </div>
          </div>
        </div>
        
        <div class="form-section">
          <div class="form-tabs">
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'login' }"
              @click="activeTab = 'login'"
            >
              登录
            </button>
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'register' }"
              @click="activeTab = 'register'"
            >
              注册
            </button>
          </div>
          
          <!-- 登录表单 -->
          <form 
            v-if="activeTab === 'login'" 
            class="login-form" 
            @submit.prevent="handleLogin"
          >
            <div class="form-group">
              <label for="login-username">用户名或邮箱</label>
              <input
                id="login-username"
                v-model="loginForm.username"
                type="text"
                class="form-input"
                placeholder="请输入用户名或邮箱"
                required
              />
            </div>
            
            <div class="form-group">
              <label for="login-password">密码</label>
              <div class="password-input-container">
                <input
                  id="login-password"
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  class="form-input"
                  placeholder="请输入密码"
                  required
                />
                <button 
                  type="button" 
                  class="password-toggle"
                  @click="showPassword = !showPassword"
                >
                  <i :class="showPassword ? 'icon-eye-off' : 'icon-eye'"></i>
                </button>
              </div>
            </div>
            
            <div class="form-options">
              <label class="checkbox-label">
                <input 
                  v-model="loginForm.remember" 
                  type="checkbox" 
                  class="checkbox-input"
                />
                <span class="checkbox-custom"></span>
                <span class="checkbox-text">记住我</span>
              </label>
              
              <button 
                type="button" 
                class="forgot-password"
                @click="showForgotPassword = true"
              >
                忘记密码？
              </button>
            </div>
            
            <button 
              type="submit" 
              class="submit-btn"
              :disabled="loginLoading"
            >
              <span v-if="loginLoading" class="loading-spinner"></span>
              <span v-else>登录</span>
            </button>
            
            <div class="divider">
              <span>或使用第三方登录</span>
            </div>
            
            <div class="social-login">
              <button type="button" class="social-btn social-btn--github">
                <i class="icon-github"></i>
                GitHub
              </button>
              <button type="button" class="social-btn social-btn--google">
                <i class="icon-google"></i>
                Google
              </button>
            </div>
          </form>
          
          <!-- 注册表单 -->
          <form 
            v-else 
            class="register-form" 
            @submit.prevent="handleRegister"
          >
            <div class="form-group">
              <label for="register-username">用户名</label>
              <input
                id="register-username"
                v-model="registerForm.username"
                type="text"
                class="form-input"
                placeholder="请输入用户名"
                required
              />
            </div>
            
            <div class="form-group">
              <label for="register-email">邮箱</label>
              <input
                id="register-email"
                v-model="registerForm.email"
                type="email"
                class="form-input"
                placeholder="请输入邮箱地址"
                required
              />
            </div>
            
            <div class="form-group">
              <label for="register-password">密码</label>
              <div class="password-input-container">
                <input
                  id="register-password"
                  v-model="registerForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  class="form-input"
                  placeholder="请输入密码"
                  required
                />
                <button 
                  type="button" 
                  class="password-toggle"
                  @click="showPassword = !showPassword"
                >
                  <i :class="showPassword ? 'icon-eye-off' : 'icon-eye'"></i>
                </button>
              </div>
              <div class="password-strength">
                <div 
                  class="strength-bar"
                  :class="passwordStrengthClass"
                ></div>
                <span class="strength-text">{{ passwordStrengthText }}</span>
              </div>
            </div>
            
            <div class="form-group">
              <label for="register-confirm">确认密码</label>
              <input
                id="register-confirm"
                v-model="registerForm.confirmPassword"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                placeholder="请再次输入密码"
                required
              />
            </div>
            
            <div class="form-options">
              <label class="checkbox-label">
                <input 
                  v-model="registerForm.agree" 
                  type="checkbox" 
                  class="checkbox-input"
                  required
                />
                <span class="checkbox-custom"></span>
                <span class="checkbox-text">
                  我已阅读并同意
                  <a href="#" class="link">服务协议</a>
                  和
                  <a href="#" class="link">隐私政策</a>
                </span>
              </label>
            </div>
            
            <button 
              type="submit" 
              class="submit-btn"
              :disabled="registerLoading"
            >
              <span v-if="registerLoading" class="loading-spinner"></span>
              <span v-else>注册</span>
            </button>
          </form>
        </div>
      </div>
    </div>
    
    <!-- 忘记密码模态框 -->
    <div v-if="showForgotPassword" class="modal-overlay" @click="showForgotPassword = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>忘记密码</h3>
          <button class="modal-close" @click="showForgotPassword = false">
            <i class="icon-x"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <form class="forgot-password-form" @submit.prevent="handleForgotPassword">
            <div class="form-group">
              <label for="forgot-email">邮箱地址</label>
              <input
                id="forgot-email"
                v-model="forgotPasswordForm.email"
                type="email"
                class="form-input"
                placeholder="请输入注册邮箱"
                required
              />
            </div>
            
            <p class="forgot-password-text">
              我们将向您的邮箱发送重置密码的链接，请查收邮件并按照说明操作。
            </p>
          </form>
        </div>
        
        <div class="modal-footer">
          <button 
            type="button" 
            class="btn btn--secondary"
            @click="showForgotPassword = false"
          >
            取消
          </button>
          <button 
            type="button" 
            class="btn btn--primary"
            @click="handleForgotPassword"
            :disabled="forgotPasswordLoading"
          >
            <span v-if="forgotPasswordLoading" class="loading-spinner"></span>
            <span v-else>发送重置邮件</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ParticlesBackground from '@/components/ParticlesBackground.vue'
import { useUserStore } from '@/store/user'

// 路由和状态管理
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const activeTab = ref<'login' | 'register'>('login')
const showPassword = ref(false)
const showForgotPassword = ref(false)
const loginLoading = ref(false)
const registerLoading = ref(false)
const forgotPasswordLoading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 注册表单数据
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  agree: false
})

// 忘记密码表单数据
const forgotPasswordForm = reactive({
  email: ''
})

// 密码强度计算
const passwordStrength = computed(() => {
  const password = registerForm.password
  if (!password) return 0
  
  let strength = 0
  if (password.length >= 8) strength += 1
  if (/[a-z]/.test(password)) strength += 1
  if (/[A-Z]/.test(password)) strength += 1
  if (/\d/.test(password)) strength += 1
  if (/[^a-zA-Z\d]/.test(password)) strength += 1
  
  return Math.min(strength, 5)
})

const passwordStrengthClass = computed(() => {
  const strength = passwordStrength.value
  if (strength <= 2) return 'strength-weak'
  if (strength <= 3) return 'strength-medium'
  return 'strength-strong'
})

const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return '未输入'
  if (strength <= 2) return '弱'
  if (strength <= 3) return '中'
  return '强'
})

// 表单处理函数
const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }
  
  loginLoading.value = true
  try {
    // 调用登录API
    await userStore.login({
      username: loginForm.username,
      password: loginForm.password
    })
    
    ElMessage.success('登录成功')
    
    // 跳转到首页
    router.push('/')
  } catch (error: any) {
    console.error('登录失败', error)
    ElMessage.error(error.response?.data?.message || '登录失败，请检查用户名和密码')
  } finally {
    loginLoading.value = false
  }
}

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.email || !registerForm.password) {
    ElMessage.error('请填写完整信息')
    return
  }
  
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  if (!registerForm.agree) {
    ElMessage.error('请同意服务协议和隐私政策')
    return
  }
  
  registerLoading.value = true
  try {
    // 调用注册API
    await $api.auth.register({
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password
    })
    
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    
    // 清空注册表单
    registerForm.username = ''
    registerForm.email = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
    registerForm.agree = false
  } catch (error: any) {
    console.error('注册失败', error)
    ElMessage.error(error.response?.data?.message || '注册失败，请稍后重试')
  } finally {
    registerLoading.value = false
  }
}

const handleForgotPassword = async () => {
  if (!forgotPasswordForm.email) {
    ElMessage.error('请输入邮箱地址')
    return
  }
  
  forgotPasswordLoading.value = true
  try {
    // 调用忘记密码API
    await $api.auth.forgotPassword({
      email: forgotPasswordForm.email
    })
    
    ElMessage.success('重置邮件已发送，请查收')
    showForgotPassword.value = false
    
    // 清空表单
    forgotPasswordForm.email = ''
  } catch (error: any) {
    console.error('发送重置邮件失败', error)
    ElMessage.error(error.response?.data?.message || '发送重置邮件失败，请稍后重试')
  } finally {
    forgotPasswordLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-container {
  position: relative;
  width: 100%;
  max-width: 1200px;
  height: 700px;
  background: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-large);
  overflow: hidden;
  display: flex;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.grid-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(64, 158, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(64, 158, 255, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  opacity: 0.3;
}

.glow-effect {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle,
    rgba(64, 158, 255, 0.1) 0%,
    transparent 70%
  );
  animation: pulse 4s ease-in-out infinite;
}

.login-content {
  position: relative;
  z-index: 2;
  display: flex;
  width: 100%;
  height: 100%;
}

.brand-section {
  flex: 1;
  background: linear-gradient(135deg, var(--primary-color) 0%, #1e6fba 100%);
  color: white;
  padding: var(--spacing-xxl);
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.2;
}

.logo-container {
  text-align: center;
  margin-bottom: var(--spacing-xxl);
}

.logo-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--spacing-lg);
  font-size: 32px;
  backdrop-filter: blur(10px);
}

.brand-name {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: var(--spacing-sm);
  letter-spacing: 1px;
}

.brand-tagline {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.feature-list {
  .feature-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-lg);
    font-size: 16px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    i {
      font-size: 20px;
      opacity: 0.8;
    }
  }
}

.form-section {
  flex: 1;
  padding: var(--spacing-xxl);
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: var(--bg-color-lighter);
}

.form-tabs {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.tab-btn {
  flex: 1;
  padding: var(--spacing-lg) var(--spacing-xl);
  background: transparent;
  border: none;
  border-bottom: 2px solid transparent;
  color: var(--text-color-light);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition-fast);
  
  &.active {
    color: var(--primary-color);
    border-bottom-color: var(--primary-color);
  }
  
  &:hover {
    color: var(--primary-color);
  }
}

.login-form,
.register-form {
  .form-group {
    margin-bottom: var(--spacing-lg);
  }
  
  label {
    display: block;
    margin-bottom: var(--spacing-sm);
    color: var(--text-color);
    font-weight: 500;
    font-size: 14px;
  }
}

.form-input {
  width: 100%;
  padding: var(--spacing-md);
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  color: var(--text-color);
  font-size: 14px;
  outline: none;
  transition: var(--transition-fast);
  
  &:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
  }
  
  &::placeholder {
    color: var(--text-color-light);
  }
}

.password-input-container {
  position: relative;
  
  .form-input {
    padding-right: 45px;
  }
}

.password-toggle {
  position: absolute;
  right: var(--spacing-sm);
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: var(--text-color-light);
  cursor: pointer;
  border-radius: var(--border-radius-base);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition-fast);
  
  &:hover {
    background: var(--bg-color-light);
    color: var(--text-color);
  }
}

.password-strength {
  margin-top: var(--spacing-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  
  .strength-bar {
    flex: 1;
    height: 4px;
    background: var(--bg-color-light);
    border-radius: 2px;
    overflow: hidden;
    
    &.strength-weak {
      background: linear-gradient(90deg, var(--danger-color) 0%, var(--danger-color) 33%, var(--bg-color-light) 33%, var(--bg-color-light) 100%);
    }
    
    &.strength-medium {
      background: linear-gradient(90deg, var(--warning-color) 0%, var(--warning-color) 66%, var(--bg-color-light) 66%, var(--bg-color-light) 100%);
    }
    
    &.strength-strong {
      background: var(--success-color);
    }
  }
  
  .strength-text {
    font-size: 12px;
    color: var(--text-color-light);
    min-width: 40px;
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  font-size: 14px;
  
  .checkbox-input {
    display: none;
  }
  
  .checkbox-custom {
    width: 16px;
    height: 16px;
    border: 2px solid var(--border-color);
    border-radius: 3px;
    position: relative;
    transition: var(--transition-fast);
    
    .checkbox-input:checked + & {
      background: var(--primary-color);
      border-color: var(--primary-color);
    }
    
    .checkbox-input:checked + &::after {
      content: '✓';
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      color: white;
      font-size: 10px;
      font-weight: bold;
    }
  }
  
  .checkbox-text {
    color: var(--text-color);
  }
  
  .link {
    color: var(--primary-color);
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
}

.forgot-password {
  background: none;
  border: none;
  color: var(--primary-color);
  font-size: 14px;
  cursor: pointer;
  text-decoration: underline;
  
  &:hover {
    color: var(--primary-color-dark);
  }
}

.submit-btn {
  width: 100%;
  padding: var(--spacing-lg);
  background: var(--primary-color);
  border: none;
  border-radius: var(--border-radius-base);
  color: white;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition-fast);
  position: relative;
  
  &:hover:not(:disabled) {
    background: var(--primary-color-dark);
    transform: translateY(-1px);
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  display: inline-block;
}

.divider {
  display: flex;
  align-items: center;
  margin: var(--spacing-xl) 0;
  
  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: var(--border-color);
  }
  
  span {
    padding: 0 var(--spacing-md);
    color: var(--text-color-light);
    font-size: 14px;
  }
}

.social-login {
  display: flex;
  gap: var(--spacing-md);
}

.social-btn {
  flex: 1;
  padding: var(--spacing-md);
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  color: var(--text-color);
  font-size: 14px;
  cursor: pointer;
  transition: var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  
  &:hover {
    background: var(--bg-color-light);
    transform: translateY(-1px);
  }
  
  &.social-btn--github:hover {
    border-color: #333;
    color: #333;
  }
  
  &.social-btn--google:hover {
    border-color: #db4437;
    color: #db4437;
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 3000;
}

.modal-content {
  background: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-large);
  width: 400px;
  max-width: 90vw;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
  
  h3 {
    margin: 0;
    color: var(--text-color);
  }
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-light);
  cursor: pointer;
  transition: var(--transition-fast);
  
  &:hover {
    background: var(--bg-color-light);
    color: var(--text-color);
  }
}

.modal-body {
  padding: var(--spacing-lg);
  flex: 1;
}

.forgot-password-text {
  color: var(--text-color-light);
  font-size: 14px;
  line-height: 1.5;
  margin-top: var(--spacing-md);
}

.modal-footer {
  padding: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
}

.btn {
  padding: var(--spacing-md) var(--spacing-lg);
  border: none;
  border-radius: var(--border-radius-base);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition-fast);
  
  &--primary {
    background: var(--primary-color);
    color: white;
    
    &:hover:not(:disabled) {
      background: var(--primary-color-dark);
    }
  }
  
  &--secondary {
    background: var(--bg-color-light);
    color: var(--text-color);
    
    &:hover {
      background: var(--bg-color-lighter);
    }
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(1.1);
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    height: auto;
    min-height: 100vh;
    border-radius: 0;
    flex-direction: column;
  }
  
  .brand-section {
    padding: var(--spacing-xl);
    flex: none;
    height: 300px;
  }
  
  .form-section {
    padding: var(--spacing-xl);
    flex: 1;
  }
  
  .logo-icon {
    width: 60px;
    height: 60px;
    font-size: 24px;
  }
  
  .brand-name {
    font-size: 24px;
  }
  
  .social-login {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .brand-section,
  .form-section {
    padding: var(--spacing-lg);
  }
  
  .form-tabs {
    gap: var(--spacing-sm);
  }
  
  .tab-btn {
    padding: var(--spacing-md) var(--spacing-lg);
    font-size: 14px;
  }
  
  .modal-content {
    width: 95vw;
    margin: var(--spacing-md);
  }
}
</style>