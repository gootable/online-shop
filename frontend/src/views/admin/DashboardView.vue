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
  const res = await getStats()
  stats.value = res.data
})
</script>

<template>
  <div>
    <h2 style="margin-bottom:20px">仪表盘</h2>
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background:#ecf5ff"><el-icon :size="28" color="#409eff"><User /></el-icon></div>
            <div>
              <div class="stat-label">用户总数</div>
              <div class="stat-value">{{ stats.totalUsers }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background:#f0f9eb"><el-icon :size="28" color="#67c23a"><Goods /></el-icon></div>
            <div>
              <div class="stat-label">商品总数</div>
              <div class="stat-value">{{ stats.totalProducts }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background:#fdf6ec"><el-icon :size="28" color="#e6a23c"><Document /></el-icon></div>
            <div>
              <div class="stat-label">订单总数</div>
              <div class="stat-value">{{ stats.totalOrders }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background:#fef0f0"><el-icon :size="28" color="#f56c6c"><Money /></el-icon></div>
            <div>
              <div class="stat-label">总营业额</div>
              <div class="stat-value">{{ formatPrice(stats.totalRevenue) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="24">
        <el-card>
          <template #header>订单统计</template>
            <el-row :gutter="16">
              <el-col :span="6"><div class="order-stat"><span class="dot warning"></span>待支付：{{ stats.pendingOrders }}</div></el-col>
              <el-col :span="6"><div class="order-stat"><span class="dot primary"></span>已支付：{{ stats.paidOrders }}</div></el-col>
              <el-col :span="6"><div class="order-stat"><span class="dot success"></span>已发货：{{ stats.shippedOrders }}</div></el-col>
              <el-col :span="6"><div class="order-stat"><span class="dot info"></span>已完成：{{ stats.completedOrders }}</div></el-col>
            </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-top: 4px;
}

.order-stat {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

.dot.warning { background: #e6a23c; }
.dot.primary { background: #409eff; }
.dot.success { background: #67c23a; }
.dot.info { background: #909399; }
</style>
