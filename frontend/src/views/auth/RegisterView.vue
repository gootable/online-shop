<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

const validateConfirm = (_rule: any, value: string, callback: Function) => {
  if (value !== form.password) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度为3-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value?.validate()
  if (!valid) return
  loading.value = true
  try {
    await userStore.register(form.username, form.password, form.nickname || undefined)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
            <el-icon :size="48"><UserFilled /></el-icon>
          </div>
          <h1>加入我们</h1>
          <p class="brand-desc">注册账号，开启品质购物之旅</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><Star /></el-icon>
              <span>新用户专享优惠</span>
            </div>
            <div class="feature-item">
              <el-icon><Star /></el-icon>
              <span>收藏您喜爱的商品</span>
            </div>
            <div class="feature-item">
              <el-icon><Star /></el-icon>
              <span>订单物流实时追踪</span>
            </div>
          </div>
        </div>
      </div>
      <div class="auth-right">
        <div class="auth-form-wrap">
          <h2 class="form-title">创建账号</h2>
          <p class="form-subtitle">填写信息完成注册</p>
          <el-form ref="formRef" :model="form" :rules="rules" size="large">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="nickname">
              <el-input v-model="form.nickname" placeholder="昵称（选填）" prefix-icon="EditPen" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" class="auth-btn" @click="handleRegister">
                注册
              </el-button>
            </el-form-item>
          </el-form>
          <div class="auth-footer">
            已有账号？<router-link to="/login">去登录</router-link>
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
  min-height: 560px;
  background: var(--color-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

.auth-left {
  width: 400px;
  background: linear-gradient(135deg, #2D3436, #1A1A2E);
  color: #FFF;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.brand-area { text-align: center; }

.brand-icon {
  width: 88px;
  height: 88px;
  background: rgba(255,255,255,0.12);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
}

.brand-area h1 { font-size: var(--font-size-2xl); font-weight: 700; margin-bottom: 8px; }

.brand-desc { font-size: var(--font-size-md); opacity: 0.75; margin-bottom: 40px; }

.feature-list { text-align: left; display: inline-flex; flex-direction: column; gap: 16px; }

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: var(--font-size-base);
  opacity: 0.85;
}

.auth-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 56px;
}

.auth-form-wrap { width: 100%; max-width: 340px; }

.form-title { font-size: 26px; font-weight: 700; color: var(--color-text-primary); margin-bottom: 6px; }

.form-subtitle { font-size: var(--font-size-base); color: var(--color-text-secondary); margin-bottom: 28px; }

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

.auth-footer a { color: var(--color-primary); font-weight: 500; }

@media (max-width: 768px) {
  .auth-card { flex-direction: column; width: 100%; }
  .auth-left { width: 100%; padding: 28px 24px; }
  .brand-icon { width: 56px; height: 56px; }
  .feature-list { display: none; }
  .auth-right { padding: 28px 24px; }
}
</style>
