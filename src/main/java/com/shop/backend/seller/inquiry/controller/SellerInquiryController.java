package com.shop.backend.seller.inquiry.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.seller.inquiry.dto.InquiryAnswerRequest;
import com.shop.backend.seller.inquiry.service.SellerInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/product-inquiries")
public class SellerInquiryController {

    private final SellerInquiryService sellerInquiryService;
    private final SellerChecker sellerChecker;

    // 문의 답변 (S-06)
    @PostMapping("/{inquiryId}/answer")
    public Map<String, Object> answer(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long inquiryId,
            @RequestBody InquiryAnswerRequest req) {

        if (!sellerChecker.isSeller(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }

        sellerInquiryService.answer(inquiryId, req, id);
        return Map.of("code", "ok");
    }
}