package com.shop.backend.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String seller_id;

    @Column(nullable = false)
    private String name;            // 상품명 (최대 100)

    @Column(nullable = false)
    private Long category_id;       // 카테고리 ID

    private int price;             // 판매가 (100 이상)

    private int discount_rate;     // 할인율 (0~90)

    private int stock;             // 재고 (0 이상)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PackagingType storage_type;   // ROOM/COLD/FROZEN

    @Column(nullable = true)
    private String origin;         // 원산지

    @Column(length = 2000, nullable = true)
    private String description;    // 상품 설명 (HTML)

    @Column(name = "thumbnail_url", length = 2048, nullable = false)
    private String thumbnail;      // 대표 이미지 URL

    @ElementCollection
    @CollectionTable(
            name = "product_image",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "url", length = 2048, nullable = false)
    @OrderColumn(name = "sort_order")
    private List<String> detail_images = new ArrayList<>();  // 이미지 목록(최대 10)

    // 큐레이션(관리자 지정): BEST/SALE/NEW/BENEFIT
    private boolean best;
    private boolean sale;
    private boolean new_arrival;
    private boolean benefit;

    @Column(nullable = false)
    private String status = "PENDING";   // 상태: PENDING(검증대기) → APPROVED(승인)

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDate createdAt;

    public Product() {}   // JPA용 기본 생성자
}
