<template>
  <div class="sign-page">
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
.sign-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
