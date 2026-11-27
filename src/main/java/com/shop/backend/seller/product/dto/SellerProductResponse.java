package com.shop.backend.seller.product.dto;

import lombok.Builder;
import lombok.Getter;

// 판매자 내 상품 목록 응답 (S-11)
@Getter
@Builder
public class SellerProductResponse {

    private Long product_id;
    private String name;
    private int price;
    private int discount_rate;
    private int stock;          // 판매자용 — 재고
    private String status;      // 판매자용 — 상태
    private String thumbnail;
    private String created_at;  // DB date → 문자열로

    public String getThumbnail_url() {
        return thumbnail;
    }
}
