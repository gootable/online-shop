package com.demo.shop.service;

import com.demo.shop.common.PageResult;
import com.demo.shop.entity.Order;
import com.demo.shop.vo.OrderVO;

public interface OrderService {
    OrderVO create(Long userId, String receiverName, String receiverPhone,
                   String receiverAddress, java.util.List<Long> cartItemIds);
    PageResult<OrderVO> pageByUser(Long userId, int page, int size, Integer status);
    OrderVO getDetail(Long orderId, Long userId);
    void cancel(Long orderId, Long userId);
    void pay(Long orderId, Long userId);
    void confirm(Long orderId, Long userId);
    PageResult<OrderVO> pageAll(int page, int size, Integer status, String orderNo);
    void ship(Long orderId);
    void deliver(Long orderId);
}
