<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <!-- 个人资料 -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>个人资料</span>
              <el-button type="primary" @click="handleUpdate">修改信息</el-button>
            </div>
          </template>
          
          <div class="profile-content">
            <!-- 个人基本信息 -->
            <div class="user-info">
              <el-avatar :size="100" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) }}
              </el-avatar>
              <div class="user-details">
                <h3>{{ userStore.userInfo?.nickname }}</h3>
                <el-tag :type="roleTagType">{{ userStore.userInfo?.roleName }}</el-tag>
              </div>
            </div>
            
            <!-- 详细信息表格 -->
            <div class="info-table">
              <el-table :data="infoTableData" style="width: 100%" border>
                <el-table-column prop="label" label="信息项" width="120" />
                <el-table-column prop="value" label="内容" />
              </el-table>
            </div>
            
            <!-- 修改表单 -->
            <el-form v-if="showEditForm" ref="formRef" :model="form" :rules="rules" label-width="100px" class="edit-form">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="form.nickname" placeholder="请输入昵称" />
                  </el-form-item>
                  <el-form-item label="邮箱">
                    <el-input v-model="form.email" placeholder="请输入邮箱" />
                  </el-form-item>
                  <el-form-item label="手机号">
                    <el-input v-model="form.phone" placeholder="请输入手机号" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="学院">
                    <el-input v-model="form.college" placeholder="请输入学院" />
                  </el-form-item>
                  <el-form-item label="专业">
                    <el-input v-model="form.major" placeholder="请输入专业" />
                  </el-form-item>
                  <el-form-item label="班级">
                    <el-input v-model="form.className" placeholder="请输入班级" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item>
                <el-button @click="showEditForm = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-col>
      
      <!-- 系统通知 -->
      <el-col :span="10">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Bell /></el-icon>
              <span style="margin-left: 8px;">系统通知</span>
            </div>
          </template>
          <div class="notification-list">
            <div v-for="(item, index) in notifications" :key="index" class="notification-item">
              <div class="notification-date">{{ item.date }}</div>
              <div class="notification-content">{{ item.content }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateUser } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const formRef = ref(null)
const showEditForm = ref(false)

const form = reactive({
  nickname: '',
  email: '',
  phone: '',
  college: '',
  major: '',
  className: ''
})

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

// 角色标签类型
const roleTagType = computed(() => {
  const role = userStore.userInfo?.role
  if (role === 1) return 'danger' // 管理员
  if (role === 2) return 'warning' // 社长
  if (role === 3) return 'success' // 学生
  return ''
})

// 信息表格数据
const infoTableData = computed(() => {
  const user = userStore.userInfo
  return [
    { label: '登录角色', value: user?.roleName || '-' },
    { label: '用户名', value: user?.username || '-' },
    { label: '用户姓名', value: user?.nickname || '-' },
    { label: '邮箱', value: user?.email || '-' },
    { label: '联系电话', value: user?.phone || '-' },
    { label: '学院', value: user?.college || '-' },
    { label: '专业', value: user?.major || '-' },
    { label: '班级', value: user?.className || '-' }
  ]
})

// 系统通知
const notifications = [
  {
    date: '2026-03-22',
    content: '社团管理系统已更新至最新版本，新增了个人资料管理功能'
  },
  {
    date: '2026-03-20',
    content: '春季社团招新活动即将开始，请各社团做好准备'
  },
  {
    date: '2026-03-15',
    content: '系统将于本周末进行维护，届时可能会短暂无法访问'
  }
]

const initForm = () => {
  if (userStore.userInfo) {
    Object.assign(form, {
      nickname: userStore.userInfo.nickname,
      email: userStore.userInfo.email,
      phone: userStore.userInfo.phone,
      college: userStore.userInfo.college,
      major: userStore.userInfo.major,
      className: userStore.userInfo.className
    })
  }
}

const handleUpdate = () => {
  showEditForm.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    await updateUser(form)
    ElMessage.success('修改成功')
    userStore.getUserInfo()
    showEditForm.value = false
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  initForm()
})
</script>

