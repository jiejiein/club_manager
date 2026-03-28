<template>
  <div class="payment-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>缴费管理</span>
          <el-button type="primary" @click="handleAdd">发布缴费</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="未缴费" :value="0" />
            <el-option label="已缴费" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="缴费标题" />
        <el-table-column prop="clubName" label="所属社团" />
        <el-table-column prop="amount" label="金额">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止日期" width="160" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleRecords(row)">缴费记录</el-button>
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
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布缴费" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="社团" prop="clubId">
          <el-select v-model="form.clubId" placeholder="请选择社团" style="width: 100%">
            <el-option v-for="club in myClubs" :key="club.id" :label="club.name" :value="club.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入缴费标题" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="截止日期" prop="deadline">
          <el-date-picker v-model="form.deadline" type="datetime" placeholder="选择截止日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入缴费说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePagination } from '@/composables/usePagination'
import { getPaymentPage, createPayment, deletePayment } from '@/api/payment'
import { getMyClubs } from '@/api/club'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
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
  return await getPaymentPage({
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
  clubId: null,
  title: '',
  amount: 0,
  deadline: null,
  description: ''
})

const rules = {
  clubId: [{ required: true, message: '请选择社团', trigger: 'change' }],
  title: [{ required: true, message: '请输入缴费标题', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  deadline: [{ required: true, message: '请选择截止日期', trigger: 'change' }]
}

const handleAdd = () => {
  Object.assign(form, { clubId: null, title: '', amount: 0, deadline: null, description: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    await createPayment(form)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该缴费记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deletePayment(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleRecords = (row) => {
  router.push(`/payment-record/${row.id}`)
}

onMounted(() => {
  loadMyClubs()
})
</script>

<style lang="scss" scoped>
.payment-page {
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
