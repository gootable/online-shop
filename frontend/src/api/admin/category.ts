import request from '../request'
import type { Category, Result } from '../../types'

export function getCategoryTree(): Promise<Result<Category[]>> {
  return request.get('/admin/categories/tree')
}

export function createCategory(data: { name: string; parentId?: number }): Promise<Result<null>> {
  return request.post('/admin/categories', data)
}

export function updateCategory(id: number, data: { name: string }): Promise<Result<null>> {
  return request.put(`/admin/categories/${id}`, data)
}

export function deleteCategory(id: number): Promise<Result<null>> {
  return request.delete(`/admin/categories/${id}`)
}
