package com.shop.backend.productinquiry.service;

import com.shop.backend.productinquiry.dto.ProductInquiryRequest;
import com.shop.backend.productinquiry.entity.ProductInquiry;
import com.shop.backend.productinquiry.repository.ProductInquiryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductInquiryService {

    private final ProductInquiryRepository repository;

    public ProductInquiryService(ProductInquiryRepository repository) {
        this.repository = repository;
    }

    // 상품 문의 목록 조회 (U-26)
    public Page<ProductInquiry> getList(Long productId, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("created_at").descending());
        return repository.findByProduct(productId, pageable);
    }

    // 상품 문의 작성 (U-25)
    public void create(Long productId, ProductInquiryRequest req, String writer) {
        // title/content를 정화 없이 그대로 저장 → 저장형 XSS 진단 포인트
        Boolean sec = req.getIs_secret();
        boolean isSecret = (sec != null) && sec;

        ProductInquiry inquiry = new ProductInquiry(
                productId, req.getTitle(), req.getContent(), isSecret, writer);  // writer = 토큰 id
        repository.save(inquiry);
    }
}
