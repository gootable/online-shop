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
  { title: '提交订单', desc: '' },
  { title: '付款成功', desc: '' },
  { title: '商品发货', desc: '' },
  { title: '确认送达', desc: '' },
  { title: '交易完成', desc: '' }
]

function getActive(status: number) {
  if (status <= 0) return 0
  return status
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(Number(route.params.id))
    order.value = res.data
    if (order.value.createdAt) steps[0].desc = formatDate(order.value.createdAt)
    if (order.value.payTime) steps[1].desc = formatDate(order.value.payTime)
    if (order.value.shipTime) steps[2].desc = formatDate(order.value.shipTime)
    if (order.value.deliverTime) steps[3].desc = formatDate(order.value.deliverTime)
  } finally { loading.value = false }
})

async function handleCancel() {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '提示', { type: 'warning' })
    await cancelOrder(order.value!.id)
    ElMessage.success('订单已取消')
    order.value = (await getOrderDetail(order.value!.id)).data
  } catch { /* cancelled */ }
}

async function handlePay() {
  await payOrder(order.value!.id)
  ElMessage.success('支付成功')
  order.value = (await getOrderDetail(order.value!.id)).data
}

</script>

<template>
  <div v-loading="loading" class="page-container">
    <div v-if="order" class="detail">
      <div class="card status-card">
        <div class="status-header">
          <h2>订单状态：
            <el-tag :type="order.status === -1 ? 'danger' : 'primary'" size="large">
              {{ OrderStatusMap[order.status] }}
            </el-tag>
          </h2>
        </div>
        <el-steps v-if="order.status !== -1" :active="getActive(order.status)" finish-status="success" align-center>
          <el-step v-for="(s, i) in steps" :key="i" :title="s.title" :description="s.desc" />
        </el-steps>
        <el-alert v-else title="此订单已取消" type="error" show-icon :closable="false" />
      </div>

      <div class="card info-card">
        <h3>收货信息</h3>
        <div class="info-grid">
          <p><span>收货人：</span>{{ order.receiverName }}</p>
          <p><span>电话：</span>{{ order.receiverPhone }}</p>
          <p><span>地址：</span>{{ order.receiverAddress }}</p>
          <p><span>订单号：</span>{{ order.orderNo }}</p>
          <p><span>创建时间：</span>{{ formatDate(order.createdAt) }}</p>
        </div>
      </div>

      <div class="card">
        <h3>商品清单</h3>
        <el-table :data="order.items" style="width:100%">
          <el-table-column label="商品" min-width="280">
            <template #default="{ row }">
              <div class="product-cell">
                <el-image :src="row.productImage" fit="cover" style="width:64px;height:64px;border-radius:6px">
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

      <div class="card bottom-bar">
        <span class="total">实付金额：<strong>{{ formatPrice(order.totalAmount) }}</strong></span>
        <div class="actions">
          <el-button v-if="order.status === 0" type="danger" round @click="handlePay">立即支付</el-button>
          <el-button v-if="order.status === 0" round @click="handleCancel">取消订单</el-button>
          <el-button round @click="router.push('/orders')">返回列表</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail { display: flex; flex-direction: column; gap: 16px; }

.card { background: var(--color-white); border-radius: var(--radius-md); padding: 24px; box-shadow: var(--shadow-sm); }
.card h3 { font-size: var(--font-size-md); font-weight: 600; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid var(--color-border-light); }

.status-card { padding-bottom: 28px; }

.status-header { margin-bottom: 24px; }
.status-header h2 { font-size: var(--font-size-lg); display: flex; align-items: center; gap: 12px; }

.info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.info-grid p { font-size: var(--font-size-base); color: var(--color-text-secondary); }
.info-grid p span { color: var(--color-text-regular); font-weight: 500; }

.product-cell { display: flex; align-items: center; gap: 12px; }
.img-fb { width: 64px; height: 64px; display: flex; align-items: center; justify-content: center; background: #F5F5F5; color: #CCC; border-radius: 6px; }

.bottom-bar { display: flex; align-items: center; justify-content: space-between; }
.total { font-size: var(--font-size-base); }
.total strong { font-size: 24px; color: var(--color-price); margin-left: 8px; }
.actions { display: flex; gap: 8px; }
</style>
