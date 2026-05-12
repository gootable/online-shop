package com.demo.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.shop.common.BusinessException;
import com.demo.shop.entity.CartItem;
import com.demo.shop.entity.Product;
import com.demo.shop.mapper.CartItemMapper;
import com.demo.shop.mapper.ProductMapper;
import com.demo.shop.service.CartService;
import com.demo.shop.vo.CartItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    @Override
    public List<CartItemVO> listByUser(Long userId) {
        List<CartItem> items = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .orderByDesc(CartItem::getCreatedAt));

        if (items.isEmpty()) return new ArrayList<>();

        List<Long> productIds = items.stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());
        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<CartItemVO> vos = new ArrayList<>();
        for (CartItem item : items) {
            Product product = productMap.get(item.getProductId());
            if (product == null || product.getStatus() == 0) {
                // Product removed or off-shelf — skip and could optionally remove from cart
                continue;
            }
            CartItemVO vo = new CartItemVO();
            vo.setId(item.getId());
            vo.setUserId(item.getUserId());
            vo.setProductId(item.getProductId());
            vo.setProductName(product.getName());
            vo.setProductImage(product.getMainImage());
            vo.setPrice(product.getPrice());
            vo.setQuantity(item.getQuantity());
            vo.setSelected(item.getSelected() == 1);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public CartItemVO add(Long userId, Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException("商品不存在或已下架");
        }

        // Check if already in cart
        CartItem existing = cartItemMapper.selectOne(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .eq(CartItem::getProductId, productId));

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartItemMapper.updateById(existing);
            return toVO(existing, product);
        }

        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setSelected(1);
        cartItemMapper.insert(item);

        return toVO(item, product);
    }

    @Override
    public void update(Long userId, Long cartItemId, Integer quantity, Boolean selected) {
        CartItem item = cartItemMapper.selectById(cartItemId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        if (quantity != null) {
            item.setQuantity(quantity);
        }
        if (selected != null) {
            item.setSelected(selected ? 1 : 0);
        }
        cartItemMapper.updateById(item);
    }

    @Override
    public void remove(Long userId, Long cartItemId) {
        CartItem item = cartItemMapper.selectById(cartItemId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        cartItemMapper.deleteById(cartItemId);
    }

    @Override
    public void clear(Long userId) {
        cartItemMapper.delete(
                new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
    }

    @Override
    public void selectAll(Long userId, Boolean selected) {
        List<CartItem> items = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
        for (CartItem item : items) {
            item.setSelected(selected ? 1 : 0);
            cartItemMapper.updateById(item);
        }
    }

    private CartItemVO toVO(CartItem item, Product product) {
        CartItemVO vo = new CartItemVO();
        vo.setId(item.getId());
        vo.setUserId(item.getUserId());
        vo.setProductId(item.getProductId());
        vo.setProductName(product.getName());
        vo.setProductImage(product.getMainImage());
        vo.setPrice(product.getPrice());
        vo.setQuantity(item.getQuantity());
        vo.setSelected(item.getSelected() == 1);
        return vo;
    }
}
