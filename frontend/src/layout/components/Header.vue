<template>
  <!-- 顶部导航栏：固定在右侧区域上方 -->
  <el-header class="header">
    <!-- 左侧区域：折叠按钮 + 面包屑导航 -->
    <div class="header-left">
      <!-- 
        侧边栏折叠按钮
        点击时触发 toggle-collapse 事件，通知父组件切换侧边栏状态
        根据 isCollapse 显示不同图标：Fold(展开状态) / Expand(收起状态)
      -->
      <div class="collapse-btn" @click="$emit('toggle-collapse')">
        <el-icon :size="20">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>

      <!-- 
        面包屑导航
        显示当前页面路径，点击可返回首页
        currentRoute?.meta?.title 获取当前路由的页面标题
      -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">
          <el-icon><HomeFilled /></el-icon>
        </el-breadcrumb-item>
        <el-breadcrumb-item>{{ currentRoute?.meta?.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 右侧区域：功能按钮 + 用户信息 -->
    <div class="header-right">
      <!-- 
        通知按钮
        有未读消息时显示红点 badge
        点击触发 open-notifications 事件，打开通知弹窗
      -->
      <div class="header-icon-btn" @click="$emit('open-notifications')">
        <el-badge :value="unreadCount" class="notification-badge" v-if="unreadCount > 0">
          <el-icon :size="18"><Bell /></el-icon>
        </el-badge>
        <el-icon :size="18" v-else><Bell /></el-icon>
      </div>

      <!-- 
        全屏切换按钮
        点击切换浏览器全屏模式
      -->
      <div class="header-icon-btn" @click="toggleFullscreen">
        <el-icon :size="18"><FullScreen /></el-icon>
      </div>

      <!-- 
        用户下拉菜单
        trigger="click": 点击触发下拉
        显示用户头像、昵称、角色
      -->
      <el-dropdown trigger="click" class="user-dropdown">
        <!-- 用户信息展示区域 -->
        <div class="user-info">
          <!-- 
            用户头像
            :size="36": 头像大小 36px
            :src: 头像图片地址
            如果没有头像，显示昵称首字母
          -->
          <el-avatar 
            :size="36" 
            :src="userStore.userInfo?.avatar"
            class="user-avatar"
          >
            {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
          </el-avatar>
          <!-- 非移动端显示用户名和角色 -->
          <div v-if="!isMobile" class="user-meta">
            <div class="user-name">{{ userStore.userInfo?.nickname || '用户' }}</div>
            <div class="user-role">{{ roleText }}</div>
          </div>
          <!-- 下拉箭头图标 -->
          <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
        </div>
        
        <!-- 下拉菜单内容 -->
        <template #dropdown>
          <el-dropdown-menu class="user-dropdown-menu">
            <el-dropdown-item @click="router.push('/profile')">
              <el-icon><UserFilled /></el-icon>
              个人资料
            </el-dropdown-item>
            <el-dropdown-item @click="router.push('/dashboard')">
              <el-icon><DataAnalysis /></el-icon>
              数据看板
            </el-dropdown-item>
            <el-dropdown-item @click="$emit('change-password')">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-dropdown-item>
            <!-- divided: 显示分割线 -->
            <el-dropdown-item divided @click="handleLogout" class="logout-item">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
/**
 * Header 组件 - 顶部导航栏
 * 
 * 职责：
 * 1. 显示面包屑导航，指示当前页面位置
 * 2. 提供侧边栏折叠/展开控制
 * 3. 显示通知、全屏等功能按钮
 * 4. 显示用户信息，提供快捷操作菜单
 * 
 * 与父组件通信：
 * - Props: 接收父组件传递的状态数据
 * - Events: 通过事件通知父组件执行操作
 */
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'

// ==================== Props 定义 ====================

/**
 * 定义组件接收的属性（父传子）
 * 使用 defineProps 声明，Vue 会自动校验类型
 */
const props = defineProps({
  /** 侧边栏是否收起：控制折叠按钮图标显示 */
  isCollapse: {
    type: Boolean,
    default: false
  },
  /** 是否为移动端：控制是否显示用户名 */
  isMobile: {
    type: Boolean,
    default: false
  },
  /** 未读通知数量：控制通知红点显示 */
  unreadCount: {
    type: Number,
    default: 0
  }
})

// ==================== Events 定义 ====================

/**
 * 定义组件触发的事件（子传父）
 * 父组件通过 @event-name 监听这些事件
 */
const emit = defineEmits([
  'toggle-collapse',      // 切换侧边栏折叠状态
  'open-notifications',   // 打开通知弹窗
  'change-password'       // 打开修改密码弹窗
])

// ==================== 组合式函数 ====================

// Vue Router：用于页面跳转
const router = useRouter()
// 当前路由信息：用于获取页面标题
const route = useRoute()
// Pinia 用户状态：获取当前登录用户信息
const userStore = useUserStore()

// ==================== 计算属性 ====================

/**
 * 当前路由对象
 * 用于面包屑显示当前页面标题
 */
const currentRoute = computed(() => route)

/**
 * 用户角色文本
 * 根据 role 数字映射为中文角色名
 * 1-管理员, 2-社长, 3-学生
 */
const roleText = computed(() => {
  const roleMap = { 1: '管理员', 2: '社长', 3: '学生' }
  return roleMap[userStore.userInfo?.role] || '用户'
})

// ==================== 方法 ====================

/**
 * 切换全屏模式
 * 使用浏览器原生 Fullscreen API
 * 如果当前非全屏，则请求全屏；否则退出全屏
 */
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

/**
 * 处理用户退出登录
 * 1. 弹出确认对话框
 * 2. 用户确认后调用 store 的 logout 方法
 * 3. 跳转到登录页面
 */
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()      // 清除用户状态和 token
    router.push('/login')   // 跳转到登录页
  })
}
</script>

