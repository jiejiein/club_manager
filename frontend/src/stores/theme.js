import { defineStore } from 'pinia'
import { ref, watch, computed } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  // 主题模式：light、dark、auto
  const themeMode = ref('light')
  // 当前实际应用的主题（auto模式下根据系统设置决定）
  const isDark = ref(false)
  // 系统主题偏好监听器
  let mediaQueryListener = null

  // 可用的主题列表
  const themes = [
    { id: 'light', name: '日间模式', icon: 'Sunny', color: '#f5a623' },
    { id: 'dark', name: '夜间模式', icon: 'Moon', color: '#0284c7' },
    { id: 'auto', name: '跟随系统', icon: 'Monitor', color: '#43e97b' }
  ]

  // 当前主题信息
  const currentTheme = computed(() => {
    return themes.find(t => t.id === themeMode.value) || themes[0]
  })

  // 初始化主题
  const initTheme = () => {
    const savedMode = localStorage.getItem('themeMode') || 'light'
    themeMode.value = savedMode
    applyThemeMode()
  }

  // 应用主题模式
  const applyThemeMode = () => {
    if (themeMode.value === 'auto') {
      // 跟随系统
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      isDark.value = prefersDark
      setupSystemThemeListener()
    } else {
      // 手动设置
      isDark.value = themeMode.value === 'dark'
      removeSystemThemeListener()
    }
    applyTheme()
  }

  // 设置系统主题监听器
  const setupSystemThemeListener = () => {
    if (!mediaQueryListener) {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
      mediaQueryListener = (e) => {
        isDark.value = e.matches
        applyTheme()
      }
      mediaQuery.addEventListener('change', mediaQueryListener)
    }
  }

  // 移除系统主题监听器
  const removeSystemThemeListener = () => {
    if (mediaQueryListener) {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
      mediaQuery.removeEventListener('change', mediaQueryListener)
      mediaQueryListener = null
    }
  }

  // 切换主题模式
  const setThemeMode = (mode) => {
    themeMode.value = mode
    localStorage.setItem('themeMode', mode)
    applyThemeMode()
  }

  // 切换到下一个主题
  const toggleTheme = () => {
    const modes = ['light', 'dark', 'auto']
    const currentIndex = modes.indexOf(themeMode.value)
    const nextIndex = (currentIndex + 1) % modes.length
    setThemeMode(modes[nextIndex])
  }

  // 应用主题到DOM
  const applyTheme = () => {
    const html = document.documentElement
    
    if (isDark.value) {
      html.classList.add('dark')
      html.setAttribute('data-theme', 'dark')
    } else {
      html.classList.remove('dark')
      html.setAttribute('data-theme', 'light')
    }
    
    // 触发主题变化事件，供其他组件监听
    window.dispatchEvent(new CustomEvent('themechange', { 
      detail: { isDark: isDark.value, mode: themeMode.value }
    }))
  }

  // 监听主题变化
  watch(isDark, () => {
    applyTheme()
  })

  // 清理监听器
  const cleanup = () => {
    removeSystemThemeListener()
  }

  return {
    themeMode,
    isDark,
    themes,
    currentTheme,
    initTheme,
    setThemeMode,
    toggleTheme,
    cleanup
  }
})
