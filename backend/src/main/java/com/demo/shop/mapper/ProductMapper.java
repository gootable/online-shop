package com.demo.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.shop.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE products SET stock = stock - #{quantity} " +
            "WHERE id = #{productId} AND stock >= #{quantity}")
    int deductStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    @Select("SELECT p.*, c.name as category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE p.status = 1 " +
            "ORDER BY p.sales DESC LIMIT #{limit}")
    List<Product> selectHotProducts(@Param("limit") int limit);
}
