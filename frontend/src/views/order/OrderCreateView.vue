<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../../stores/cart'
import { createOrder } from '../../api/order'
import { formatPrice } from '../../utils/format'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = ref({ receiverName: '', receiverPhone: '', receiverAddress: '' })

const rules: FormRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  receiverAddress: [{ required: true, message: '请输入收货地址', trigger: 'blur' }]
}

const selectedItems = computed(() => cartStore.items.filter(item => item.selected))

onMounted(() => cartStore.fetchCart())

async function submitOrder() {
  const valid = await formRef.value?.validate()
  if (!valid) return
  if (selectedItems.value.length === 0) { ElMessage.warning('请选择商品'); return }

  submitting.value = true
  try {
    await createOrder({
      receiverName: form.value.receiverName,
      receiverPhone: form.value.receiverPhone,
      receiverAddress: form.value.receiverAddress,
      cartItemIds: selectedItems.value.map(item => item.id)
    })
    ElMessage.success('下单成功')
    await cartStore.fetchCart()
    router.push('/orders')
  } finally { submitting.value = false }
}
</script>

<template>
  <div class="page-container">
    <h1 class="page-title">确认订单</h1>

    <div v-if="selectedItems.length === 0" class="empty card" style="padding:60px;text-align:center">
      <el-empty description="没有选中商品">
        <el-button type="primary" round @click="router.push('/cart')">返回购物车</el-button>
      </el-empty>
    </div>

    <div v-else class="create-content">
      <div class="card">
        <h3>收货信息</h3>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" style="max-width:520px">
          <el-form-item label="收货人" prop="receiverName">
            <el-input v-model="form.receiverName" placeholder="请输入收货人" />
          </el-form-item>
          <el-form-item label="电话" prop="receiverPhone">
            <el-input v-model="form.receiverPhone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="地址" prop="receiverAddress">
            <el-input v-model="form.receiverAddress" placeholder="请输入详细地址" />
          </el-form-item>
        </el-form>
      </div>

      <div class="card">
        <h3>商品清单</h3>
        <div v-for="item in selectedItems" :key="item.id" class="product-item">
          <el-image :src="item.productImage" fit="cover" class="pi-img">
            <template #error><div class="img-fb"><el-icon><PictureFilled /></el-icon></div></template>
          </el-image>
          <span class="pi-name">{{ item.productName }}</span>
          <span class="pi-price">{{ formatPrice(item.price) }}</span>
          <span class="pi-qty">x{{ item.quantity }}</span>
          <span class="pi-subtotal">{{ formatPrice(item.price * item.quantity) }}</span>
        </div>
      </div>

      <div class="card submit-bar">
        <span class="total-text">实付：<strong>{{ formatPrice(cartStore.totalPrice) }}</strong></span>
        <el-button type="danger" size="large" round :loading="submitting" @click="submitOrder">提交订单</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.create-content { display: flex; flex-direction: column; gap: 16px; }

.card { background: var(--color-white); border-radius: var(--radius-md); padding: 24px; box-shadow: var(--shadow-sm); }
.card h3 { font-size: var(--font-size-md); font-weight: 600; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid var(--color-border-light); }

.product-item {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 0; border-bottom: 1px solid var(--color-border-light);
}
.pi-img { width: 64px; height: 64px; border-radius: 6px; flex-shrink: 0; border: 1px solid var(--color-border); }
.img-fb { width: 64px; height: 64px; display: flex; align-items: center; justify-content: center; background: #F5F5F5; color: #CCC; border-radius: 6px; }
.pi-name { flex: 1; font-size: var(--font-size-base); font-weight: 500; }
.pi-price { color: var(--color-text-secondary); font-size: var(--font-size-base); width: 100px; text-align: center; }
.pi-qty { color: var(--color-text-placeholder); width: 60px; text-align: center; }
.pi-subtotal { font-size: var(--font-size-base); font-weight: 600; color: var(--color-price); width: 100px; text-align: right; }

.submit-bar { display: flex; align-items: center; justify-content: flex-end; gap: 20px; }
.total-text { font-size: var(--font-size-base); }
.total-text strong { font-size: 24px; color: var(--color-price); }
</style>
