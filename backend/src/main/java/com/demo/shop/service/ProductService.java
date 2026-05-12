package com.demo.shop.service;

import com.demo.shop.common.PageResult;
import com.demo.shop.dto.ProductQueryRequest;
import com.demo.shop.entity.Product;
import com.demo.shop.vo.ProductVO;

import java.util.List;

public interface ProductService {
    PageResult<ProductVO> page(ProductQueryRequest request);
    ProductVO getDetail(Long id);
    List<ProductVO> getHotProducts(int limit);
    PageResult<ProductVO> search(String keyword, int page, int size);
    Product getById(Long id);
    void create(Product product);
    void update(Product product);
    void updateStatus(Long id, Integer status);
}
