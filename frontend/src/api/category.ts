import request from './request'
import type { Category, Result } from '../types'

export function getCategories(): Promise<Result<Category[]>> {
  return request.get('/categories')
}

export function getCategoryTree(): Promise<Result<Category[]>> {
  return request.get('/categories/tree')
}

export function getCategoryChildren(id: number): Promise<Result<Category[]>> {
  return request.get(`/categories/${id}/children`)
}
