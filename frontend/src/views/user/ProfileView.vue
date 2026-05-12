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

const profileForm = ref({
  nickname: '',
  email: '',
  phone: ''
})

const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const pwdSubmitting = ref(false)

const validateConfirm = (_rule: any, value: string, callback: Function) => {
  if (value !== pwdForm.value.newPassword) {
    callback(new Error('两次输入不一致'))
  } else {
    callback()
  }
}

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

onMounted(async () => {
  try {
    const res = await getProfile()
    profile.value = res.data
    profileForm.value.nickname = res.data.nickname || ''
    profileForm.value.email = res.data.email || ''
    profileForm.value.phone = res.data.phone || ''
  } catch { /* ignore */ }
})

async function handleUpdateProfile() {
  const valid = await profileFormRef.value?.validate()
  if (!valid) return
  await updateProfile(profileForm.value)
  ElMessage.success('更新成功')
  userStore.fetchUserInfo()
}

async function handleChangePassword() {
  const valid = await pwdFormRef.value?.validate()
  if (!valid) return
  pwdSubmitting.value = true
  try {
    await changePassword(pwdForm.value.oldPassword, pwdForm.value.newPassword)
    ElMessage.success('密码修改成功')
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } finally {
    pwdSubmitting.value = false
  }
}
</script>

<template>
  <div class="page-container">
    <h1 class="page-title">个人中心</h1>

    <div class="profile-content">
      <el-card>
        <template #header>基本信息</template>
        <el-form ref="profileFormRef" :model="profileForm" label-width="80px" style="max-width:480px">
          <el-form-item label="昵称">
            <el-input v-model="profileForm.nickname" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="profileForm.email" />
          </el-form-item>
          <el-form-item label="手机">
            <el-input v-model="profileForm.phone" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUpdateProfile">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card style="margin-top:16px">
        <template #header>修改密码</template>
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px" style="max-width:480px">
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
            <el-button type="primary" :loading="pwdSubmitting" @click="handleChangePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.profile-content {
  max-width: 720px;
}
</style>
