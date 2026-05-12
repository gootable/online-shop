package com.demo.shop.controller;

import com.demo.shop.common.PageResult;
import com.demo.shop.common.Result;
import com.demo.shop.dto.ProductQueryRequest;
import com.demo.shop.service.ProductService;
import com.demo.shop.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Result<PageResult<ProductVO>> list(ProductQueryRequest request) {
        return Result.ok(productService.page(request));
    }

    @GetMapping("/hot")
    public Result<List<ProductVO>> hot() {
        return Result.ok(productService.getHotProducts(8));
    }

    @GetMapping("/search")
    public Result<PageResult<ProductVO>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(productService.search(keyword, page, size));
    }

    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        return Result.ok(productService.getDetail(id));
    }
}
