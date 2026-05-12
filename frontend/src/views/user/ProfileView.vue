<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getProfile, updateProfile, changePassword } from '../../api/user'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import type { User } from '../../types'
import type { FormInstance, FormRules } from 'element-plus'

const userStore = useUserStore()
const profile = ref<User | null>(null)
const profileFormRef = ref<FormInstance>()
const pwdFormRef = ref<FormInstance>()
const profileSubmitting = ref(false)
const pwdSubmitting = ref(false)

const profileForm = ref({ nickname: '', email: '', phone: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirm = (_rule: any, value: string, callback: Function) => {
  if (value !== pwdForm.value.newPassword) callback(new Error('两次输入不一致'))
  else callback()
}

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

onMounted(async () => {
  try {
    const res = await getProfile()
    profile.value = res.data
    profileForm.value = { nickname: res.data.nickname || '', email: res.data.email || '', phone: res.data.phone || '' }
  } catch { /* ignore */ }
})

async function handleUpdateProfile() {
  const valid = await profileFormRef.value?.validate()
  if (!valid) return
  profileSubmitting.value = true
  try {
    await updateProfile(profileForm.value)
    ElMessage.success('更新成功')
    userStore.fetchUserInfo()
  } finally { profileSubmitting.value = false }
}

async function handleChangePwd() {
  const valid = await pwdFormRef.value?.validate()
  if (!valid) return
  pwdSubmitting.value = true
  try {
    await changePassword(pwdForm.value.oldPassword, pwdForm.value.newPassword)
    ElMessage.success('密码修改成功')
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } finally { pwdSubmitting.value = false }
}
</script>

<template>
  <div class="page-container">
    <h1 class="page-title">个人中心</h1>
    <div class="profile-grid">
      <div class="card">
        <div class="card-header"><el-icon :size="20"><User /></el-icon> 基本信息</div>
        <div class="card-body">
          <el-form ref="profileFormRef" :model="profileForm" label-width="80px">
            <el-form-item label="昵称"><el-input v-model="profileForm.nickname" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="profileForm.email" /></el-form-item>
            <el-form-item label="手机"><el-input v-model="profileForm.phone" /></el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="profileSubmitting" round @click="handleUpdateProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="card">
        <div class="card-header"><el-icon :size="20"><Lock /></el-icon> 修改密码</div>
        <div class="card-body">
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="pwdSubmitting" round @click="handleChangePwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  max-width: 920px;
}

.card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-md);
  font-weight: 600;
  padding: 16px 20px;
  color: var(--color-primary);
  border-bottom: 1px solid var(--color-border-light);
  background: var(--color-bg-warm);
}

.card-body {
  padding: 20px;
}

@media (max-width: 768px) {
  .profile-grid { grid-template-columns: 1fr; }
}
</style>
