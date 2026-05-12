<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <router-link to="/" class="sb-logo">AiDemo 商城</router-link>
        <span class="sb-badge">后台管理</span>
      </div>
      <div class="sidebar-user">
        <el-icon :size="18"><UserFilled /></el-icon>
        <span>{{ userStore.username }}</span>
      </div>
      <el-menu :default-active="route.path" router background-color="#1A1A2E" text-color="#A0AEC0" active-text-color="#FFF">
        <el-menu-item index="/admin">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <el-button text style="color:#A0AEC0" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon> 退出
        </el-button>
      </div>
    </aside>
    <div class="admin-main">
      <header class="admin-topbar">
        <router-link to="/" class="tb-home">
          <el-icon><HomeFilled /></el-icon> 返回首页
        </router-link>
      </header>
      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-sidebar {
  width: 220px;
  background: #1A1A2E;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 24px 20px;
  text-align: center;
}

.sb-logo {
  color: #FFF;
  font-size: 20px;
  font-weight: 700;
  display: block;
}

.sb-badge {
  color: #A0AEC0;
  font-size: 12px;
  margin-top: 4px;
  display: block;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.sidebar-user {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  color: #A0AEC0;
  font-size: 13px;
  border-top: 1px solid rgba(255,255,255,0.06);
  border-bottom: 1px solid rgba(255,255,255,0.06);
}

.sidebar-footer {
  margin-top: auto;
  padding: 16px 20px;
  border-top: 1px solid rgba(255,255,255,0.06);
}

:deep(.el-menu) { border-right: none; }

:deep(.el-menu-item) {
  margin: 2px 8px;
  border-radius: 6px;
  height: 44px;
  line-height: 44px;
}

:deep(.el-menu-item.is-active) {
  background: var(--color-primary) !important;
}

:deep(.el-menu-item:hover) {
  background: rgba(255,255,255,0.08) !important;
}

.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: var(--color-bg);
}

.admin-topbar {
  height: 48px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: var(--color-white);
  box-shadow: var(--shadow-sm);
  gap: 8px;
}

.tb-home {
  font-size: 13px;
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color var(--transition-fast);
}

.tb-home:hover { color: var(--color-primary); }

.admin-content {
  flex: 1;
  padding: 20px;
}
</style>
