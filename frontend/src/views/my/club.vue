<template>
  <div class="my-club-page">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2>我的社团</h2>
        <p>加入感兴趣的社团，开启精彩的校园生活</p>
      </div>
      <div class="stats-overview">
        <div class="stat-item">
          <div class="stat-value">{{ tableData.length }}</div>
          <div class="stat-label">已加入社团</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ myApplies.filter(a => a.status === 0).length }}</div>
          <div class="stat-label">待审核申请</div>
        </div>
      </div>
    </div>

    <!-- 已加入社团 -->
    <el-card class="club-section" shadow="never">
      <template #header>
        <div class="section-header">
          <span class="title">
            <el-icon><Star /></el-icon>
            已加入社团
          </span>
          <el-tag type="success" effect="dark">{{ tableData.length }} 个</el-tag>
        </div>
      </template>

      <el-row :gutter="20" v-if="tableData.length > 0">
        <el-col :xs="24" :sm="12" :lg="8" v-for="club in tableData" :key="club.id">
          <div class="club-card" :style="{ '--card-color': getClubColor(club.typeId) }">
            <div class="card-header">
              <div class="club-icon">
                <el-icon :size="32"><component :is="getClubIcon(club.typeId)" /></el-icon>
              </div>
              <div class="club-badge">{{ club.typeName }}</div>
            </div>
            <h3 class="club-name">{{ club.name }}</h3>
            <p class="club-desc">{{ club.description || '暂无描述' }}</p>
            <div class="club-meta">
              <div class="meta-item">
                <el-icon><User /></el-icon>
                <span>{{ club.memberCount }} 成员</span>
              </div>
              <div class="meta-item">
                <el-icon><UserFilled /></el-icon>
                <span>社长：{{ club.presidentName }}</span>
              </div>
            </div>
            <div class="card-actions">
              <el-button type="primary" plain @click="viewClubDetail(club)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button type="danger" link @click="handleQuit(club)">
                退出社团
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-empty v-else description="还没有加入任何社团">
        <template #image>
          <el-icon :size="80" color="#dcdfe6"><OfficeBuilding /></el-icon>
        </template>
        <el-button type="primary" @click="scrollToApply">立即申请加入</el-button>
      </el-empty>
    </el-card>

    <!-- 申请入团 -->
    <el-card id="apply-section" class="apply-section" shadow="never">
      <template #header>
        <div class="section-header">
          <span class="title">
            <el-icon><Plus /></el-icon>
            申请入团
          </span>
        </div>
      </template>

      <div class="apply-form-wrapper">
        <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-position="top">
          <el-row :gutter="20">
            <el-col :xs="24" :md="12">
              <el-form-item label="选择社团" prop="clubId">
                <el-select 
                  v-model="applyForm.clubId" 
                  placeholder="请选择想要加入的社团"
                  style="width: 100%"
                  size="large"
                >
                  <el-option 
                    v-for="club in availableClubs" 
                    :key="club.id" 
                    :label="club.name" 
                    :value="club.id"
                  >
                    <div class="club-option">
                      <span class="club-name">{{ club.name }}</span>
                      <span class="club-divider">|</span>
                      <span class="club-type">{{ club.typeName }}</span>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="12">
              <el-form-item label="申请理由" prop="reason">
                <el-input 
                  v-model="applyForm.reason" 
                  placeholder="请简述您想加入该社团的原因..."
                  type="textarea"
                  :rows="3"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button type="primary" size="large" @click="handleApply" :loading="applying">
              <el-icon><Position /></el-icon>
              提交申请
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 我的申请记录 -->
      <div class="apply-history">
        <h4>我的申请记录</h4>
        <el-table :data="myApplies" stripe v-loading="loadingApplies">
          <el-table-column prop="clubName" label="社团名称" min-width="150" />
          <el-table-column prop="reason" label="申请理由" min-width="200" show-overflow-tooltip />
          <el-table-column prop="status" label="审核状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" effect="light">
                {{ row.status === 1 ? '已通过' : row.status === 2 ? '已拒绝' : '待审核' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="rejectReason" label="拒绝原因" min-width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <span v-if="row.status === 2" class="reject-reason">{{ row.rejectReason || '无' }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="申请时间" width="160" />
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <el-button 
                v-if="row.status === 0" 
                type="danger" 
                link 
                @click="cancelApply(row)"
              >
                撤回
              </el-button>
              <span v-else class="text-gray">-</span>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="myApplies.length === 0" description="暂无申请记录" :image-size="80" />
      </div>
    </el-card>

    <!-- 社团详情弹窗 -->
    <el-dialog v-model="detailVisible" title="社团详情" width="600px" destroy-on-close>
      <div v-if="currentClub" class="club-detail">
        <div class="detail-header">
          <div class="detail-icon" :style="{ background: getClubColor(currentClub.typeId) }">
            <el-icon :size="40" color="#fff"><component :is="getClubIcon(currentClub.typeId)" /></el-icon>
          </div>
          <div class="detail-info">
            <h3>{{ currentClub.name }}</h3>
            <el-tag size="small">{{ currentClub.typeName }}</el-tag>
          </div>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="社长">{{ currentClub.presidentName }}</el-descriptions-item>
          <el-descriptions-item label="成员数">{{ currentClub.memberCount }} 人</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentClub.status === 1 ? 'success' : 'warning'">
              {{ currentClub.status === 1 ? '正常运营' : '待审核' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="加入时间">{{ currentClub.joinTime }}</el-descriptions-item>
        </el-descriptions>
        <div class="detail-section">
          <h4>社团简介</h4>
          <p>{{ currentClub.description || '暂无简介' }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getMyClubs, getActiveClubs, quitClub } from '@/api/club'
import { submitApply, getMyApplies, cancelApply as cancelApplyApi } from '@/api/apply'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Star, Plus, User, UserFilled, View, Position, 
  OfficeBuilding, Monitor, Camera, Basketball, 
  FirstAidKit, MagicStick 
} from '@element-plus/icons-vue'

const tableData = ref([])
const activeClubs = ref([])
const myApplies = ref([])
const loadingApplies = ref(false)
const applying = ref(false)
const detailVisible = ref(false)
const currentClub = ref(null)
const applyFormRef = ref(null)

const applyForm = reactive({
  clubId: null,
  reason: ''
})

const applyRules = {
  clubId: [{ required: true, message: '请选择社团', trigger: 'change' }],
  reason: [{ required: true, message: '请输入申请理由', trigger: 'blur' }]
}

// 计算可申请的社团（排除已加入的）
const availableClubs = computed(() => {
  const joinedIds = tableData.value.map(c => c.id)
  const appliedIds = myApplies.value.filter(a => a.status === 0).map(a => a.clubId)
  return activeClubs.value.filter(c => !joinedIds.includes(c.id) && !appliedIds.includes(c.id))
})

// 获取社团颜色
const getClubColor = (typeId) => {
  const colors = {
    1: 'linear-gradient(135deg, #0284c7 0%, #0ea5e9 100%)',
    2: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    3: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    4: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    5: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  }
  return colors[typeId] || colors[1]
}

// 获取社团图标
const getClubIcon = (typeId) => {
  const icons = {
    1: Monitor,
    2: Camera,
    3: Basketball,
    4: FirstAidKit,
    5: MagicStick
  }
  return icons[typeId] || OfficeBuilding
}

// 获取状态类型
const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const loadData = async () => {
  try {
    const res = await getMyClubs()
    tableData.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const loadActiveClubs = async () => {
  try {
    const res = await getActiveClubs()
    activeClubs.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const loadMyApplies = async () => {
  loadingApplies.value = true
  try {
    const res = await getMyApplies()
    myApplies.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loadingApplies.value = false
  }
}

const handleApply = async () => {
  const valid = await applyFormRef.value.validate().catch(() => false)
  if (!valid) return

  applying.value = true
  try {
    await submitApply(applyForm.clubId, applyForm.reason)
    ElMessage.success('申请已提交，请等待审核')
    applyForm.clubId = null
    applyForm.reason = ''
    applyFormRef.value.resetFields()
    loadMyApplies()
  } catch (error) {
    console.error(error)
  } finally {
    applying.value = false
  }
}

const cancelApply = async (row) => {
  try {
    await ElMessageBox.confirm('确定要撤回这条申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelApplyApi(row.id)
    ElMessage.success('已撤回申请')
    loadMyApplies()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleQuit = async (club) => {
  try {
    await ElMessageBox.confirm(
      `确定要退出「${club.name}」吗？退出后将无法参与该社团的活动。`,
      '退出确认',
      {
        confirmButtonText: '确定退出',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await quitClub(club.id)
    ElMessage.success('已退出社团')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const viewClubDetail = (club) => {
  currentClub.value = club
  detailVisible.value = true
}

const scrollToApply = () => {
  document.getElementById('apply-section')?.scrollIntoView({ behavior: 'smooth' })
}

onMounted(() => {
  loadData()
  loadActiveClubs()
  loadMyApplies()
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

.my-club-page {
  padding: 20px;
  position: relative;

  /* ========== 欢迎横幅流动渐变动画 ========== */
  .welcome-banner {
    background: linear-gradient(135deg, #0284c7, #38bdf8, #3b82f6, #0284c7, #38bdf8);
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

  /* ========== el-card 渐变边框 + 毛玻璃 ========== */
  .club-section,
  .apply-section {
    margin-bottom: 24px;
    border-radius: 16px;
    position: relative;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border: none;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06);

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 16px;
      padding: 1.5px;
      background: linear-gradient(135deg, rgba(2, 132, 199, 0.4), rgba(56, 189, 248, 0.3), rgba(59, 130, 246, 0.2));
      -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      pointer-events: none;
    }

    :deep(.el-card__header) {
      position: relative;
      padding: 16px 20px;
      border-bottom: 1px solid rgba(2, 132, 199, 0.08);

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 12px;
        bottom: 12px;
        width: 4px;
        border-radius: 4px;
        background: linear-gradient(180deg, #0284c7, #38bdf8, #3b82f6);
        box-shadow: 0 0 12px rgba(2, 132, 199, 0.3);
      }
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title {
        font-size: 18px;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 8px;
        color: #1e293b;

        .el-icon {
          color: #0284c7;
        }
      }
    }
  }

  /* ========== 社团卡片渐变边框 + 悬停3D微效果 ========== */
  .club-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    border: 1.5px solid transparent;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 12px;
      padding: 1.5px;
      background: linear-gradient(135deg, var(--card-color, #0284c7), rgba(2, 132, 199, 0.2), var(--card-color, #38bdf8));
      -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      pointer-events: none;
      opacity: 0;
      transition: opacity 0.4s ease;
    }

    &:hover {
      transform: translateY(-6px) rotateX(2deg);
      box-shadow: 0 16px 40px rgba(2, 132, 199, 0.15), 0 0 0 1px rgba(2, 132, 199, 0.1);

      &::before {
        opacity: 1;
      }
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .club-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        background: var(--card-color);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        box-shadow: 0 4px 12px rgba(2, 132, 199, 0.25);
      }

      .club-badge {
        padding: 4px 12px;
        background: linear-gradient(135deg, rgba(2, 132, 199, 0.08), rgba(56, 189, 248, 0.08));
        border-radius: 20px;
        font-size: 12px;
        color: #0284c7;
        border: 1px solid rgba(2, 132, 199, 0.12);
      }
    }

    .club-name {
      margin: 0 0 8px;
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
    }

    .club-desc {
      margin: 0 0 16px;
      font-size: 14px;
      color: #64748b;
      line-height: 1.5;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .club-meta {
      display: flex;
      gap: 20px;
      margin-bottom: 16px;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: #64748b;

        .el-icon {
          color: #0284c7;
        }
      }
    }

    .card-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 16px;
      border-top: 1px solid rgba(2, 132, 199, 0.06);

      .el-button--primary {
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 0 20px rgba(2, 132, 199, 0.35);
          transform: translateY(-1px);
        }
      }
    }
  }

  /* ========== 申请表单毛玻璃 ========== */
  .apply-form-wrapper {
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-radius: 14px;
    padding: 24px;
    margin-bottom: 24px;
    border: 1px solid rgba(255, 255, 255, 0.5);
    box-shadow: 0 4px 24px rgba(2, 132, 199, 0.06);

    .club-option {
      display: flex;
      align-items: center;
      width: 100%;

      .club-name {
        font-size: 14px;
        color: #303133;
        font-weight: 500;
      }

      .club-divider {
        color: #dcdfe6;
        margin: 0 10px;
      }

      .club-type {
        font-size: 12px;
        color: #909399;
      }
    }
  }

  /* ========== 申请历史表格 ========== */
  .apply-history {
    h4 {
      margin: 0 0 16px;
      font-size: 16px;
      color: #1e293b;
    }

    :deep(.el-table) {
      border-radius: 12px;
      overflow: hidden;

      th.el-table__cell {
        background: rgba(2, 132, 199, 0.04) !important;
        color: #475569;
        font-weight: 600;
      }

      .el-table__row {
        transition: all 0.3s ease;

        &:hover > td.el-table__cell {
          background: linear-gradient(90deg, rgba(2, 132, 199, 0.04), rgba(56, 189, 248, 0.04)) !important;
        }
      }
    }

    .reject-reason {
      color: #f56c6c;
    }

    .text-gray {
      color: #c0c4cc;
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
      background: linear-gradient(90deg, #0284c7, #38bdf8, #3b82f6, #0284c7);
      background-size: 200% 100%;
      animation: borderFlow 3s linear infinite;
    }
  }

  .club-detail {
    .detail-header {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 24px;

      .detail-icon {
        width: 72px;
        height: 72px;
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 8px 24px rgba(2, 132, 199, 0.3);
      }

      .detail-info {
        h3 {
          margin: 0 0 8px;
          font-size: 20px;
          color: #1e293b;
        }
      }
    }

    .detail-section {
      margin-top: 24px;

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
  .my-club-page {
    padding: 12px;

    .welcome-banner {
      flex-direction: column;
      text-align: center;
      gap: 20px;

      .stats-overview {
        gap: 30px;
      }
    }
  }
}
</style>
