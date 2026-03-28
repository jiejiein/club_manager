<template>
  <div class="club-member-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成员管理</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="姓名" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="加入时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="isCurrentClubPresident && row.userId !== userStore.userInfo?.id" type="primary" link @click="handleTransfer(row)">转移社长</el-button>
            <el-button v-if="isCurrentClubPresident" type="danger" link @click="handleRemove(row)">移除</el-button>
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
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getClubMembers, removeMember } from '@/api/apply'
import { getClubById, createTransfer } from '@/api/club'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const clubId = route.params.id
const loading = ref(false)
const tableData = ref([])
const isCurrentClubPresident = ref(false)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const checkClubPresident = async () => {
  try {
    const res = await getClubById(clubId)
    const club = res.data
    // 管理员或社长都有权限
    isCurrentClubPresident.value = userStore.userInfo?.id === club.presidentId || userStore.userInfo?.role === 1
  } catch (error) {
    console.error(error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getClubMembers(clubId, {
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

const handleRemove = (row) => {
  ElMessageBox.confirm('确定要移除该成员吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await removeMember(clubId, row.userId)
    ElMessage.success('移除成功')
    loadData()
  })
}

const handleTransfer = (row) => {
  ElMessageBox.prompt('请输入转移理由', '申请转移社长职位', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '请输入转移理由'
  }).then(async ({ value }) => {
    await createTransfer(clubId, row.userId, value)
    ElMessage.success('转移申请已提交，等待管理员审核')
    loadData()
  }).catch(() => {
    ElMessage.info('已取消申请')
  })
}

onMounted(async () => {
  await checkClubPresident()
  loadData()
})
</script>

<style lang="scss" scoped>
.club-member-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
