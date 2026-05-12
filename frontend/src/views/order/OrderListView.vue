<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrders, cancelOrder, payOrder } from '../../api/order'
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

const statusOptions = [
  { label: '全部', value: undefined },
  { label: '待支付', value: 0 },
  { label: '已支付', value: 1 },
  { label: '已发货', value: 2 },
  { label: '已送达', value: 3 },
  { label: '已完成', value: 4 }
]

onMounted(fetchOrders)

function fetchOrders() {
  loading.value = true
  getOrders({ page: page.value, size, status: statusFilter.value })
    .then(res => {
      orders.value = res.data.records
      total.value = res.data.total
    })
    .finally(() => loading.value = false)
}

function changeStatus(val: number | undefined) {
  statusFilter.value = val
  page.value = 1
  fetchOrders()
}

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

function getStatusType(status: number): string {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'primary'
    case 2: return 'success'
    case 3: case 4: return 'info'
    case -1: return 'danger'
    default: return 'info'
  }
}
</script>

<template>
  <div class="page-container">
    <h1 class="page-title">我的订单</h1>

    <div class="status-tabs">
      <el-radio-group :model-value="statusFilter" @change="changeStatus">
        <el-radio-button v-for="opt in statusOptions" :key="opt.label" :value="opt.value">
          {{ opt.label }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <div v-loading="loading" class="order-list">
      <div v-if="orders.length === 0 && !loading" class="empty-orders">
        <el-empty description="暂无订单" />
      </div>

      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <el-tag :type="getStatusType(order.status)" size="small">
            {{ OrderStatusMap[order.status] }}
          </el-tag>
        </div>

        <div v-for="item in order.items" :key="item.id" class="order-item"
             @click="router.push(`/orders/${order.id}`)">
          <el-image :src="item.productImage" fit="cover" style="width:64px;height:64px;border-radius:4px">
            <template #error><div class="img-fallback"><el-icon :size="28"><PictureFilled /></el-icon></div></template>
          </el-image>
          <span class="item-name">{{ item.productName }}</span>
          <span class="item-price">{{ formatPrice(item.price) }}</span>
          <span class="item-qty">x{{ item.quantity }}</span>
        </div>

        <div class="order-footer">
          <span class="order-total">共 {{ order.items.length }} 件，合计：
            <strong>{{ formatPrice(order.totalAmount) }}</strong>
          </span>
          <span class="order-time">{{ formatDate(order.createdAt) }}</span>
          <div class="order-actions">
            <el-button v-if="order.status === 0" type="danger" size="small" @click="handlePay(order)">
              立即支付
            </el-button>
            <el-button v-if="order.status === 0" size="small" @click="handleCancel(order)">
              取消订单
            </el-button>
            <el-button size="small" @click="router.push(`/orders/${order.id}`)">
              查看详情
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="total > size" class="pagination-wrap">
      <el-pagination layout="prev, pager, next" :total="total" :page-size="size"
        :current-page="page" @current-change="(p: number) => { page = p; fetchOrders() }" />
    </div>
  </div>
</template>

<style scoped>
.status-tabs {
  margin-bottom: 16px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.order-no {
  font-size: 13px;
  color: #909399;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}

.order-item:hover {
  background: #f5f7fa;
}

.item-name {
  flex: 1;
  font-size: 14px;
}

.item-price {
  font-size: 14px;
  color: #606266;
}

.item-qty {
  font-size: 13px;
  color: #909399;
}

.img-fallback {
  width: 64px;
  height: 64px;
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
  padding: 12px 16px;
}

.order-total {
  font-size: 14px;
}

.order-total strong {
  color: #e64242;
  font-size: 16px;
}

.order-time {
  font-size: 12px;
  color: #909399;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.empty-orders {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
}
</style>
