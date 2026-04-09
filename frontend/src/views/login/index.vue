<template>
  <div class="login-page" :class="currentTheme">
    <!-- 主题切换器 -->
    <div class="theme-switcher">
      <div class="theme-btn" @click="showThemePanel = !showThemePanel">
        <el-icon :size="20"><Brush /></el-icon>
      </div>
      <transition name="fade">
        <div v-show="showThemePanel" class="theme-panel">
          <div class="theme-title">选择主题</div>
          <div class="theme-list">
            <div
              v-for="theme in themes"
              :key="theme.id"
              class="theme-item"
              :class="{ active: currentTheme === theme.id }"
              @click="changeTheme(theme.id)"
            >
              <div class="theme-preview" :style="{ background: theme.gradient }"></div>
              <span class="theme-name">{{ theme.name }}</span>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
      <div class="particles">
        <span v-for="n in 20" :key="n" class="particle"></span>
      </div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-wrapper">
      <div class="login-card">
        <!-- Logo区域 -->
        <div class="brand-section">
          <div class="logo-icon">
            <el-icon :size="48" color="#fff"><School /></el-icon>
          </div>
          <h1 class="brand-title">高校社团与活动管理平台</h1>
          <p class="brand-subtitle">连接校园，成就梦想</p>
        </div>

        <!-- 登录表单 -->
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <div class="input-wrapper">
              <el-icon class="input-icon"><User /></el-icon>
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                size="large"
                class="custom-input"
              />
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
                class="custom-input"
              />
            </div>
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <a href="#" class="forgot-link">忘记密码?</a>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              class="login-btn"
            >
              <span class="btn-text">登 录</span>
              <el-icon class="btn-icon"><ArrowRight /></el-icon>
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 注册链接 -->
        <div class="register-section">
          <span class="text-muted">还没有账号?</span>
          <router-link to="/register" class="register-link">
            立即注册
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>

        <!-- 社交登录 -->
        <div class="social-login">
          <div class="divider">
            <span>其他方式登录</span>
          </div>
          <div class="social-icons">
            <div class="social-icon wechat" @click="handleSocialLogin('wechat')">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="social-icon qq" @click="handleSocialLogin('qq')">
              <el-icon><ChatLineRound /></el-icon>
            </div>
            <div class="social-icon weibo" @click="handleSocialLogin('weibo')">
              <el-icon><Microphone /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部版权 -->
      <div class="footer">
        <p>© 2026 高校社团管理和活动预约平台 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)
const showThemePanel = ref(false)
const currentTheme = ref('purple')

