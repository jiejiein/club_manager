<template>
  <div class="system-log-page page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统日志</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="成功" :value="0" />
            <el-option label="失败" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">信息查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="用户昵称" width="120" />
        <el-table-column prop="operation" label="操作名称" width="150" />
        <el-table-column prop="method" label="请求方法" width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executionTime" label="执行时间" width="100">
          <template #default="{ row }">
            {{ row.executionTime }}ms
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
import { getSystemLogPage, deleteSystemLog } from '@/api/system-log'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({
  username: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSystemLogPage({
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

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该日志吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteSystemLog(row.id)
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

.system-log-page {
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
