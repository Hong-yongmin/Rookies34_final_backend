package com.shop.backend.productinquiry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Entity
public class ProductInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long product_id;    // 어떤 상품의 문의인지

    private String title;       // 제목 (최대 50자)

    @Column(length = 1000)
    private String content;     // 내용 (최대 1000자)

    private boolean is_secret;  // 비밀글 여부 — 열람 권한 진단 포인트(조회 시)

    private String author;      // 작성자(아이디) — 추후 JWT 토큰에서 추출

    private LocalDateTime created_at = LocalDateTime.now();

    @Column(length = 1000)
    private String answer;      // 판매자 답변 (null = 미답변)

    protected ProductInquiry() {}   // JPA용 기본 생성자

    public ProductInquiry(Long product_id, String title, String content, boolean is_secret, String author) {
        this.product_id = product_id;
        this.title = title;       // 저장 전 정화(Sanitize) 안 함 → 저장형 XSS 그대로 저장
        this.content = content;
        this.is_secret = is_secret;
        this.author = author;
    }

    public void writeAnswer(String answer) {
        this.answer = answer;
    }
}