// 主题配置
const themes = [
  { id: 'purple', name: '梦幻紫', gradient: 'linear-gradient(135deg, #0284c7 0%, #0ea5e9 100%)' },
  { id: 'blue', name: '天空蓝', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { id: 'green', name: '清新绿', gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
  { id: 'orange', name: '活力橙', gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
  { id: 'dark', name: '深邃黑', gradient: 'linear-gradient(135deg, #2c3e50 0%, #4ca1af 100%)' },
  { id: 'red', name: '热情红', gradient: 'linear-gradient(135deg, #ff0844 0%, #ffb199 100%)' },
  { id: 'pink', name: '浪漫粉', gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { id: 'cyan', name: '青柠青', gradient: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)' }
]

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 切换主题
const changeTheme = (themeId) => {
  currentTheme.value = themeId
  localStorage.setItem('loginTheme', themeId)
  showThemePanel.value = false
  ElMessage.success(`已切换到${themes.find(t => t.id === themeId)?.name}主题`)
}

// 加载保存的主题
onMounted(() => {
  const savedTheme = localStorage.getItem('loginTheme')
  if (savedTheme && themes.find(t => t.id === savedTheme)) {
    currentTheme.value = savedTheme
  }
})

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.loginAction(form)
    ElMessage.success({
      message: '欢迎回来!',
      type: 'success',
      duration: 2000
    })
    router.push('/dashboard')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSocialLogin = (type) => {
  ElMessage.info('社交登录功能开发中')
}
</script>

<style lang="scss" scoped>
// 主题变量定义
$themes: (
  purple: (
    primary: #0284c7,
    secondary: #0ea5e9,
    orb1: linear-gradient(135deg, #f093fb 0%, #f5576c 100%),
    orb2: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%),
    orb3: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)
  ),
  blue: (
    primary: #4facfe,
    secondary: #00f2fe,
    orb1: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%),
    orb2: linear-gradient(135deg, #fa709a 0%, #fee140 100%),
    orb3: linear-gradient(135deg, #667eea 0%, #764ba2 100%)
  ),
  green: (
    primary: #43e97b,
    secondary: #38f9d7,
    orb1: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%),
    orb2: linear-gradient(135deg, #667eea 0%, #764ba2 100%),
    orb3: linear-gradient(135deg, #fa709a 0%, #fee140 100%)
  ),
  orange: (
    primary: #fa709a,
    secondary: #fee140,
    orb1: linear-gradient(135deg, #ff0844 0%, #ffb199 100%),
    orb2: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%),
    orb3: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)
  ),
  dark: (
    primary: #2c3e50,
    secondary: #4ca1af,
    orb1: linear-gradient(135deg, #434343 0%, #000000 100%),
    orb2: linear-gradient(135deg, #2c3e50 0%, #4ca1af 100%),
    orb3: linear-gradient(135deg, #141e30 0%, #243b55 100%)
  ),
  red: (
    primary: #ff0844,
    secondary: #ffb199,
    orb1: linear-gradient(135deg, #fa709a 0%, #fee140 100%),
    orb2: linear-gradient(135deg, #667eea 0%, #764ba2 100%),
    orb3: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)
  ),
  pink: (
    primary: #f093fb,
    secondary: #f5576c,
    orb1: linear-gradient(135deg, #667eea 0%, #764ba2 100%),
    orb2: linear-gradient(135deg, #ff0844 0%, #ffb199 100%),
    orb3: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)
  ),
  cyan: (
    primary: #11998e,
    secondary: #38ef7d,
    orb1: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%),
    orb2: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%),
    orb3: linear-gradient(135deg, #667eea 0%, #764ba2 100%)
  )
);

// 生成主题样式
@each $theme-name, $theme-colors in $themes {
  .login-page.#{$theme-name} {
    background: linear-gradient(135deg, map-get($theme-colors, primary) 0%, map-get($theme-colors, secondary) 100%);

    .orb-1 {
      background: map-get($theme-colors, orb1);
    }
    .orb-2 {
      background: map-get($theme-colors, orb2);
    }
    .orb-3 {
      background: map-get($theme-colors, orb3);
    }

    .logo-icon {
      background: linear-gradient(135deg, map-get($theme-colors, primary) 0%, map-get($theme-colors, secondary) 100%);
      box-shadow: 0 10px 30px rgba(map-get($theme-colors, primary), 0.4);
    }

    @keyframes logo-pulse {
      0%, 100% {
        box-shadow: 0 10px 30px rgba(map-get($theme-colors, primary), 0.4), 0 0 20px rgba(map-get($theme-colors, primary), 0.15);
        transform: scale(1);
      }
      50% {
        box-shadow: 0 10px 50px rgba(map-get($theme-colors, primary), 0.6), 0 0 40px rgba(map-get($theme-colors, primary), 0.25), 0 0 60px rgba(map-get($theme-colors, secondary), 0.15);
        transform: scale(1.05);
      }
    }

    .custom-input {
      :deep(.el-input__wrapper) {
        &.is-focus {
          border-color: map-get($theme-colors, primary);
          box-shadow: 0 0 0 4px rgba(map-get($theme-colors, primary), 0.1), 0 0 20px rgba(map-get($theme-colors, primary), 0.08);
          animation: input-glow 2s ease-in-out infinite;
        }
      }
    }

    @keyframes input-glow {
      0%, 100% {
        box-shadow: 0 0 0 4px rgba(map-get($theme-colors, primary), 0.1), 0 0 20px rgba(map-get($theme-colors, primary), 0.08);
      }
      50% {
        box-shadow: 0 0 0 4px rgba(map-get($theme-colors, primary), 0.15), 0 0 30px rgba(map-get($theme-colors, primary), 0.12);
      }
    }

    .input-wrapper:focus-within .input-icon {
      color: map-get($theme-colors, primary);
    }

    .forgot-link {
      color: map-get($theme-colors, primary);
      &:hover {
        color: map-get($theme-colors, secondary);
      }
    }

    .login-btn {
      background: linear-gradient(135deg, map-get($theme-colors, primary) 0%, map-get($theme-colors, secondary) 100%);
      &:hover {
        box-shadow: 0 10px 30px rgba(map-get($theme-colors, primary), 0.4), 0 0 40px rgba(map-get($theme-colors, primary), 0.15);
      }
    }

    .register-link {
      color: map-get($theme-colors, primary);
      &:hover {
        color: map-get($theme-colors, secondary);
      }
    }

    .theme-btn {
      background: linear-gradient(135deg, map-get($theme-colors, primary) 0%, map-get($theme-colors, secondary) 100%);
    }

    // 渐变边框 - 使用伪元素实现
    .login-card::before {
      background: linear-gradient(135deg, map-get($theme-colors, primary), map-get($theme-colors, secondary), rgba(255,255,255,0.3), map-get($theme-colors, primary));
      background-size: 300% 300%;
      animation: gradient-border-rotate 4s ease infinite;
    }
  }
}

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transition: background 0.5s ease;
}

// 主题切换器
.theme-switcher {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
}

.theme-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #fff;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  transition: all 0.3s;
  animation: rotate 3s linear infinite;
  animation-play-state: paused;

  &:hover {
    transform: scale(1.1);
    animation-play-state: running;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.theme-panel {
  position: absolute;
  top: 60px;
  right: 0;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 20px;
  min-width: 200px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.theme-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e2e8f0;
}

.theme-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.theme-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 10px;
  transition: all 0.3s;

  &:hover {
    background: #f1f5f9;
  }

  &.active {
    background: #e0f2fe;
    .theme-name {
      color: #0284c7;
      font-weight: 600;
    }
  }
}

.theme-preview {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  margin-bottom: 6px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.theme-name {
  font-size: 12px;
  color: #64748b;
  transition: all 0.3s;
}

// 动态背景
.animated-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 20s infinite ease-in-out;
  transition: background 0.5s ease;
}

.orb-1 {
  width: 600px;
  height: 600px;
  top: -200px;
  right: -100px;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  bottom: -150px;
  left: -100px;
  animation-delay: -5s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(50px, -50px) scale(1.1);
  }
  50% {
    transform: translate(-30px, 30px) scale(0.9);
  }
  75% {
    transform: translate(30px, 50px) scale(1.05);
  }
}

// 粒子效果 - 更丰富的颜色层次
.particles {
  position: absolute;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  border-radius: 50%;
  animation: particle-float 15s infinite;
}

@for $i from 1 through 20 {
  .particle:nth-child(#{$i}) {
    left: random(100) * 1%;
    top: random(100) * 1%;
    animation-delay: random(15) * -1s;
    animation-duration: 10s + random(10);
    @if $i % 5 == 0 {
      // 大粒子 - 带渐变色
      width: 6px;
      height: 6px;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.1));
      box-shadow: 0 0 10px rgba(255, 255, 255, 0.4);
    } @else if $i % 3 == 0 {
      // 中粒子 - 柔和发光
      width: 4px;
      height: 4px;
      background: rgba(255, 255, 255, 0.6);
      box-shadow: 0 0 6px rgba(255, 255, 255, 0.3);
    } @else {
      // 小粒子 - 基础
      width: 3px;
      height: 3px;
      background: rgba(255, 255, 255, 0.4);
    }
  }
}

@keyframes particle-float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) rotate(720deg);
    opacity: 0;
  }
}

