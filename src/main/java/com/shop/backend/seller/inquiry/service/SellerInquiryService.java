package com.shop.backend.seller.inquiry.service;

import com.shop.backend.seller.inquiry.dto.InquiryAnswerRequest;
import com.shop.backend.seller.inquiry.dto.SellerInquiryResponse;
import com.shop.backend.seller.inquiry.repository.SellerInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerInquiryService {

    private final SellerInquiryRepository sellerInquiryRepository;

    // 받은 문의 목록 조회 (S-12) — 내 상품에 달린 문의
    public List<SellerInquiryResponse> getMyInquiries(String sellerId, int page) {
        return sellerInquiryRepository.getMyInquiries(sellerId, page);
    }

    // 문의 답변 (S-06)
    public void answer(Long inquiryId, InquiryAnswerRequest req, String sellerId) {
        sellerInquiryRepository.answer(inquiryId, req.getAnswer());
    }
}
