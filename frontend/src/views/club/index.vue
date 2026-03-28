<template>
  <div class="club-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>社团列表</span>
          <el-button type="primary" @click="handleAdd">创建社团</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="社团名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.typeId" placeholder="全部" clearable @change="handleSearch">
            <el-option v-for="type in clubTypes" :key="type.id" :label="type.name" :value="type.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="待审核" :value="0" />
            <el-option label="正常运营" :value="1" />
            <el-option label="已注销" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">信息查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="社团名称" />
        <el-table-column prop="typeName" label="类型" />
        <el-table-column prop="presidentName" label="社长" />
        <el-table-column prop="memberCount" label="成员数" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : 'danger'">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button v-if="isCurrentUserPresident(row)" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleMembers(row)">成员</el-button>
            <el-button v-if="row.status === 0" type="warning" link @click="handleAudit(row, 1)">审核通过</el-button>
            <el-button v-if="row.status === 0" type="danger" link @click="handleAudit(row, 2)">拒绝</el-button>
            <el-button v-if="isCurrentUserPresident(row)" type="danger" link @click="handleQuit(row)">申请退社</el-button>
            <el-button v-if="isAdmin()" type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑社团' : '创建社团'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入社团名称" />
        </el-form-item>
        <el-form-item label="类型" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择类型" style="width: 100%">
            <el-option v-for="type in clubTypes" :key="type.id" :label="type.name" :value="type.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入社团描述" />
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getClubPage, createClub, updateClub, deleteClub, auditClub, getAllClubTypes, quitClub } from '@/api/club'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])
const clubTypes = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

// 检查当前用户是否是社团社长
const isCurrentUserPresident = (row) => {
  return userStore.userInfo?.id === row.presidentId
}

// 检查当前用户是否是管理员
const isAdmin = () => {
  return userStore.userInfo?.role === 1
}

const searchForm = reactive({
  keyword: '',
  typeId: null,
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  name: '',
  typeId: null,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入社团名称', trigger: 'blur' }],
  typeId: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getClubPage({
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

const loadClubTypes = async () => {
  try {
    const res = await getAllClubTypes()
    clubTypes.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleAdd = () => {
  Object.assign(form, { id: null, name: '', typeId: null, description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, { id: row.id, name: row.name, typeId: row.typeId, description: row.description })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (form.id) {
      await updateClub(form)
    } else {
      await createClub(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该社团吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteClub(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleAudit = (row, status) => {
  if (status === 2) {
    ElMessageBox.prompt('请输入拒绝原因', '拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }).then(async ({ value }) => {
      await auditClub(row.id, status, value)
      ElMessage.success('操作成功')
      loadData()
    })
  } else {
    ElMessageBox.confirm('确定审核通过吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await auditClub(row.id, status)
      ElMessage.success('审核通过')
      loadData()
    })
  }
}

const handleMembers = (row) => {
  router.push(`/club-member/${row.id}`)
}

// 处理申请退社
const handleQuit = (row) => {
  ElMessageBox.confirm('确定要申请退社吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await quitClub(row.id)
      ElMessage.success('退社申请已提交')
      loadData()
    } catch (error) {
      console.error(error)
    }
  })
}

onMounted(() => {
  loadData()
  loadClubTypes()
})
</script>

<style lang="scss" scoped>
.club-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }
}
</style>
