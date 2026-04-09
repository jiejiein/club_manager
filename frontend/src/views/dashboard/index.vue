<template>
  <div class="dashboard">
    <!-- ==================== 欢迎区域 ==================== -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-text">
          <!-- 欢迎语：显示用户昵称 -->
          <h1 class="welcome-title">
            欢迎回来，{{ userStore.userInfo?.nickname || '用户' }}
            <span class="welcome-wave">👋</span>
          </h1>
          <!-- 根据时间段显示不同的问候语 -->
          <p class="welcome-subtitle">{{ welcomeMessage }}</p>
        </div>
        <!-- 日期显示 -->
        <div class="welcome-date">
          <div class="date-day">{{ currentDay }}</div>
          <div class="date-full">{{ currentDate }}</div>
        </div>
      </div>
    </div>

    <!-- ==================== 统计卡片区域 ==================== -->
    <!-- 
      el-row: Element Plus 栅格布局
      :gutter="20": 列间距 20px
      :xs="24": 超小屏幕（手机）占 24 格（整行）
      :sm="12": 小屏幕占 12 格（半行）
      :lg="6": 大屏幕占 6 格（1/4 行）
    -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="(item, index) in statsCards" :key="item.title">
        <!-- 
          统计卡片
          :class: 动态添加不同颜色的样式类
          :style: 动态设置动画延迟，实现依次出现的动画效果
        -->
        <div class="stat-card" :class="`stat-card-${index + 1}`" :style="{ animationDelay: `${index * 0.1}s` }">
          <!-- 背景装饰图标 -->
          <div class="stat-bg-icon">
            <el-icon :size="80"><component :is="item.icon" /></el-icon>
          </div>
          <!-- 卡片内容 -->
          <div class="stat-content">
            <!-- 图标容器：渐变背景 -->
            <div class="stat-icon-wrapper" :style="{ background: item.gradient }">
              <el-icon :size="24"><component :is="item.icon" /></el-icon>
            </div>
            <!-- 数值和标题 -->
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(item.value) }}</div>
              <div class="stat-title">{{ item.title }}</div>
            </div>
          </div>
          <!-- 趋势指示 -->
          <div class="stat-trend" v-if="item.trend">
            <el-icon :size="14"><component :is="item.trend > 0 ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
            <span :class="item.trend > 0 ? 'trend-up' : 'trend-down'">{{ Math.abs(item.trend) }}%</span>
            <span class="trend-text">较上月</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- ==================== 图表区域 ==================== -->
    <el-row :gutter="20" class="charts-row">
      <!-- 左侧：社团成员分布图表 -->
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title">
              <el-icon><PieChart /></el-icon>
              <span>社团成员分布</span>
            </div>
            <!-- 图表类型切换：饼图/柱状图 -->
            <el-radio-group v-model="memberChartType" size="small">
              <el-radio-button label="pie">饼图</el-radio-button>
              <el-radio-button label="bar">柱状</el-radio-button>
            </el-radio-group>
          </div>
          <!-- ECharts 图表容器 -->
          <div ref="memberChartRef" class="chart-container"></div>
        </div>
      </el-col>
      
      <!-- 右侧：活动参与统计图表 -->
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title">
              <el-icon><TrendCharts /></el-icon>
              <span>活动参与统计</span>
            </div>
            <el-tag type="success" effect="plain" size="small">近30天</el-tag>
          </div>
          <div ref="activityChartRef" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <!-- ==================== 底部区域：快捷操作 + 最新通知 ==================== -->
    <el-row :gutter="20" class="bottom-row">
      <!-- 快捷操作 -->
      <el-col :xs="24" :lg="16">
        <div class="section-card quick-actions">
          <div class="section-header">
            <div class="section-title">
              <el-icon><Operation /></el-icon>
              <span>快捷操作</span>
            </div>
          </div>
          <!-- 快捷操作网格 -->
          <div class="actions-grid">
            <div 
              v-for="action in quickActions" 
              :key="action.name" 
              class="action-item" 
              @click="router.push(action.path)"
            >
              <div class="action-icon" :style="{ background: action.bgColor }">
                <el-icon :size="24"><component :is="action.icon" /></el-icon>
              </div>
              <span class="action-name">{{ action.name }}</span>
            </div>
          </div>
        </div>
      </el-col>
      
      <!-- 最新通知 -->
      <el-col :xs="24" :lg="8">
        <div class="section-card recent-notices">
          <div class="section-header">
            <div class="section-title">
              <el-icon><Bell /></el-icon>
              <span>最新通知</span>
            </div>
            <el-link type="primary" :underline="false" @click="router.push('/notification')">查看更多</el-link>
          </div>
          <!-- 通知列表 -->
          <div class="notice-list">
            <div v-for="(notice, index) in recentNotices" :key="index" class="notice-item">
              <!-- 状态圆点：不同颜色表示不同类型 -->
              <div class="notice-dot" :class="`dot-${notice.type}`"></div>
              <div class="notice-content">
                <div class="notice-title">{{ notice.title }}</div>
                <div class="notice-time">{{ notice.time }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
/**
 * Dashboard 首页组件 - 数据看板
 * 
 * 功能模块：
 * 1. 欢迎区域：显示用户信息和当前日期
 * 2. 统计卡片：展示关键数据指标（根据角色显示不同数据）
 * 3. 数据图表：使用 ECharts 展示社团成员分布和活动参与情况
 * 4. 快捷操作：根据角色提供常用功能入口
 * 5. 最新通知：展示系统通知列表
 * 
 * 数据来源：
 * - 统计数据：调用后端 API 获取
 * - 图表数据：调用分页 API 获取社团和活动数据
 * - 用户信息：从 Pinia Store 获取
 */
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'  // 图表库
import { useUserStore } from '@/stores/user'
import { getAdminDashboard, getPresidentDashboard } from '@/api/dashboard'
import { getClubPage } from '@/api/club'
import { getActivityPage } from '@/api/activity'

// ==================== 组合式函数 ====================

// 路由实例：用于页面跳转
const router = useRouter()
// 用户状态管理
const userStore = useUserStore()

// ==================== 响应式状态 ====================

/** 社团成员分布图表的 DOM 引用 */
const memberChartRef = ref(null)
/** 活动参与统计图表的 DOM 引用 */
const activityChartRef = ref(null)
/** 社团成员图表类型：'pie' 饼图 / 'bar' 柱状图 */
const memberChartType = ref('pie')
/** 仪表盘统计数据 */
const dashboardData = ref({})

// ==================== 计算属性 ====================

/**
 * 当前日期（日）
 * 用于欢迎区域的大号日期显示
 */
const currentDay = computed(() => {
  return new Date().getDate()
})

/**
 * 完整日期字符串
 * 格式：2024年3月 周一
 */
const currentDate = computed(() => {
  const date = new Date()
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${date.getFullYear()}年${date.getMonth() + 1}月 ${weekDays[date.getDay()]}`
})

/**
 * 根据当前时间段生成欢迎语
 * 凌晨/早上/上午/中午/下午/晚上
 */
const welcomeMessage = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，注意休息哦'
  if (hour < 9) return '早上好，开启美好的一天'
  if (hour < 12) return '上午好，工作顺利'
  if (hour < 14) return '中午好，记得休息'
  if (hour < 18) return '下午好，继续加油'
  return '晚上好，今天辛苦了'
})

/**
 * 统计卡片数据
 * 根据用户角色返回不同的统计数据：
 * - 管理员：社团总数、用户总数、活动总数、待审核申请
 * - 社长：成员数量、活动总数
 */
const statsCards = computed(() => {
  if (userStore.userInfo?.role === 1) {
    // 管理员看板数据
    return [
      { 
        title: '社团总数', 
        value: dashboardData.value.totalClubs || 0, 
        icon: 'OfficeBuilding', 
        gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        trend: 12  // 较上月增长 12%
      },
      { 
        title: '用户总数', 
        value: dashboardData.value.totalUsers || 0, 
        icon: 'User', 
        gradient: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
        trend: 8
      },
      { 
        title: '活动总数', 
        value: dashboardData.value.totalActivities || 0, 
        icon: 'Calendar', 
        gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
        trend: 15
      },
      { 
        title: '待审核申请', 
        value: dashboardData.value.pendingApplies || 0, 
        icon: 'Document', 
        gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
        trend: -5  // 负数表示下降
      }
    ]
  } else {
    // 社长看板数据
    return [
      { 
        title: '成员数量', 
        value: dashboardData.value.totalMembers || 0, 
        icon: 'User', 
        gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        trend: 10
      },
      { 
        title: '活动总数', 
        value: dashboardData.value.totalActivities || 0, 
        icon: 'Calendar', 
        gradient: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
        trend: 5
      }
    ]
  }
})

/**
 * 快捷操作列表
 * 根据用户角色返回不同的快捷入口：
 * - 管理员：创建活动、发布通知、费用管理、申请审批、用户管理、社团类型
 * - 社长：创建活动、发布通知、费用管理、申请审批
 * - 学生：我的社团、我的活动、我的费用
 */
const quickActions = computed(() => {
  const role = userStore.userInfo?.role
  
  // 管理员操作
  if (role === 1) {
    return [
      { name: '创建活动', icon: 'Plus', path: '/activity', bgColor: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
      { name: '发布通知', icon: 'Bell', path: '/notification', bgColor: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
      { name: '费用管理', icon: 'Wallet', path: '/payment', bgColor: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
      { name: '申请审批', icon: 'DocumentChecked', path: '/apply', bgColor: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
      { name: '用户管理', icon: 'UserFilled', path: '/user', bgColor: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
      { name: '社团类型', icon: 'Grid', path: '/club-type', bgColor: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)' }
    ]
  }
  // 社长操作
  else if (role === 2) {
    return [
      { name: '创建活动', icon: 'Plus', path: '/activity', bgColor: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
      { name: '发布通知', icon: 'Bell', path: '/notification', bgColor: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
      { name: '费用管理', icon: 'Wallet', path: '/payment', bgColor: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
      { name: '申请审批', icon: 'DocumentChecked', path: '/apply', bgColor: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
    ]
  }
  // 学生操作
  else {
    return [
      { name: '我的社团', icon: 'OfficeBuilding', path: '/my-clubs', bgColor: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
      { name: '我的活动', icon: 'Calendar', path: '/my-activities', bgColor: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)' },
      { name: '我的费用', icon: 'Wallet', path: '/my-payments', bgColor: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }
    ]
  }
})

/**
 * 最新通知列表（模拟数据）
 * type: info-信息, warning-警告, success-成功, danger-危险
 */
const recentNotices = ref([
  { title: '新学期社团招新活动开始报名', time: '10分钟前', type: 'info' },
  { title: '系统将于今晚进行维护升级', time: '1小时前', type: 'warning' },
  { title: '恭喜您成功创建新的社团', time: '2小时前', type: 'success' },
  { title: '有新的活动申请待审批', time: '3小时前', type: 'danger' },
])

// ==================== 方法 ====================

/**
 * 格式化数字
 * 大于 10000 显示为 "x.xw" 格式
 * @param {number} num - 要格式化的数字
 * @returns {string} 格式化后的字符串
 */
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toString()
}

/**
 * 加载仪表盘数据
 * 根据用户角色调用不同的 API：
 * - 管理员：获取全局统计数据
 * - 社长：获取自己社团的统计数据
 */
const loadDashboard = async () => {
  try {
    if (userStore.userInfo?.role === 1) {
      // 管理员：获取全局统计数据
      const res = await getAdminDashboard()
      dashboardData.value = res.data
    } else if (userStore.userInfo?.role === 2) {
      // 社长：先获取自己的社团，再获取该社团的统计数据
      const myClubs = await getClubPage({ current: 1, size: 1 })
      if (myClubs.data.records.length > 0) {
        const res = await getPresidentDashboard(myClubs.data.records[0].id)
        dashboardData.value = res.data
      }
    }
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

// 图表实例（用于后续更新和销毁）
let memberChart = null
let activityChart = null

/**
 * 初始化图表
 * 1. 获取社团和活动数据
 * 2. 初始化 ECharts 实例
 * 3. 设置图表配置项
 */
const initCharts = async () => {
  // 获取数据
  const clubs = await getClubPage({ current: 1, size: 10 })
  const activities = await getActivityPage({ current: 1, size: 10 })

  // 初始化成员分布图表
  memberChart = echarts.init(memberChartRef.value)
  updateMemberChart(clubs.data.records)

  // 初始化活动参与图表（柱状图）
  activityChart = echarts.init(activityChartRef.value)
  activityChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e2e8f0',
      borderWidth: 1,
      textStyle: { color: '#1e293b' },
      formatter: '{b}<br/>{a}: <b>{c}</b>人'  // 提示框格式
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true  // 包含坐标轴标签
    },
    xAxis: {
      type: 'category',
      data: activities.data.records.map(a => a.title?.slice(0, 6) || '未命名'),
      axisLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f1f5f9' } },
      axisLabel: { color: '#64748b' }
    },
    series: [{
      name: '报名人数',
      type: 'bar',
      data: activities.data.records.map(a => a.currentParticipants || 0),
      itemStyle: {
        borderRadius: [8, 8, 0, 0],  // 圆角
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ])
      },
      barWidth: '50%'
    }]
  })
}

/**
 * 更新社团成员分布图表
 * 根据 memberChartType 切换饼图或柱状图
 * @param {Array} clubs - 社团数据列表
 */
const updateMemberChart = (clubs) => {
  // 饼图配置
  const option = memberChartType.value === 'pie' ? {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e2e8f0',
      borderWidth: 1,
      textStyle: { color: '#1e293b' },
      formatter: '{b}<br/>{c}人 ({d}%)'  // 显示名称、人数、百分比
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { color: '#64748b' }
    },
    series: [{
      name: '成员分布',
      type: 'pie',
      radius: ['40%', '70%'],  // 环形饼图
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      data: clubs.map(c => ({ 
        name: c.name, 
        value: c.memberCount || 0 
      }))
    }]
  } : {
    // 柱状图配置
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e2e8f0',
      borderWidth: 1,
      textStyle: { color: '#1e293b' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f1f5f9' } },
      axisLabel: { color: '#64748b' }
    },
    yAxis: {
      type: 'category',
      data: clubs.map(c => c.name?.slice(0, 8) || '未命名'),
      axisLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b' }
    },
    series: [{
      name: '成员数',
      type: 'bar',
      data: clubs.map(c => c.memberCount || 0),
      itemStyle: {
        borderRadius: [0, 8, 8, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ])
      },
      barWidth: '60%'
    }]
  }
  
  // 更新图表配置
  memberChart.setOption(option, true)
}

// ==================== 监听器 ====================

/**
 * 监听图表类型变化
 * 当用户切换饼图/柱状图时，重新渲染图表
 */
watch(memberChartType, () => {
  if (memberChart) {
    getClubPage({ current: 1, size: 10 }).then(res => {
      updateMemberChart(res.data.records)
    })
  }
})

// ==================== 生命周期钩子 ====================

/**
 * 组件挂载时执行
 * 1. 加载仪表盘数据
 * 2. 初始化图表
 * 3. 监听窗口大小变化，自适应调整图表尺寸
 */
onMounted(() => {
  loadDashboard()
  initCharts()
  
  // 窗口大小变化时，图表自适应
  window.addEventListener('resize', () => {
    memberChart?.resize()
    activityChart?.resize()
  })
})
</script>

<style lang="scss" scoped>
// ==================== 整体布局 - 网格背景纹理 ====================
.dashboard {
  padding: 0;
  position: relative;
  min-height: 100vh;

  // 微妙的网格背景纹理
  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image:
      linear-gradient(rgba(102, 126, 234, 0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(102, 126, 234, 0.03) 1px, transparent 1px);
    background-size: 40px 40px;
    pointer-events: none;
    z-index: 0;
  }

  // 确保所有子元素在网格之上
  > * {
    position: relative;
    z-index: 1;
  }
}

// ==================== 欢迎区域 - 流动渐变背景 ====================
.welcome-section {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 30%, #a855f7 60%, #6366f1 100%);
  background-size: 200% 200%;
  animation: gradientFlow 8s ease infinite;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  color: #fff;
  position: relative;
  overflow: hidden;

  /* 装饰性圆形背景 - 增加层次感 */
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -10%;
    width: 400px;
    height: 400px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    animation: welcome-orb 8s ease-in-out infinite;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -30%;
    right: 10%;
    width: 200px;
    height: 200px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 50%;
    animation: welcome-orb 6s ease-in-out infinite reverse;
  }
}

@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes welcome-orb {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(20px, -20px) scale(1.1); }
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 挥手动画 */
.welcome-wave {
  animation: wave 2s infinite;
  display: inline-block;
}

@keyframes wave {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(20deg); }
  75% { transform: rotate(-10deg); }
}

.welcome-subtitle {
  font-size: 15px;
  opacity: 0.9;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.08);
}

.welcome-date {
  text-align: right;

  .date-day {
    font-size: 48px;
    font-weight: 700;
    line-height: 1;
    text-shadow: 0 2px 15px rgba(0, 0, 0, 0.15);
  }

  .date-full {
    font-size: 14px;
    opacity: 0.8;
    margin-top: 4px;
  }
}

// ==================== 统计卡片 - 渐变边框 + 3D 翻转微效果 ====================
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: card-slide-up 0.6s ease-out;
  transform-style: preserve-3d;
  perspective: 800px;

  // 渐变边框伪元素
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    border-radius: 16px 16px 0 0;
    opacity: 0;
    transition: opacity 0.4s ease;
  }

  // 不同卡片不同渐变色
  &.stat-card-1::before {
    background: linear-gradient(90deg, #667eea, #764ba2);
  }
  &.stat-card-2::before {
    background: linear-gradient(90deg, #11998e, #38ef7d);
  }
  &.stat-card-3::before {
    background: linear-gradient(90deg, #f093fb, #f5576c);
  }
  &.stat-card-4::before {
    background: linear-gradient(90deg, #4facfe, #00f2fe);
  }

  /* 悬停上浮 + 3D 微翻转效果 */
  &:hover {
    transform: translateY(-6px) rotateX(2deg);
    box-shadow:
      0 20px 40px -10px rgba(99, 102, 241, 0.2), 0 0 30px rgba(99, 102, 241, 0.08);

    &::before {
      opacity: 1;
    }

    .stat-bg-icon {
      opacity: 0.08;
      transform: scale(1.1) rotate(5deg);
    }
  }
}

/* 卡片入场动画 */
@keyframes card-slide-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 背景装饰图标 */
.stat-bg-icon {
  position: absolute;
  right: -20px;
  bottom: -20px;
  opacity: 0.05;
  color: #667eea;
  transition: all 0.4s ease;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 图标容器 */
.stat-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.stat-info {
  flex: 1;
}

/* 数值 */
.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
  margin-bottom: 6px;
}

.stat-title {
  font-size: 14px;
  color: #64748b;
}

/* 趋势指示 */
.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 12px;
  font-size: 13px;

  .trend-up {
    color: #10b981;  // 绿色-上升
    font-weight: 600;
  }

  .trend-down {
    color: #ef4444;  // 红色-下降
    font-weight: 600;
  }

  .trend-text {
    color: #94a3b8;
    margin-left: 4px;
  }
}

// ==================== 图表区域 - 毛玻璃 + 渐变边框 ====================
.charts-row {
  margin-bottom: 24px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 3px;
    background: linear-gradient(90deg, #6366f1, #a855f7, #3b82f6);
  }
}

@keyframes chart-border-flow {
  0% { background-position: 0% 0%; }
  100% { background-position: 200% 0%; }
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;

  .el-icon {
    color: #667eea;
  }
}

.chart-container {
  height: 300px;
}

// ==================== 底部区域 ====================
.bottom-row {
  .section-card {
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(12px) saturate(150%);
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    height: 100%;
    transition: all 0.4s ease;

    &:hover {
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.08), 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    }
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;

  .el-icon {
    color: #667eea;
  }
}

// ==================== 快捷操作 - 悬停发光效果 ====================
.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  // 悬停发光背景
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    background: radial-gradient(circle, rgba(102, 126, 234, 0.08) 0%, transparent 70%);
    border-radius: 50%;
    transform: translate(-50%, -50%);
    transition: all 0.5s ease;
  }

  &:hover {
    background: rgba(248, 250, 252, 0.8);
    transform: translateY(-4px);

    &::before {
      width: 150%;
      height: 150%;
    }
  }
}

.action-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 1;

  .action-item:hover & {
    transform: scale(1.1);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  }
}

.action-name {
  font-size: 13px;
  color: #475569;
  font-weight: 500;
  position: relative;
  z-index: 1;
  transition: color 0.3s;

  .action-item:hover & {
    color: #1e293b;
  }
}

// ==================== 通知列表 - 左侧渐变色条 ====================
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 12px 12px 16px;
  border-radius: 10px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;

  // 左侧渐变色条
  &::before {
    content: '';
    position: absolute;
    top: 8px;
    left: 0;
    width: 3px;
    height: calc(100% - 16px);
    border-radius: 2px;
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  &.dot-info::before {
    background: linear-gradient(180deg, #3b82f6, #60a5fa);
  }
  &.dot-success::before {
    background: linear-gradient(180deg, #10b981, #34d399);
  }
  &.dot-warning::before {
    background: linear-gradient(180deg, #f59e0b, #fbbf24);
  }
  &.dot-danger::before {
    background: linear-gradient(180deg, #ef4444, #f87171);
  }

  &:hover {
    background: #f8fafc;

    &::before {
      opacity: 1;
    }
  }
}

/* 状态圆点 */
.notice-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;
  transition: all 0.3s ease;

  &.dot-info { background: #3b82f6; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); }
  &.dot-success { background: #10b981; box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1); }
  &.dot-warning { background: #f59e0b; box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1); }
  &.dot-danger { background: #ef4444; box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1); }

  .notice-item:hover & {
    transform: scale(1.3);
  }
}

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-title {
  font-size: 14px;
  color: #1e293b;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.3s;

  .notice-item:hover & {
    color: #667eea;
  }
}

.notice-time {
  font-size: 12px;
  color: #94a3b8;
}

// ==================== 响应式适配 ====================
@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .welcome-date {
    text-align: center;
  }

  .welcome-title {
    font-size: 22px;
    justify-content: center;
  }

  .stat-value {
    font-size: 24px;
  }

  .actions-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
