package com.shop.backend.product.controller;

import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.product.dto.CurationRequest;
import com.shop.backend.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/curations")
public class AdminCurationController {

    private final AdminProductService adminProductService;
    private final AdminChecker adminChecker;

    // 베스트·세일·신상·특가 상품 지정/해제 (A-07)  — 권한: 관리자
    // PUT /api/admin/curations   body: { "product_ids":[1,2], "curation":"BEST" }  (curation null이면 해제)
    @PutMapping
    public Map<String, String> setCuration(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @RequestBody CurationRequest req) {

        if (!adminChecker.isAdmin(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }

        adminProductService.setCuration(req);
        return Map.of("code", "ok");
    }
}
