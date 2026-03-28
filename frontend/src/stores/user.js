import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const loginAction = async (loginForm) => {
    const res = await login(loginForm)
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('token', res.data.token)
    return res
  }

  const getUserInfo = async () => {
    if (!token.value) return
    try {
      const res = await getCurrentUser()
      userInfo.value = res.data
    } catch (error) {
      logout()
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    loginAction,
    getUserInfo,
    logout
  }
})
