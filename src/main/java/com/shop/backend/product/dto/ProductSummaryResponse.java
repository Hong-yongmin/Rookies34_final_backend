package com.shop.backend.product.dto;

import lombok.Builder;
import lombok.Getter;

// 목록 / 검색 응답용 (요약)
@Getter
@Builder
public class ProductSummaryResponse {

    private Long product_id;
    private String name;
    private Long category_id;
    private int price;
    private int discount_rate;
    private String thumbnail;   // DB thumbnail_url → 응답 thumbnail
}
