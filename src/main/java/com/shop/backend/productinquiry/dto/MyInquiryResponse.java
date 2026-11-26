package com.shop.backend.productinquiry.dto;

import lombok.Builder;
import lombok.Getter;

// 내 상품 문의 내역 응답 (U-53) — 마이페이지, 내가 쓴 문의 목록
@Getter
@Builder
public class MyInquiryResponse {

    private Long inquiry_id;
    private Long product_id;
    private String product_name;   // JOIN product로 상품명
    private String title;
    private boolean is_secret;
    private boolean is_answered;    // 답변 여부
    private String created_at;
}
