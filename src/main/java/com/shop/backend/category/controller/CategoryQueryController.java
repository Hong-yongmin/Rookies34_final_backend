package com.shop.backend.category.controller;

import com.shop.backend.category.dto.CategoryResponse;
import com.shop.backend.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryQueryController {

    private final CategoryService categoryService;

    // 카테고리 전체 조회  — 권한: 공개(비로그인 가능). 프론트가 parent_id로 트리 조립
    // GET /api/categories
    @GetMapping
    public Map<String, Object> list() {
        List<CategoryResponse> categories = categoryService.getList();
        return Map.of(
                "code", "ok",
                "num", categories.size(),
                "categories", categories
        );
    }
}
