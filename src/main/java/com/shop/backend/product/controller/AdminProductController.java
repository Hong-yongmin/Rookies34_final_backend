package com.shop.backend.product.controller;

import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.product.dto.AdminProductDetailResponse;
import com.shop.backend.product.dto.AdminProductResponse;
import com.shop.backend.product.dto.ProductDecisionRequest;
import com.shop.backend.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final AdminChecker adminChecker;

    // 관리자 상품 목록 조회  — 권한: 관리자
    // GET /api/admin/products?status=PENDING&page=1   (status 없으면 전체)
    @GetMapping
    public Map<String, Object> list(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page) {

        if (!adminChecker.isAdmin(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }

        List<AdminProductResponse> products = adminProductService.getList(status, page);

        return Map.of(
                "code", "ok",
                "num", products.size(),
                "products", products
        );
    }

    // 관리자 상품 상세 조회  — 권한: 관리자 (승인 전 상품도 조회 가능)
    // GET /api/admin/products/{productId}
    @GetMapping("/{productId}")
    public Map<String, Object> detail(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long productId) {

        if (!adminChecker.isAdmin(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }

        AdminProductDetailResponse product = adminProductService.getDetail(productId);
        return Map.of("code", "ok", "product", product);
    }

    // 판매자 상품 승인/반려/철회 (A-05)  — 권한: 관리자
    // PATCH /api/admin/products/{productId}/approval   body: { "decision": "APPROVE|REJECT|WITHDRAW" }
    @PatchMapping("/{productId}/approval")
    public Map<String, String> decide(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long productId,
            @RequestBody ProductDecisionRequest req) {

        if (!adminChecker.isAdmin(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }

        adminProductService.decide(productId, req.getDecision());
        return Map.of("code", "ok");
    }
}
