<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUsers, updateUserStatus, updateUserRole } from '../../api/admin/user'
import { formatDate } from '../../utils/format'
import { ElMessage } from 'element-plus'
import type { User } from '../../types'

const users = ref<User[]>([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const size = 10

onMounted(fetchUsers)

function fetchUsers() {
  loading.value = true
  getUsers({ page: page.value, size })
    .then(res => { users.value = res.data.records; total.value = res.data.total })
    .finally(() => loading.value = false)
}

async function toggleStatus(user: User) {
  const newStatus = user.status === 1 ? 0 : 1
  await updateUserStatus(user.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  fetchUsers()
}

async function toggleRole(user: User) {
  const newRole = user.role === 0 ? 1 : 0
  await updateUserRole(user.id, newRole)
  ElMessage.success(newRole === 0 ? '已设为管理员' : '已取消管理员')
  fetchUsers()
}
</script>

<template>
  <div>
    <div class="card">
      <el-table v-loading="loading" :data="users" style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 0 ? 'danger' : 'info'" size="small">{{ row.role === 0 ? '管理员' : '用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" @click="toggleRole(row)">
              {{ row.role === 0 ? '取消管理' : '设为管理' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div style="margin-top:16px;text-align:center" v-if="total > size">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="size"
        :current-page="page" @current-change="(p: number) => { page = p; fetchUsers() }" />
    </div>
  </div>
</template>

<style scoped>
.card { background: var(--color-white); border-radius: var(--radius-md); box-shadow: var(--shadow-sm); padding: 16px; }
</style>
