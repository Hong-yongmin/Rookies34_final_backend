package com.shop.backend.products.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;          // 상품명 (최대 100)
    private Long categoryId;      // 카테고리 ID (추후 Category 엔티티와 연관 가능)
    private int price;            // 판매가 (100 이상)
    private int discountRate;     // 할인율 (0~90)
    private int stock;            // 재고 (0 이상)

    @Enumerated(EnumType.STRING)
    private PackagingType packagingType;   // ROOM/COLD/FROZEN

    private String origin;        // 원산지

    @Column(length = 2000)
    private String description;   // 상품 설명 (HTML) — 저장형 XSS 진단 포인트

    private String thumbnailUrl;  // 대표 이미지 URL

    @ElementCollection            // 이미지 목록(최대 10) → product_images 테이블로 저장
    private List<String> images = new ArrayList<>();

    // 큐레이션(관리자 지정): BEST/SALE/NEW/BENEFIT — 간단화를 위해 플래그로
    private boolean best;
    private boolean sale;
    private boolean newArrival;
    private boolean benefit;

    private LocalDateTime createdAt = LocalDateTime.now();

    protected Product() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getCategoryId() { return categoryId; }
    public int getPrice() { return price; }
    public int getDiscountRate() { return discountRate; }
    public int getStock() { return stock; }
    public PackagingType getPackagingType() { return packagingType; }
    public String getOrigin() { return origin; }
    public String getDescription() { return description; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public List<String> getImages() { return images; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
