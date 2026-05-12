import request from './request'
import type { Product, PageResult, Result } from '../types'

export function getProducts(params: {
  page?: number
  size?: number
  keyword?: string
  categoryId?: number
  sort?: string
}): Promise<Result<PageResult<Product>>> {
  return request.get('/products', { params })
}

export function getProductDetail(id: number): Promise<Result<Product>> {
  return request.get(`/products/${id}`)
}

export function getHotProducts(): Promise<Result<Product[]>> {
  return request.get('/products/hot')
}

export function searchProducts(keyword: string, page?: number, size?: number): Promise<Result<PageResult<Product>>> {
  return request.get('/products/search', { params: { keyword, page, size } })
}
