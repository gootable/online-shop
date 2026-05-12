<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getOrders, shipOrder, deliverOrder } from '../../api/admin/order'
import { formatPrice, formatDate } from '../../utils/format'
import { OrderStatusMap } from '../../types'
import { ElMessage } from 'element-plus'
import type { Order } from '../../types'

const orders = ref<Order[]>([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const size = 10
const statusFilter = ref<number | undefined>(undefined)
const orderNo = ref('')

onMounted(fetchOrders)

function fetchOrders() {
  loading.value = true
  getOrders({ page: page.value, size, status: statusFilter.value, orderNo: orderNo.value })
    .then(res => { orders.value = res.data.records; total.value = res.data.total })
    .finally(() => loading.value = false)
}

async function handleShip(order: Order) { await shipOrder(order.id); ElMessage.success('已发货'); fetchOrders() }
async function handleDeliver(order: Order) { await deliverOrder(order.id); ElMessage.success('已送达'); fetchOrders() }

function getTagType(status: number) {
  const map: Record<number, string> = { 0: 'warning', 1: 'primary', 2: 'success', '-1': 'danger' }
  return map[status] || 'info'
}
</script>

<template>
  <div>
    <div class="toolbar">
      <el-input v-model="orderNo" placeholder="搜索订单号..." clearable style="width:240px" @keyup.enter="fetchOrders" />
      <el-select v-model="statusFilter" placeholder="状态" clearable style="width:140px" @change="fetchOrders">
        <el-option label="待支付" :value="0" />
        <el-option label="已支付" :value="1" />
        <el-option label="已发货" :value="2" />
        <el-option label="已送达" :value="3" />
        <el-option label="已完成" :value="4" />
        <el-option label="已取消" :value="-1" />
      </el-select>
      <el-button type="primary" @click="fetchOrders">查询</el-button>
    </div>

    <div class="card">
      <el-table v-loading="loading" :data="orders" style="width:100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">{{ formatPrice(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.status)" size="small">{{ OrderStatusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="90" />
        <el-table-column prop="receiverPhone" label="电话" width="120" />
        <el-table-column prop="receiverAddress" label="地址" min-width="160" />
        <el-table-column label="时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" size="small" type="primary" @click="handleShip(row)">发货</el-button>
            <el-button v-if="row.status === 2" size="small" type="success" @click="handleDeliver(row)">送达</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div style="margin-top:16px;text-align:center" v-if="total > size">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="size"
        :current-page="page" @current-change="(p: number) => { page = p; fetchOrders() }" />
    </div>
  </div>
</template>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
.card { background: var(--color-white); border-radius: var(--radius-md); box-shadow: var(--shadow-sm); padding: 16px; }
</style>
