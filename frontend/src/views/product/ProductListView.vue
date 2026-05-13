<script setup lang="ts">
import { ref, onMounted, watch, computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProducts } from '../../api/product'
import { getCategoryTree } from '../../api/category'
import ProductCard from '../../components/product/ProductCard.vue'
import type { Product, Category } from '../../types'

const route = useRoute()
const router = useRouter()
const products = ref<Product[]>([])
const categoryTree = ref<Category[]>([])
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

// Track which parent categories are expanded
const expandedIds = reactive(new Set<number>())

// Find all ancestor IDs for a given category ID
function getAncestorIds(catId: number, nodes: Category[], path: number[] = []): number[] {
  for (const node of nodes) {
    if (node.id === catId) return path
    if (node.children && node.children.length > 0) {
      const found = getAncestorIds(catId, node.children, [...path, node.id])
      if (found.length > 0 || node.id === catId) return found
    }
  }
  return []
}

// Expand ancestors AND the category itself (if it has children)
function expandAncestors(catId: number | undefined) {
  if (!catId) return
  const ancestors = getAncestorIds(catId, categoryTree.value)
  ancestors.forEach(id => expandedIds.add(id))
  // Also expand the category itself if it has children
  expandedIds.add(catId)
}

// Visible items: only children of expanded parents (or depth 0)
interface FlatCategory extends Category {
  depth: number
  isParent: boolean
  expanded: boolean
}
const flatCategories = computed<FlatCategory[]>(() => {
  const result: FlatCategory[] = []
  function walk(nodes: Category[], depth: number) {
    for (const node of nodes) {
      const hasChildren = !!(node.children && node.children.length > 0)
      const isExpanded = expandedIds.has(node.id)
      result.push({ ...node, depth, isParent: hasChildren, expanded: isExpanded })
      if (hasChildren && isExpanded && node.children) {
        walk(node.children, depth + 1)
      }
    }
  }
  walk(categoryTree.value, 0)
  return result
})

onMounted(async () => {
  try {
    const res = await getCategoryTree()
    categoryTree.value = res.data
    expandAncestors(categoryId.value)
  } catch { /* ignore */ }
  fetchProducts()
})

// React to route query changes from header nav
watch(() => route.query, (q) => {
  keyword.value = (q.keyword as string) || ''
  const newCatId = q.categoryId ? Number(q.categoryId) : undefined
  categoryId.value = newCatId
  sort.value = (q.sort as string) || 'sales_desc'
  page.value = Number(q.page) || 1
  expandAncestors(newCatId)
  fetchProducts()
})

function toggleExpand(cat: FlatCategory) {
  if (expandedIds.has(cat.id)) {
    expandedIds.delete(cat.id)
  } else {
    expandedIds.add(cat.id)
  }
}

function countChildren(cat: FlatCategory): number {
  if (!cat.children) return 0
  let count = cat.children.length
  for (const child of cat.children) {
    if (child.children) count += child.children.length
  }
  return count
}

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
            <div class="cat-list" v-if="categoryTree.length > 0">
              <div class="cat-item all-item" :class="{ active: !categoryId }" @click="selectCategory(undefined)">
                <span class="cat-text">全部分类</span>
                <span v-if="!categoryId" class="cat-count-badge"></span>
              </div>

              <div v-for="cat in flatCategories" :key="cat.id" class="cat-group">
                <div
                  class="cat-item"
                  :class="{
                    active: categoryId === cat.id,
                    'depth-0': cat.depth === 0 && cat.isParent,
                    'depth-1': cat.depth === 1,
                    'depth-2': cat.depth >= 2,
                    expanded: cat.isParent && cat.expanded
                  }"
                >
                  <!-- Expand/collapse arrow (parents only) -->
                  <span v-if="cat.isParent" class="cat-arrow"
                    @click.stop="toggleExpand(cat)"
                    role="button" :aria-label="cat.expanded ? '折叠' : '展开'">
                    <el-icon :size="14" class="arrow-icon">
                      <ArrowRight v-if="!cat.expanded" />
                      <ArrowDown v-else />
                    </el-icon>
                  </span>
                  <!-- Full-width clickable label -->
                  <span class="cat-text" @click="selectCategory(cat.id)">
                    {{ cat.name }}
                  </span>
                  <span v-if="cat.isParent" class="cat-count">{{ countChildren(cat) }}</span>
                </div>
              </div>
            </div>
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
  padding: 0;
  position: sticky;
  top: 160px;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
  overflow-x: hidden;
}

.category-nav h3 {
  font-size: var(--font-size-lg);
  font-weight: 700;
  padding: 20px 20px 16px;
  color: var(--color-text-primary);
  margin: 0;
}

.cat-list {
  padding: 0 12px 16px;
}

/* Individual item */
.cat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 44px;              /* Minimum touch target */
  padding: 8px 12px;
  margin: 2px 4px;
  border-radius: 8px;
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  transition: all var(--transition-fast);
  position: relative;
  user-select: none;
}

.cat-item:hover {
  background: #F5F5F5;
}

.cat-item.active {
  background: linear-gradient(135deg, #FFF0F2, #FFF5F5);
  color: var(--color-primary);
  font-weight: 600;
}

/* All categories */
.cat-item.all-item {
  font-weight: 600;
  color: var(--color-text-primary);
  padding: 12px 16px;
  margin-bottom: 6px;
  border-bottom: 1px solid var(--color-border-light);
  border-radius: 0;
  margin-left: 0;
  margin-right: 0;
  cursor: pointer;
  min-height: 44px;
}

.cat-item.all-item:hover {
  background: #FAFAFA;
}

.cat-item.all-item.active {
  background: linear-gradient(135deg, #FFF0F2, #FFF5F5);
  border-bottom-color: transparent;
  border-radius: 8px;
  margin: 2px 4px 6px;
}

/* Hierarchy depth */
.cat-item.depth-0 {
  font-weight: 600;
  color: var(--color-text-primary);
  margin-top: 4px;
}

.cat-item.depth-1 {
  padding-left: 28px;
  font-size: var(--font-size-sm);
  margin: 1px 4px 1px 8px;
  border-left: 2px solid transparent;
}

.cat-item.depth-1:hover {
  border-left-color: #E0E0E0;
}

.cat-item.depth-1.active {
  border-left-color: var(--color-primary);
}

.cat-item.depth-2 {
  padding-left: 46px;
  font-size: var(--font-size-xs);
  margin: 1px 4px 1px 16px;
  border-left: 2px solid transparent;
}

.cat-item.depth-2:hover {
  border-left-color: #E0E0E0;
}

.cat-item.depth-2.active {
  border-left-color: var(--color-primary);
}

/* Arrow icon — only clickable area for expand/collapse */
.cat-arrow {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: var(--color-text-placeholder);
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.cat-arrow:hover {
  background: #ECECEC;
  color: var(--color-text-primary);
}

.arrow-icon {
  transition: transform var(--transition-fast);
}

/* Category text — full-row click to select category */
.cat-text {
  flex: 1;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 4px 0;
}

/* Child count badge */
.cat-count {
  font-size: 11px;
  color: var(--color-text-placeholder);
  background: #F0F0F0;
  padding: 2px 8px;
  border-radius: 10px;
  flex-shrink: 0;
  min-width: 20px;
  text-align: center;
}

.cat-item.active .cat-count {
  background: rgba(230, 0, 18, 0.1);
  color: var(--color-primary);
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
