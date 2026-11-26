package com.shop.backend.seller.inquiry.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.productinquiry.entity.ProductInquiry;
import com.shop.backend.seller.inquiry.dto.InquiryAnswerRequest;
import com.shop.backend.seller.inquiry.dto.SellerInquiryResponse;
import com.shop.backend.seller.inquiry.service.SellerInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/product-inquiries")
public class SellerInquiryController {

    private final SellerInquiryService sellerInquiryService;
    private final SellerChecker sellerChecker;

    // 받은 문의 목록 조회 (S-12) — 내 상품에 달린 문의
    @GetMapping
    public Map<String, Object> myInquiries(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @RequestParam(defaultValue = "1") int page) {

        if (!sellerChecker.isSeller(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }

        Page<ProductInquiry> result = sellerInquiryService.getMyInquiries(id, page);
        List<SellerInquiryResponse> inquiries = result.getContent().stream()
                .map(i -> new SellerInquiryResponse(i, sellerInquiryService.productName(i.getProduct_id())))
                .toList();

        return Map.of(
                "code", "ok",
                "num", result.getTotalElements(),
                "inquiries", inquiries
        );
    }

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