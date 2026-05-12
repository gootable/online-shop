import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getMe } from '../api/auth'
import type { User } from '../types'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<User | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 0)
  const username = computed(() => userInfo.value?.nickname || userInfo.value?.username || '')

  async function login(username: string, password: string) {
    const res = await loginApi(username, password)
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('token', res.data.token)
  }

  async function register(username: string, password: string, nickname?: string) {
    await registerApi(username, password, nickname)
  }

  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getMe()
      userInfo.value = res.data
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, isLoggedIn, isAdmin, username, login, register, fetchUserInfo, logout }
})
