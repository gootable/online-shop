<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useCartStore } from '../../stores/cart'
import { getCategoryTree } from '../../api/category'
import type { Category } from '../../types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()
const keyword = ref('')
const navCategories = ref<Category[]>([])

onMounted(async () => {
  await userStore.fetchUserInfo()
  if (userStore.isLoggedIn) {
    cartStore.fetchCart()
  }
  try {
    const res = await getCategoryTree()
    navCategories.value = res.data
  } catch { /* ignore */ }
})

// Sync search input with route query
watch(() => route.query.keyword, (val) => {
  keyword.value = (val as string) || ''
})

function handleSearch() {
  const kw = keyword.value.trim()
  if (!kw) return
  if (route.path === '/products') {
    router.replace({ query: { keyword: kw } })
  } else {
    router.push({ path: '/products', query: { keyword: kw } })
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="main-layout">
    <!-- Top bar -->
    <div class="top-bar">
      <div class="top-bar-content">
        <span class="top-bar-text">欢迎来到网上商城，品质好货，尽在其中</span>
        <div class="top-bar-right">
          <template v-if="userStore.isLoggedIn">
            <router-link to="/orders" class="top-link">我的订单</router-link>
            <span class="sep">|</span>
            <router-link to="/profile" class="top-link">个人中心</router-link>
            <span class="sep">|</span>
            <router-link v-if="userStore.isAdmin" to="/admin" class="top-link">后台管理</router-link>
            <span v-if="userStore.isAdmin" class="sep">|</span>
            <span class="top-link logout" @click="handleLogout">退出</span>
          </template>
          <template v-else>
            <router-link to="/login" class="top-link">登录</router-link>
            <span class="sep">|</span>
            <router-link to="/register" class="top-link">注册</router-link>
          </template>
        </div>
      </div>
    </div>

    <!-- Main header -->
    <header class="main-header">
      <div class="header-content">
        <router-link to="/" class="logo">
          <div class="logo-icon"><el-icon :size="26"><ShoppingCartFull /></el-icon></div>
          <span class="logo-text">AiDemo 商城</span>
        </router-link>

        <div class="header-search">
          <el-input v-model="keyword" placeholder="搜索你想要的..." size="large"
            @keyup.enter="handleSearch" clearable
            class="search-input">
            <template #suffix>
              <el-button type="primary" class="search-btn" @click="handleSearch">
                <el-icon :size="18"><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
          <div class="hot-words">
            <span v-for="w in ['手机', '耳机', '连衣裙', '台灯']" :key="w"
              class="hot-word" @click="keyword=w;handleSearch()">{{ w }}</span>
          </div>
        </div>

        <div class="header-actions">
          <router-link to="/cart" class="header-action cart-action">
            <el-badge :value="cartStore.totalCount" :hidden="cartStore.totalCount === 0" :max="99">
              <el-icon :size="26"><ShoppingCart /></el-icon>
            </el-badge>
            <span class="action-label">购物车</span>
          </router-link>
        </div>
      </div>

      <!-- Category nav -->
      <nav class="category-nav-bar">
        <div class="nav-content">
          <span class="nav-item" :class="{ active: route.path === '/products' && !route.query.categoryId }"
            @click="router.push('/products')">全部商品</span>
          <span v-for="item in navCategories" :key="item.id"
            class="nav-item"
            :class="{ active: route.query.categoryId === String(item.id) }"
            @click="router.push({ path: '/products', query: { categoryId: item.id } })">
            {{ item.name }}
          </span>
        </div>
      </nav>
    </header>

    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- Footer -->
    <footer class="main-footer">
      <div class="footer-content">
        <div class="footer-col">
          <h4>购物指南</h4>
          <p>购物流程</p>
          <p>支付方式</p>
          <p>配送说明</p>
        </div>
        <div class="footer-col">
          <h4>售后服务</h4>
          <p>退换货政策</p>
          <p>退款说明</p>
          <p>联系客服</p>
        </div>
        <div class="footer-col">
          <h4>关于我们</h4>
          <p>公司介绍</p>
          <p>联系我们</p>
          <p>加入我们</p>
        </div>
        <div class="footer-col contact">
          <h4>客服热线</h4>
          <p class="phone">400-888-8888</p>
          <p class="hours">周一至周日 9:00-22:00</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2026 AiDemo 商城 — 软件工程课程设计项目</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
/* Top bar */
.top-bar {
  background: #1A1A1A;
  color: #CCC;
  font-size: var(--font-size-xs);
  height: 36px;
  line-height: 36px;
}

.top-bar-content {
  max-width: var(--max-width);
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
}

.top-bar-text { color: #999; }

.top-bar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.top-link {
  color: #CCC;
  cursor: pointer;
  transition: color var(--transition-fast);
}

.top-link:hover { color: #FFF; }

.sep { color: #555; }

/* Main header */
.main-header {
  background: var(--color-white);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow-sm);
}

.header-content {
  max-width: var(--max-width);
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 16px 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.logo-icon {
  color: var(--color-primary);
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  letter-spacing: -0.5px;
}

.logo-text::first-letter {
  color: var(--color-primary);
}

.header-search {
  flex: 1;
  max-width: 560px;
  position: relative;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px 0 0 20px;
  border-right: none;
  box-shadow: 2px 0 0 0 var(--color-primary);
}

.search-btn {
  height: 40px;
  border-radius: 0 20px 20px 0 !important;
  padding: 0 20px !important;
  margin: 0 !important;
}

.hot-words {
  display: flex;
  gap: 12px;
  padding: 6px 0 0 4px;
}

.hot-word {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: color var(--transition-fast);
}

.hot-word:hover { color: var(--color-primary); }

.header-actions {
  flex-shrink: 0;
}

.header-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  color: var(--color-text-regular);
  transition: all var(--transition-fast);
  cursor: pointer;
}

.header-action:hover {
  color: var(--color-primary);
  background: var(--color-primary-light);
}

.action-label {
  font-size: var(--font-size-xs);
  margin-top: 2px;
}

/* Category nav */
.category-nav-bar {
  border-top: 2px solid var(--color-primary);
  background: var(--color-white);
  position: relative;
}

.nav-content {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  gap: 0;
  overflow-x: auto;
  overflow-y: hidden;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none; /* Firefox */
  mask-image: linear-gradient(to right, transparent 0%, black 8px, black calc(100% - 40px), transparent 100%);
  -webkit-mask-image: linear-gradient(to right, transparent 0%, black 8px, black calc(100% - 40px), transparent 100%);
}

.nav-content::-webkit-scrollbar {
  display: none; /* Chrome/Safari */
}

.nav-item {
  padding: 12px 18px;
  font-size: var(--font-size-md);
  color: var(--color-text-regular);
  position: relative;
  transition: all var(--transition-fast);
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
  user-select: none;
}

.nav-item:first-child {
  padding-left: 4px;
}

.nav-item:last-child {
  padding-right: 40px; /* extra space for fade gradient */
}

.nav-item:hover, .nav-item.active {
  color: var(--color-primary);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 3px;
  background: var(--color-primary);
  border-radius: 2px;
}

/* Main content */
.main-content {
  min-height: calc(100vh - 260px);
  padding-bottom: 20px;
}

/* Footer */
.main-footer {
  background: #1A1A1A;
  color: #999;
  padding: 40px 0 0;
}

.footer-content {
  max-width: var(--max-width);
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  padding: 0 20px 32px;
  border-bottom: 1px solid #333;
}

.footer-col h4 {
  font-size: var(--font-size-md);
  color: #DDD;
  margin-bottom: 16px;
}

.footer-col p {
  font-size: var(--font-size-sm);
  margin-bottom: 10px;
  cursor: pointer;
  transition: color var(--transition-fast);
}

.footer-col p:hover { color: #FFF; }

.footer-col.contact .phone {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 4px;
}

.footer-col.contact .hours {
  font-size: var(--font-size-xs);
}

.footer-bottom {
  text-align: center;
  padding: 20px;
  font-size: var(--font-size-xs);
  color: #666;
}

/* Page transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .header-content { flex-wrap: wrap; gap: 12px; }
  .logo-text { font-size: 18px; }
  .header-search { order: 3; flex-basis: 100%; max-width: none; }
  .top-bar { display: none; }
  .nav-content { padding: 0 8px; }
  .nav-item { padding: 10px 14px; font-size: var(--font-size-base); }
  .footer-content { flex-wrap: wrap; gap: 24px; }
  .footer-col { flex-basis: 45%; }
}
</style>
