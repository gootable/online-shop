package com.demo.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.shop.common.BusinessException;
import com.demo.shop.common.PageResult;
import com.demo.shop.entity.*;
import com.demo.shop.mapper.*;
import com.demo.shop.service.OrderService;
import com.demo.shop.vo.OrderItemVO;
import com.demo.shop.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public OrderVO create(Long userId, String receiverName, String receiverPhone,
                          String receiverAddress, List<Long> cartItemIds) {
        // Get cart items
        List<CartItem> cartItems = cartItemMapper.selectBatchIds(cartItemIds);

        // Validate: all items belong to user and are selected
        for (CartItem item : cartItems) {
            if (!item.getUserId().equals(userId)) {
                throw new BusinessException("购物车项不属于当前用户");
            }
            if (item.getSelected() != 1) {
                throw new BusinessException("请先选中要购买的商品");
            }
        }

        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车项为空");
        }

        // Get products and calculate total
        List<Long> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productMapper.selectBatchIds(productIds);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = products.stream()
                    .filter(p -> p.getId().equals(cartItem.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("商品不存在"));

            if (product.getStatus() != 1) {
                throw new BusinessException("商品【" + product.getName() + "】已下架");
            }

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(itemTotal);
            orderItems.add(orderItem);
        }

        // Create order
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // PENDING
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        orderMapper.insert(order);

        // Create order items
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // Remove purchased cart items
        cartItemMapper.deleteBatchIds(cartItemIds);

        return toVO(order, orderItems);
    }

    @Override
    public PageResult<OrderVO> pageByUser(Long userId, int page, int size, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreatedAt);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        Page<Order> p = new Page<>(page, size);
        Page<Order> result = orderMapper.selectPage(p, wrapper);

        List<OrderVO> vos = result.getRecords().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectList(
                            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
                    return toVO(order, items);
                })
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), vos);
    }

    @Override
    public OrderVO getDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (userId != null && !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        return toVO(order, items);
    }

    @Override
    @Transactional
    public void cancel(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只能取消待支付订单");
        }
        order.setStatus(-1);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void pay(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }

        // Deduct stock
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            int affected = productMapper.deductStock(item.getProductId(), item.getQuantity());
            if (affected == 0) {
                throw new BusinessException("商品【" + item.getProductName() + "】库存不足");
            }
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public PageResult<OrderVO> pageAll(int page, int size, Integer status, String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .orderByDesc(Order::getCreatedAt);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(Order::getOrderNo, orderNo);
        }

        Page<Order> p = new Page<>(page, size);
        Page<Order> result = orderMapper.selectPage(p, wrapper);

        List<OrderVO> vos = result.getRecords().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectList(
                            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
                    return toVO(order, items);
                })
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), vos);
    }

    @Override
    @Transactional
    public void ship(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 1) throw new BusinessException("只能对已支付订单发货");
        order.setStatus(2);
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void deliver(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 2) throw new BusinessException("只能对已发货订单确认送达");
        order.setStatus(3);
        order.setDeliverTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // Increment sales
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setSales(product.getSales() + item.getQuantity());
                productMapper.updateById(product);
            }
        }
    }

    private String generateOrderNo() {
        String prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String suffix = String.format("%05d", (int) (Math.random() * 100000));
        return prefix + suffix;
    }

    private OrderVO toVO(Order order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        List<OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderItemVO iv = new OrderItemVO();
            BeanUtils.copyProperties(item, iv);
            return iv;
        }).collect(Collectors.toList());
        vo.setItems(itemVOs);
        return vo;
    }
}
