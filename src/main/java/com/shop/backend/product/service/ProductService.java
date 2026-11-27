package com.shop.backend.product.service;

import com.shop.backend.product.dto.*;
import com.shop.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getFrequentlyPurchasedProducts(String userId, Pageable page) {
        long total = repository.countPurchasedProducts(userId);
        List<ProductSummaryResponse> products = repository.getFrequentlyPurchasedProducts(
                userId, page.getPageSize(), page.getOffset());
        return new PageImpl<>(products, page, total);
    }

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

    // 목록 전체 개수 (U-20 페이지네이션용) — getList와 동일한 분기
    public int countList(ListType listType, Long categoryId) {
        return switch (listType) {
            case ALL      -> repository.countByCategory(null);
            case CATEGORY -> {
                if (categoryId == null) {
                    throw new IllegalArgumentException("category_id는 CATEGORY 조회 시 필수입니다.");
                }
                yield repository.countByCategory(categoryId);
            }
            case BEST    -> repository.countByCuration("BEST");
            case SALE    -> repository.countByCuration("SALE");
            case NEW     -> repository.countByCuration("NEW_ARRIVAL");
            case BENEFIT -> repository.countByCuration("BENEFIT");
        };
    }

    // 검색 (U-21)
    public List<ProductSummaryResponse> search(String keyword, ProductSort sort, int page) {
        return repository.search(keyword, sort, page);
    }

    // 검색 전체 개수 (U-21 페이지네이션용)
    public int countSearch(String keyword) {
        return repository.countBySearch(keyword);
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
