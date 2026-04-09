<template>
  <div class="notification-page page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通知列表</span>
          <el-button v-if="canPublish" type="primary" @click="handleAdd">发布通知</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable @change="handleSearch">
            <el-option label="系统通知" :value="1" />
            <el-option label="社团通知" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="typeName" label="类型" />
        <el-table-column prop="senderName" label="发布人" />
        <el-table-column prop="priority" label="优先级">
          <template #default="{ row }">
            <el-tag :type="row.priority === 2 ? 'danger' : row.priority === 1 ? 'warning' : ''">
              {{ row.priority === 2 ? '紧急' : row.priority === 1 ? '重要' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="canDelete(row)" type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="viewMode ? '通知详情' : '发布通知'" width="500px">
      <el-form v-if="!viewMode" ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="系统通知" :value="1" :disabled="isStudent || isPresident" />
            <el-option label="社团通知" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="普通" :value="0" />
            <el-option label="重要" :value="1" :disabled="isStudent" />
            <el-option label="紧急" :value="2" :disabled="isStudent || isPresident" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" rows="5" placeholder="请输入通知内容" />
        </el-form-item>
      </el-form>
      <div v-else>
        <h3>{{ viewData.title }}</h3>
        <p style="color: #999; margin: 10px 0">{{ viewData.createTime }}</p>
        <p>{{ viewData.content }}</p>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="!viewMode" type="primary" @click="handleSubmit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getNotificationPage, createNotification, deleteNotification } from '@/api/notification'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const viewMode = ref(false)
const viewData = ref({})
const formRef = ref(null)

const searchForm = reactive({
  type: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  title: '',
  content: '',
  type: 1,
  priority: 0
})

const rules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

// 权限控制
const canPublish = computed(() => {
  return true // 所有用户都可以发布通知
})

const canDelete = computed(() => {
  return (row) => {
    const role = userStore.userInfo?.role
    const userId = userStore.userInfo?.id
    // 管理员可以删除所有通知，其他用户只能删除自己发布的
    return role === 1 || row.senderId === userId
  }
})

// 用户角色判断
const isStudent = computed(() => {
  return userStore.userInfo?.role === 3
})

const isPresident = computed(() => {
  return userStore.userInfo?.role === 2
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNotificationPage({
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

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleAdd = () => {
  viewMode.value = false
  Object.assign(form, { title: '', content: '', type: 1, priority: 0 })
  dialogVisible.value = true
}

const handleView = (row) => {
  viewMode.value = true
  viewData.value = row
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    await createNotification(form)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该通知吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteNotification(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => {
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

.notification-page {
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
    box-shadow: 0 4px 24px rgba(99, 102, 241, 0.06);
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.75);
      box-shadow: 0 8px 32px rgba(99, 102, 241, 0.1);
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
  :deep(.el-button--primary) {
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 0 20px rgba(99, 102, 241, 0.35);
      transform: translateY(-1px);
    }
  }

  :deep(.el-button.is-link) {
    transition: all 0.3s ease;

    &:hover {
      text-shadow: 0 0 8px currentColor;
      filter: brightness(1.1);
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

  :deep(.el-dialog__header) {
    padding: 20px 24px 16px;
    border-bottom: 1px solid rgba(99, 102, 241, 0.08);
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
        background: linear-gradient(135deg, #6366f1, #a855f7);
        box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
      }
    }
  }
}

@keyframes borderFlow {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}
</style>
