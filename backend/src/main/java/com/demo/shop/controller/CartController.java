package com.demo.shop.controller;

import com.demo.shop.common.Result;
import com.demo.shop.dto.CartAddRequest;
import com.demo.shop.security.UserContext;
import com.demo.shop.service.CartService;
import com.demo.shop.vo.CartItemVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<List<CartItemVO>> list() {
        Long userId = UserContext.getCurrentUserId();
        return Result.ok(cartService.listByUser(userId));
    }

    @PostMapping
    public Result<CartItemVO> add(@Valid @RequestBody CartAddRequest request) {
        Long userId = UserContext.getCurrentUserId();
        CartItemVO item = cartService.add(userId, request.getProductId(), request.getQuantity());
        return Result.ok(item);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = UserContext.getCurrentUserId();
        Integer quantity = body.containsKey("quantity") ? ((Number) body.get("quantity")).intValue() : null;
        Boolean selected = body.containsKey("selected") ? (Boolean) body.get("selected") : null;
        cartService.update(userId, id, quantity, selected);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        cartService.remove(userId, id);
        return Result.ok();
    }

    @DeleteMapping
    public Result<Void> clear() {
        Long userId = UserContext.getCurrentUserId();
        cartService.clear(userId);
        return Result.ok();
    }

    @PutMapping("/select-all")
    public Result<Void> selectAll(@RequestBody Map<String, Boolean> body) {
        Long userId = UserContext.getCurrentUserId();
        Boolean selected = body.getOrDefault("selected", true);
        cartService.selectAll(userId, selected);
        return Result.ok();
    }
}
