package com.shop.backend.category.controller;

import com.shop.backend.category.dto.CategoryAddRequest;
import com.shop.backend.category.service.CategoryService;
import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final AdminChecker adminChecker;

    // 카테고리 등록 (A-06)  — 권한: 관리자
    // POST /api/admin/categories   body: { "name":"채소", "parent_id":1 }  (parent_id 없으면 최상위)
    @PostMapping
    public Map<String, String> create(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @RequestBody CategoryAddRequest req) {

        if (!adminChecker.isAdmin(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }

        categoryService.create(req);
        return Map.of("code", "ok");
    }

    // 카테고리 수정 (A-06)  — 권한: 관리자
    // PUT /api/admin/categories/{categoryId}
    @PutMapping("/{categoryId}")
    public Map<String, String> update(
            @RequestAttribute(value = JwtAuthenticationFilter.MEMBER_ID, required = false) String id,
            @PathVariable Long categoryId,
            @RequestBody CategoryAddRequest req) {

        if (!adminChecker.isAdmin(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }

        categoryService.update(categoryId, req);
        return Map.of("code", "ok");
    }
}
