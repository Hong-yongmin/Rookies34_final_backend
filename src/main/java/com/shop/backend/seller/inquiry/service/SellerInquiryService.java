package com.shop.backend.seller.inquiry.service;

import com.shop.backend.productinquiry.entity.ProductInquiry;
import com.shop.backend.productinquiry.repository.ProductInquiryRepository;
import com.shop.backend.seller.inquiry.dto.InquiryAnswerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerInquiryService {

    private final ProductInquiryRepository inquiryRepository;   // 기존 거 재사용

    // 문의 답변 (S-06)
    public void answer(Long inquiryId, InquiryAnswerRequest req, String sellerId) {
        ProductInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음: " + inquiryId));

        // 본인 상품 문의 확인 생략 → 타 판매자 상품 문의에도 답변 가능 (수평권한 진단 포인트)

        inquiry.writeAnswer(req.getAnswer());
        inquiryRepository.save(inquiry);
    }
}