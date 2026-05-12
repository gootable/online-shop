<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useCartStore } from '../../stores/cart'
import { useUserStore } from '../../stores/user'
import { formatPrice } from '../../utils/format'
import type { Product } from '../../types'

const props = defineProps<{ product: Product }>()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

function goToDetail() {
  router.push(`/products/${props.product.id}`)
}

async function addToCart(event: Event) {
  event.stopPropagation()
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  await cartStore.addToCart(props.product.id)
}
</script>

<template>
  <div class="product-card" @click="goToDetail">
    <div class="card-image">
      <el-image :src="product.mainImage" fit="cover" lazy>
        <template #error>
          <div class="img-placeholder">
            <el-icon :size="44"><PictureFilled /></el-icon>
          </div>
        </template>
      </el-image>
      <div v-if="product.stock === 0" class="sold-out-overlay">
        <span>已售罄</span>
      </div>
    </div>
    <div class="card-body">
      <h3 class="card-name" :title="product.name">{{ product.name }}</h3>
      <div class="card-price-row">
        <span class="card-price">{{ formatPrice(product.price) }}</span>
        <span class="card-sales">已售 {{ product.sales }}</span>
      </div>
      <el-button class="card-add-btn" :disabled="product.stock === 0" @click="addToCart">
        <el-icon :size="16"><ShoppingCart /></el-icon> 加入购物车
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.product-card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-sm);
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

.product-card:hover .card-add-btn {
  opacity: 1;
  visibility: visible;
}

.card-image {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  background: #FAFAFA;
}

.card-image :deep(.el-image) {
  width: 100%;
  height: 100%;
  transition: transform var(--transition-normal);
}

.product-card:hover .card-image :deep(.el-image img) {
  transform: scale(1.05);
}

.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F5;
  color: #CCC;
}

.sold-out-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
}

.sold-out-overlay span {
  color: #FFF;
  font-size: 18px;
  font-weight: 600;
  border: 2px solid #FFF;
  padding: 6px 20px;
  border-radius: 20px;
}

.card-body {
  padding: 14px;
}

.card-name {
  font-size: var(--font-size-base);
  font-weight: 500;
  color: var(--color-text-primary);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 42px;
  margin-bottom: 10px;
}

.card-price-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 10px;
}

.card-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-price);
  letter-spacing: -0.5px;
}

.card-sales {
  font-size: var(--font-size-xs);
  color: var(--color-text-placeholder);
}

.card-add-btn {
  width: 100%;
  border-radius: 20px;
  opacity: 0.85;
  visibility: visible;
  transition: all var(--transition-normal);
}

@media (min-width: 769px) {
  .card-add-btn {
    opacity: 0;
    visibility: hidden;
  }
}
</style>