<style lang="scss" scoped>
/* ========== 现代简约科技风 - 个人中心 ========== */
.profile-page {
  /* ========== el-card 渐变边框 + 毛玻璃 ========== */
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
      background: linear-gradient(135deg, rgba(2, 132, 199, 0.4), rgba(56, 189, 248, 0.3), rgba(59, 130, 246, 0.2));
      -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      pointer-events: none;
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

  /* ========== 头像区域渐变发光边框 ========== */
  .profile-content {
    .user-info {
      display: flex;
      align-items: center;
      gap: 20px;
      margin-bottom: 30px;
      padding-bottom: 20px;
      border-bottom: 1px solid rgba(2, 132, 199, 0.08);

      :deep(.el-avatar) {
        border: 3px solid transparent;
        background-clip: padding-box;
        position: relative;
        box-shadow: 0 0 0 3px transparent;

        background-image: linear-gradient(#fff, #fff), linear-gradient(135deg, #0284c7, #38bdf8, #3b82f6);
        background-origin: border-box;
        background-clip: padding-box, border-box;
        box-shadow: 0 0 20px rgba(2, 132, 199, 0.2), 0 0 0 3px transparent;
        animation: avatarGlow 3s ease-in-out infinite;
      }

      .user-details {
        h3 {
          margin: 0 0 10px;
          font-size: 20px;
          font-weight: 600;
          color: #1e293b;
        }
      }
    }

    /* ========== 信息表格毛玻璃效果 ========== */
    .info-table {
      margin-bottom: 20px;

      :deep(.el-table) {
        border-radius: 12px;
        overflow: hidden;
        background: rgba(255, 255, 255, 0.6);
        backdrop-filter: blur(12px);
        -webkit-backdrop-filter: blur(12px);

        .el-table__header-wrapper th {
          background-color: rgba(2, 132, 199, 0.04) !important;
          color: #475569;
          font-weight: 600;
        }

        .el-table__body-wrapper {
          .el-table__row:nth-child(odd) {
            background-color: rgba(2, 132, 199, 0.02);
          }

          .el-table__row {
            transition: all 0.3s ease;

            &:hover > td.el-table__cell {
              background: linear-gradient(90deg, rgba(2, 132, 199, 0.04), rgba(56, 189, 248, 0.04)) !important;
            }
          }
        }
      }
    }

    /* ========== 编辑表单毛玻璃 + 聚焦发光效果 ========== */
    .edit-form {
      margin-top: 20px;
      padding: 24px;
      background: rgba(255, 255, 255, 0.6);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      border-radius: 14px;
      border: 1px solid rgba(255, 255, 255, 0.5);
      box-shadow: 0 4px 24px rgba(2, 132, 199, 0.06);

      :deep(.el-input__wrapper) {
        border-radius: 8px;
        transition: all 0.3s ease;
        box-shadow: 0 0 0 1px rgba(2, 132, 199, 0.1) inset;

        &:hover {
          box-shadow: 0 0 0 1px rgba(2, 132, 199, 0.2) inset;
        }

        &.is-focus {
          box-shadow: 0 0 0 1px #0284c7 inset, 0 0 0 3px rgba(2, 132, 199, 0.1);
        }
      }

      :deep(.el-button--primary) {
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 0 20px rgba(2, 132, 199, 0.35);
          transform: translateY(-1px);
        }
      }
    }
  }

  /* ========== 通知列表毛玻璃 ========== */
  .notification-list {
    .notification-item {
      padding: 12px 0;
      border-bottom: 1px solid rgba(2, 132, 199, 0.06);
      transition: all 0.3s ease;

      &:hover {
        background: rgba(2, 132, 199, 0.02);
        border-radius: 8px;
        padding-left: 8px;
        padding-right: 8px;
      }

      &:last-child {
        border-bottom: none;
      }

      .notification-date {
        font-size: 12px;
        color: #94a3b8;
        margin-bottom: 6px;
      }

      .notification-content {
        font-size: 14px;
        color: #475569;
        line-height: 1.4;
      }
    }
  }
}

@keyframes avatarGlow {
  0%, 100% {
    box-shadow: 0 0 20px rgba(2, 132, 199, 0.2), 0 0 0 3px rgba(2, 132, 199, 0.1);
  }
  50% {
    box-shadow: 0 0 30px rgba(2, 132, 199, 0.35), 0 0 0 3px rgba(56, 189, 248, 0.2);
  }
}
</style>
