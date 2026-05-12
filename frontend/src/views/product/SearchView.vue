<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchProducts } from '../../api/product'
import ProductCard from '../../components/product/ProductCard.vue'
import type { Product } from '../../types'

const route = useRoute()
const router = useRouter()
const products = ref<Product[]>([])
const total = ref(0)
const loading = ref(false)
const keyword = ref((route.query.keyword as string) || '')
const page = ref(Number(route.query.page) || 1)
const size = 20

function fetchResults() {
  if (!keyword.value) return
  loading.value = true
  searchProducts(keyword.value, page.value, size)
    .then(res => {
      products.value = res.data.records
      total.value = res.data.total
    })
    .finally(() => loading.value = false)
}

function changePage(p: number) {
  page.value = p
  router.replace({ query: { keyword: keyword.value, page: p } })
  fetchResults()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(fetchResults)

watch(() => route.query.keyword, (val) => {
  keyword.value = (val as string) || ''
  page.value = 1
  fetchResults()
})
</script>

<template>
  <div class="page-container">
    <div class="search-header">
      <h1 class="page-title">
        搜索结果：<span class="kw">"{{ keyword }}"</span>
        <span v-if="total > 0" class="count">（共 {{ total }} 件）</span>
      </h1>
    </div>
    <div v-loading="loading" class="product-grid">
      <ProductCard v-for="p in products" :key="p.id" :product="p" />
    </div>
    <el-empty v-if="!loading && products.length === 0" description="未找到相关商品" />
    <div v-if="total > size" class="pagination-wrap">
      <el-pagination layout="prev, pager, next" background :total="total" :page-size="size"
        :current-page="page" @current-change="changePage" />
    </div>
  </div>
</template>

<style scoped>
.search-header { margin-bottom: 20px; }
.kw { color: var(--color-primary); }
.count { font-size: var(--font-size-base); font-weight: 400; color: var(--color-text-secondary); }
.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  min-height: 200px;
}
.pagination-wrap { display: flex; justify-content: center; margin-top: 24px; }
@media (max-width: 1200px) { .product-grid { grid-template-columns: repeat(4, 1fr); } }
@media (max-width: 768px) { .product-grid { grid-template-columns: repeat(2, 1fr); gap: 10px; } }
</style>