<style lang="scss" scoped>
/* 顶部导航栏容器 - 增强毛玻璃效果 */
.header {
  height: 70px;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  position: relative;
  z-index: 10;
  border-bottom: 1px solid rgba(0, 0, 0, 0.03);

  /* 底部渐变发光线条 - 科技感分割线 */
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 10%;
    right: 10%;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(2, 132, 199, 0.3), rgba(14, 165, 233, 0.2), rgba(6, 182, 212, 0.3), transparent);
    box-shadow: 0 0 8px rgba(2, 132, 199, 0.15), 0 0 20px rgba(2, 132, 199, 0.05);
  }
}

/* 左侧区域：折叠按钮 + 面包屑 */
.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 侧边栏折叠按钮 - 增强悬停效果 */
.collapse-btn {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(248, 250, 252, 0.8);
  border: 1px solid rgba(226, 232, 240, 0.8);

  /* 悬停效果：渐变背景 + 阴影 + 发光 */
  &:hover {
    background: linear-gradient(135deg, #0284c7 0%, #0ea5e9 100%);
    color: #fff;
    border-color: transparent;
    box-shadow: 0 4px 12px rgba(2, 132, 199, 0.35), 0 0 20px rgba(2, 132, 199, 0.15);
    transform: translateY(-2px);
  }

  /* 点击效果：缩小 */
  &:active {
    transform: scale(0.95) translateY(0);
  }
}

/* 面包屑导航样式覆盖 */
.breadcrumb {
  :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
      color: #94a3b8;
      font-weight: 500;

      &.is-link {
        color: #64748b;

        &:hover {
          color: #0284c7;
        }
      }
    }

    &:last-child .el-breadcrumb__inner {
      color: #1e293b;
      font-weight: 600;
    }
  }
}

/* 右侧区域：功能按钮 + 用户 */
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 图标按钮通用样式：通知、全屏 */
.header-icon-btn {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(248, 250, 252, 0.8);
  border: 1px solid rgba(226, 232, 240, 0.8);
  position: relative;
  overflow: hidden;

  /* 悬停效果 */
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    background: rgba(2, 132, 199, 0.1);
    border-radius: 50%;
    transform: translate(-50%, -50%);
    transition: width 0.4s, height 0.4s;
  }

  &:hover {
    background: #fff;
    color: #0284c7;
    border-color: rgba(2, 132, 199, 0.4);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(2, 132, 199, 0.15), 0 0 15px rgba(2, 132, 199, 0.08);

    &::before {
      width: 60px;
      height: 60px;
    }
  }

  &:active {
    transform: translateY(0);
  }

  .el-icon {
    position: relative;
    z-index: 1;
  }
}

/* 通知红点样式 */
.notification-badge {
  :deep(.el-badge__content) {
    background: linear-gradient(135deg, #ef4444 0%, #f87171 100%);
    border: 2px solid #fff;
    font-size: 10px;
    height: 18px;
    line-height: 16px;
    padding: 0 6px;
    border-radius: 9px;
    font-weight: 600;
    box-shadow: 0 2px 4px rgba(239, 68, 68, 0.3);
  }
}

/* 用户下拉菜单容器 */
.user-dropdown {
  margin-left: 4px;
}

/* 用户信息展示区域 - 微妙渐变边框悬停效果 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 14px 6px 6px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(248, 250, 252, 0.8);
  border: 1px solid rgba(226, 232, 240, 0.8);
  position: relative;

  /* 悬停效果 - 渐变边框 */
  &:hover {
    background: #fff;
    border-color: transparent;
    box-shadow:
      0 4px 12px rgba(2, 132, 199, 0.12),
      0 0 20px rgba(2, 132, 199, 0.06);

    /* 渐变边框效果 */
    &::before {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 12px;
      padding: 1px;
      background: linear-gradient(135deg, rgba(2, 132, 199, 0.4), rgba(14, 165, 233, 0.3), rgba(6, 182, 212, 0.4));
      -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      pointer-events: none;
    }

    .user-avatar {
      transform: scale(1.05);
      box-shadow: 0 4px 12px rgba(2, 132, 199, 0.3);
    }

    .dropdown-arrow {
      transform: rotate(180deg);
      color: #0284c7;
    }
  }
}

/* 用户头像 */
.user-avatar {
  border: 2px solid #fff;
  background: linear-gradient(135deg, #0284c7 0%, #0ea5e9 100%);
  color: #fff;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(2, 132, 199, 0.25);
}

/* 用户元信息：昵称 + 角色 */
.user-meta {
  line-height: 1.3;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.user-role {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

/* 下拉箭头 */
.dropdown-arrow {
  color: #94a3b8;
  font-size: 12px;
  transition: all 0.3s ease;
}

/* 下拉菜单样式覆盖 */
:deep(.user-dropdown-menu) {
  padding: 8px;
  min-width: 160px;

  .el-dropdown-menu__item {
    padding: 10px 12px;
    border-radius: 8px;
    margin-bottom: 4px;
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 14px;
    color: #475569;

    &:hover {
      background: #f1f5f9;
      color: #0284c7;
    }

    .el-icon {
      font-size: 16px;
    }

    /* 退出登录项：红色样式 */
    &.logout-item {
      color: #ef4444;

      &:hover {
        background: #fef2f2;
      }
    }
  }
}
</style>
