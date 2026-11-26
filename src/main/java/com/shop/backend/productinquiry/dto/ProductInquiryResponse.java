package com.shop.backend.productinquiry.dto;

import lombok.Builder;
import lombok.Getter;

// 상품 문의 목록 응답 (U-26) — JdbcTemplate RowMapper로 매핑
@Getter
@Builder
public class ProductInquiryResponse {

    private Long inquiry_id;
    private String title;
    private String writer;        // ⚠️ 마스킹 없이 그대로 노출 → 작성자 개인정보 노출 진단 포인트
    private String created_at;
    private boolean is_secret;     // 비밀글 여부
    private boolean is_answered;   // 답변 여부
    private String content;       // ⚠️ 비밀글도 그대로 반환 → 비밀글 열람 진단 포인트
    private String answer;         // 판매자 답변 (없으면 null)
}
