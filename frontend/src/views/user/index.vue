<template>
  <div class="user-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="handleSearch">信息查询</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名/昵称/学号" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部" clearable @change="handleSearch">
            <el-option label="管理员" :value="1" />
            <el-option label="社长" :value="2" />
            <el-option label="普通用户" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="college" label="学院" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'danger' : row.role === 2 ? 'warning' : ''">
              {{ row.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="warning" link @click="handleResetPassword(row)">重置密码</el-button>
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
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { usePagination } from '@/composables/usePagination'
import { getUserPage, updateUserStatus, resetPassword } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  role: null,
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
  return await getUserPage({
    ...params,
    ...searchForm
  })
}, { immediate: true })

const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleResetPassword = (row) => {
  ElMessageBox.prompt('请输入新密码', '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^.{6,}$/,
    inputErrorMessage: '密码至少6位'
  }).then(async ({ value }) => {
    await resetPassword(row.id, value)
    ElMessage.success('密码重置成功')
  })
}
</script>

<style lang="scss" scoped>
.user-page {
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
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding-left: 5px;
  }
}
</style>
