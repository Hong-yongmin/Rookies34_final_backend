package com.shop.backend.product.dto;

public enum ListType {
    ALL,        // 전체 상품
    CATEGORY,   // 카테고리별 (category_id가 없으면 전체)
    BEST,       // 베스트
    SALE,       // 세일
    NEW,        // 신상
    BENEFIT     // 특가
}
