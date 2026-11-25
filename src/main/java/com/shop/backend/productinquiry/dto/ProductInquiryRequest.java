package com.shop.backend.productinquiry.dto;

import lombok.Getter;
import lombok.Setter;

// 상품 문의 작성 요청 (U-25)
@Getter
@Setter
public class ProductInquiryRequest {

    private String title;        // 제목 (최대 50자)
    private String content;      // 내용 (최대 1000자)
    private Boolean is_secret;   // 비밀글 여부 (기본 false)
    // 작성자(writer)는 요청에서 받지 않고 JWT 토큰 id에서 추출
}
