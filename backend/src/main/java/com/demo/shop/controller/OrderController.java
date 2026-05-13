package com.demo.shop.controller;

import com.demo.shop.common.PageResult;
import com.demo.shop.common.Result;
import com.demo.shop.dto.OrderCreateRequest;
import com.demo.shop.security.UserContext;
import com.demo.shop.service.OrderService;
import com.demo.shop.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<OrderVO> create(@Valid @RequestBody OrderCreateRequest request) {
        Long userId = UserContext.getCurrentUserId();
        OrderVO order = orderService.create(userId, request.getReceiverName(),
                request.getReceiverPhone(), request.getReceiverAddress(), request.getCartItemIds());
        return Result.ok(order);
    }

    @GetMapping
    public Result<PageResult<OrderVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(orderService.pageByUser(userId, page, size, status));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(orderService.getDetail(id, userId));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        orderService.cancel(id, userId);
        return Result.ok();
    }

    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        orderService.pay(id, userId);
        return Result.ok();
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        orderService.confirm(id, userId);
        return Result.ok();
    }
}
