<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotProducts } from '../../api/product'
import type { Product } from '../../types'
import { formatPrice } from '../../utils/format'

const router = useRouter()
const products = ref<Product[]>([])

onMounted(async () => {
  try {
    const res = await getHotProducts()
    products.value = res.data
  } catch { /* will be handled when API is ready */ }
})
</script>

<template>
  <div class="home">
    <div class="hero-banner">
      <h1>欢迎来到网上商城</h1>
      <p>精选好物，尽在其中</p>
    </div>
    <div class="page-container">
      <h2 class="section-title">热销商品</h2>
      <div class="product-grid">
        <div v-for="p in products" :key="p.id" class="product-card" @click="router.push(`/products/${p.id}`)">
          <el-image :src="p.mainImage" fit="cover" class="product-image">
            <template #error>
              <div class="image-placeholder"><el-icon :size="48"><PictureFilled /></el-icon></div>
            </template>
          </el-image>
          <div class="product-info">
            <h3 class="product-name">{{ p.name }}</h3>
            <div class="product-price">{{ formatPrice(p.price) }}</div>
            <div class="product-sales">已售 {{ p.sales }}</div>
          </div>
        </div>
      </div>
      <el-empty v-if="products.length === 0" description="暂无商品" />
    </div>
  </div>
</template>

<style scoped>
.hero-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  text-align: center;
  padding: 80px 20px;
  margin-bottom: 40px;
}

.hero-banner h1 {
  font-size: 40px;
  margin-bottom: 12px;
}

.hero-banner p {
  font-size: 18px;
  opacity: 0.9;
}

.section-title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 220px;
}

.image-placeholder {
  width: 100%;
  height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 15px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 18px;
  font-weight: 600;
  color: #e64242;
}

.product-sales {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>
