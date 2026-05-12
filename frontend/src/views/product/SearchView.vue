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
const size = 12

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
  window.scrollTo(0, 0)
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
    <h1 class="page-title">
      搜索"{{ keyword }}"的结果
      <span v-if="total > 0" class="result-count">（共 {{ total }} 件）</span>
    </h1>
    <div v-loading="loading" class="product-grid">
      <ProductCard v-for="p in products" :key="p.id" :product="p" />
    </div>
    <el-empty v-if="!loading && products.length === 0" description="未找到相关商品" />
    <div v-if="total > size" class="pagination-wrap">
      <el-pagination layout="prev, pager, next" :total="total" :page-size="size"
        :current-page="page" @current-change="changePage" />
    </div>
  </div>
</template>

<style scoped>
.result-count {
  font-size: 16px;
  font-weight: 400;
  color: #909399;
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
</style>