// 登录包装器 - 增加 perspective 实现 3D 感
.login-wrapper {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 440px;
  padding: 20px;
  perspective: 1000px;
}

// 登录卡片 - 增强 3D 感 + 渐变边框
.login-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(24px) saturate(180%);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow:
    0 25px 50px -12px rgba(0, 0, 0, 0.25),
    0 0 0 1px rgba(255, 255, 255, 0.15) inset,
    0 1px 0 rgba(255, 255, 255, 0.3) inset;
  animation: card-appear 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
  perspective: 1000px;
  transform-style: preserve-3d;
  position: relative;
  transition: transform 0.4s ease, box-shadow 0.4s ease;

  // 渐变边框伪元素
  &::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    border-radius: 26px;
    z-index: -1;
    opacity: 0.6;
    transition: opacity 0.4s ease;
  }

  &:hover {
    transform: translateY(-4px) rotateX(2deg);
    box-shadow:
      0 35px 60px -15px rgba(0, 0, 0, 0.3),
      0 0 0 1px rgba(255, 255, 255, 0.15) inset,
      0 1px 0 rgba(255, 255, 255, 0.3) inset;

    &::before {
      opacity: 1;
    }
  }
}

@keyframes gradient-border-rotate {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes card-appear {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95) rotateX(10deg);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1) rotateX(0deg);
  }
}

