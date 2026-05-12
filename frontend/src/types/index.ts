export interface User {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  role: number
  status: number
  createdAt: string
}

export interface Category {
  id: number
  name: string
  parentId: number
  sortOrder: number
  children?: Category[]
}

export interface Product {
  id: number
  name: string
  description: string
  categoryId: number
  categoryName?: string
  price: number
  stock: number
  mainImage: string
  images: string[]
  status: number
  sales: number
  createdAt: string
}

export interface CartItem {
  id: number
  userId: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  selected: boolean
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  totalAmount: number
  status: number
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  payTime: string | null
  shipTime: string | null
  deliverTime: string | null
  items: OrderItem[]
  createdAt: string
}

export interface OrderItem {
  id: number
  orderId: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  totalPrice: number
}

export interface Result<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  total: number
  page: number
  size: number
  records: T[]
}

export interface AdminStats {
  totalUsers: number
  totalOrders: number
  totalProducts: number
  totalRevenue: number
  pendingOrders: number
  paidOrders: number
  shippedOrders: number
  completedOrders: number
}

export type OrderStatus = 0 | 1 | 2 | 3 | 4 | -1

export const OrderStatusMap: Record<number, string> = {
  0: '待支付',
  1: '已支付',
  2: '已发货',
  3: '已送达',
  4: '已完成',
  [-1]: '已取消'
}
