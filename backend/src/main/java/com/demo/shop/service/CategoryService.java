package com.demo.shop.service;

import com.demo.shop.entity.Category;
import com.demo.shop.vo.CategoryVO;

import java.util.List;

import java.util.Set;

public interface CategoryService {
    List<CategoryVO> listAll();
    List<CategoryVO> getTree();
    List<CategoryVO> getChildren(Long parentId);
    Category getById(Long id);
    void create(Category category);
    void update(Category category);
    void delete(Long id);

    /**
     * 获取某个分类及其所有后代分类的 ID 集合。
     * 用于商品查询：选中父类时，应显示该父类下所有子类商品。
     */
    Set<Long> getDescendantIds(Long categoryId);
}
