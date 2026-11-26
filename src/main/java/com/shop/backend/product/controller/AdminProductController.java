package com.shop.backend.product.controller;

import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.product.dto.ProductDecisionRequest;
import com.shop.backend.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final AdminChecker adminChecker;

    // 판매자 상품 승인/반려/철회 (A-05)  — 권한: 관리자
    // PATCH /api/admin/products/{productId}   body: { "decision": "APPROVE|REJECT|WITHDRAW" }
    @PatchMapping("/{productId}")
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
