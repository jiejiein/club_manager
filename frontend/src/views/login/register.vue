<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">用户注册</h2>
      <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" prefix-icon="UserFilled" size="large" />
        </el-form-item>
        <el-form-item prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学号" prefix-icon="Postcard" size="large" />
        </el-form-item>
        <el-form-item prop="college">
          <el-input v-model="form.college" placeholder="请输入学院" prefix-icon="School" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleRegister" class="register-btn">
            注 册
          </el-button>
        </el-form-item>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  studentNo: '',
  college: ''
})

const validatePass = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  college: [{ required: true, message: '请输入学院', trigger: 'blur' }]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  position: relative;
  overflow: hidden;

  // 动态渐变光球
  &::before,
  &::after {
    content: '';
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.6;
    animation: float 8s ease-in-out infinite;
  }

  &::before {
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, rgba(99, 102, 241, 0.5), transparent 70%);
    top: -100px;
    right: -100px;
    animation-delay: 0s;
  }

  &::after {
    width: 350px;
    height: 350px;
    background: radial-gradient(circle, rgba(168, 85, 247, 0.4), transparent 70%);
    bottom: -80px;
    left: -80px;
    animation-delay: -4s;
  }
}

.login-box {
  width: 420px;
  padding: 44px 40px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.05) inset,
    0 0 80px rgba(99, 102, 241, 0.1);
  animation: cardSlideUp 0.6s ease-out;
  position: relative;
  z-index: 1;

  // 顶部渐变光线装饰
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 20%;
    right: 20%;
    height: 2px;
    background: linear-gradient(90deg, transparent, #818cf8, #a855f7, #818cf8, transparent);
    border-radius: 2px;
  }

  .title {
    text-align: center;
    margin-bottom: 32px;
    color: #fff;
    font-size: 22px;
    font-weight: 700;
    letter-spacing: 1px;
    text-shadow: 0 2px 10px rgba(99, 102, 241, 0.3);
  }

  // 输入框聚焦发光效果
  :deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: none;
    transition: all 0.3s ease;

    &:hover {
      border-color: rgba(129, 140, 248, 0.3);
    }

    &.is-focus {
      border-color: #818cf8;
      box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.15),
        0 0 20px rgba(129, 140, 248, 0.1);
      background: rgba(255, 255, 255, 0.08);
    }
  }

  :deep(.el-input__inner) {
    color: #e2e8f0;

    &::placeholder {
      color: rgba(255, 255, 255, 0.35);
    }
  }

  :deep(.el-input__prefix .el-icon) {
    color: rgba(255, 255, 255, 0.4);
  }

  :deep(.el-form-item__label) {
    color: rgba(255, 255, 255, 0.7);
    font-weight: 500;
  }

  :deep(.el-form-item__error) {
    color: #f87171;
  }

  .register-btn {
    width: 100%;
    height: 44px;
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #a855f7 100%);
    border: none;
    color: #fff;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 2px;
    border-radius: 12px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;

    // 光泽扫过效果
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
      transition: left 0.6s ease;
    }

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(99, 102, 241, 0.4);

      &::before {
        left: 100%;
      }
    }

    &:active {
      transform: translateY(0);
    }
  }

  .login-link {
    text-align: center;
    color: rgba(255, 255, 255, 0.5);
    font-size: 14px;

    a {
      color: #a5b4fc;
      text-decoration: none;
      font-weight: 500;
      transition: all 0.3s;

      &:hover {
        color: #c7d2fe;
        text-shadow: 0 0 10px rgba(165, 180, 252, 0.3);
      }
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.05); }
}

@keyframes cardSlideUp {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
