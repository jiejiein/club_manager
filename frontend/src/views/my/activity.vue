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
.my-activity-page {
  padding: 20px;

  .welcome-banner {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    padding: 30px;
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #fff;

    .welcome-content {
      h2 {
        margin: 0 0 8px;
        font-size: 28px;
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

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 36px;
          font-weight: bold;
          line-height: 1;
        }

        .stat-label {
          font-size: 14px;
          opacity: 0.8;
          margin-top: 4px;
        }
      }
    }
  }

  .filter-section {
    margin-bottom: 24px;
    border-radius: 12px;

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
          color: var(--el-text-color-regular);
          background: var(--el-fill-color-light);

          &:hover {
            background: var(--el-fill-color);
          }

          &.active {
            background: var(--el-color-primary);
            color: #fff;
          }

          .tab-badge {
            margin-left: 4px;
          }
        }
      }

      .filter-search {
        width: 240px;
      }
    }
  }

  .activity-list {
    .activity-card {
      background: var(--el-bg-color);
      border: 1px solid var(--el-border-color);
      border-radius: 12px;
      overflow: hidden;
      box-shadow: var(--el-box-shadow-light);
      transition: all 0.3s ease;
      margin-bottom: 20px;

      &:hover {
        transform: translateY(-4px);
        box-shadow: var(--el-box-shadow);
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
          background: var(--el-bg-color);
          border: 1px solid var(--el-border-color);

          &.status-0 { color: var(--el-text-color-secondary); }
          &.status-1 { color: #e6a23c; }
          &.status-2 { color: #67c23a; }
          &.status-3 { color: #409eff; }
          &.status-4 { color: var(--el-text-color-secondary); }
          &.status-5 { color: #f56c6c; }
        }
      }

      .card-content {
        padding: 16px;

        .activity-title {
          margin: 0 0 12px;
          font-size: 16px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .activity-club {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 13px;
          color: var(--el-text-color-regular);
          margin-bottom: 12px;

          .el-icon {
            color: var(--el-color-primary);
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
            color: var(--el-text-color-secondary);

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
            color: var(--el-text-color-secondary);
            margin-bottom: 6px;
          }
        }

        .card-actions {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-top: 12px;
          border-top: 1px solid var(--el-border-color-light);
        }
      }
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
        color: #606266;
      }

      p {
        margin: 0;
        line-height: 1.8;
        color: #303133;
      }
    }
  }
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
