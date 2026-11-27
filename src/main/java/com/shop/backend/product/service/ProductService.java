package com.shop.backend.product.service;

import com.shop.backend.product.dto.*;
import com.shop.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    // 목록 조회 (U-20)
    public List<ProductSummaryResponse> getList(ListType listType, Long categoryId, ProductSort sort, int page) {
        return switch (listType) {
            case ALL      -> repository.getListByCategory(null, sort, page);
            case CATEGORY -> repository.getListByCategory(categoryId, sort, page);
            case BEST    -> repository.getListByCuration("BEST", sort, page);
            case SALE    -> repository.getListByCuration("SALE", sort, page);
            case NEW     -> repository.getListByCuration("NEW_ARRIVAL", sort, page);  // NEW → NEW_ARRIVAL
            case BENEFIT -> repository.getListByCuration("BENEFIT", sort, page);
        };
    }

    // 검색 (U-21)
    public List<ProductSummaryResponse> search(String keyword, ProductSort sort, int page) {
        return repository.search(keyword, sort, page);
    }

    // 상세 (U-22)
    public DetailResponse getDetail(Long productId) {
        ProductDetailResponse detail = repository.getDetail(productId);
        if (detail == null) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId);
        }
        detail.setDetail_images(repository.getDetailImages(productId));   // 상세 이미지 별도 조회
        return DetailResponse.builder().code("ok").detail(detail).build();
    }
}
