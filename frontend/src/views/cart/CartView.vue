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
    <h1 class="page-title">购物车</h1>
    <div v-if="cartStore.items.length > 0" class="cart-content">
      <el-table :data="cartStore.items" style="width:100%" @selection-change="() => {}">
        <el-table-column width="50">
          <template #default="{ row }">
            <el-checkbox :model-value="row.selected"
              @change="(val: boolean) => cartStore.updateItem(row.id, { selected: val })" />
          </template>
        </el-table-column>
        <el-table-column label="商品" min-width="300">
          <template #default="{ row }">
            <div class="product-cell" @click="router.push(`/products/${row.productId}`)">
              <el-image :src="row.productImage" fit="cover" style="width:80px;height:80px;border-radius:4px">
                <template #error><div class="img-placeholder"><el-icon :size="32"><PictureFilled /></el-icon></div></template>
              </el-image>
              <span class="product-name">{{ row.productName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">{{ formatPrice(row.price) }}</template>
        </el-table-column>
        <el-table-column label="数量" width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" size="small"
              @change="(val: number) => cartStore.updateItem(row.id, { quantity: val! })" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">{{ formatPrice(row.price * row.quantity) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button type="danger" link @click="cartStore.removeItem(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox v-model="allSelected">全选</el-checkbox>
        </div>
        <div class="footer-right">
          <span class="total-text">
            已选 <strong>{{ cartStore.selectedItems.length }}</strong> 件，
            合计：<strong class="total-price">{{ formatPrice(cartStore.totalPrice) }}</strong>
          </span>
          <el-button type="danger" size="large" :disabled="cartStore.selectedItems.length === 0"
            @click="checkout">
            去结算
          </el-button>
        </div>
      </div>
    </div>

    <el-empty v-else description="购物车是空的">
      <el-button type="primary" @click="router.push('/products')">去逛逛</el-button>
    </el-empty>
  </div>
</template>

<style scoped>
.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.product-name {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.img-placeholder {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
  border-radius: 4px;
}

.cart-content {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.total-text {
  font-size: 15px;
  color: #606266;
}

.total-price {
  font-size: 20px;
  color: #e64242;
}
</style>
