<template>
  <div class="activity-page page-container">
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
        @size-change="loadData"
        @current-change="loadData"
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getActivityPage, createActivity, updateActivity, deleteActivity, auditActivity } from '@/api/activity'
import { getMyClubs } from '@/api/club'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const myClubs = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

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

// 权限控制
const isAdmin = computed(() => {
  return userStore.userInfo?.role === 1
})

const isPresident = computed(() => {
  return userStore.userInfo?.role === 2
})

// 检查是否是活动所属社团的社长
const isActivityOwner = computed(() => {
  return (row) => {
    // 管理员可以操作所有活动
    if (isAdmin.value) return true
    // 社长只能操作自己社团的活动
    if (isPresident.value) {
      return myClubs.value.some(club => club.id === row.clubId)
    }
    return false
  }
})

const getStatusType = (status) => {
  const types = { 1: 'warning', 2: 'success', 3: 'primary', 4: 'info', 5: 'danger' }
  return types[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getActivityPage({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
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

const handleSearch = () => {
  pagination.current = 1
  loadData()
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
  loadData()
  loadMyClubs()
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
    background: radial-gradient(ellipse at 20% 50%, rgba(2, 132, 199, 0.06) 0%, transparent 50%),
                radial-gradient(ellipse at 80% 20%, rgba(56, 189, 248, 0.05) 0%, transparent 50%),
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

.activity-page {
  position: relative;
  z-index: 1;

  /* ========== 毛玻璃搜索表单 ========== */
  .search-form {
    margin-bottom: 20px;
    padding: 20px 24px;
    background: rgba(255, 255, 255, 0.65);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-radius: 14px;
    border: 1px solid rgba(255, 255, 255, 0.5);
    box-shadow: 0 4px 24px rgba(2, 132, 199, 0.06);
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.75);
      box-shadow: 0 8px 32px rgba(2, 132, 199, 0.1);
    }
  }

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
      background: linear-gradient(135deg, rgba(2, 132, 199, 0.4), rgba(56, 189, 248, 0.3), rgba(59, 130, 246, 0.2), rgba(2, 132, 199, 0.1));
      -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      pointer-events: none;
      transition: opacity 0.3s;
    }

    &:hover::before {
      background: linear-gradient(135deg, rgba(2, 132, 199, 0.6), rgba(56, 189, 248, 0.5), rgba(59, 130, 246, 0.4), rgba(2, 132, 199, 0.3));
    }
  }

  /* ========== Card Header 左侧渐变装饰条 ========== */
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
      background: rgba(2, 132, 199, 0.04) !important;
      color: #475569;
      font-weight: 600;
      font-size: 13px;
    }

    .el-table__row {
      transition: all 0.3s ease;

      &:hover > td.el-table__cell {
        background: linear-gradient(90deg, rgba(2, 132, 199, 0.04), rgba(56, 189, 248, 0.04), rgba(59, 130, 246, 0.02)) !important;
      }
    }

    .el-table__body tr.el-table__row--striped td.el-table__cell {
      background: rgba(2, 132, 199, 0.02);
    }
  }

  /* ========== 操作按钮悬停发光 ========== */
  .action-buttons {
    display: flex;
    align-items: center;
    gap: 12px;

    .el-button {
      transition: all 0.3s ease;

      &:hover {
        text-shadow: 0 0 8px currentColor;
        filter: brightness(1.1);
      }
    }
  }

  :deep(.el-button--primary) {
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 0 20px rgba(2, 132, 199, 0.35);
      transform: translateY(-1px);
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

  :deep(.el-dialog__header) {
    padding: 20px 24px 16px;
    border-bottom: 1px solid rgba(2, 132, 199, 0.08);
  }

  :deep(.el-dialog__body) {
    padding: 20px 24px;
  }

  /* ========== 分页器美化 ========== */
  :deep(.el-pagination) {
    margin-top: 20px;
    justify-content: flex-end;

    .el-pager li {
      border-radius: 8px;
      transition: all 0.3s ease;

      &.is-active {
        background: linear-gradient(135deg, #0284c7, #38bdf8);
        box-shadow: 0 4px 12px rgba(2, 132, 199, 0.3);
      }
    }
  }
}

@keyframes borderFlow {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}
</style>
