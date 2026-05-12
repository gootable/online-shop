import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCart, addToCart as addCartApi, updateCartItem, removeCartItem, clearCart as clearCartApi, selectAll as selectAllApi } from '../api/cart'
import type { CartItem } from '../types'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const loading = ref(false)

  const totalCount = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const selectedItems = computed(() => items.value.filter(item => item.selected))
  const totalPrice = computed(() => selectedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0))

  async function fetchCart() {
    loading.value = true
    try {
      const res = await getCart()
      items.value = res.data
    } finally {
      loading.value = false
    }
  }

  async function addToCart(productId: number, quantity: number = 1) {
    await addCartApi(productId, quantity)
    await fetchCart()
  }

  async function updateItem(id: number, data: { quantity?: number; selected?: boolean }) {
    await updateCartItem(id, data)
    await fetchCart()
  }

  async function removeItem(id: number) {
    await removeCartItem(id)
    await fetchCart()
  }

  async function clearSelected() {
    await clearCartApi()
    await fetchCart()
  }

  async function toggleSelectAll(selected: boolean) {
    await selectAllApi(selected)
    await fetchCart()
  }

  return { items, loading, totalCount, selectedItems, totalPrice, fetchCart, addToCart, updateItem, removeItem, clearSelected, toggleSelectAll }
})
