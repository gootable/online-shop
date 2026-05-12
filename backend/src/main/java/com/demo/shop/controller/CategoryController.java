package com.demo.shop.controller;

import com.demo.shop.common.Result;
import com.demo.shop.service.CategoryService;
import com.demo.shop.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<List<CategoryVO>> listAll() {
        return Result.ok(categoryService.listAll());
    }

    @GetMapping("/tree")
    public Result<List<CategoryVO>> getTree() {
        return Result.ok(categoryService.getTree());
    }

    @GetMapping("/{id}/children")
    public Result<List<CategoryVO>> getChildren(@PathVariable Long id) {
        return Result.ok(categoryService.getChildren(id));
    }
}
