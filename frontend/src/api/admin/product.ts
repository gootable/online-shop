import request from '../request'
import type { Product, PageResult, Result } from '../../types'

export function getProducts(params: any): Promise<Result<PageResult<Product>>> {
  return request.get('/admin/products', { params })
}

export function createProduct(data: Partial<Product>): Promise<Result<null>> {
  return request.post('/admin/products', data)
}

export function updateProduct(id: number, data: Partial<Product>): Promise<Result<null>> {
  return request.put(`/admin/products/${id}`, data)
}

export function updateProductStatus(id: number, status: number): Promise<Result<null>> {
  return request.put(`/admin/products/${id}/status`, { status })
}

export function uploadImage(file: File): Promise<Result<string>> {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/products/upload', formData)
}
