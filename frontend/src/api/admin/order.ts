import request from '../request'
import type { Order, PageResult, Result } from '../../types'

export function getOrders(params: { page?: number; size?: number; status?: number; orderNo?: string }): Promise<Result<PageResult<Order>>> {
  return request.get('/admin/orders', { params })
}

export function getOrderDetail(id: number): Promise<Result<Order>> {
  return request.get(`/admin/orders/${id}`)
}

export function shipOrder(id: number): Promise<Result<null>> {
  return request.put(`/admin/orders/${id}/ship`)
}

export function deliverOrder(id: number): Promise<Result<null>> {
  return request.put(`/admin/orders/${id}/deliver`)
}
