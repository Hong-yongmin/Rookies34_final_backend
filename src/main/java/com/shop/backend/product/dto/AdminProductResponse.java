package com.shop.backend.product.dto;

import lombok.Builder;
import lombok.Getter;

// 관리자 상품 목록 응답 — 승인/반려/철회 판단용 최소 정보
@Getter
@Builder
public class AdminProductResponse {

    private Long product_id;
    private String seller_id;     // 어느 판매자 상품인지
    private String name;
    private Long category_id;     // 숫자 (이름은 추후 JOIN)
    private int price;
    private String status;        // PENDING/APPROVED/REJECTED/WITHDRAWN
    private String created_at;
}
