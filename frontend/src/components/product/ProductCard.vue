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
    <div class="product-image-wrap">
      <el-image :src="product.mainImage" fit="cover" class="product-image">
        <template #error>
          <div class="image-placeholder">
            <el-icon :size="48"><PictureFilled /></el-icon>
          </div>
        </template>
      </el-image>
      <div v-if="product.stock === 0" class="sold-out-mask">
        <span>已售罄</span>
      </div>
    </div>
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <div class="product-price">{{ formatPrice(product.price) }}</div>
      <div class="product-footer">
        <span class="product-sales">已售 {{ product.sales }}</span>
        <el-button type="primary" size="small" :disabled="product.stock === 0" @click="addToCart">
          <el-icon><ShoppingCart /></el-icon>
          加购
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
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

.product-image-wrap {
  position: relative;
  width: 100%;
  height: 220px;
}

.product-image {
  width: 100%;
  height: 100%;
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

.sold-out-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
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
  margin-bottom: 8px;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-sales {
  font-size: 12px;
  color: #999;
}
</style>
