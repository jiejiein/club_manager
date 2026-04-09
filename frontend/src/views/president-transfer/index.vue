<template>
  <div class="president-transfer-page page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>社长转移申请管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="clubName" label="社团名称" />
        <el-table-column prop="currentPresidentName" label="现任社长" />
        <el-table-column prop="newPresidentName" label="新社长" />
        <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="success" link @click="handleAudit(row, 1)">通过</el-button>
            <el-button v-if="row.status === 0" type="danger" link @click="handleReject(row)">拒绝</el-button>
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

    <el-dialog v-model="rejectDialogVisible" title="拒绝理由" width="400px">
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="拒绝理由" prop="rejectReason">
          <el-input v-model="rejectForm.rejectReason" type="textarea" rows="4" placeholder="请输入拒绝理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTransferPage, auditTransfer } from '@/api/club'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const rejectDialogVisible = ref(false)
const rejectFormRef = ref(null)
const currentTransfer = ref(null)

const searchForm = reactive({
  status: null
})

const rejectForm = reactive({
  rejectReason: ''
})

const rejectRules = {
  rejectReason: [{ required: true, message: '请输入拒绝理由', trigger: 'blur' }]
}

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || ''
}

const getStatusName = (status) => {
  const names = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return names[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTransferPage({
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

const handleAudit = (row, status) => {
  ElMessageBox.confirm(`确定要批准该转移申请吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await auditTransfer(row.id, status, null)
    ElMessage.success('审核通过')
    loadData()
  })
}

const handleReject = (row) => {
  currentTransfer.value = row
  rejectForm.rejectReason = ''
  rejectDialogVisible.value = true
}

const handleConfirmReject = async () => {
  await rejectFormRef.value.validate()
  try {
    await auditTransfer(currentTransfer.value.id, 2, rejectForm.rejectReason)
    ElMessage.success('已拒绝该申请')
    rejectDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
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

.president-transfer-page {
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
