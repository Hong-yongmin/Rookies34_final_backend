package com.shop.backend.product.entity;

// 큐레이션 종류 (관리자 지정) — product.curation 컬럼 (enum, null=미지정)
public enum Curation {
    BEST,          // 베스트
    SALE,          // 세일
    NEW_ARRIVAL,   // 신상
    BENEFIT        // 특가
}
