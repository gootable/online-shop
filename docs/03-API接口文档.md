# 网上商城系统 — API 接口文档

## 基础约定

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **认证方式**: `Authorization: Bearer <token>`（Header）
- **统一响应格式**:

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

- **错误码**: `200` 成功 | `400` 参数错误 | `401` 未认证 | `403` 无权限 | `404` 不存在 | `500` 服务端错误
- **分页响应格式**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "page": 1,
    "size": 10,
    "records": [ ... ]
  }
}
```

---

## 1. 认证模块 `/api/auth`

### POST /api/auth/register — 用户注册

**公开接口**

```
POST /api/auth/register
Content-Type: application/json

{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三"
}
```

**成功响应 (200):**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 3,
    "username": "zhangsan",
    "nickname": "张三",
    "role": 1,
    "status": 1,
    "createdAt": "2026-05-12T17:00:00"
  }
}
```

**错误 (400):** 用户名已存在 / 参数校验失败

---

### POST /api/auth/login — 用户登录

**公开接口**

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "zhangsan",
  "password": "123456"
}
```

**成功响应 (200):**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 3,
      "username": "zhangsan",
      "nickname": "张三",
      "role": 1,
      "status": 1
    }
  }
}
```

**错误 (400):** 用户名或密码错误 / 账号已被禁用

---

### GET /api/auth/me — 获取当前用户

**需认证**

```
GET /api/auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**成功响应 (200):**
```json
{
  "code": 200,
  "data": {
    "id": 3,
    "username": "zhangsan",
    "nickname": "张三",
    "email": null,
    "phone": null,
    "avatar": null,
    "role": 1,
    "status": 1,
    "createdAt": "2026-05-12T17:00:00"
  }
}
```

---

## 2. 用户模块 `/api/users`

### GET /api/users/profile — 获取个人资料

**需认证**

```
GET /api/users/profile
```

### PUT /api/users/profile — 更新个人资料

**需认证**

```
PUT /api/users/profile
Content-Type: application/json

{
  "nickname": "新昵称",
  "email": "user@example.com",
  "phone": "13800138000",
  "avatar": "/uploads/xxx.jpg"
}
```

### PUT /api/users/password — 修改密码

**需认证**

```
PUT /api/users/password
Content-Type: application/json

{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

**错误 (400):** 原密码错误

---

## 3. 分类模块 `/api/categories`

### GET /api/categories — 获取所有一级分类

**公开接口**

```
GET /api/categories
```

**响应:**
```json
{
  "code": 200,
  "data": [
    { "id": 1, "name": "电子产品", "parentId": 0, "sortOrder": 1 },
    { "id": 2, "name": "服装鞋帽", "parentId": 0, "sortOrder": 2 }
  ]
}
```

### GET /api/categories/tree — 获取完整分类树

**公开接口**

```
GET /api/categories/tree
```

**响应:**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1, "name": "电子产品", "parentId": 0, "sortOrder": 1,
      "children": [
        { "id": 5, "name": "手机", "parentId": 1, "sortOrder": 1, "children": [] },
        { "id": 6, "name": "电脑", "parentId": 1, "sortOrder": 2, "children": [] }
      ]
    }
  ]
}
```

### GET /api/categories/{id}/children — 获取子分类

**公开接口**

```
GET /api/categories/1/children
```

---

## 4. 商品模块 `/api/products`

### GET /api/products — 商品列表（分页+筛选+排序）

**公开接口**

```
GET /api/products?page=1&size=10&keyword=手机&categoryId=5&sort=sales_desc
```

**参数说明:**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| page | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| keyword | string | 否 | 搜索关键词（模糊匹配商品名） |
| categoryId | long | 否 | 分类 ID（自动包含所有子类商品） |
| sort | string | 否 | price_asc / price_desc / sales_desc / newest |

**响应:**
```json
{
  "code": 200,
  "data": {
    "total": 50,
    "page": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "name": "iPhone 15 智能手机",
        "description": "全新 A16 芯片",
        "categoryId": 5,
        "categoryName": "手机",
        "price": 5999.00,
        "stock": 100,
        "mainImage": "/uploads/xxx.jpg",
        "images": ["/uploads/a.jpg", "/uploads/b.jpg"],
        "status": 1,
        "sales": 256,
        "createdAt": "2026-05-12T10:00:00"
      }
    ]
  }
}
```

---

### GET /api/products/hot — 热销商品

**公开接口**

```
GET /api/products/hot
```

**响应:** 返回 Top 8 热销商品数组 `data: [...]`

---

### GET /api/products/search — 商品搜索

**公开接口**

```
GET /api/products/search?keyword=耳机&page=1&size=10
```

---

### GET /api/products/{id} — 商品详情

**公开接口**

```
GET /api/products/1
```

**响应:** 返回单个 ProductVO 对象

**错误 (404):** 商品不存在

---

## 5. 购物车模块 `/api/cart`

所有接口需认证

### GET /api/cart — 获取购物车列表

```
GET /api/cart
```

**响应:**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "userId": 2,
      "productId": 1,
      "productName": "iPhone 15 智能手机",
      "productImage": "/uploads/xxx.jpg",
      "price": 5999.00,
      "quantity": 2,
      "selected": true
    }
  ]
}
```

