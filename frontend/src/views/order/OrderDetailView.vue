<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, cancelOrder, payOrder } from '../../api/order'
import { formatPrice, formatDate } from '../../utils/format'
import { OrderStatusMap } from '../../types'
import type { Order } from '../../types'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const order = ref<Order | null>(null)
const loading = ref(false)

const steps = [
  { status: 0, title: '待支付', desc: '' },
  { status: 1, title: '已支付', desc: '' },
  { status: 2, title: '已发货', desc: '' },
  { status: 3, title: '已送达', desc: '' },
  { status: 4, title: '已完成', desc: '' }
]

onMounted(async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(Number(route.params.id))
    order.value = res.data
    // Fill step descriptions
    if (order.value.payTime) steps[1].desc = formatDate(order.value.payTime)
    if (order.value.shipTime) steps[2].desc = formatDate(order.value.shipTime)
    if (order.value.deliverTime) steps[3].desc = formatDate(order.value.deliverTime)
  } finally {
    loading.value = false
  }
})

function getActiveStep(status: number): number {
  if (status === -1) return -1
  if (status === 0) return 0
  return status
}

async function handleCancel() {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '提示', { type: 'warning' })
    await cancelOrder(order.value!.id)
    ElMessage.success('订单已取消')
    const res = await getOrderDetail(order.value!.id)
    order.value = res.data
  } catch { /* cancelled */ }
}

async function handlePay() {
  await payOrder(order.value!.id)
  ElMessage.success('支付成功')
  const res = await getOrderDetail(order.value!.id)
  order.value = res.data
}
</script>

<template>
  <div v-loading="loading" class="page-container">
    <div v-if="order" class="order-detail">
      <div class="order-status-bar">
        <h2>订单状态：{{ OrderStatusMap[order.status] }}</h2>
        <el-steps v-if="order.status !== -1" :active="getActiveStep(order.status)" finish-status="success" align-center>
          <el-step v-for="step, i in steps" :key="i" :title="step.title" :description="step.desc" />
        </el-steps>
        <el-alert v-else title="此订单已取消" type="error" show-icon :closable="false" />
      </div>

      <div class="order-info">
        <div class="section">
          <h3>收货信息</h3>
          <p>{{ order.receiverName }} | {{ order.receiverPhone }}</p>
          <p>{{ order.receiverAddress }}</p>
        </div>

        <div class="section">
          <h3>订单信息</h3>
          <p>订单号：{{ order.orderNo }}</p>
          <p>创建时间：{{ formatDate(order.createdAt) }}</p>
          <p v-if="order.payTime">支付时间：{{ formatDate(order.payTime) }}</p>
        </div>

        <div class="section">
          <h3>商品清单</h3>
          <el-table :data="order.items" style="width:100%">
            <el-table-column label="商品" min-width="250">
              <template #default="{ row }">
                <div class="product-cell">
                  <el-image :src="row.productImage" fit="cover" style="width:60px;height:60px">
                    <template #error><div class="img-fb"><el-icon><PictureFilled /></el-icon></div></template>
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
              <template #default="{ row }">{{ formatPrice(row.totalPrice) }}</template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <div class="order-actions-bar">
        <span class="total-amount">实付：<strong>{{ formatPrice(order.totalAmount) }}</strong></span>
        <div class="actions">
          <el-button v-if="order.status === 0" type="danger" @click="handlePay">立即支付</el-button>
          <el-button v-if="order.status === 0" @click="handleCancel">取消订单</el-button>
          <el-button @click="router.push('/orders')">返回列表</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-detail {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.order-status-bar {
  padding-bottom: 24px;
  margin-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
}

.order-status-bar h2 {
  margin-bottom: 20px;
  font-size: 18px;
}

.section {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.section h3 {
  font-size: 15px;
  margin-bottom: 12px;
}

.section p {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.img-fb {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}

.order-actions-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.total-amount strong {
  font-size: 22px;
  color: #e64242;
}

.actions {
  display: flex;
  gap: 8px;
}
</style>
