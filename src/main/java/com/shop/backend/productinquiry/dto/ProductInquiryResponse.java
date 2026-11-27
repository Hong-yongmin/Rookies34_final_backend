package com.shop.backend.productinquiry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

// 상품 문의 목록 응답 (U-26)
@Getter
@Builder
public class ProductInquiryResponse {

    private Long inquiry_id;
    private String title;
    private String writer;
    private String created_at;
    private boolean is_secret;     // 비밀글 여부
    @Getter(onMethod_ = @JsonProperty("is_answered"))
    private boolean is_answered;   // 답변 여부
    private String content;
    private String answer;         // 판매자 답변 (없으면 null)
}
