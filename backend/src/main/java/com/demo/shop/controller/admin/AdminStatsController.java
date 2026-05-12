package com.demo.shop.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.shop.common.Result;
import com.demo.shop.entity.Order;
import com.demo.shop.entity.User;
import com.demo.shop.mapper.OrderMapper;
import com.demo.shop.mapper.ProductMapper;
import com.demo.shop.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    @GetMapping
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();

        long totalUsers = userMapper.selectCount(null);
        long totalOrders = orderMapper.selectCount(null);
        long totalProducts = productMapper.selectCount(
                new LambdaQueryWrapper<com.demo.shop.entity.Product>().eq(com.demo.shop.entity.Product::getStatus, 1));

        // Revenue from paid/shipped/delivered/completed orders
        double totalRevenue = orderMapper.selectList(null).stream()
                .filter(o -> o.getStatus() >= 1)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();

        long pendingOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getStatus, 0));
        long paidOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getStatus, 1));
        long shippedOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getStatus, 2));
        long completedOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().in(Order::getStatus, 3, 4));

        data.put("totalUsers", totalUsers);
        data.put("totalOrders", totalOrders);
        data.put("totalProducts", totalProducts);
        data.put("totalRevenue", totalRevenue);
        data.put("pendingOrders", pendingOrders);
        data.put("paidOrders", paidOrders);
        data.put("shippedOrders", shippedOrders);
        data.put("completedOrders", completedOrders);

        return Result.ok(data);
    }
}
