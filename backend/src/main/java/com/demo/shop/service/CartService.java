package com.demo.shop.service;

import com.demo.shop.vo.CartItemVO;

import java.util.List;

public interface CartService {
    List<CartItemVO> listByUser(Long userId);
    CartItemVO add(Long userId, Long productId, Integer quantity);
    void update(Long userId, Long cartItemId, Integer quantity, Boolean selected);
    void remove(Long userId, Long cartItemId);
    void clear(Long userId);
    void selectAll(Long userId, Boolean selected);
}
