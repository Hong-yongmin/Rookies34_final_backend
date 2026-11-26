package com.shop.backend.seller.product.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.product.entity.Product;
import com.shop.backend.seller.product.dto.SellerProductRequest;
import com.shop.backend.seller.product.dto.SellerProductResponse;
import com.shop.backend.seller.product.service.SellerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/products")
public class SellerProductController {
    private final SellerProductService sellerProductService;
    private final SellerChecker sellerChecker;

    // 내 상품 목록 조회 (S-11)
    @GetMapping
    public Map<String, Object> myProducts(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page) {

        if (!sellerChecker.isSeller(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }

        Page<Product> result = sellerProductService.getMyProducts(id, status, keyword, page);
        List<SellerProductResponse> products =
                result.map(SellerProductResponse::new).getContent();

        return Map.of(
                "code", "ok",
                "num", result.getTotalElements(),
                "products", products
        );
    }

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

    // 상품 수정 (S-02)
    @PutMapping("/{productId}")
    public Map<String, Object> update(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long productId,
            @ModelAttribute SellerProductRequest req) {

        // 판매자 권한 확인
        if (!sellerChecker.isSeller(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }

        sellerProductService.update(productId, req, id);
        return Map.of("code", "ok");
    }
}
