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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    // Cache: parentId → child IDs
    private volatile Map<Long, List<Long>> parentChildCache;
    private volatile long cacheTimestamp = 0;

    private Map<Long, List<Long>> getParentChildMap() {
        if (parentChildCache == null || System.currentTimeMillis() - cacheTimestamp > 60000) {
            List<Category> all = categoryMapper.selectList(null);
            Map<Long, List<Long>> map = new HashMap<>();
            for (Category c : all) {
                if (c.getParentId() != null && c.getParentId() > 0) {
                    map.computeIfAbsent(c.getParentId(), k -> new ArrayList<>()).add(c.getId());
                }
            }
            parentChildCache = map;
            cacheTimestamp = System.currentTimeMillis();
        }
        return parentChildCache;
    }

    private void invalidateCache() {
        parentChildCache = null;
    }

    @Override
    public Set<Long> getDescendantIds(Long categoryId) {
        Set<Long> ids = new LinkedHashSet<>();
        ids.add(categoryId);
        Map<Long, List<Long>> pcMap = getParentChildMap();
        collectDescendants(categoryId, pcMap, ids);
        return ids;
    }

    private void collectDescendants(Long parentId, Map<Long, List<Long>> pcMap, Set<Long> collector) {
        List<Long> children = pcMap.get(parentId);
        if (children != null) {
            for (Long childId : children) {
                if (collector.add(childId)) {
                    collectDescendants(childId, pcMap, collector);
                }
            }
        }
    }

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
        invalidateCache();
    }

    @Override
    public void update(Category category) {
        categoryMapper.updateById(category);
        invalidateCache();
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
        invalidateCache();
    }

    private CategoryVO toVO(Category c) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(c, vo);
        return vo;
    }
}