---

### POST /api/cart — 加入购物车

```
POST /api/cart
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

**说明:** 若商品已在购物车中，`quantity` 累加而非新增

---

### PUT /api/cart/{id} — 更新购物车项

```
PUT /api/cart/1
Content-Type: application/json

{
  "quantity": 3,
  "selected": true
}
```

**说明:** `quantity` 和 `selected` 至少传一个

---

### DELETE /api/cart/{id} — 删除单个购物车项

```
DELETE /api/cart/1
```

---

### DELETE /api/cart — 清空购物车

```
DELETE /api/cart
```

---

### PUT /api/cart/select-all — 全选/取消全选

```
PUT /api/cart/select-all
Content-Type: application/json

{
  "selected": true
}
```

---

## 6. 订单模块 `/api/orders`

所有接口需认证

### POST /api/orders — 创建订单

```
POST /api/orders
Content-Type: application/json

{
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "receiverAddress": "北京市朝阳区 xx 路 xx 号",
  "cartItemIds": [1, 2, 3]
}
```

**说明:** 下单后自动从购物车中删除对应项。商品信息以快照形式存入 order_items。

**响应:**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "2026051217300012345",
    "userId": 2,
    "totalAmount": 6198.00,
    "status": 0,
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "receiverAddress": "北京市朝阳区 xx 路 xx 号",
    "payTime": null,
    "shipTime": null,
    "deliverTime": null,
    "createdAt": "2026-05-12T17:30:00",
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "iPhone 15 智能手机",
        "productImage": "/uploads/xxx.jpg",
        "price": 5999.00,
        "quantity": 1,
        "totalPrice": 5999.00
      },
      {
        "id": 2,
        "productId": 9,
        "productName": "简约台灯 LED 护眼",
        "productImage": "/uploads/yyy.jpg",
        "price": 89.00,
        "quantity": 1,
        "totalPrice": 89.00
      }
    ]
  }
}
```

---

### GET /api/orders — 我的订单列表

```
GET /api/orders?page=1&size=10&status=0
```

**参数说明:** `status` 可选，按订单状态筛选

---

### GET /api/orders/{id} — 订单详情

```
GET /api/orders/1
```

---

### PUT /api/orders/{id}/cancel — 取消订单

```
PUT /api/orders/1/cancel
```

**限制:** 只能取消 `status == 0 (PENDING)` 的订单

---

### PUT /api/orders/{id}/pay — 模拟支付

```
PUT /api/orders/1/pay
```

**处理逻辑:** 扣减商品库存（乐观锁）→ 状态变为 PAID → 记录 payTime

---

## 7. 后台管理 `/api/admin`

所有接口需认证 + 管理员权限（role == 0）

### 7.1 商品管理 `/api/admin/products`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/products` | 商品列表（同前台参数） |
| POST | `/api/admin/products` | 新增商品 |
| PUT | `/api/admin/products/{id}` | 更新商品 |
| PUT | `/api/admin/products/{id}/status` | 上架/下架 `{"status": 0}` |
| POST | `/api/admin/products/upload` | 上传图片（multipart/form-data, 字段名 `file`），返回 URL |

### 7.2 订单管理 `/api/admin/orders`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/orders` | 所有订单（可选 status/orderNo 筛选） |
| GET | `/api/admin/orders/{id}` | 订单详情 |
| PUT | `/api/admin/orders/{id}/ship` | 发货（PAID → SHIPPED） |
| PUT | `/api/admin/orders/{id}/deliver` | 确认送达（SHIPPED → DELIVERED） |

### 7.3 用户管理 `/api/admin/users`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/users` | 用户列表（分页） |
| PUT | `/api/admin/users/{id}/status` | 启用/禁用 `{"status": 0}` |
| PUT | `/api/admin/users/{id}/role` | 设置角色 `{"role": 0}` |

### 7.4 分类管理 `/api/admin/categories`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/categories/tree` | 分类树 |
| POST | `/api/admin/categories` | 新增分类 |
| PUT | `/api/admin/categories/{id}` | 更新分类 |
| DELETE | `/api/admin/categories/{id}` | 删除（有子分类/商品时拒绝） |

### 7.5 仪表盘 `/api/admin/stats`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/stats` | 统计数据 |

**响应:**
```json
{
  "code": 200,
  "data": {
    "totalUsers": 128,
    "totalOrders": 356,
    "totalProducts": 45,
    "totalRevenue": 268900.00,
    "pendingOrders": 12,
    "paidOrders": 8,
    "shippedOrders": 15,
    "completedOrders": 320
  }
}
```

---

## 错误处理

所有未预期的异常统一由 `GlobalExceptionHandler` 处理，返回格式：

```json
{
  "code": 500,
  "message": "服务器内部错误",
  "data": null
}
```

`BusinessException` 可自定义 code 和 message，用于业务逻辑错误的精确提示。