// 品牌区域
.brand-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: logo-pulse 3s ease-in-out infinite, glowPulse 4s ease-in-out infinite;
  transition: all 0.5s ease;
  position: relative;

  // 内部光泽层
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 60%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    border-radius: 20px;
    animation: logo-shimmer 3s ease-in-out infinite;
  }
}

@keyframes logo-shimmer {
  0%, 100% { left: -100%; }
  50% { left: 150%; }
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #1e293b 0%, #475569 50%, #1e293b 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: title-shimmer 4s linear infinite;
}

@keyframes title-shimmer {
  0% { background-position: 0% center; }
  100% { background-position: 200% center; }
}

.brand-subtitle {
  font-size: 14px;
  color: #64748b;
  font-weight: 400;
  letter-spacing: 2px;
}

// 登录表单
.login-form {
  :deep(.el-form-item) {
    margin-bottom: 24px;
  }
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  font-size: 18px;
  color: #94a3b8;
  z-index: 1;
  transition: all 0.4s ease;
}

.custom-input {
  :deep(.el-input__wrapper) {
    padding-left: 48px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    border: 2px solid transparent;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    background: rgba(248, 250, 252, 0.8);

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      background: rgba(255, 255, 255, 0.95);
    }

    &.is-focus {
      border-color: #0284c7;
      box-shadow: 0 0 0 4px rgba(2, 132, 199, 0.1), 0 0 20px rgba(2, 132, 199, 0.08);
      background: rgba(255, 255, 255, 1);
    }
  }

  :deep(.el-input__inner) {
    height: 48px;
    font-size: 15px;

    &::placeholder {
      color: #94a3b8;
    }
  }
}

.input-wrapper:focus-within .input-icon {
  color: #0284c7;
  transform: scale(1.15);
}

// 表单选项
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  :deep(.el-checkbox__label) {
    color: #64748b;
    font-size: 13px;
  }
}

.forgot-link {
  font-size: 13px;
  text-decoration: none;
  transition: all 0.3s;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 0;
    height: 1px;
    background: currentColor;
    transition: width 0.3s ease;
  }

  &:hover::after {
    width: 100%;
  }
}

// 登录按钮 - 增强 shimmer 光泽扫过效果
.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  border: none;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  // 持续循环的 shimmer 光泽
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 60%;
    height: 100%;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.15),
      rgba(255, 255, 255, 0.3),
      rgba(255, 255, 255, 0.15),
      transparent
    );
    transition: none;
    animation: btn-shimmer 3s ease-in-out infinite;
  }

  // 底部光泽线
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 50%;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.1), transparent);
    pointer-events: none;
    border-radius: 0 0 12px 12px;
  }

  &:hover {
    transform: translateY(-3px);
    filter: brightness(1.1);
  }

  &:active {
    transform: translateY(-1px);
    filter: brightness(0.95);
  }

  .btn-text {
    margin-right: 8px;
    position: relative;
    z-index: 1;
  }

  .btn-icon {
    transition: transform 0.3s;
    position: relative;
    z-index: 1;
  }

  &:hover .btn-icon {
    transform: translateX(4px);
  }
}

