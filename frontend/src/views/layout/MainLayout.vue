<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useCartStore } from '../../stores/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const keyword = ref('')

onMounted(async () => {
  await userStore.fetchUserInfo()
  if (userStore.isLoggedIn) {
    cartStore.fetchCart()
  }
})

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="main-layout">
    <header class="main-header">
      <div class="header-content">
        <router-link to="/" class="logo">网上商城</router-link>
        <div class="header-search">
          <el-input v-model="keyword" placeholder="搜索商品..." size="large" @keyup.enter="router.push({ path: '/search', query: { keyword } })">
            <template #prepend>
              <el-button @click="router.push({ path: '/search', query: { keyword } })">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
        <div class="header-actions">
          <router-link to="/cart" class="cart-link">
            <el-badge :value="cartStore.totalCount" :hidden="cartStore.totalCount === 0">
              <el-icon :size="24"><ShoppingCart /></el-icon>
            </el-badge>
            <span>购物车</span>
          </router-link>
          <template v-if="userStore.isLoggedIn">
            <el-dropdown>
              <span class="user-info">
                <el-icon><User /></el-icon>
                {{ userStore.username }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="router.push('/orders')">我的订单</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" @click="router.push('/admin')">后台管理</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <router-link v-else to="/login" class="login-link">登录</router-link>
        </div>
      </div>
    </header>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>


<style scoped>
.main-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 20px;
  height: 64px;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  color: #409eff;
  white-space: nowrap;
}

.header-search {
  flex: 1;
  max-width: 500px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  white-space: nowrap;
}

.cart-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 14px;
}

.login-link {
  color: #409eff;
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  font-size: 14px;
}

.main-content {
  min-height: calc(100vh - 64px);
}
</style>
