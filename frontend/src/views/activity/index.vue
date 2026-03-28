<template>
  <div class="activity-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动列表</span>
          <el-button v-if="isAdmin || isPresident" type="primary" @click="handleAdd">发布活动</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="活动标题" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="待审核" :value="1" />
            <el-option label="已发布" :value="2" />
            <el-option label="进行中" :value="3" />
            <el-option label="已结束" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">信息查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="活动标题" />
        <el-table-column prop="clubName" label="所属社团" />
        <el-table-column prop="location" label="地点" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="currentParticipants" label="报名人数">
          <template #default="{ row }">
            {{ row.currentParticipants }} / {{ row.maxParticipants }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button v-if="isActivityOwner(row)" type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="success" link @click="handleSignList(row)">报名列表</el-button>
              <el-button v-if="isActivityOwner(row)" type="danger" link @click="handleDelete(row)">删除</el-button>
              <!-- 只有管理员可以审核 -->
              <el-button v-if="isAdmin && row.status === 1" type="warning" link @click="handleAudit(row, 2)">审核</el-button>
              <!-- 社长看到审核中状态 -->
              <el-tag v-else-if="isPresident && row.status === 1" type="warning">审核中</el-tag>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑活动' : '发布活动'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属社团" prop="clubId" v-if="!form.id">
          <el-select v-model="form.clubId" placeholder="请选择社团" style="width: 100%">
            <el-option v-for="club in myClubs" :key="club.id" :label="club.name" :value="club.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报名开始" prop="signUpStart">
          <el-date-picker v-model="form.signUpStart" type="datetime" placeholder="选择报名开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报名截止" prop="signUpEnd">
          <el-date-picker v-model="form.signUpEnd" type="datetime" placeholder="选择报名截止时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxParticipants">
          <el-input-number v-model="form.maxParticipants" :min="1" />
        </el-form-item>
        <el-form-item label="活动描述">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入活动描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePagination } from '@/composables/usePagination'
import { getActivityPage, createActivity, updateActivity, deleteActivity, auditActivity } from '@/api/activity'
import { getMyClubs } from '@/api/club'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

// 权限控制
const isAdmin = computed(() => userStore.userInfo?.role === 1)
const isPresident = computed(() => userStore.userInfo?.role === 2)

// 检查是否是活动所属社团的社长
const isActivityOwner = computed(() => (row) => {
  if (isAdmin.value) return true
  if (isPresident.value) {
    return myClubs.value.some(club => club.id === row.clubId)
  }
  return false
})

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: null
})

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 分页和数据
const {
  loading,
  tableData,
  pagination,
  loadData,
  handleSizeChange,
  handleCurrentChange
} = usePagination(async (params) => {
  return await getActivityPage({
    ...params,
    ...searchForm
  })
}, { immediate: true })

// 我的社团列表
const myClubs = ref([])

const loadMyClubs = async () => {
  try {
    const res = await getMyClubs()
    myClubs.value = res.data
  } catch (error) {
    console.error(error)
  }
}

// 弹窗和表单
const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  clubId: null,
  title: '',
  location: '',
  startTime: null,
  endTime: null,
  signUpStart: null,
  signUpEnd: null,
  maxParticipants: 100,
  description: ''
})

const rules = {
  clubId: [{ required: true, message: '请选择所属社团', trigger: 'change' }],
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  signUpStart: [{ required: true, message: '请选择报名开始时间', trigger: 'change' }],
  signUpEnd: [{ required: true, message: '请选择报名截止时间', trigger: 'change' }],
  maxParticipants: [{ required: true, message: '请输入最大人数', trigger: 'blur' }]
}

const getStatusType = (status) => {
  const types = { 1: 'warning', 2: 'success', 3: 'primary', 4: 'info', 5: 'danger' }
  return types[status] || ''
}

const handleAdd = () => {
  if (myClubs.value.length === 0) {
    ElMessage.warning('您还没有管理的社团')
    return
  }
  Object.assign(form, {
    id: null, title: '', location: '', startTime: null, endTime: null,
    signUpStart: null, signUpEnd: null, maxParticipants: 100, description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (form.id) {
      await updateActivity(form)
    } else {
      await createActivity(form, form.clubId)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该活动吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteActivity(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleAudit = (row, status) => {
  ElMessageBox.confirm('确定审核通过吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await auditActivity(row.id, status)
    ElMessage.success('审核通过')
    loadData()
  })
}

const handleSignList = (row) => {
  router.push(`/activity-sign/${row.id}`)
}

onMounted(() => {
  loadMyClubs()
})
</script>

<style lang="scss" scoped>
.activity-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }

  .action-buttons {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}
</style>
