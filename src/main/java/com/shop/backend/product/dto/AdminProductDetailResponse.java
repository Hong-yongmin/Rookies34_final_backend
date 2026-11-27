package com.shop.backend.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 관리자 상품 상세 응답 — 심사용. 손님 상세와 달리 status/seller_id 포함
@Getter
@Setter
@Builder
public class AdminProductDetailResponse {

    private Long product_id;
    private String seller_id;      // 누구 상품인지 (심사용)
    private String status;         // 승인 상태 (심사용)
    private Long category_id;
    private String name;
    private int price;
    private int discount_rate;
    private int stock;
    private String storage_type;
    private String origin;         // 원산지 (반려 사유 판단)
    private String description;
    private String thumbnail;
    private List<String> detail_images;   // 상세 이미지 (별도 세팅)
    private String created_at;
}
