<template>
  <div class="my-activity-page">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2>我的活动</h2>
        <p>探索精彩活动，记录美好时光</p>
      </div>
      <div class="stats-overview">
        <div class="stat-item">
          <div class="stat-value">{{ stats.signedUp }}</div>
          <div class="stat-label">已报名</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.ongoing }}</div>
          <div class="stat-label">进行中</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.completed }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
    </div>

    <!-- 活动筛选 -->
    <el-card class="filter-section" shadow="never">
      <div class="filter-bar">
        <div class="filter-tabs">
          <div 
            v-for="tab in tabs" 
            :key="tab.value"
            class="filter-tab"
            :class="{ active: activeTab === tab.value }"
            @click="activeTab = tab.value"
          >
            <el-icon :size="16"><component :is="tab.icon" /></el-icon>
            <span>{{ tab.label }}</span>
            <el-badge v-if="tab.count" :value="tab.count" class="tab-badge" />
          </div>
        </div>
        <div class="filter-search">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索活动..."
            clearable
            prefix-icon="Search"
          />
        </div>
      </div>
    </el-card>

    <!-- 活动列表 -->
    <div class="activity-list">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :lg="8" v-for="activity in filteredActivities" :key="activity.id">
          <div class="activity-card" :class="`status-${activity.status}`">
            <div class="card-cover">
              <div class="cover-placeholder" :style="{ background: getActivityGradient(activity.id) }">
                <el-icon :size="48" color="#fff"><Calendar /></el-icon>
              </div>
              <div class="status-badge" :class="`status-${activity.status}`">
                {{ activity.statusName }}
              </div>
            </div>
            <div class="card-content">
              <h3 class="activity-title">{{ activity.title }}</h3>
              <div class="activity-club">
                <el-icon><OfficeBuilding /></el-icon>
                <span>{{ activity.clubName }}</span>
              </div>
              <div class="activity-meta">
                <div class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ activity.location || '待定' }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatDateTime(activity.startTime) }}</span>
                </div>
              </div>
              <div class="activity-progress">
                <div class="progress-info">
                  <span>报名进度</span>
                  <span>{{ activity.currentParticipants }}/{{ activity.maxParticipants }}</span>
                </div>
                <el-progress 
                  :percentage="Math.round((activity.currentParticipants / activity.maxParticipants) * 100)" 
                  :show-text="false"
                  :stroke-width="6"
                  :color="getProgressColor(activity)"
                />
              </div>
              <div class="card-actions">
                <template v-if="activity.mySignUpStatus === undefined">
                  <el-button 
                    v-if="canSignUp(activity) && isClubMember(activity.clubId)" 
                    type="primary" 
                    @click="handleSignUp(activity)"
                    :loading="signingUp === activity.id"
                  >
                    <el-icon><Plus /></el-icon>
                    立即报名
                  </el-button>
                  <el-button v-else-if="canSignUp(activity) && !isClubMember(activity.clubId)" disabled>
                    非社团成员
                  </el-button>
                  <el-button v-else disabled>报名已截止</el-button>
                </template>
                <template v-else>
                  <el-tag :type="getSignUpStatusType(activity.mySignUpStatus)">
                    {{ getSignUpStatusText(activity.mySignUpStatus) }}
                  </el-tag>
                  <el-button 
                    v-if="activity.mySignUpStatus === 0" 
                    type="danger" 
                    link
                    @click="handleCancel(activity)"
                  >
                    取消报名
                  </el-button>
                </template>
                <el-button type="primary" link @click="viewDetail(activity)">
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-empty v-if="filteredActivities.length === 0" description="暂无相关活动">
        <template #image>
          <el-icon :size="80" color="#dcdfe6"><Calendar /></el-icon>
        </template>
      </el-empty>
    </div>

    <!-- 活动详情弹窗 -->
    <el-dialog v-model="detailVisible" title="活动详情" width="600px" destroy-on-close>
      <div v-if="currentActivity" class="activity-detail">
        <div class="detail-cover" :style="{ background: getActivityGradient(currentActivity.id) }">
          <el-icon :size="64" color="#fff"><Calendar /></el-icon>
        </div>
        <h3 class="detail-title">{{ currentActivity.title }}</h3>
        <div class="detail-tags">
          <el-tag :type="getStatusType(currentActivity.status)">{{ currentActivity.statusName }}</el-tag>
          <el-tag type="info">{{ currentActivity.clubName }}</el-tag>
        </div>
        <el-descriptions :column="1" border class="detail-info">
          <el-descriptions-item label="活动地点">{{ currentActivity.location || '待定' }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDateTime(currentActivity.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDateTime(currentActivity.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="报名截止">{{ formatDateTime(currentActivity.signUpEnd) }}</el-descriptions-item>
          <el-descriptions-item label="报名人数">{{ currentActivity.currentParticipants }}/{{ currentActivity.maxParticipants }} 人</el-descriptions-item>
        </el-descriptions>
        <div class="detail-section">
          <h4>活动简介</h4>
          <p>{{ currentActivity.description || '暂无活动简介' }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyActivities, signUpActivity, cancelSignUp, getAvailableActivities } from '@/api/activity'
import { getMyClubs } from '@/api/club'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Calendar, OfficeBuilding, Location, Clock, Plus, Search,
  Collection, Timer, Check, CircleCheck
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const loading = ref(false)
const allActivities = ref([])
const myActivities = ref([])
const activeTab = ref('all')
const searchKeyword = ref('')
const detailVisible = ref(false)
const currentActivity = ref(null)
const signingUp = ref(null)
const myClubs = ref([]) // 存储用户的社团信息

const tabs = [
  { label: '全部活动', value: 'all', icon: 'Collection', count: 0 },
  { label: '可报名', value: 'available', icon: 'Plus', count: 0 },
  { label: '已报名', value: 'signed', icon: 'CircleCheck', count: 0 },
  { label: '进行中', value: 'ongoing', icon: 'Timer', count: 0 },
  { label: '已结束', value: 'ended', icon: 'Check', count: 0 }
]

// 统计数据
const stats = computed(() => {
  return {
    signedUp: myActivities.value.filter(a => a.mySignUpStatus !== undefined).length,
    ongoing: myActivities.value.filter(a => a.status === 3).length,
    completed: myActivities.value.filter(a => a.status === 4).length
  }
})

// 筛选后的活动
const filteredActivities = computed(() => {
  let result = []
  
  switch (activeTab.value) {
    case 'available':
      result = allActivities.value.filter(a => canSignUp(a) && a.mySignUpStatus === undefined && isClubMember(a.clubId))
      break
    case 'signed':
      result = allActivities.value.filter(a => a.mySignUpStatus !== undefined)
      break
    case 'ongoing':
      result = allActivities.value.filter(a => a.status === 3)
      break
    case 'ended':
      result = allActivities.value.filter(a => a.status === 4 || a.status === 5)
      break
    default:
      result = allActivities.value
  }
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(a => 
      a.title.toLowerCase().includes(keyword) ||
      a.clubName.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 获取活动渐变背景
const getActivityGradient = (id) => {
  const gradients = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)'
  ]
  return gradients[id % gradients.length]
}

// 获取进度条颜色
const getProgressColor = (activity) => {
  const rate = activity.currentParticipants / activity.maxParticipants
  if (rate >= 0.9) return '#f56c6c'
  if (rate >= 0.7) return '#e6a23c'
  return '#67c23a'
}

// 获取状态类型
const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'primary', 4: 'info', 5: 'danger' }
  return types[status] || 'info'
}

// 获取报名状态类型
const getSignUpStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

// 获取报名状态文本
const getSignUpStatusText = (status) => {
  const texts = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return texts[status] || '未知'
}

// 是否可以报名
const canSignUp = (activity) => {
  return activity.status === 2 && dayjs().isBefore(dayjs(activity.signUpEnd))
}

// 检查用户是否是社团成员
const isClubMember = (clubId) => {
  return myClubs.value.some(club => club.id === clubId)
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '待定'
  return dayjs(datetime).format('MM-DD HH:mm')
}

const loadData = async () => {
  loading.value = true
  try {
    // 获取用户的社团信息
    const clubsRes = await getMyClubs()
    myClubs.value = clubsRes.data || []
    
    // 获取所有可报名的活动
    const availableRes = await getAvailableActivities()
    // 获取我的活动
    const myRes = await getMyActivities()
    
    myActivities.value = myRes.data || []
    
    // 合并数据，标记已报名的活动
    const availableActivities = availableRes.data || []
    const myActivityIds = myActivities.value.map(a => a.id)
    
    allActivities.value = availableActivities.map(activity => {
      const myActivity = myActivities.value.find(a => a.id === activity.id)
      return {
        ...activity,
        mySignUpStatus: myActivity ? myActivity.signUpStatus : undefined
      }
    })
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSignUp = async (activity) => {
  signingUp.value = activity.id
  try {
    await signUpActivity(activity.id)
    ElMessage.success('报名成功')
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    signingUp.value = null
  }
}

const handleCancel = async (activity) => {
  try {
    await ElMessageBox.confirm('确定要取消报名吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelSignUp(activity.id)
    ElMessage.success('已取消报名')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const viewDetail = (activity) => {
  currentActivity.value = activity
  detailVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
/* ========== 现代简约科技风 - 学生端 ========== */
@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.my-activity-page {
  padding: 20px;
  position: relative;

  /* ========== 欢迎横幅流动渐变动画 ========== */
  .welcome-banner {
    background: linear-gradient(135deg, #6366f1, #a855f7, #3b82f6, #6366f1, #a855f7);
    background-size: 300% 300%;
    animation: gradientFlow 8s ease infinite;
    border-radius: 16px;
    padding: 30px;
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #fff;
    position: relative;
    overflow: hidden;

    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 50%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
      animation: shimmer 4s ease-in-out infinite;
    }

    .welcome-content {
      position: relative;
      z-index: 1;

      h2 {
        margin: 0 0 8px;
        font-size: 28px;
        text-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
      }

      p {
        margin: 0;
        opacity: 0.9;
        font-size: 14px;
      }
    }

    .stats-overview {
      display: flex;
      gap: 40px;
      position: relative;
      z-index: 1;

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 36px;
          font-weight: bold;
          line-height: 1;
          text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
        }

        .stat-label {
          font-size: 14px;
          opacity: 0.8;
          margin-top: 4px;
        }
      }
    }
  }

  /* ========== 筛选区域毛玻璃 ========== */
  .filter-section {
    margin-bottom: 24px;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border: none;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06);
    position: relative;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 16px;
      padding: 1.5px;
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.3), rgba(168, 85, 247, 0.2), rgba(59, 130, 246, 0.15));
      -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      pointer-events: none;
    }

    .filter-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 16px;

      .filter-tabs {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;

        .filter-tab {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 8px 16px;
          border-radius: 20px;
          cursor: pointer;
          transition: all 0.3s;
          font-size: 14px;
          color: #64748b;
          background: rgba(99, 102, 241, 0.04);
          border: 1px solid transparent;

          &:hover {
            background: rgba(99, 102, 241, 0.08);
            border-color: rgba(99, 102, 241, 0.1);
          }

          &.active {
            background: linear-gradient(135deg, #6366f1, #a855f7);
            color: #fff;
            box-shadow: 0 4px 16px rgba(99, 102, 241, 0.35);
            border-color: transparent;
          }

          .tab-badge {
            margin-left: 4px;
          }
        }
      }

      .filter-search {
        width: 240px;

        :deep(.el-input__wrapper) {
          border-radius: 20px;
          background: rgba(99, 102, 241, 0.04);
          box-shadow: none;
          border: 1px solid rgba(99, 102, 241, 0.08);
          transition: all 0.3s;

          &:hover, &.is-focus {
            border-color: rgba(99, 102, 241, 0.3);
            box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.08);
          }
        }
      }
    }
  }

  /* ========== 活动卡片渐变边框 + 悬停3D微效果 ========== */
  .activity-list {
    .activity-card {
      background: #fff;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
      margin-bottom: 20px;
      border: 1.5px solid transparent;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        inset: 0;
        border-radius: 12px;
        padding: 1.5px;
        background: linear-gradient(135deg, rgba(99, 102, 241, 0.4), rgba(168, 85, 247, 0.2), rgba(59, 130, 246, 0.3));
        -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
        -webkit-mask-composite: xor;
        mask-composite: exclude;
        pointer-events: none;
        opacity: 0;
        transition: opacity 0.4s ease;
      }

      &:hover {
        transform: translateY(-6px) rotateX(2deg);
        box-shadow: 0 16px 40px rgba(99, 102, 241, 0.15), 0 0 0 1px rgba(99, 102, 241, 0.1);

        &::before {
          opacity: 1;
        }
      }

      .card-cover {
        position: relative;
        height: 140px;

        .cover-placeholder {
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .status-badge {
          position: absolute;
          top: 12px;
          right: 12px;
          padding: 4px 12px;
          border-radius: 20px;
          font-size: 12px;
          font-weight: 500;
          background: rgba(255, 255, 255, 0.95);
          backdrop-filter: blur(8px);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

          &.status-0 { color: #909399; }
          &.status-1 { color: #e6a23c; }
          &.status-2 { color: #67c23a; }
          &.status-3 { color: #409eff; }
          &.status-4 { color: #909399; }
          &.status-5 { color: #f56c6c; }
        }
      }

      .card-content {
        padding: 16px;

        .activity-title {
          margin: 0 0 12px;
          font-size: 16px;
          font-weight: 600;
          color: #1e293b;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .activity-club {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 13px;
          color: #64748b;
          margin-bottom: 12px;

          .el-icon {
            color: #6366f1;
          }
        }

        .activity-meta {
          display: flex;
          flex-direction: column;
          gap: 8px;
          margin-bottom: 16px;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 13px;
            color: #94a3b8;

            .el-icon {
              font-size: 14px;
            }
          }
        }

        .activity-progress {
          margin-bottom: 16px;

          .progress-info {
            display: flex;
            justify-content: space-between;
            font-size: 12px;
            color: #94a3b8;
            margin-bottom: 6px;
          }
        }

        .card-actions {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-top: 12px;
          border-top: 1px solid rgba(99, 102, 241, 0.06);

          .el-button--primary {
            transition: all 0.3s ease;

            &:hover {
              box-shadow: 0 0 20px rgba(99, 102, 241, 0.35);
              transform: translateY(-1px);
            }
          }
        }
      }
    }
  }

  /* ========== 弹窗毛玻璃 + 渐变顶部边框 ========== */
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    box-shadow: 0 24px 80px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(255, 255, 255, 0.2);

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 3px;
      background: linear-gradient(90deg, #6366f1, #a855f7, #3b82f6, #6366f1);
      background-size: 200% 100%;
      animation: borderFlow 3s linear infinite;
    }
  }

  .activity-detail {
    .detail-cover {
      height: 180px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 20px;
    }

    .detail-title {
      margin: 0 0 12px;
      font-size: 20px;
      font-weight: 600;
      color: #1e293b;
    }

    .detail-tags {
      display: flex;
      gap: 8px;
      margin-bottom: 20px;
    }

    .detail-info {
      margin-bottom: 20px;
    }

    .detail-section {
      h4 {
        margin: 0 0 12px;
        font-size: 14px;
        color: #475569;
      }

      p {
        margin: 0;
        line-height: 1.8;
        color: #334155;
      }
    }
  }
}

@keyframes borderFlow {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}

@media (max-width: 768px) {
  .my-activity-page {
    padding: 12px;

    .welcome-banner {
      flex-direction: column;
      text-align: center;
      gap: 20px;

      .stats-overview {
        gap: 24px;
      }
    }

    .filter-section {
      .filter-bar {
        flex-direction: column;
        align-items: stretch;

        .filter-search {
          width: 100%;
        }
      }
    }
  }
}
</style>
