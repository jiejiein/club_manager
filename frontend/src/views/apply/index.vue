<template>
  <div class="apply-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>申请列表</span>
        </div>
      </template>

      <!-- 搜索表单 -->
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
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="申请人" />
        <el-table-column prop="clubName" label="申请社团" />
        <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" link @click="handleAudit(row, 1)">通过</el-button>
              <el-button type="danger" link @click="handleAudit(row, 2)">拒绝</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedIds.length > 0">
        <el-button type="success" @click="handleBatchAudit(1)">
          批量通过 ({{ selectedIds.length }})
        </el-button>
        <el-button type="danger" @click="handleBatchAudit(2)">
          批量拒绝 ({{ selectedIds.length }})
        </el-button>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { getApplyPage, auditApply, batchAudit } from '@/api/apply'
import { usePagination } from '@/composables/usePagination'
import { useSearch } from '@/composables/useSearch'
import { ElMessage, ElMessageBox } from 'element-plus'

// ==================== 状态管理 ====================

// 搜索表单
const { searchForm, handleSearch, handleReset } = useSearch(() => {
  pagination.current = 1
  loadData()
})

// 分页和数据
const {
  loading,
  tableData,
  selectedIds,
  pagination,
  loadData,
  handleCurrentChange,
  handleSizeChange,
  handleSelectionChange
} = usePagination(async (params) => {
  return await getApplyPage({
    ...params,
    ...searchForm
  })
}, { immediate: true })

// ==================== 工具函数 ====================

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return textMap[status] || '未知'
}

// ==================== 事件处理 ====================

const handleAudit = async (row, status) => {
  const isReject = status === 2
  
  try {
    if (isReject) {
      const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '请输入拒绝原因'
          }
          return true
        }
      })
      await auditApply(row.id, status, value)
    } else {
      await ElMessageBox.confirm('确定通过该申请吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await auditApply(row.id, status)
    }
    
    ElMessage.success(isReject ? '已拒绝' : '审核通过')
    loadData()
  } catch (error) {
    // 用户取消操作，不处理
    if (error === 'cancel') return
    console.error('审核失败:', error)
  }
}

const handleBatchAudit = async (status) => {
  const isReject = status === 2
  const count = selectedIds.value.length
  
  try {
    if (isReject) {
      const { value } = await ElMessageBox.prompt(
        `请输入 ${count} 条申请的拒绝原因`, 
        '批量拒绝', 
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputValidator: (value) => {
            if (!value || value.trim() === '') {
              return '请输入拒绝原因'
            }
            return true
          }
        }
      )
      await batchAudit(selectedIds.value, status, value)
    } else {
      await ElMessageBox.confirm(
        `确定批量通过 ${count} 条申请吗？`, 
        '提示', 
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      await batchAudit(selectedIds.value, status)
    }
    
    ElMessage.success(isReject ? '批量拒绝成功' : '批量通过成功')
    loadData()
  } catch (error) {
    if (error === 'cancel') return
    console.error('批量审核失败:', error)
  }
}
</script>

<style scoped>
.apply-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.batch-actions {
  margin-top: 20px;
  padding: 15px;
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
