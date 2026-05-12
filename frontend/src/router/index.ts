import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/auth/LoginView.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/auth/RegisterView.vue')
    },
    {
      path: '/',
      component: () => import('../views/layout/MainLayout.vue'),
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('../views/home/HomeView.vue')
        },
        {
          path: 'products',
          name: 'ProductList',
          component: () => import('../views/product/ProductListView.vue')
        },
        {
          path: 'products/:id',
          name: 'ProductDetail',
          component: () => import('../views/product/ProductDetailView.vue')
        },
        {
          path: 'search',
          name: 'Search',
          component: () => import('../views/product/SearchView.vue')
        },
        {
          path: 'cart',
          name: 'Cart',
          component: () => import('../views/cart/CartView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'orders',
          name: 'OrderList',
          component: () => import('../views/order/OrderListView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'orders/create',
          name: 'OrderCreate',
          component: () => import('../views/order/OrderCreateView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'orders/:id',
          name: 'OrderDetail',
          component: () => import('../views/order/OrderDetailView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('../views/user/ProfileView.vue'),
          meta: { requiresAuth: true }
        }
      ]
    },
    {
      path: '/admin',
      component: () => import('../views/layout/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('../views/admin/DashboardView.vue')
        },
        {
          path: 'products',
          name: 'AdminProducts',
          component: () => import('../views/admin/ProductManageView.vue')
        },
        {
          path: 'orders',
          name: 'AdminOrders',
          component: () => import('../views/admin/OrderManageView.vue')
        },
        {
          path: 'users',
          name: 'AdminUsers',
          component: () => import('../views/admin/UserManageView.vue')
        },
        {
          path: 'categories',
          name: 'AdminCategories',
          component: () => import('../views/admin/CategoryManageView.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return next('/login')
  }

  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    return next('/')
  }

  next()
})

export default router
