<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getStats } from '../../api/admin/stats'
import { formatPrice } from '../../utils/format'
import type { AdminStats } from '../../types'

const stats = ref<AdminStats>({
  totalUsers: 0, totalOrders: 0, totalRevenue: 0, totalProducts: 0,
  pendingOrders: 0, paidOrders: 0, shippedOrders: 0, completedOrders: 0
})

onMounted(async () => {
  try {
    const res = await getStats()
    stats.value = res.data
  } catch { /* */ }
})
</script>

<template>
  <div>
    <h2 style="margin-bottom:20px;font-size:20px;font-weight:600">仪表盘</h2>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background:#E8F4FF"><el-icon :size="26" color="#1677FF"><User /></el-icon></div>
        <div>
          <div class="stat-label">用户总数</div>
          <div class="stat-value">{{ stats.totalUsers }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#E8F8EE"><el-icon :size="26" color="#07C160"><Goods /></el-icon></div>
        <div>
          <div class="stat-label">在售商品</div>
          <div class="stat-value">{{ stats.totalProducts }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#FFF5E8"><el-icon :size="26" color="#FF6B00"><Document /></el-icon></div>
        <div>
          <div class="stat-label">订单总数</div>
          <div class="stat-value">{{ stats.totalOrders }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#FFF0F2"><el-icon :size="26" color="#E60012"><Money /></el-icon></div>
        <div>
          <div class="stat-label">总营业额</div>
          <div class="stat-value">{{ formatPrice(stats.totalRevenue) }}</div>
        </div>
      </div>
    </div>

    <div class="order-stats card">
      <h3>订单统计</h3>
      <div class="order-stats-grid">
        <div class="os-item"><span class="dot warning"></span>待支付 <strong>{{ stats.pendingOrders }}</strong></div>
        <div class="os-item"><span class="dot primary"></span>已支付 <strong>{{ stats.paidOrders }}</strong></div>
        <div class="os-item"><span class="dot success"></span>已发货 <strong>{{ stats.shippedOrders }}</strong></div>
        <div class="os-item"><span class="dot info"></span>已完成 <strong>{{ stats.completedOrders }}</strong></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-sm);
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-label { font-size: 14px; color: var(--color-text-secondary); }
.stat-value { font-size: 28px; font-weight: 700; color: var(--color-text-primary); margin-top: 2px; }

.card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.card h3 { font-size: 16px; font-weight: 600; margin-bottom: 16px; }

.order-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.os-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
}

.os-item strong { font-size: 22px; margin-left: 4px; }

.dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
.dot.warning { background: #FF6B00; }
.dot.primary { background: #1677FF; }
.dot.success { background: #07C160; }
.dot.info { background: #909399; }

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .order-stats-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