@keyframes btn-shimmer {
  0% { left: -100%; }
  50% { left: 150%; }
  100% { left: 150%; }
}

// 注册区域
.register-section {
  text-align: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;

  .text-muted {
    color: #94a3b8;
    font-size: 14px;
  }
}

.register-link {
  font-weight: 600;
  text-decoration: none;
  margin-left: 8px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;

  &:hover {
    gap: 8px;
  }

  .el-icon {
    font-size: 14px;
  }
}

// 社交登录
.social-login {
  margin-top: 32px;
}

.divider {
  position: relative;
  text-align: center;
  margin-bottom: 20px;

  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(
      90deg,
      transparent,
      #e2e8f0 20%,
      #e2e8f0 80%,
      transparent
    );
  }

  span {
    position: relative;
    background: rgba(255, 255, 255, 0.92);
    padding: 0 16px;
    color: #94a3b8;
    font-size: 13px;
  }
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.social-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 20px;
  position: relative;
  overflow: hidden;

  &.wechat {
    background: #f0fdf4;
    color: #22c55e;

    &:hover {
      background: #22c55e;
      color: #fff;
      transform: translateY(-3px) scale(1.05);
      box-shadow: 0 8px 25px rgba(34, 197, 94, 0.4), 0 0 20px rgba(34, 197, 94, 0.2);
    }
  }

  &.qq {
    background: #eff6ff;
    color: #3b82f6;

    &:hover {
      background: #3b82f6;
      color: #fff;
      transform: translateY(-3px) scale(1.05);
      box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4), 0 0 20px rgba(59, 130, 246, 0.2);
    }
  }

  &.weibo {
    background: #fef2f2;
    color: #ef4444;

    &:hover {
      background: #ef4444;
      color: #fff;
      transform: translateY(-3px) scale(1.05);
      box-shadow: 0 8px 25px rgba(239, 68, 68, 0.4), 0 0 20px rgba(239, 68, 68, 0.2);
    }
  }
}

// 底部版权
.footer {
  text-align: center;
  margin-top: 32px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

// 响应式
@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
    border-radius: 20px;
  }

  .brand-title {
    font-size: 24px;
  }

  .gradient-orb {
    filter: blur(60px);
  }

  .theme-switcher {
    top: 10px;
    right: 10px;
  }

  .theme-panel {
    right: -10px;
    min-width: 180px;
  }

  .theme-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

// 3D 悬停效果
.login-card:hover {
  transform: perspective(1000px) rotateX(1deg) rotateY(-1deg) translateY(-4px);
  box-shadow: 0 30px 60px -12px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.15) inset,
    0 0 40px rgba(2, 132, 199, 0.15);
}

// 渐变边框效果
.login-card::before {
  content: '';
  position: absolute;
  top: -1px; left: -1px; right: -1px; bottom: -1px;
  background: linear-gradient(135deg, rgba(2, 132, 199, 0.3), rgba(56, 189, 248, 0.1), rgba(59, 130, 246, 0.3));
  border-radius: 25px;
  z-index: -1;
  opacity: 0;
  transition: opacity 0.5s ease;
}

.login-card:hover::before {
  opacity: 1;
}

// 光晕脉冲动画
@keyframes glowPulse {
  0%, 100% { box-shadow: 0 4px 15px rgba(2, 132, 199, 0.4), 0 0 20px rgba(2, 132, 199, 0.1); }
  50% { box-shadow: 0 4px 25px rgba(2, 132, 199, 0.6), 0 0 40px rgba(2, 132, 199, 0.2); }
}

// 社交图标发光
.social-login .social-icon:hover {
  transform: translateY(-3px) scale(1.1);
  box-shadow: 0 8px 20px rgba(2, 132, 199, 0.3);
}
</style>