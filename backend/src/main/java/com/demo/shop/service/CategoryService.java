package com.demo.shop.service;

import com.demo.shop.entity.Category;
import com.demo.shop.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    List<CategoryVO> listAll();
    List<CategoryVO> getTree();
    List<CategoryVO> getChildren(Long parentId);
    Category getById(Long id);
    void create(Category category);
    void update(Category category);
    void delete(Long id);
}
