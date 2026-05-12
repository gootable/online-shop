<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate()
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-left">
        <div class="brand-area">
          <div class="brand-icon">
            <el-icon :size="48"><ShoppingCartFull /></el-icon>
          </div>
          <h1>AiDemo 商城</h1>
          <p class="brand-desc">品质生活，从这里开始</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><CircleCheck /></el-icon>
              <span>正品保障，品质无忧</span>
            </div>
            <div class="feature-item">
              <el-icon><CircleCheck /></el-icon>
              <span>极速物流，准时送达</span>
            </div>
            <div class="feature-item">
              <el-icon><CircleCheck /></el-icon>
              <span>售后无忧，退换便捷</span>
            </div>
          </div>
        </div>
      </div>
      <div class="auth-right">
        <div class="auth-form-wrap">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">登录您的账号</p>
          <el-form ref="formRef" :model="form" :rules="rules" size="large">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User"
                :style="{ '--el-input-border-radius': '8px' }" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码"
                prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" class="auth-btn" @click="handleLogin">
                立即登录
              </el-button>
            </el-form-item>
          </el-form>
          <div class="auth-footer">
            还没有账号？<router-link to="/register">立即注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #FFF0F2 0%, #FFF5F5 50%, #FFFBF7 100%);
  padding: 20px;
}

.auth-card {
  display: flex;
  width: 880px;
  min-height: 520px;
  background: var(--color-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

.auth-left {
  width: 420px;
  background: linear-gradient(135deg, var(--color-primary-hover), var(--color-primary));
  color: #FFF;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.brand-area {
  text-align: center;
}

.brand-icon {
  width: 88px;
  height: 88px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
}

.brand-area h1 {
  font-size: var(--font-size-2xl);
  font-weight: 700;
  margin-bottom: 8px;
}

.brand-desc {
  font-size: var(--font-size-md);
  opacity: 0.85;
  margin-bottom: 40px;
}

.feature-list {
  text-align: left;
  display: inline-flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: var(--font-size-base);
  opacity: 0.9;
}

.auth-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 56px;
}

.auth-form-wrap {
  width: 100%;
  max-width: 340px;
}

.form-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 6px;
}

.form-subtitle {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin-bottom: 32px;
}

.auth-btn {
  width: 100%;
  height: 46px;
  font-size: var(--font-size-md);
  border-radius: 8px;
  letter-spacing: 2px;
}

.auth-footer {
  text-align: center;
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin-top: 16px;
}

.auth-footer a {
  color: var(--color-primary);
  font-weight: 500;
}

@media (max-width: 768px) {
  .auth-card {
    flex-direction: column;
    width: 100%;
  }
  .auth-left {
    width: 100%;
    padding: 32px 24px;
  }
  .brand-icon { width: 64px; height: 64px; }
  .brand-area h1 { font-size: 22px; }
  .feature-list { display: none; }
  .auth-right { padding: 32px 24px; }
}
</style>
