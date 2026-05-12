import request from './request'
import type { Order, PageResult, Result } from '../types'

export function getOrders(params: { page?: number; size?: number; status?: number }): Promise<Result<PageResult<Order>>> {
  return request.get('/orders', { params })
}

export function getOrderDetail(id: number): Promise<Result<Order>> {
  return request.get(`/orders/${id}`)
}

export function createOrder(data: {
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  cartItemIds: number[]
}): Promise<Result<Order>> {
  return request.post('/orders', data)
}

export function cancelOrder(id: number): Promise<Result<null>> {
  return request.put(`/orders/${id}/cancel`)
}

export function payOrder(id: number): Promise<Result<null>> {
  return request.put(`/orders/${id}/pay`)
}
