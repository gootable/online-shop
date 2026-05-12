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

const form = ref({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: ''
})

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

  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要购买的商品')
    return
  }

  submitting.value = true
  try {
    const cartItemIds = selectedItems.value.map(item => item.id)
    await createOrder({
      receiverName: form.value.receiverName,
      receiverPhone: form.value.receiverPhone,
      receiverAddress: form.value.receiverAddress,
      cartItemIds
    })
    ElMessage.success('下单成功')
    await cartStore.fetchCart()
    router.push('/orders')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page-container">
    <h1 class="page-title">确认订单</h1>

    <div v-if="selectedItems.length === 0" style="background:#fff;padding:40px;border-radius:8px;text-align:center">
      <el-empty description="没有选中商品">
        <el-button type="primary" @click="router.push('/cart')">返回购物车</el-button>
      </el-empty>
    </div>

    <div v-else class="order-create">
      <div class="section">
        <h3>收货信息</h3>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="收货人" prop="receiverName">
            <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="联系电话" prop="receiverPhone">
            <el-input v-model="form.receiverPhone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="收货地址" prop="receiverAddress">
            <el-input v-model="form.receiverAddress" placeholder="请输入详细收货地址" />
          </el-form-item>
        </el-form>
      </div>

      <div class="section">
        <h3>商品清单</h3>
        <el-table :data="selectedItems" style="width:100%">
          <el-table-column label="商品" min-width="300">
            <template #default="{ row }">
              <div class="product-cell">
                <el-image :src="row.productImage" fit="cover" style="width:60px;height:60px;border-radius:4px">
                  <template #error><div class="img-place"><el-icon :size="24"><PictureFilled /></el-icon></div></template>
                </el-image>
                <span>{{ row.productName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="100">
            <template #default="{ row }">{{ formatPrice(row.price) }}</template>
          </el-table-column>
          <el-table-column label="数量" width="80">
            <template #default="{ row }">{{ row.quantity }}</template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="{ row }">{{ formatPrice(row.price * row.quantity) }}</template>
          </el-table-column>
        </el-table>
      </div>

      <div class="order-footer">
        <span class="total-text">实付金额：<strong class="total-price">{{ formatPrice(cartStore.totalPrice) }}</strong></span>
        <el-button type="danger" size="large" :loading="submitting" @click="submitOrder">提交订单</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-create {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
}

.section h3 {
  font-size: 16px;
  margin-bottom: 16px;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.img-place {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
  border-radius: 4px;
}

.order-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
}

.total-text {
  font-size: 15px;
}

.total-price {
  font-size: 22px;
  color: #e64242;
}
</style>
