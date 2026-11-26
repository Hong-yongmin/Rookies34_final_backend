package com.shop.backend.productinquiry.dto;

import lombok.Builder;
import lombok.Getter;

// 내 상품 문의 상세 응답 (목록에서 클릭 시) — content/answer 포함
@Getter
@Builder
public class MyInquiryDetailResponse {

    private Long inquiry_id;
    private Long product_id;
    private String product_name;   // JOIN product로 상품명
    private String title;
    private String content;        // 질문 내용
    private boolean is_secret;
    private boolean is_answered;
    private String answer;         // 판매자 답변 (없으면 null)
    private String answered_at;    // 답변일 (없으면 null)
    private String created_at;
}
