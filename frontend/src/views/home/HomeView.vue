<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotProducts } from '../../api/product'
import { getCategories } from '../../api/category'
import ProductCard from '../../components/product/ProductCard.vue'
import type { Product, Category } from '../../types'

const router = useRouter()
const products = ref<Product[]>([])
const categories = ref<Category[]>([])

onMounted(async () => {
  try {
    const [hotRes, catRes] = await Promise.all([
      getHotProducts(),
      getCategories()
    ])
    products.value = hotRes.data
    categories.value = catRes.data.slice(0, 8)
  } catch { /* API not ready yet */ }
})
</script>

<template>
  <div class="home">
    <!-- Hero banner -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">品质生活，从这里开始</h1>
        <p class="hero-subtitle">精选万款好物，享品质购物体验</p>
        <div class="hero-actions">
          <el-button size="large" round class="hero-btn-primary" @click="router.push('/products')">
            立即选购
            <el-icon><ArrowRight /></el-icon>
          </el-button>
          <el-button size="large" round class="hero-btn-secondary" @click="router.push('/products?sort=newest')">
            新品上市
          </el-button>
        </div>
      </div>
      <div class="hero-decor">
        <div class="decor-circle c1"></div>
        <div class="decor-circle c2"></div>
        <div class="decor-circle c3"></div>
      </div>
    </section>

    <!-- Category quick nav -->
    <section class="page-container">
      <div class="category-quick">
        <div v-for="cat in categories" :key="cat.id" class="cat-item"
          @click="router.push(`/products?categoryId=${cat.id}`)">
          <div class="cat-icon-wrap">
            <el-icon :size="28"><GoodsFilled /></el-icon>
          </div>
          <span class="cat-name">{{ cat.name }}</span>
        </div>
      </div>
    </section>

    <!-- Hot products -->
    <section class="page-container">
      <div class="section-header">
        <div>
          <h2 class="section-title">🔥 热销爆款</h2>
          <p class="section-subtitle">大家都在买的好物</p>
        </div>
        <router-link to="/products" class="section-more">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      <div class="product-grid">
        <ProductCard v-for="p in products" :key="p.id" :product="p" />
      </div>
      <el-empty v-if="products.length === 0" description="暂无商品" />
    </section>

    <!-- Promo banner -->
    <section class="page-container">
      <div class="promo-banner" @click="router.push('/products?sort=newest')">
        <div class="promo-text">
          <h3>新品上架</h3>
          <p>每周更新精选好物，发现更多惊喜</p>
          <span class="promo-link">去看看 <el-icon><ArrowRight /></el-icon></span>
        </div>
        <div class="promo-visual">
          <el-icon :size="80"><Present /></el-icon>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* Hero */
.hero {
  position: relative;
  background: linear-gradient(135deg, #FF4757 0%, #E60012 50%, #FF6348 100%);
  padding: 80px 20px;
  text-align: center;
  color: #FFF;
  overflow: hidden;
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 700px;
  margin: 0 auto;
}

.hero-title {
  font-size: 44px;
  font-weight: 700;
  margin-bottom: 16px;
  letter-spacing: 1px;
}

.hero-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 36px;
  font-weight: 300;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.hero-btn-primary {
  background: #FFF;
  color: var(--color-primary);
  border: none;
  font-weight: 600;
  padding: 14px 36px;
  font-size: 16px;
}

.hero-btn-primary:hover {
  background: #FFF;
  color: var(--color-primary-hover);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.hero-btn-secondary {
  background: rgba(255,255,255,0.2);
  color: #FFF;
  border: 1px solid rgba(255,255,255,0.4);
  font-weight: 500;
  padding: 14px 36px;
  font-size: 16px;
}

.hero-btn-secondary:hover {
  background: rgba(255,255,255,0.3);
  border-color: #FFF;
}

.hero-decor .decor-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255,255,255,0.08);
}

.decor-circle.c1 {
  width: 300px;
  height: 300px;
  top: -80px;
  right: -60px;
}

.decor-circle.c2 {
  width: 200px;
  height: 200px;
  bottom: -40px;
  left: 10%;
}

.decor-circle.c3 {
  width: 100px;
  height: 100px;
  top: 40%;
  right: 20%;
}

/* Category quick nav */
.category-quick {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 16px;
  margin: 24px 0 8px;
}

.cat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 12px;
  background: var(--color-white);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-sm);
}

.cat-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover);
  color: var(--color-primary);
}

.cat-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFF0F2, #FFF5F5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
}

.cat-name {
  font-size: var(--font-size-sm);
  font-weight: 500;
}

/* Section header */
.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-title {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--color-text-primary);
}

.section-subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-top: 4px;
}

.section-more {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  transition: color var(--transition-fast);
  padding-bottom: 2px;
}

.section-more:hover {
  color: var(--color-primary);
}

/* Product grid */
.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

/* Promo banner */
.promo-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #FFFBF7, #FFF8F5);
  border: 1px solid #FFE8D0;
  border-radius: var(--radius-lg);
  padding: 40px 48px;
  margin: 8px 0 20px;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.promo-banner:hover {
  box-shadow: var(--shadow-md);
}

.promo-text h3 {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 8px;
}

.promo-text p {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin-bottom: 12px;
}

.promo-link {
  font-size: var(--font-size-base);
  color: var(--color-primary);
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.promo-visual {
  color: #FFD0A0;
}

@media (max-width: 1200px) {
  .product-grid { grid-template-columns: repeat(4, 1fr); }
  .category-quick { grid-template-columns: repeat(4, 1fr); }
}

@media (max-width: 768px) {
  .hero { padding: 48px 16px; }
  .hero-title { font-size: 28px; }
  .hero-subtitle { font-size: 15px; }
  .product-grid { grid-template-columns: repeat(2, 1fr); gap: 10px; }
  .category-quick { grid-template-columns: repeat(4, 1fr); gap: 8px; }
  .cat-item { padding: 14px 8px; }
  .cat-icon-wrap { width: 40px; height: 40px; }
  .promo-banner { padding: 24px; flex-direction: column; text-align: center; gap: 16px; }
}
</style>
