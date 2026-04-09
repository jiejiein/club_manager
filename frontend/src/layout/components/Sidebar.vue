<template>
  <!-- 
    侧边栏容器
    :width 动态计算：收起时 72px，展开时 260px
    使用 transition 实现宽度变化动画
  -->
  <el-aside :width="isCollapse ? '72px' : '260px'" class="aside">
    <!-- Logo 区域 -->
    <div class="logo-section">
      <!-- Logo 图标：固定显示 -->
      <div class="logo-icon">
        <el-icon :size="28" color="#fff"><School /></el-icon>
      </div>
      <!-- 
        Logo 文字和主题切换
        展开时显示完整内容（文字 + 主题切换）
      -->
      <div v-if="!isCollapse" class="logo-content">
        <span class="logo-text">高校社团与活动管理平台</span>
        <ThemeSwitch />
      </div>
      <!-- 收起时只显示主题切换按钮 -->
      <div v-else class="theme-toggle-mini">
        <ThemeSwitch />
      </div>
    </div>

    <!-- 菜单区域 -->
    <div class="menu-wrapper">
      <!-- 
        Element Plus 菜单组件
        :default-active: 当前激活的菜单项（高亮显示）
        :collapse: 是否收起
        :collapse-transition: 禁用默认的折叠动画（使用自定义动画）
        router: 启用 vue-router 模式，点击菜单自动跳转
      -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="custom-menu"
      >
        <!-- 首页/数据看板 - 所有角色可见 -->
        <el-menu-item index="/dashboard" class="menu-item">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>
            <span class="menu-title">数据看板</span>
          </template>
        </el-menu-item>

        <!-- ==================== 管理员菜单 (role === 1) ==================== -->
        <template v-if="userRole === 1">
          <!-- 菜单分组标题 -->
          <div v-if="!isCollapse" class="menu-divider">管理</div>
          
          <el-menu-item index="/user" class="menu-item">
            <el-icon><User /></el-icon>
            <template #title>
              <span class="menu-title">用户管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/club-type" class="menu-item">
            <el-icon><Grid /></el-icon>
            <template #title>
              <span class="menu-title">社团类型</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/club" class="menu-item">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>
              <span class="menu-title">社团管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/activity" class="menu-item">
            <el-icon><Calendar /></el-icon>
            <template #title>
              <span class="menu-title">活动管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/apply" class="menu-item">
            <el-icon><Document /></el-icon>
            <template #title>
              <span class="menu-title">申请管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/notification" class="menu-item">
            <el-icon><Bell /></el-icon>
            <template #title>
              <span class="menu-title">通知管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/payment" class="menu-item">
            <el-icon><Wallet /></el-icon>
            <template #title>
              <span class="menu-title">费用管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/president-transfer" class="menu-item">
            <el-icon><Switch /></el-icon>
            <template #title>
              <span class="menu-title">社长转移申请</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/system-log" class="menu-item">
            <el-icon><Document /></el-icon>
            <template #title>
              <span class="menu-title">系统日志</span>
            </template>
          </el-menu-item>
          
          <div v-if="!isCollapse" class="menu-divider">我的</div>
          <el-menu-item index="/profile" class="menu-item">
            <el-icon><UserFilled /></el-icon>
            <template #title>
              <span class="menu-title">个人资料</span>
            </template>
          </el-menu-item>
        </template>

        <!-- ==================== 社长菜单 (role === 2) ==================== -->
        <template v-if="userRole === 2">
          <div v-if="!isCollapse" class="menu-divider">我的社团</div>
          
          <el-menu-item index="/club" class="menu-item">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>
              <span class="menu-title">社团信息</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/activity" class="menu-item">
            <el-icon><Calendar /></el-icon>
            <template #title>
              <span class="menu-title">活动管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/apply" class="menu-item">
            <el-icon><Document /></el-icon>
            <template #title>
              <span class="menu-title">入团申请</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/notification" class="menu-item">
            <el-icon><Bell /></el-icon>
            <template #title>
              <span class="menu-title">通知发布</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/payment" class="menu-item">
            <el-icon><Wallet /></el-icon>
            <template #title>
              <span class="menu-title">费用管理</span>
            </template>
          </el-menu-item>
          
          <div v-if="!isCollapse" class="menu-divider">我的</div>
          <el-menu-item index="/profile" class="menu-item">
            <el-icon><UserFilled /></el-icon>
            <template #title>
              <span class="menu-title">个人资料</span>
            </template>
          </el-menu-item>
        </template>

        <!-- ==================== 学生菜单 (role === 3) ==================== -->
        <template v-if="userRole === 3">
          <div v-if="!isCollapse" class="menu-divider">我的</div>
          
          <el-menu-item index="/my-club" class="menu-item">
            <el-icon><Star /></el-icon>
            <template #title>
              <span class="menu-title">我的社团</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/my-activity" class="menu-item">
            <el-icon><Clock /></el-icon>
            <template #title>
              <span class="menu-title">我的活动</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/my-payment" class="menu-item">
            <el-icon><Money /></el-icon>
            <template #title>
              <span class="menu-title">我的缴费</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/notification" class="menu-item">
            <el-icon><Bell /></el-icon>
            <template #title>
              <span class="menu-title">通知管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/profile" class="menu-item">
            <el-icon><UserFilled /></el-icon>
            <template #title>
              <span class="menu-title">个人资料</span>
            </template>
          </el-menu-item>
        </template>
      </el-menu>
    </div>
  </el-aside>
</template>

