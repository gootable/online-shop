<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProducts } from '../../api/product'
import { getCategories } from '../../api/category'
import ProductCard from '../../components/product/ProductCard.vue'
import type { Product, Category } from '../../types'

const route = useRoute()
const router = useRouter()
const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const total = ref(0)
const loading = ref(false)

const keyword = ref((route.query.keyword as string) || '')
const categoryId = ref(route.query.categoryId ? Number(route.query.categoryId) : undefined)
const sort = ref((route.query.sort as string) || 'sales_desc')
const page = ref(Number(route.query.page) || 1)
const size = 20

const sortOptions = [
  { label: '综合排序', value: 'sales_desc' },
  { label: '价格升序', value: 'price_asc' },
  { label: '价格降序', value: 'price_desc' },
  { label: '最新上架', value: 'newest' }
]

onMounted(async () => {
  try {
    const catRes = await getCategories()
    categories.value = catRes.data
  } catch { /* ignore */ }
  fetchProducts()
})

// React to route query changes from header nav
watch(() => route.query, (q) => {
  keyword.value = (q.keyword as string) || ''
  categoryId.value = q.categoryId ? Number(q.categoryId) : undefined
  sort.value = (q.sort as string) || 'sales_desc'
  page.value = Number(q.page) || 1
  fetchProducts()
})

function fetchProducts() {
  loading.value = true
  getProducts({ page: page.value, size, keyword: keyword.value, categoryId: categoryId.value, sort: sort.value })
    .then(res => {
      products.value = res.data.records
      total.value = res.data.total
    })
    .finally(() => loading.value = false)
}

function search() {
  page.value = 1
  router.replace({ query: { keyword: keyword.value || undefined, page: 1 } })
}

function selectCategory(id: number | undefined) {
  categoryId.value = id
  page.value = 1
  router.replace({ query: { keyword: keyword.value || undefined, categoryId: id, page: 1 } })
}

function changeSort(val: string) {
  sort.value = val
  router.replace({ query: { ...route.query, sort: val } })
}

function changePage(p: number) {
  page.value = p
  fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<template>
  <div class="product-list-page">
    <div class="page-container">
      <div class="content-layout">
        <!-- Sidebar -->
        <aside class="sidebar">
          <div class="category-nav card">
            <h3>商品分类</h3>
            <ul>
              <li :class="{ active: !categoryId }" @click="selectCategory(undefined)">全部分类</li>
              <li v-for="cat in categories" :key="cat.id"
                  :class="{ active: categoryId === cat.id }"
                  @click="selectCategory(cat.id)">
                {{ cat.name }}
              </li>
            </ul>
          </div>
        </aside>

        <!-- Main area -->
        <div class="main-area">
          <div class="toolbar card">
            <div class="toolbar-top">
              <el-input v-model="keyword" placeholder="搜索商品..." clearable
                class="search-input" @keyup.enter="search" @clear="search">
                <template #prefix><el-icon><Search /></el-icon></template>
              </el-input>
              <div class="sort-group">
                <el-radio-group v-model="sort" size="small" @change="changeSort">
                  <el-radio-button v-for="opt in sortOptions" :key="opt.value" :value="opt.value">
                    {{ opt.label }}
                  </el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </div>

          <div v-loading="loading" class="product-grid" element-loading-text="正在加载...">
            <ProductCard v-for="p in products" :key="p.id" :product="p" />
          </div>

          <el-empty v-if="!loading && products.length === 0" description="暂无商品" />

          <div v-if="total > size" class="pagination-wrap">
            <el-pagination
              layout="prev, pager, next"
              background
              :total="total"
              :page-size="size"
              :current-page="page"
              @current-change="changePage"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-list-page {
  background: var(--color-bg);
  min-height: 100vh;
  padding-top: 8px;
}

.content-layout {
  display: flex;
  gap: 20px;
}

.sidebar {
  width: 200px;
  flex-shrink: 0;
}

.category-nav {
  padding: 16px;
  position: sticky;
  top: 160px;
}

.category-nav h3 {
  font-size: var(--font-size-md);
  font-weight: 600;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--color-primary);
  color: var(--color-text-primary);
}

.category-nav ul { display: flex; flex-direction: column; gap: 2px; }

.category-nav li {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 6px;
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  transition: all var(--transition-fast);
}

.category-nav li:hover {
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.category-nav li.active {
  background: var(--color-primary-light);
  color: var(--color-primary);
  font-weight: 600;
}

.main-area { flex: 1; min-width: 0; }

.toolbar {
  padding: 16px;
  margin-bottom: 16px;
}

.toolbar-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.search-input { max-width: 320px; }

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  min-height: 200px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-bottom: 20px;
}

@media (max-width: 1200px) {
  .product-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 900px) {
  .sidebar { display: none; }
  .product-grid { grid-template-columns: repeat(2, 1fr); gap: 10px; }
  .toolbar-top { flex-direction: column; }
  .search-input { max-width: none; }
}
</style>
