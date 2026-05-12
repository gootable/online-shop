package com.demo.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.shop.common.BusinessException;
import com.demo.shop.entity.Category;
import com.demo.shop.entity.Product;
import com.demo.shop.mapper.CategoryMapper;
import com.demo.shop.mapper.ProductMapper;
import com.demo.shop.service.CategoryService;
import com.demo.shop.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public List<CategoryVO> listAll() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        return categories.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getTree() {
        List<Category> all = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        Map<Long, List<Category>> childrenMap = all.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() > 0)
                .collect(Collectors.groupingBy(Category::getParentId));

        List<CategoryVO> roots = new ArrayList<>();
        for (Category c : all) {
            if (c.getParentId() == null || c.getParentId() == 0) {
                CategoryVO vo = toVO(c);
                buildChildren(vo, childrenMap);
                roots.add(vo);
            }
        }
        return roots;
    }

    private void buildChildren(CategoryVO parent, Map<Long, List<Category>> childrenMap) {
        List<Category> children = childrenMap.get(parent.getId());
        if (children != null) {
            List<CategoryVO> childVOs = children.stream().map(c -> {
                CategoryVO vo = toVO(c);
                buildChildren(vo, childrenMap);
                return vo;
            }).collect(Collectors.toList());
            parent.setChildren(childVOs);
        }
    }

    @Override
    public List<CategoryVO> getChildren(Long parentId) {
        List<Category> children = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, parentId)
                        .orderByAsc(Category::getSortOrder));
        return children.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public void create(Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public void update(Category category) {
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Long id) {
        // Check if has children
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("该分类下有子分类，无法删除");
        }
        // Check if has products
        Long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id));
        if (productCount > 0) {
            throw new BusinessException("该分类下有商品，无法删除");
        }
        categoryMapper.deleteById(id);
    }

    private CategoryVO toVO(Category c) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(c, vo);
        return vo;
    }
}
