package com.shop.backend.product.service;

import com.shop.backend.product.dto.AdminProductDetailResponse;
import com.shop.backend.product.dto.AdminProductResponse;
import com.shop.backend.product.dto.CurationRequest;
import com.shop.backend.product.dto.ProductDecision;
import com.shop.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;

    // 관리자 상품 목록 조회 — status 선택 필터
    public List<AdminProductResponse> getList(String status, int page) {
        return productRepository.getAllForAdmin(status, page);
    }

    // 관리자 상품 상세 조회 — 상태 무관, 상세 이미지까지 세팅
    public AdminProductDetailResponse getDetail(Long productId) {
        AdminProductDetailResponse detail = productRepository.getDetailForAdmin(productId);
        if (detail == null) {
            throw new IllegalArgumentException("상품 없음: " + productId);
        }
        detail.setDetail_images(productRepository.getDetailImages(productId));
        return detail;
    }

    // A-07 큐레이션 지정/해제 — 여러 상품에 curation 설정(또는 null로 해제)
    public void setCuration(CurationRequest req) {
        if (req.getProduct_ids() == null || req.getProduct_ids().isEmpty()) {
            throw new IllegalArgumentException("product_ids가 필요합니다");
        }
        productRepository.updateCuration(req.getProduct_ids(), req.getCuration());
    }

    // A-05 상품 승인/반려/철회 — decision을 status로 바꿔 UPDATE
    public void decide(Long productId, ProductDecision decision) {
        int updated = productRepository.updateStatus(productId, decision.toStatus());
        if (updated == 0) {
            throw new IllegalArgumentException("상품 없음: " + productId);
        }
    }
}
