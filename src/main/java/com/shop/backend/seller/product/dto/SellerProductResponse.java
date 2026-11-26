package com.shop.backend.seller.product.dto;

import com.shop.backend.product.entity.Product;
import lombok.Getter;
import java.time.LocalDate;

// 판매자 내 상품 목록 응답 (S-11)
@Getter
public class SellerProductResponse {

    private Long product_id;
    private String name;
    private int price;
    private int discount_rate;
    private int stock;          // 판매자용 — 재고 포함
    private String status;      // 판매자용 — 상태 포함 (PENDING/APPROVED/REJECTED/WITHDRAWN)
    private String thumbnail_url;
    private LocalDate created_at;

    public SellerProductResponse(Product p) {
        this.product_id = p.getId();
        this.name = p.getName();
        this.price = p.getPrice();
        this.discount_rate = p.getDiscount_rate();
        this.stock = p.getStock();
        this.status = p.getStatus();
        this.thumbnail_url = p.getThumbnail();   // 엔티티 필드는 thumbnail, 컬럼은 thumbnail_url
        this.created_at = p.getCreatedAt();
    }
}
