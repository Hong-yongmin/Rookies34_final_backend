package com.shop.backend.product.dto;

import com.shop.backend.product.entity.Product;
import lombok.Getter;

// 목록 / 검색 응답용 (요약)
@Getter
public class ProductSummaryResponse {

    private Long product_id;
    private String name;
    private Long category_id;
    private int price;
    private int discount_rate;
    private String thumbnail;

    public ProductSummaryResponse(Product p) {
        this.product_id = p.getId();
        this.name = p.getName();
        this.category_id = p.getCategory_id();
        this.price = p.getPrice();
        this.discount_rate = p.getDiscount_rate();
        this.thumbnail = p.getThumbnail();
    }
}
