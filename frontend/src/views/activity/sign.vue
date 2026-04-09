<template>
  <div class="sign-page page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报名列表</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="姓名" />
        <el-table-column prop="status" label="审核状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'">
              {{ row.status === 1 ? '已通过' : row.status === 2 ? '已拒绝' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checked" label="签到状态">
          <template #default="{ row }">
            <el-tag :type="row.checked ? 'success' : 'info'">
              {{ row.checked ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signUpTime" label="报名时间" width="160" />
        <el-table-column prop="rating" label="评分">
          <template #default="{ row }">
            <el-rate v-if="row.rating" v-model="row.rating" disabled />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="isActivityOwner && row.status === 0" type="success" link @click="handleAudit(row, 1)">通过</el-button>
            <el-button v-if="isActivityOwner && row.status === 0" type="danger" link @click="handleAudit(row, 2)">拒绝</el-button>
            <el-button v-if="canCheckIn(row) && row.status === 1 && !row.checked" type="primary" link @click="handleCheckIn(row)">签到</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getSignUpList, auditSignUp, checkIn } from '@/api/activity'
import { getActivityById } from '@/api/activity'
import { getMyClubs } from '@/api/club'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const activityId = route.params.id
const loading = ref(false)
const tableData = ref([])
const activity = ref(null)
const myClubs = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const isAdmin = computed(() => {
  return userStore.userInfo?.role === 1
})

const isPresident = computed(() => {
  return userStore.userInfo?.role === 2
})

const isActivityOwner = computed(() => {
  if (isAdmin.value) return true
  if (isPresident.value && activity.value) {
    return myClubs.value.some(club => club.id === activity.value.clubId)
  }
  return false
})

const canCheckIn = (row) => {
  if (!isActivityOwner.value) return false
  
  if (isAdmin.value) return true
  
  if (isPresident.value) {
    return myClubs.value.some(club => club.id === activity.value.clubId)
  }
  
  return false
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSignUpList(activityId, {
      current: pagination.current,
      size: pagination.size
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadActivityInfo = async () => {
  try {
    const res = await getActivityById(activityId)
    activity.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadMyClubs = async () => {
  try {
    const res = await getMyClubs()
    myClubs.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleAudit = async (row, status) => {
  try {
    await auditSignUp(row.id, status)
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleCheckIn = async (row) => {
  try {
    await checkIn(row.id)
    ElMessage.success('签到成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  await loadActivityInfo()
  await loadMyClubs()
  loadData()
})
</script>

<style lang="scss" scoped>
/* ========== 现代简约科技风 - 页面容器 ========== */
.page-container {
  padding: 4px;
  min-height: 100%;
  position: relative;

  &::before {
    content: '';
    position: fixed;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(ellipse at 20% 50%, rgba(99, 102, 241, 0.06) 0%, transparent 50%),
                radial-gradient(ellipse at 80% 20%, rgba(168, 85, 247, 0.05) 0%, transparent 50%),
                radial-gradient(ellipse at 40% 80%, rgba(59, 130, 246, 0.04) 0%, transparent 50%);
    pointer-events: none;
    z-index: 0;
    animation: bgFloat 20s ease-in-out infinite;
  }
}

@keyframes bgFloat {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  33% { transform: translate(2%, -1%) rotate(1deg); }
  66% { transform: translate(-1%, 2%) rotate(-1deg); }
}

.sign-page {
  position: relative;
  z-index: 1;

  /* ========== el-card 渐变边框 ========== */
  :deep(.el-card) {
    position: relative;
    border: none;
    border-radius: 16px;
    overflow: visible;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06);

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 16px;
      padding: 1.5px;
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.4), rgba(168, 85, 247, 0.3), rgba(59, 130, 246, 0.2), rgba(99, 102, 241, 0.1));
      -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      pointer-events: none;
      transition: opacity 0.3s;
    }

    &:hover::before {
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.6), rgba(168, 85, 247, 0.5), rgba(59, 130, 246, 0.4), rgba(99, 102, 241, 0.3));
    }
  }

  /* ========== Card Header 左侧渐变装饰条 ========== */
  :deep(.el-card__header) {
    position: relative;
    padding: 16px 20px;
    border-bottom: 1px solid rgba(99, 102, 241, 0.08);

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 12px;
      bottom: 12px;
      width: 4px;
      border-radius: 4px;
      background: linear-gradient(180deg, #6366f1, #a855f7, #3b82f6);
      box-shadow: 0 0 12px rgba(99, 102, 241, 0.3);
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
  }

  /* ========== 表格渐变行悬停 ========== */
  :deep(.el-table) {
    border-radius: 12px;
    overflow: hidden;

    th.el-table__cell {
      background: rgba(99, 102, 241, 0.04) !important;
      color: #475569;
      font-weight: 600;
      font-size: 13px;
    }

    .el-table__row {
      transition: all 0.3s ease;

      &:hover > td.el-table__cell {
        background: linear-gradient(90deg, rgba(99, 102, 241, 0.04), rgba(168, 85, 247, 0.04), rgba(59, 130, 246, 0.02)) !important;
      }
    }

    .el-table__body tr.el-table__row--striped td.el-table__cell {
      background: rgba(99, 102, 241, 0.02);
    }
  }

  /* ========== 操作按钮悬停发光 ========== */
  :deep(.el-button.is-link) {
    transition: all 0.3s ease;

    &:hover {
      text-shadow: 0 0 8px currentColor;
      filter: brightness(1.1);
    }
  }

  /* ========== 分页器美化 ========== */
  :deep(.el-pagination) {
    margin-top: 20px;
    justify-content: flex-end;

    .el-pager li {
      border-radius: 8px;
      transition: all 0.3s ease;

      &.is-active {
        background: linear-gradient(135deg, #6366f1, #a855f7);
        box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
      }
    }
  }
}
</style>