<script setup>
/**
 * Sidebar 组件 - 侧边导航栏
 * 
 * 职责：
 * 1. 显示 Logo 和主题切换
 * 2. 根据用户角色显示不同的菜单项
 * 3. 支持展开/收起状态切换
 * 4. 高亮显示当前激活的菜单项
 * 
 * 角色权限：
 * - 管理员(role=1)：拥有所有管理功能
 * - 社长(role=2)：管理自己的社团
 * - 学生(role=3)：查看个人信息和参与的社团活动
 */
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import ThemeSwitch from '@/components/ThemeSwitch.vue'

// ==================== Props 定义 ====================

/**
 * 定义组件接收的属性
 * isCollapse: 控制侧边栏展开/收起状态
 */
const props = defineProps({
  /** 侧边栏是否收起 */
  isCollapse: {
    type: Boolean,
    default: false
  }
})

// ==================== 组合式函数 ====================

// 当前路由信息：用于确定哪个菜单项应该高亮
const route = useRoute()
// Pinia 用户状态：获取当前用户角色，用于权限控制
const userStore = useUserStore()

// ==================== 计算属性 ====================

/**
 * 当前激活的菜单项
 * 根据当前路由路径确定，用于菜单高亮显示
 */
const activeMenu = computed(() => route.path)

/**
 * 当前用户角色
 * 1-管理员, 2-社长, 3-学生
 * 用于控制显示哪些菜单项
 */
const userRole = computed(() => userStore.userInfo?.role)
</script>

<style lang="scss" scoped>
/* 侧边栏容器：深色渐变背景 + 微妙网格纹理 */
.aside {
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.15);
  position: relative;

  /* 顶部装饰性渐变 */
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 200px;
    background: linear-gradient(180deg, rgba(2, 132, 199, 0.12) 0%, rgba(14, 165, 233, 0.05) 50%, transparent 100%);
    pointer-events: none;
  }

  /* 微妙网格纹理叠加 */
  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(255, 255, 255, 0.02) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255, 255, 255, 0.02) 1px, transparent 1px);
    background-size: 40px 40px;
    pointer-events: none;
    z-index: 0;
  }
}

/* Logo 区域 */
.logo-section {
  height: 70px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  position: relative;
  z-index: 1;
}

/* Logo 图标 - 呼吸发光动画 */
.logo-icon {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #0284c7 0%, #0ea5e9 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 15px rgba(2, 132, 199, 0.4);
  transition: all 0.3s ease;
  animation: glowPulse 3s ease-in-out infinite;

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 20px rgba(2, 132, 199, 0.6), 0 0 30px rgba(2, 132, 199, 0.2);
  }
}

/* Logo 文字 */
.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.3px;
  white-space: nowrap;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* Logo 内容区：文字 + 主题切换 */
.logo-content {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;

  .logo-text {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

/* 收起状态下的主题切换按钮 */
.theme-toggle-mini {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 菜单滚动区域 */
.menu-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 16px 12px;
  position: relative;
  z-index: 1;

  /* 自定义滚动条 */
  &::-webkit-scrollbar {
    width: 5px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.15);
    border-radius: 3px;

    &:hover {
      background: rgba(255, 255, 255, 0.25);
    }
  }

  /* 底部装饰性渐变光晕 */
  &::after {
    content: '';
    position: sticky;
    bottom: 0;
    left: 0;
    right: 0;
    display: block;
    height: 60px;
    background: linear-gradient(0deg, rgba(15, 23, 42, 0.9) 0%, transparent 100%);
    pointer-events: none;
    margin: 0 -12px -16px;
    padding: 0 12px;
  }
}

/* 菜单样式覆盖 */
.custom-menu {
  background: transparent;
  border: none;

  /* 菜单项样式 */
  :deep(.el-menu-item) {
    height: 48px;
    line-height: 48px;
    margin-bottom: 6px;
    border-radius: 10px;
    color: #94a3b8;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;

    /* 左侧激活指示条 */
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      width: 3px;
      background: linear-gradient(180deg, #0284c7 0%, #0ea5e9 50%, #06b6d4 100%);
      opacity: 0;
      transition: opacity 0.3s;
    }

    /* 悬停效果 */
    &:hover {
      background: rgba(255, 255, 255, 0.08);
      color: #fff;

      .el-icon {
        color: #fff;
      }
    }

    /* 激活状态 - 更强的光影效果和渐变边框 */
    &.is-active {
      background: linear-gradient(135deg, rgba(2, 132, 199, 0.25) 0%, rgba(14, 165, 233, 0.15) 50%, rgba(6, 182, 212, 0.2) 100%);
      color: #fff;
      box-shadow:
        0 4px 15px rgba(2, 132, 199, 0.3),
        0 0 20px rgba(2, 132, 199, 0.1),
        inset 0 1px 0 rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(2, 132, 199, 0.35);

      &::before {
        opacity: 1;
      }

      .el-icon {
        color: #fff;
      }
    }

    .el-icon {
      color: #64748b;
      font-size: 18px;
      transition: all 0.3s;
    }

    &:hover .el-icon {
      color: #fff;
    }
  }

  /* 子菜单标题样式 */
  :deep(.el-sub-menu__title) {
    height: 48px;
    line-height: 48px;
    border-radius: 10px;
    color: #94a3b8;

    &:hover {
      background: rgba(255, 255, 255, 0.05);
      color: #fff;
    }
  }
}

/* 菜单标题文字 */
.menu-title {
  font-size: 14px;
  font-weight: 500;
  margin-left: 4px;
}

/* 菜单分组标题 - 渐变色 */
.menu-divider {
  padding: 16px 16px 8px;
  font-size: 11px;
  font-weight: 600;
  background: linear-gradient(90deg, rgba(2, 132, 199, 0.6), rgba(14, 165, 233, 0.5), rgba(6, 182, 212, 0.6));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-transform: uppercase;
  letter-spacing: 1px;
}
</style>
