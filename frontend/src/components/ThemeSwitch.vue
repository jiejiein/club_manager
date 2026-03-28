<template>
  <div class="theme-switch-wrapper">
    <el-dropdown trigger="click" placement="bottom-end" :show-timeout="0" :hide-timeout="0">
      <div class="theme-switch-btn" :class="{ 'is-dark': themeStore.isDark }" :title="themeStore.currentTheme?.name">
        <transition name="icon-fade" mode="out-in">
          <el-icon :size="20" :key="themeStore.themeMode">
            <Sunny v-if="themeStore.themeMode === 'light'" />
            <Moon v-else-if="themeStore.themeMode === 'dark'" />
            <Monitor v-else />
          </el-icon>
        </transition>
        <span v-if="showText" class="theme-text">{{ themeStore.currentTheme?.name }}</span>
      </div>
      <template #dropdown>
        <el-dropdown-menu class="theme-dropdown-menu">
          <div class="theme-dropdown-header">
            <span>切换主题</span>
          </div>
          <el-dropdown-item
            v-for="theme in themeStore.themes"
            :key="theme.id"
            :class="{ active: themeStore.themeMode === theme.id }"
            @click="themeStore.setThemeMode(theme.id)"
          >
            <div class="theme-option">
              <div class="theme-icon" :style="{ background: theme.color }">
                <el-icon :size="16">
                  <Sunny v-if="theme.icon === 'Sunny'" />
                  <Moon v-else-if="theme.icon === 'Moon'" />
                  <Monitor v-else />
                </el-icon>
              </div>
              <span class="theme-name">{{ theme.name }}</span>
              <el-icon v-if="themeStore.themeMode === theme.id" class="check-icon"><Check /></el-icon>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { useThemeStore } from '@/stores/theme'

defineProps({
  showText: {
    type: Boolean,
    default: false
  }
})

const themeStore = useThemeStore()
</script>

<style lang="scss" scoped>
.theme-switch-wrapper {
  display: inline-flex;
}

.theme-switch-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-1px);
  }

  &.is-dark {
    background: rgba(102, 126, 234, 0.2);
    border-color: rgba(102, 126, 234, 0.4);
    
    &:hover {
      background: rgba(102, 126, 234, 0.3);
    }
  }
}

.theme-text {
  font-size: 13px;
  font-weight: 500;
}

// 图标切换动画
.icon-fade-enter-active,
.icon-fade-leave-active {
  transition: all 0.2s ease;
}

.icon-fade-enter-from {
  opacity: 0;
  transform: rotate(-90deg) scale(0.5);
}

.icon-fade-leave-to {
  opacity: 0;
  transform: rotate(90deg) scale(0.5);
}

// 下拉菜单样式
:deep(.theme-dropdown-menu) {
  padding: 8px;
  min-width: 180px;
}

.theme-dropdown-header {
  padding: 8px 12px;
  font-size: 12px;
  color: #909399;
  font-weight: 500;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 4px;
}

.theme-option {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.theme-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: transform 0.2s;
}

.theme-name {
  flex: 1;
  font-size: 14px;
}

.check-icon {
  color: #67c23a;
  font-size: 16px;
}

:deep(.el-dropdown-menu__item) {
  padding: 8px 12px;
  border-radius: 8px;
  margin: 2px 0;
  
  &:hover {
    background: #f5f7fa;
    
    .theme-icon {
      transform: scale(1.1);
    }
  }
  
  &.active {
    background: #ecf5ff;
    
    .theme-name {
      color: #409eff;
      font-weight: 500;
    }
  }
}

// 深色模式适配
:deep([data-theme='dark']) {
  .theme-dropdown-header {
    color: #a0a0a0;
    border-bottom-color: #404040;
  }
  
  .el-dropdown-menu__item {
    color: #c0c0c0;
    
    &:hover {
      background: #2a2a2a;
    }
    
    &.active {
      background: #1a2a3a;
      
      .theme-name {
        color: #667eea;
      }
    }
  }
}
</style>
