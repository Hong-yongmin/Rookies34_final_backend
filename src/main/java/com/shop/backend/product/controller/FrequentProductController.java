package com.shop.backend.product.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.UserChecker;
import com.shop.backend.product.dto.ProductSummaryResponse;
import com.shop.backend.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FrequentProductController {
    private static final int PAGE_SIZE = 20;

    private final ProductService productService;
    private final UserChecker userChecker;

    @GetMapping("/api/me/frequent-products")
    public Map<String, Object> getFrequentlyPurchasedProducts(
            @RequestAttribute(name = JwtAuthenticationFilter.MEMBER_ID, required = false) String userId,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        if (!userChecker.isUser(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "페이지 번호는 1 이상이어야 합니다.");
        }

        Page<ProductSummaryResponse> products = productService.getFrequentlyPurchasedProducts(
                userId, PageRequest.of(page - 1, PAGE_SIZE));
        return Map.of("code", "ok", "num", products.getTotalElements(), "products", products.getContent());
    }
}
