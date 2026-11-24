package com.shop.backend.product.service;

import com.shop.backend.product.dto.ListType;
import com.shop.backend.product.dto.ProductDetailResponse;
import com.shop.backend.product.dto.ProductSort;
import com.shop.backend.product.entity.Product;
import com.shop.backend.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // 목록 조회 (U-20)
    public Page<Product> getList(ListType listType, Long categoryId, ProductSort sort, int page) {
        Pageable pageable = PageRequest.of(page - 1, 20, toSort(sort));  // page는 1부터
        return switch (listType) {
            case CATEGORY -> {
                if (categoryId == null) {
                    throw new IllegalArgumentException("category_id는 CATEGORY 조회 시 필수입니다.");
                }
                yield repository.findByCategory(categoryId, pageable);
            }
            case BEST    -> repository.findByBestTrue(pageable);
            case SALE    -> repository.findBySaleTrue(pageable);
            case NEW     -> repository.findNewArrivals(pageable);
            case BENEFIT -> repository.findByBenefitTrue(pageable);
        };
    }

    // 검색 (U-21)
    public Page<Product> search(String keyword, ProductSort sort, int page) {
        Pageable pageable = PageRequest.of(page - 1, 20, toSort(sort));
        return repository.findByNameContaining(keyword, pageable);
    }

    // 상세 (U-22)
    public ProductDetailResponse getDetail(Long productId) {
        Product p = repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));
        return new ProductDetailResponse(p);
    }

    // 정렬 조건 변환
    private Sort toSort(ProductSort sort) {
        if (sort == null) {
            return Sort.by("created_at").descending();
        }
        return switch (sort) {
            case PRICE_ASC  -> Sort.by("price").ascending();
            case PRICE_DESC -> Sort.by("price").descending();
            case NEW        -> Sort.by("created_at").descending();
            case RECOMMEND  -> Sort.by("created_at").descending();
        };
    }
}
