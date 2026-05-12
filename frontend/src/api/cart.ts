import request from './request'
import type { CartItem, Result } from '../types'

export function getCart(): Promise<Result<CartItem[]>> {
  return request.get('/cart')
}

export function addToCart(productId: number, quantity: number): Promise<Result<CartItem>> {
  return request.post('/cart', { productId, quantity })
}

export function updateCartItem(id: number, data: { quantity?: number; selected?: boolean }): Promise<Result<null>> {
  return request.put(`/cart/${id}`, data)
}

export function removeCartItem(id: number): Promise<Result<null>> {
  return request.delete(`/cart/${id}`)
}

export function clearCart(): Promise<Result<null>> {
  return request.delete('/cart')
}

export function selectAll(selected: boolean): Promise<Result<null>> {
  return request.put('/cart/select-all', { selected })
}
