package com.shop.backend.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

// 상품 상세 응답용 — JdbcTemplate RowMapper로 매핑
@Getter
@Setter
@Builder
public class ProductDetailResponse {

    private Long product_id;
    private String name;
    private Long category_id;
    private int price;
    private int discount_rate;
    private int stock;
    private String storage_type;
    private String origin;
    private String description;
    private String thumbnail;              // DB thumbnail → 응답 thumbnail
    private List<String> detail_images;    // product_image 테이블에서 별도 조회
}
