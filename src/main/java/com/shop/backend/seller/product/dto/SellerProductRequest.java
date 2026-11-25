package com.shop.backend.seller.product.dto;

import com.shop.backend.product.entity.PackagingType;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SellerProductRequest {

    private String name;                // 상품명
    private Long category_id;           // 카데고리 ID
    private int price;                  // 판매가
    private Integer discount_rate;      // 할인율
    private int stock;                  // 재고
    private PackagingType storage_type; // ROOM/COLD/FROZEN
    private String origin;              // 원산지
    private String description;         // 상품 설명
    private MultipartFile thumbnail;              // 대표 이미지 (파일)
    private List<MultipartFile> detail_images;    // 상세 이미지들 (파일 여러 개)
}
