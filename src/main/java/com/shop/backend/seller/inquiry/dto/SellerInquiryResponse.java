package com.shop.backend.seller.inquiry.dto;

import lombok.Builder;
import lombok.Getter;

// 판매자 받은 문의 목록 응답 (S-12) — JdbcTemplate RowMapper로 매핑
@Getter
@Builder
public class SellerInquiryResponse {

    private Long inquiry_id;
    private Long product_id;
    private String product_name;   // JOIN product로 상품명
    private String title;
    private String content;        // 판매자는 비밀글도 내용 봄
    private boolean is_secret;
    private boolean is_answered;    // 답변 여부
    private String created_at;
}
