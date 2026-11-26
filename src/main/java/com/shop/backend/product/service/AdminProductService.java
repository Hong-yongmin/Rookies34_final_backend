package com.shop.backend.product.service;

import com.shop.backend.product.dto.ProductDecision;
import com.shop.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;

    // A-05 상품 승인/반려/철회 — decision을 status로 바꿔 UPDATE
    public void decide(Long productId, ProductDecision decision) {
        int updated = productRepository.updateStatus(productId, decision.toStatus());
        if (updated == 0) {
            throw new IllegalArgumentException("상품 없음: " + productId);
        }
    }
}
