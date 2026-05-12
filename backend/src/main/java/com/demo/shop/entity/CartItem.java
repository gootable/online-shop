package com.demo.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cart_items")
public class CartItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer selected;  // 0=unchecked, 1=checked
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
