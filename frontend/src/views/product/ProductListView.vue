<script setup lang="ts">
import { ref, onMounted } from 'vue'
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
const size = 12

const sortOptions = [
  { label: '销量优先', value: 'sales_desc' },
  { label: '价格从低到高', value: 'price_asc' },
  { label: '价格从高到低', value: 'price_desc' },
  { label: '最新上架', value: 'newest' }
]

onMounted(async () => {
  try {
    const catRes = await getCategories()
    categories.value = catRes.data
  } catch { /* ignore */ }
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
  router.replace({ query: { ...route.query, keyword: keyword.value || undefined, page: 1 } })
  fetchProducts()
}

function selectCategory(id: number | undefined) {
  categoryId.value = id
  page.value = 1
  router.replace({ query: { ...route.query, categoryId: id, page: 1 } })
  fetchProducts()
}

function changeSort(val: string) {
  sort.value = val
  fetchProducts()
}

function changePage(p: number) {
  page.value = p
  fetchProducts()
  window.scrollTo(0, 0)
}
</script>

<template>
  <div class="product-list-page">
    <div class="page-container">
      <div class="content-layout">
        <aside class="sidebar">
          <div class="category-nav">
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
        <div class="main-area">
          <div class="toolbar">
            <div class="search-bar">
              <el-input v-model="keyword" placeholder="搜索商品..." clearable @keyup.enter="search" @clear="search" />
              <el-button type="primary" @click="search">搜索</el-button>
            </div>
            <div class="sort-bar">
              <span>排序：</span>
              <el-radio-group v-model="sort" @change="changeSort">
                <el-radio-button v-for="opt in sortOptions" :key="opt.value" :value="opt.value">
                  {{ opt.label }}
                </el-radio-button>
              </el-radio-group>
            </div>
          </div>

          <div v-loading="loading" class="product-grid">
            <ProductCard v-for="p in products" :key="p.id" :product="p" />
          </div>

          <el-empty v-if="!loading && products.length === 0" description="暂无商品" />

          <div v-if="total > size" class="pagination-wrap">
            <el-pagination
              layout="prev, pager, next"
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
  background: #f5f7fa;
  min-height: 100vh;
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
  background: #fff;
  border-radius: 8px;
  padding: 16px;
}

.category-nav h3 {
  font-size: 16px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.category-nav ul {
  list-style: none;
}

.category-nav li {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
}

.category-nav li:hover {
  background: #f0f2f5;
}

.category-nav li.active {
  background: #ecf5ff;
  color: #409eff;
  font-weight: 500;
}

.main-area {
  flex: 1;
  min-width: 0;
}

.toolbar {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.search-bar .el-input {
  max-width: 400px;
}

.sort-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  min-height: 200px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .sidebar {
    display: none;
  }
}
</style>
