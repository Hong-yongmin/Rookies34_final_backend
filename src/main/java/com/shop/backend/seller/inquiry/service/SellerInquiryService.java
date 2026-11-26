package com.shop.backend.seller.inquiry.service;

import com.shop.backend.product.entity.Product;
import com.shop.backend.product.repository.ProductRepository;
import com.shop.backend.productinquiry.entity.ProductInquiry;
import com.shop.backend.productinquiry.repository.ProductInquiryRepository;
import com.shop.backend.seller.inquiry.dto.InquiryAnswerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerInquiryService {

    private final ProductInquiryRepository inquiryRepository;   // 기존 거 재사용
    private final ProductRepository productRepository;          // 상품명 조회용

    // 받은 문의 목록 조회 (S-12) — 내 상품에 달린 문의
    public Page<ProductInquiry> getMyInquiries(String sellerId, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return inquiryRepository.findBySellerProducts(sellerId, pageable);
    }

    // 상품명 조회 (응답에 product_name 담기용)
    public String productName(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getName)
                .orElse("");
    }

    // 문의 답변 (S-06)
    public void answer(Long inquiryId, InquiryAnswerRequest req, String sellerId) {
        ProductInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음: " + inquiryId));

        // 본인 상품 문의 확인 생략 → 타 판매자 상품 문의에도 답변 가능 (수평권한 진단 포인트)

        inquiry.writeAnswer(req.getAnswer());
        inquiryRepository.save(inquiry);
    }
}