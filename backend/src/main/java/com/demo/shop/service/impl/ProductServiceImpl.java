package com.demo.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.shop.common.BusinessException;
import com.demo.shop.common.PageResult;
import com.demo.shop.dto.ProductQueryRequest;
import com.demo.shop.entity.Category;
import com.demo.shop.entity.Product;
import com.demo.shop.common.BusinessException;
import com.demo.shop.common.PageResult;
import com.demo.shop.dto.ProductQueryRequest;
import com.demo.shop.entity.Category;
import com.demo.shop.entity.Product;
import com.demo.shop.mapper.ProductMapper;
import com.demo.shop.service.CategoryService;
import com.demo.shop.service.ProductService;
import com.demo.shop.vo.ProductVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageResult<ProductVO> page(ProductQueryRequest request) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            wrapper.like(Product::getName, request.getKeyword());
        }
        if (request.getCategoryId() != null) {
            Set<Long> categoryIds = categoryService.getDescendantIds(request.getCategoryId());
            wrapper.in(Product::getCategoryId, new ArrayList<>(categoryIds));
        }

        // Sort
        String sort = request.getSort();
        if ("price_asc".equals(sort)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(Product::getPrice);
        } else if ("newest".equals(sort)) {
            wrapper.orderByDesc(Product::getCreatedAt);
        } else {
            // default: sales desc
            wrapper.orderByDesc(Product::getSales);
        }

        Page<Product> page = new Page<>(request.getPage(), request.getSize());
        Page<Product> result = productMapper.selectPage(page, wrapper);

        List<ProductVO> vos = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), vos);
    }

    @Override
    public ProductVO getDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return toVO(product);
    }

    @Override
    public List<ProductVO> getHotProducts(int limit) {
        List<Product> products = productMapper.selectHotProducts(limit);
        if (products.isEmpty()) {
            // Fallback: get latest products
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getStatus, 1)
                    .orderByDesc(Product::getSales)
                    .last("LIMIT " + limit);
            products = productMapper.selectList(wrapper);
        }
        return products.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<ProductVO> search(String keyword, int page, int size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .like(Product::getName, keyword)
                .orderByDesc(Product::getSales);

        Page<Product> p = new Page<>(page, size);
        Page<Product> result = productMapper.selectPage(p, wrapper);

        List<ProductVO> vos = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), vos);
    }

    @Override
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public void create(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void update(Product product) {
        productMapper.updateById(product);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(status);
        productMapper.updateById(product);
    }

    private ProductVO toVO(Product p) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(p, vo);

        // Category name
        if (p.getCategoryId() != null) {
            Category category = categoryService.getById(p.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // Parse images JSON
        if (p.getImages() != null && !p.getImages().isBlank()) {
            try {
                List<String> imageList = objectMapper.readValue(
                        p.getImages(), new TypeReference<List<String>>() {});
                vo.setImages(imageList);
            } catch (Exception e) {
                vo.setImages(new ArrayList<>());
            }
        } else {
            vo.setImages(new ArrayList<>());
        }

        return vo;
    }

}
