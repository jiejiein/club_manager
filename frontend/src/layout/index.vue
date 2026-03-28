<template>
  <!-- 整体布局容器：采用 Element Plus 的 Container 布局 -->
  <el-container class="layout-container">
    <!-- 
      侧边栏组件 
      Props: isCollapse - 控制展开/收起状态
    -->
    <Sidebar :is-collapse="isCollapse" />

    <!-- 右侧区域容器：包含顶部导航和主内容区 -->
    <el-container class="right-container">
      <!-- 
        顶部导航栏组件
        Props: 
          - isCollapse: 侧边栏状态，用于控制折叠按钮图标
          - isMobile: 是否移动端，用于响应式显示
          - unreadCount: 未读通知数量，显示红点 badge
        Events:
          - @toggle-collapse: 点击折叠按钮时触发，切换侧边栏状态
          - @open-notifications: 点击通知图标时触发，打开通知弹窗
          - @change-password: 点击修改密码时触发，打开密码弹窗
      -->
      <Header 
        :is-collapse="isCollapse"
        :is-mobile="isMobile"
        :unread-count="unreadCount"
        @toggle-collapse="isCollapse = !isCollapse"
        @open-notifications="notificationDialogVisible = true"
        @change-password="passwordDialogVisible = true"
      />

      <!-- 主内容区：显示路由对应的页面 -->
      <el-main class="main">
        <div class="main-content">
          <!-- 
            路由视图
            v-slot 用于获取当前路由组件，配合 transition 实现页面切换动画
          -->
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>

    <!-- 
      修改密码弹窗组件
      v-model: 双向绑定控制弹窗显示/隐藏
    -->
    <PasswordDialog v-model="passwordDialogVisible" />

    <!-- 
      通知弹窗组件
      Props: notifications - 通知列表数据
      Events:
        - @mark-read: 标记单个通知已读
        - @mark-all-read: 标记全部通知已读
    -->
    <NotificationDialog 
      v-model="notificationDialogVisible"
      :notifications="notifications"
      @mark-read="handleMarkAsRead"
      @mark-all-read="handleMarkAllAsRead"
    />
  </el-container>
</template>

<script setup>
/**
 * 布局主文件 - 负责整体页面结构编排和状态管理
 * 
 * 职责：
 * 1. 组合 Sidebar、Header、PasswordDialog、NotificationDialog 四个子组件
 * 2. 管理共享状态（侧边栏展开状态、通知数据等）
 * 3. 处理子组件事件，更新状态
 * 4. 加载初始数据（通知列表）
 */
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getMyNotifications, getUnreadCount } from '@/api/notification'
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'
import PasswordDialog from './components/PasswordDialog.vue'
import NotificationDialog from './components/NotificationDialog.vue'

// 用户状态管理（Pinia Store）
const userStore = useUserStore()

// ==================== 响应式状态 ====================

/** 侧边栏是否收起：true-收起, false-展开 */
const isCollapse = ref(false)

/** 是否为移动端：用于响应式布局判断 */
const isMobile = ref(false)

/** 修改密码弹窗是否显示 */
const passwordDialogVisible = ref(false)

/** 通知弹窗是否显示 */
const notificationDialogVisible = ref(false)

/** 通知列表数据 */
const notifications = ref([])

/** 未读通知数量 */
const unreadCount = ref(0)

// ==================== 方法 ====================

/**
 * 检查是否为移动端
 * 根据窗口宽度判断，小于 768px 认为是移动端
 * 如果是移动端，自动收起侧边栏
 */
const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) {
    isCollapse.value = true
  }
}

/**
 * 加载通知数据
 * 并行请求通知列表和未读数量，提高性能
 */
const loadNotifications = async () => {
  try {
    const [notificationsRes, unreadCountRes] = await Promise.all([
      getMyNotifications(),      // 获取通知列表
      getUnreadCount()           // 获取未读数量
    ])
    notifications.value = notificationsRes.data
    unreadCount.value = unreadCountRes.data
  } catch (error) {
    console.error('加载通知失败:', error)
  }
}

/**
 * 处理标记单个通知已读
 * @param {number} notificationId - 通知ID
 */
const handleMarkAsRead = (notificationId) => {
  // 找到对应通知，更新状态为已读
  const notification = notifications.value.find(item => item.id === notificationId)
  if (notification) {
    notification.isRead = 1
  }
  // 未读数量减1，最小为0
  unreadCount.value = Math.max(0, unreadCount.value - 1)
}

/**
 * 处理标记全部通知已读
 */
const handleMarkAllAsRead = () => {
  // 将所有通知标记为已读
  notifications.value.forEach(notification => {
    notification.isRead = 1
  })
  // 未读数量清零
  unreadCount.value = 0
}

// ==================== 生命周期钩子 ====================

/** 
 * 组件挂载时执行
 * 1. 检查是否为移动端
 * 2. 监听窗口大小变化
 * 3. 加载通知数据
 */
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  loadNotifications()
})

/** 
 * 组件卸载时执行
 * 清理事件监听，防止内存泄漏
 */
onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style lang="scss" scoped>
/* 整体布局容器：占满整个视口高度 */
.layout-container {
  height: 100vh;
  background: var(--el-bg-color-page);
}

/* 右侧区域容器：垂直排列（顶部导航 + 主内容） */
.right-container {
  flex-direction: column;
  overflow: hidden;
}

/* 主内容区：占满剩余空间，可滚动 */
.main {
  flex: 1;
  padding: 0;
  background: var(--el-bg-color-page);
  overflow: hidden;
  position: relative;

  /* 顶部装饰性渐变背景 - 夜间模式下更暗 */
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 300px;
    background: linear-gradient(180deg, var(--el-color-primary-light-9) 0%, transparent 100%);
    pointer-events: none;
  }
}

/* 内容滚动区域 */
.main-content {
  height: 100%;
  overflow-y: auto;
  padding: 24px;
  position: relative;
  z-index: 1;

  /* 自定义滚动条样式 */
  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background: linear-gradient(180deg, #cbd5e1 0%, #94a3b8 100%);
    border-radius: 4px;
    border: 2px solid transparent;
    background-clip: content-box;

    &:hover {
      background: linear-gradient(180deg, #94a3b8 0%, #64748b 100%);
    }
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-corner {
    background: transparent;
  }
}

/* 页面切换动画：淡入 + 左右滑动 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .main-content {
    padding: 16px;
  }
}
</style>
