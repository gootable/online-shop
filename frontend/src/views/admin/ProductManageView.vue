<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getProducts, createProduct, updateProduct, updateProductStatus, uploadImage } from '../../api/admin/product'
import { getCategoryTree } from '../../api/admin/category'
import { formatPrice } from '../../utils/format'
import { ElMessage } from 'element-plus'
import type { Product, Category } from '../../types'

const products = ref<Product[]>([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const size = 10
const keyword = ref('')
const categories = ref<Category[]>([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const form = ref({
  id: null as number | null,
  name: '',
  description: '',
  categoryId: null as number | null,
  price: 0,
  stock: 0,
  mainImage: '',
  status: 1
})

onMounted(async () => {
  const res = await getCategoryTree()
  categories.value = res.data
  fetchProducts()
})

function fetchProducts() {
  loading.value = true
  getProducts({ page: page.value, size, keyword: keyword.value })
    .then(res => {
      products.value = res.data.records
      total.value = res.data.total
    })
    .finally(() => loading.value = false)
}

function openCreate() {
  isEdit.value = false
  form.value = { id: null, name: '', description: '', categoryId: null, price: 0, stock: 0, mainImage: '', status: 1 }
  dialogVisible.value = true
}

function openEdit(p: Product) {
  isEdit.value = true
  form.value = {
    id: p.id,
    name: p.name,
    description: p.description || '',
    categoryId: p.categoryId,
    price: p.price,
    stock: p.stock,
    mainImage: p.mainImage || '',
    status: p.status
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    const data: any = { ...form.value }
    if (isEdit.value && form.value.id) {
      await updateProduct(form.value.id, data)
      ElMessage.success('更新成功')
    } else {
      await createProduct(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchProducts()
  } finally {
    submitting.value = false
  }
}

async function handleUpload(file: any) {
  const res = await uploadImage(file.raw)
  form.value.mainImage = res.data
}

async function toggleStatus(p: Product) {
  const newStatus = p.status === 1 ? 0 : 1
  await updateProductStatus(p.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
  fetchProducts()
}
</script>

<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索商品..." clearable style="width:240px" @keyup.enter="fetchProducts" @clear="fetchProducts" />
      <el-button type="primary" @click="openCreate">添加商品</el-button>
    </div>

    <el-table v-loading="loading" :data="products" style="width:100%">
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <el-image :src="row.mainImage" fit="cover" style="width:48px;height:48px;border-radius:4px">
            <template #error><div style="width:48px;height:48px;background:#f5f7fa" /></template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="180" />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column label="价格" width="100">
        <template #default="{ row }">{{ formatPrice(row.price) }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'"
            @click="toggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:16px;text-align:center">
      <el-pagination layout="prev, pager, next" :total="total" :page-size="size"
        :current-page="page" @current-change="(p: number) => { page = p; fetchProducts() }" />
    </div>

    <el-dialog :title="isEdit ? '编辑商品' : '添加商品'" v-model="dialogVisible" width="560px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width:200px" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" style="width:200px" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="图片">
          <div>
            <el-upload action="" :http-request="handleUpload" :show-file-list="false"
              accept="image/*" list-type="picture-card">
              <img v-if="form.mainImage" :src="form.mainImage" style="width:100px;height:100px;object-fit:cover" />
              <el-icon v-else :size="28"><Plus /></el-icon>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
</style>
