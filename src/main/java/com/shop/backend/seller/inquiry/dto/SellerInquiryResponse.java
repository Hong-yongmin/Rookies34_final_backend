package com.shop.backend.seller.inquiry.dto;

import com.shop.backend.productinquiry.entity.ProductInquiry;
import lombok.Getter;
import java.time.LocalDateTime;

// 판매자 받은 문의 목록 응답 (S-12)
@Getter
public class SellerInquiryResponse {

    private Long inquiry_id;
    private Long product_id;
    private String product_name;   // 어떤 상품의 문의인지 (상품명)
    private String title;
    private String content;        // 판매자는 비밀글도 내용 봄
    private boolean is_secret;
    private boolean is_answered;    // 답변 여부 (답변 대상 선택용)
    private LocalDateTime created_at;

    public SellerInquiryResponse(ProductInquiry i, String productName) {
        this.inquiry_id = i.getId();
        this.product_id = i.getProduct_id();
        this.product_name = productName;          // Service에서 상품명 조회해 전달
        this.title = i.getTitle();
        this.content = i.getContent();
        this.is_secret = i.is_secret();
        this.is_answered = (i.getAnswer() != null);
        this.created_at = i.getCreated_at();
    }
}
