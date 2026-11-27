package com.shop.backend.category.service;

import com.shop.backend.category.dto.CategoryAddRequest;
import com.shop.backend.category.dto.CategoryResponse;
import com.shop.backend.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 전체 조회 (flat)
    public List<CategoryResponse> getList() {
        return categoryRepository.getAll();
    }

    // 카테고리 등록 (A-06)
    public void create(CategoryAddRequest req) {
        categoryRepository.insert(req.getName(), req.getParent_id());
    }

    // 카테고리 수정 (A-06) — 없으면 예외
    public void update(Long categoryId, CategoryAddRequest req) {
        int updated = categoryRepository.update(categoryId, req.getName(), req.getParent_id());
        if (updated == 0) {
            throw new IllegalArgumentException("카테고리 없음: " + categoryId);
        }
    }
}
