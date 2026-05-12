<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../../stores/cart'
import { formatPrice } from '../../utils/format'

const router = useRouter()
const cartStore = useCartStore()

const allSelected = computed({
  get: () => cartStore.items.length > 0 && cartStore.items.every(item => item.selected),
  set: (val: boolean) => cartStore.toggleSelectAll(val)
})

onMounted(() => cartStore.fetchCart())

function checkout() {
  if (cartStore.selectedItems.length === 0) return
  router.push('/orders/create')
}
</script>

<template>
  <div class="page-container">
    <h1 class="page-title">我的购物车（{{ cartStore.items.length }}）</h1>

    <div v-if="cartStore.items.length > 0" class="cart-content">
      <div class="cart-table card">
        <div class="cart-header">
          <el-checkbox v-model="allSelected" label="全选" />
        </div>
        <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
          <div class="item-select">
            <el-checkbox :model-value="item.selected"
              @change="(val: boolean) => cartStore.updateItem(item.id, { selected: val })" />
          </div>
          <div class="item-product" @click="router.push(`/products/${item.productId}`)">
            <el-image :src="item.productImage" fit="cover" class="item-img">
              <template #error><div class="img-fallback"><el-icon :size="28"><PictureFilled /></el-icon></div></template>
            </el-image>
            <span class="item-name">{{ item.productName }}</span>
          </div>
          <div class="item-price">{{ formatPrice(item.price) }}</div>
          <div class="item-qty">
            <el-input-number v-model="item.quantity" :min="1" size="small"
              @change="(val: number) => cartStore.updateItem(item.id, { quantity: val! })" />
          </div>
          <div class="item-subtotal">{{ formatPrice(item.price * item.quantity) }}</div>
          <div class="item-action">
            <el-button type="danger" link @click="cartStore.removeItem(item.id)">删除</el-button>
          </div>
        </div>
      </div>

      <div class="cart-footer card">
        <div class="footer-left">
          <el-checkbox v-model="allSelected">全选</el-checkbox>
        </div>
        <div class="footer-right">
          <span class="total-label">
            已选 <strong class="count">{{ cartStore.selectedItems.length }}</strong> 件，合计：
          </span>
          <span class="total-price">{{ formatPrice(cartStore.totalPrice) }}</span>
          <el-button type="danger" size="large" round class="checkout-btn"
            :disabled="cartStore.selectedItems.length === 0" @click="checkout">
            去结算
          </el-button>
        </div>
      </div>
    </div>

    <div v-else class="empty-cart card">
      <el-empty description="购物车是空的">
        <el-button type="primary" round @click="router.push('/products')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<style scoped>
.cart-content { display: flex; flex-direction: column; gap: 16px; }

.cart-table { padding: 20px; }

.cart-header {
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-border);
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--color-border-light);
}

.item-select { flex-shrink: 0; }

.item-product {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  min-width: 0;
}

.item-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  flex-shrink: 0;
  border: 1px solid var(--color-border);
}

.img-fallback {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F5;
  color: #CCC;
  border-radius: 8px;
}

.item-name {
  font-size: var(--font-size-base);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price, .item-subtotal {
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--color-text-primary);
  width: 100px;
  text-align: center;
}

.item-qty { width: 110px; display: flex; justify-content: center; }

.item-subtotal { color: var(--color-price); }

.item-action { width: 60px; text-align: center; }

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  position: sticky;
  bottom: 0;
}

.total-label { font-size: var(--font-size-base); color: var(--color-text-secondary); }
.total-label .count { color: var(--color-primary); }

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-price);
  margin: 0 20px;
}

.checkout-btn {
  min-width: 140px;
  height: 44px;
  font-size: var(--font-size-md);
}

.empty-cart {
  padding: 60px;
  text-align: center;
}

@media (max-width: 768px) {
  .cart-item { flex-wrap: wrap; gap: 8px; }
  .item-price, .item-subtotal { width: auto; }
  .cart-footer { flex-wrap: wrap; gap: 12px; justify-content: flex-end; }
}
</style>
