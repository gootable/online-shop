package com.demo.shop.controller.admin;

import com.demo.shop.common.PageResult;
import com.demo.shop.common.Result;
import com.demo.shop.dto.ProductQueryRequest;
import com.demo.shop.entity.Product;
import com.demo.shop.service.FileService;
import com.demo.shop.service.ProductService;
import com.demo.shop.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final FileService fileService;

    @GetMapping
    public Result<PageResult<ProductVO>> list(ProductQueryRequest request) {
        return Result.ok(productService.page(request));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Product product) {
        productService.create(product);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productService.update(product);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
