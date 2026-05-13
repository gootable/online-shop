<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '../../api/product'
import { useCartStore } from '../../stores/cart'
import { useUserStore } from '../../stores/user'
import { formatPrice } from '../../utils/format'
import type { Product } from '../../types'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref<Product | null>(null)
const loading = ref(false)
const quantity = ref(1)
const currentImage = ref('')

const id = computed(() => Number(route.params.id))

onMounted(async () => {
  loading.value = true
  try {
    const res = await getProductDetail(id.value)
    product.value = res.data
    currentImage.value = res.data.mainImage || ''
  } finally {
    loading.value = false
  }
})

async function handleAddToCart() {
  if (!userStore.isLoggedIn) { router.push('/login'); return }
  await cartStore.addToCart(product.value!.id, quantity.value)
}

function handleBuyNow() {
  if (!userStore.isLoggedIn) { router.push('/login'); return }
  cartStore.addToCart(product.value!.id, quantity.value).then(() => {
    router.push('/orders/create')
  })
}

const thumbImages = computed(() => {
  if (!product.value) return []
  const imgs = product.value.mainImage ? [product.value.mainImage] : []
  if (product.value.images) imgs.push(...product.value.images)
  return imgs
})
</script>

<template>
  <div v-loading="loading" class="page-container">
    <div v-if="product" class="product-detail">
      <!-- Gallery -->
      <div class="gallery">
        <div class="main-img">
          <el-image :src="currentImage" fit="contain"
            style="width:100%;aspect-ratio:1;cursor:zoom-in"
            :preview-src-list="thumbImages"
            :preview-teleported="true"
            :zoom-rate="1.2"
            :initial-index="thumbImages.indexOf(currentImage)"
            preview-teleported>
            <template #error>
              <div class="img-placeholder"><el-icon :size="64"><PictureFilled /></el-icon></div>
            </template>
          </el-image>
        </div>
        <div v-if="thumbImages.length > 1" class="thumb-list">
          <div v-for="(img, i) in thumbImages" :key="i"
               :class="['thumb', { active: currentImage === img }]"
               @click="currentImage = img">
            <el-image :src="img" fit="cover" style="width:64px;height:64px;border-radius:6px"
              :preview-src-list="thumbImages"
              :preview-teleported="true"
              :initial-index="i" />
          </div>
        </div>
      </div>

      <!-- Info -->
      <div class="info">
        <h1 class="p-name">{{ product.name }}</h1>
        <div class="p-price-box">
          <span class="p-price">{{ formatPrice(product.price) }}</span>
          <span class="p-sales">已售 {{ product.sales }}</span>
        </div>
        <div class="p-meta">
          <span v-if="product.categoryName">分类：{{ product.categoryName }}</span>
          <span>库存：{{ product.stock > 0 ? product.stock : '已售罄' }}</span>
        </div>

        <template v-if="product.stock > 0">
          <div class="quantity-row">
            <span>数量</span>
            <el-input-number v-model="quantity" :min="1" :max="product.stock" size="large" />
            <span class="stock-info">（库存 {{ product.stock }} 件）</span>
          </div>
          <div class="btn-row">
            <el-button size="large" class="btn-cart" @click="handleAddToCart">
              <el-icon><ShoppingCart /></el-icon>加入购物车
            </el-button>
            <el-button type="danger" size="large" class="btn-buy" @click="handleBuyNow">
              立即购买
            </el-button>
          </div>
        </template>
        <el-alert v-else title="该商品已售罄" type="warning" show-icon :closable="false" />

        <div v-if="product.description" class="desc-section">
          <h3>商品详情</h3>
          <div class="desc-content">{{ product.description }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-detail {
  display: flex;
  gap: 40px;
  background: var(--color-white);
  border-radius: var(--radius-lg);
  padding: 32px;
  box-shadow: var(--shadow-sm);
}

.gallery {
  width: 460px;
  flex-shrink: 0;
}

.main-img {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
  margin-bottom: 12px;
}

.img-placeholder {
  width: 100%;
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F5;
  color: #CCC;
}

.thumb-list {
  display: flex;
  gap: 8px;
  overflow-x: auto;
}

.thumb {
  border: 2px solid transparent;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: border-color var(--transition-fast);
  flex-shrink: 0;
}

.thumb.active { border-color: var(--color-primary); }

.info { flex: 1; min-width: 0; }

.p-name {
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--color-text-primary);
  line-height: 1.4;
  margin-bottom: 16px;
}

.p-price-box {
  background: var(--color-price-bg);
  padding: 16px 20px;
  border-radius: var(--radius-md);
  margin-bottom: 20px;
  display: flex;
  align-items: baseline;
  gap: 16px;
}

.p-price {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-price);
  letter-spacing: -1px;
}

.p-sales {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.p-meta {
  display: flex;
  gap: 24px;
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin-bottom: 28px;
}

.quantity-row {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: var(--font-size-base);
  color: var(--color-text-regular);
  margin-bottom: 20px;
}

.stock-info {
  font-size: var(--font-size-xs);
  color: var(--color-text-placeholder);
}

.btn-row {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
}

.btn-cart {
  min-width: 180px;
  height: 48px;
  font-size: var(--font-size-md);
  border-radius: 24px;
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.btn-cart:hover {
  background: var(--color-primary-light);
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.btn-buy {
  min-width: 180px;
  height: 48px;
  font-size: var(--font-size-md);
  border-radius: 24px;
}

.desc-section {
  border-top: 1px solid var(--color-border);
  padding-top: 24px;
}

.desc-section h3 {
  font-size: var(--font-size-md);
  font-weight: 600;
  margin-bottom: 12px;
}

.desc-content {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  line-height: 1.8;
  white-space: pre-wrap;
}

@media (max-width: 900px) {
  .product-detail { flex-direction: column; padding: 16px; gap: 20px; }
  .gallery { width: 100%; }
  .p-price { font-size: 26px; }
  .btn-row { flex-direction: column; }
  .btn-cart, .btn-buy { min-width: auto; }
}
</style>
