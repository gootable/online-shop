<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrders, cancelOrder, payOrder, confirmOrder } from '../../api/order'
import { formatPrice, formatDate } from '../../utils/format'
import { OrderStatusMap } from '../../types'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Order } from '../../types'

const router = useRouter()
const orders = ref<Order[]>([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const statusFilter = ref<number | undefined>(undefined)
const size = 10

const tabs = [
  { label: '全部', value: undefined },
  { label: '待支付', value: 0 },
  { label: '已支付', value: 1 },
  { label: '已发货', value: 2 },
  { label: '已送达', value: 3 },
  { label: '已完成', value: 4 }
]

function getStatusTag(status: number) {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'primary'
    case 2: return 'success'
    case -1: return 'danger'
    default: return 'info'
  }
}

onMounted(fetchOrders)

function fetchOrders() {
  loading.value = true
  getOrders({ page: page.value, size, status: statusFilter.value })
    .then(res => { orders.value = res.data.records; total.value = res.data.total })
    .finally(() => loading.value = false)
}

function changeTab(val: number | undefined) { statusFilter.value = val; page.value = 1; fetchOrders() }

async function handleCancel(order: Order) {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '提示', { type: 'warning' })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch { /* cancelled */ }
}

async function handlePay(order: Order) {
  await payOrder(order.id)
  ElMessage.success('支付成功')
  fetchOrders()
}

async function handleConfirm(order: Order) {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '确认收货', { type: 'success', confirmButtonText: '确认收货' })
    await confirmOrder(order.id)
    ElMessage.success('已确认收货')
    fetchOrders()
  } catch { /* cancelled */ }
}
</script>

<template>
  <div class="page-container">
    <h1 class="page-title">我的订单</h1>

    <el-radio-group :model-value="statusFilter" size="small" @change="changeTab" class="tabs">
      <el-radio-button v-for="t in tabs" :key="t.label" :value="t.value">{{ t.label }}</el-radio-button>
    </el-radio-group>

    <div v-loading="loading" class="order-list">
      <div v-if="orders.length === 0 && !loading" class="empty card">
        <el-empty description="暂无订单" />
      </div>

      <div v-for="order in orders" :key="order.id" class="order-item card">
        <div class="order-top">
          <span class="order-no">{{ order.orderNo }}</span>
          <el-tag :type="getStatusTag(order.status)" size="small">{{ OrderStatusMap[order.status] }}</el-tag>
        </div>
        <div v-for="item in order.items" :key="item.id" class="order-product"
             @click="router.push(`/orders/${order.id}`)">
          <el-image :src="item.productImage" fit="cover" class="op-img">
            <template #error><div class="img-fb"><el-icon><PictureFilled /></el-icon></div></template>
          </el-image>
          <span class="op-name">{{ item.productName }}</span>
          <span class="op-price">{{ formatPrice(item.price) }}</span>
          <span class="op-qty">x{{ item.quantity }}</span>
        </div>
        <div class="order-bottom">
          <span class="order-time">{{ formatDate(order.createdAt) }}</span>
          <span class="order-total">共 {{ order.items.length }} 件，合计：<strong>{{ formatPrice(order.totalAmount) }}</strong></span>
          <div class="order-actions">
            <el-button v-if="order.status === 0" type="danger" size="small" round @click="handlePay(order)">立即支付</el-button>
            <el-button v-if="order.status === 0" size="small" round @click="handleCancel(order)">取消</el-button>
            <el-button v-if="order.status === 2" type="success" size="small" round @click="handleConfirm(order)">确认收货</el-button>
            <el-button size="small" round @click="router.push(`/orders/${order.id}`)">详情</el-button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="total > size" class="pagination-wrap">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="size"
        :current-page="page" @current-change="(p: number) => { page = p; fetchOrders() }" />
    </div>
  </div>
</template>

<style scoped>
.tabs { margin-bottom: 20px; }

.order-list { display: flex; flex-direction: column; gap: 16px; }

.empty { padding: 60px; text-align: center; }

.order-item { padding: 0; }

.order-top {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 20px; background: #FAFAFA; border-bottom: 1px solid var(--color-border-light);
}
.order-no { font-size: var(--font-size-sm); color: var(--color-text-secondary); }

.order-product {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 20px; cursor: pointer; transition: background var(--transition-fast);
}
.order-product:hover { background: var(--color-primary-light); }

.op-img { width: 64px; height: 64px; border-radius: 6px; flex-shrink: 0; border: 1px solid var(--color-border); }
.img-fb { width: 64px; height: 64px; display: flex; align-items: center; justify-content: center; background: #F5F5F5; color: #CCC; border-radius: 6px; }

.op-name { flex: 1; font-size: var(--font-size-base); font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.op-price { font-size: var(--font-size-base); color: var(--color-text-secondary); width: 100px; text-align: center; }
.op-qty { font-size: var(--font-size-sm); color: var(--color-text-placeholder); width: 60px; text-align: center; }

.order-bottom {
  display: flex; align-items: center; justify-content: flex-end; gap: 16px;
  padding: 12px 20px; border-top: 1px solid var(--color-border-light);
}
.order-time { font-size: var(--font-size-xs); color: var(--color-text-placeholder); margin-right: auto; }
.order-total { font-size: var(--font-size-base); }
.order-total strong { color: var(--color-price); font-size: var(--font-size-md); }

.pagination-wrap { display: flex; justify-content: center; margin-top: 24px; }
</style>
