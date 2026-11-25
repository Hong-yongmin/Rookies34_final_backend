package com.shop.backend.seller.product.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.seller.product.dto.SellerProductRequest;
import com.shop.backend.seller.product.service.SellerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/products")
public class SellerProductController {
    private final SellerProductService sellerProductService;
    private final SellerChecker sellerChecker;

    // 상품 등록 (S-02)
    @PostMapping
    public Map<String, Object> register(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @ModelAttribute SellerProductRequest req) {

        // 판매자 권한 확인
        if (!sellerChecker.isSeller(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }

        // 등록 (토큰의 id를 seller_id로)
        sellerProductService.register(req, id);
        return Map.of("code", "ok");
    }
}
