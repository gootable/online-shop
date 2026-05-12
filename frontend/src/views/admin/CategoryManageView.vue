<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from '../../api/admin/category'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Category } from '../../types'

const categories = ref<Category[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const form = ref({ id: null as number | null, name: '', parentId: 0 as number | undefined })

onMounted(fetchCategories)

async function fetchCategories() {
  const res = await getCategoryTree()
  categories.value = res.data
}

function openCreate(parentId?: number) {
  isEdit.value = false
  form.value = { id: null, name: '', parentId: parentId || 0 }
  dialogVisible.value = true
}

function openEdit(cat: Category) {
  isEdit.value = true
  form.value = { id: cat.id, name: cat.name, parentId: cat.parentId }
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (isEdit.value && form.value.id) {
      await updateCategory(form.value.id, { name: form.value.name })
      ElMessage.success('更新成功')
    } else {
      await createCategory({ name: form.value.name, parentId: form.value.parentId || undefined })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchCategories()
  } finally { submitting.value = false }
}

async function handleDelete(cat: Category) {
  try {
    await ElMessageBox.confirm(`确定要删除"${cat.name}"吗？`, '提示', { type: 'warning' })
    await deleteCategory(cat.id)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch { /* cancelled */ }
}

function getFlatList(nodes: Category[], prefix = ''): any[] {
  let result: any[] = []
  for (const node of nodes) {
    result.push({ label: prefix + node.name, value: node.id, id: node.id })
    if (node.children) result = result.concat(getFlatList(node.children, prefix + '  '))
  }
  return result
}
</script>

<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="openCreate(0)">添加分类</el-button>
    </div>

    <div class="card">
      <el-table :data="categories" style="width:100%" row-key="id" default-expand-all>
        <el-table-column prop="name" label="分类名称" min-width="300" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="primary" @click="openCreate(row.id)">添加子分类</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="isEdit ? '编辑分类' : '添加分类'" v-model="dialogVisible" width="420px">
      <el-form :model="form" label-width="70px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="父分类">
          <el-select v-model="form.parentId" placeholder="顶级分类" style="width:100%">
            <el-option label="无（顶级分类）" :value="0" />
            <el-option v-for="cat in getFlatList(categories)" :key="cat.id" :label="cat.label" :value="cat.id" />
          </el-select>
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
.toolbar { margin-bottom: 16px; }
.card { background: var(--color-white); border-radius: var(--radius-md); box-shadow: var(--shadow-sm); padding: 16px; }
</style>
