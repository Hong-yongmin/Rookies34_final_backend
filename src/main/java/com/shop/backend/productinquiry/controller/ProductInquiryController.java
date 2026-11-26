package com.shop.backend.productinquiry.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.UserChecker;
import com.shop.backend.productinquiry.dto.MyInquiryDetailResponse;
import com.shop.backend.productinquiry.dto.MyInquiryResponse;
import com.shop.backend.productinquiry.dto.ProductInquiryRequest;
import com.shop.backend.productinquiry.dto.ProductInquiryResponse;
import com.shop.backend.productinquiry.service.ProductInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductInquiryController {

    private final ProductInquiryService service;
    private final UserChecker userChecker;

    // 상품 문의 작성 (U-25)  — 권한: 로그인
    // POST /api/products/{productId}/inquiries
    @PostMapping("/api/products/{productId}/inquiries")
    public Map<String, String> create(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long productId,
            @RequestBody ProductInquiryRequest req) {

        if (!userChecker.isUser(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }

        service.create(productId, req, id);   // 토큰 id를 작성자로
        return Map.of("code", "ok");
    }

    // 상품 문의 목록 조회 (U-26)  — 권한: 비로그인 가능
    // GET /api/products/{productId}/inquiries?page=1
    @GetMapping("/api/products/{productId}/inquiries")
    public Map<String, Object> list(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page) {

        List<ProductInquiryResponse> inquiries = service.getList(productId, page);

        return Map.of(
                "code", "ok",
                "num", inquiries.size(),
                "inquiries", inquiries
        );
    }

    // 내 문의 내역 조회 (U-53)  — 권한: 로그인
    // GET /api/me/product-inquiries?page=1
    @GetMapping("/api/me/product-inquiries")
    public Map<String, Object> myInquiries(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @RequestParam(defaultValue = "1") int page) {

        if (!userChecker.isUser(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }

        List<MyInquiryResponse> inquiries = service.getMyList(id, page);

        return Map.of(
                "code", "ok",
                "num", inquiries.size(),
                "inquiries", inquiries
        );
    }

    // 내 문의 상세 조회 (U-53 상세)  — 권한: 로그인
    // GET /api/me/product-inquiries/{inquiryId}
    @GetMapping("/api/me/product-inquiries/{inquiryId}")
    public Map<String, Object> myInquiryDetail(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long inquiryId) {

        if (!userChecker.isUser(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }

        MyInquiryDetailResponse inquiry = service.getDetail(inquiryId);

        return Map.of(
                "code", "ok",
                "inquiry", inquiry
        );
    }
}
