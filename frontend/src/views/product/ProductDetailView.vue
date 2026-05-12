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
    currentImage.value = res.data.mainImage
  } finally {
    loading.value = false
  }
})

async function handleAddToCart() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  await cartStore.addToCart(product.value!.id, quantity.value)
}

function handleBuyNow() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  cartStore.addToCart(product.value!.id, quantity.value).then(() => {
    router.push('/orders/create')
  })
}
</script>

<template>
  <div v-loading="loading" class="page-container">
    <div v-if="product" class="product-detail">
      <div class="image-section">
        <div class="main-image">
          <el-image :src="currentImage" fit="contain" style="width:100%;height:400px">
            <template #error>
              <div class="image-placeholder"><el-icon :size="64"><PictureFilled /></el-icon></div>
            </template>
          </el-image>
        </div>
        <div class="image-list" v-if="product.images?.length">
          <div v-for="(img, i) in [product.mainImage, ...product.images]" :key="i"
               :class="['thumb', { active: currentImage === img }]"
               @click="currentImage = img">
            <el-image :src="img" fit="cover" style="width:60px;height:60px" />
          </div>
        </div>
      </div>
      <div class="info-section">
        <h1 class="product-name">{{ product.name }}</h1>
        <div class="product-price">{{ formatPrice(product.price) }}</div>
        <div class="product-meta">
          <span>库存：{{ product.stock > 0 ? product.stock : '已售罄' }}</span>
          <span>已售：{{ product.sales }}</span>
          <span v-if="product.categoryName">分类：{{ product.categoryName }}</span>
        </div>
        <div v-if="product.stock > 0" class="add-section">
          <div class="quantity-row">
            <span>数量：</span>
            <el-input-number v-model="quantity" :min="1" :max="product.stock" />
          </div>
          <div class="btn-row">
            <el-button size="large" @click="handleAddToCart">
              <el-icon><ShoppingCart /></el-icon> 加入购物车
            </el-button>
            <el-button type="danger" size="large" @click="handleBuyNow">立即购买</el-button>
          </div>
        </div>
        <el-alert v-else title="该商品已售罄" type="warning" show-icon :closable="false" />
        <div v-if="product.description" class="description">
          <h3>商品描述</h3>
          <p>{{ product.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-detail {
  display: flex;
  gap: 40px;
  background: #fff;
  border-radius: 8px;
  padding: 32px;
}

.image-section {
  width: 450px;
  flex-shrink: 0;
}

.main-image {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
}

.image-placeholder {
  width: 100%;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}

.image-list {
  display: flex;
  gap: 8px;
}

.thumb {
  border: 2px solid transparent;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.2s;
}

.thumb.active {
  border-color: #409eff;
}

.info-section {
  flex: 1;
}

.product-name {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
}

.product-price {
  font-size: 28px;
  font-weight: 700;
  color: #e64242;
  margin-bottom: 16px;
  padding: 12px;
  background: #fef0f0;
  border-radius: 4px;
}

.product-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #909399;
  margin-bottom: 24px;
}

.add-section {
  margin-bottom: 24px;
}

.quantity-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  font-size: 14px;
}

.btn-row {
  display: flex;
  gap: 12px;
}

.description {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.description h3 {
  font-size: 16px;
  margin-bottom: 12px;
}

.description p {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
