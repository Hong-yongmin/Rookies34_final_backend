package com.shop.backend.productinquiry.dto;

import com.shop.backend.productinquiry.entity.ProductInquiry;
import lombok.Getter;
import java.time.LocalDateTime;

// 상품 문의 목록 응답 (U-26)
@Getter
public class ProductInquiryResponse {

    private Long inquiry_id;
    private String title;
    private String writer;              // 마스킹 없이 그대로 노출 → 작성자 개인정보 노출 진단 포인트
    private LocalDateTime created_at;
    private boolean is_secret;          // 비밀글 여부(🔒 표시용)
    private boolean is_answered;        // 답변상태 (답변완료/미답변)
    private String content;             // 비밀글도 가리지 않고 그대로 반환 → 비밀글 열람 진단 포인트
    private String answer;              // 판매자 답변 (없으면 null)

    public ProductInquiryResponse(ProductInquiry i) {
        this.inquiry_id = i.getId();
        this.title = i.getTitle();
        this.writer = i.getAuthor();          // 마스킹 안 함
        this.created_at = i.getCreated_at();
        this.is_secret = i.is_secret();
        this.is_answered = (i.getAnswer() != null);
        this.content = i.getContent();        // 비밀글이어도 그대로
        this.answer = i.getAnswer();
    }
}
