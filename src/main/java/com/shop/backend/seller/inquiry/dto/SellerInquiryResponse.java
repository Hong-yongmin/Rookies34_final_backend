package com.shop.backend.seller.inquiry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Getter(onMethod_ = @JsonProperty("is_answered"))
    private boolean is_answered;    // 답변 여부
    private String answer;          // 답변 내용 (없으면 null)
    private String answered_at;     // 답변일 (없으면 null)
    private String created_at;
}
