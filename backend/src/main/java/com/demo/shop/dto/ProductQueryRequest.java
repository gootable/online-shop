package com.demo.shop.dto;

import lombok.Data;

@Data
public class ProductQueryRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private Long categoryId;
    private String sort;  // price_asc, price_desc, sales_desc, newest
}
