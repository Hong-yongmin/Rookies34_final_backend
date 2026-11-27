package com.shop.backend.category.dto;

import lombok.Builder;
import lombok.Getter;

// 카테고리 조회 응답 — flat 목록(프론트가 parent_id로 트리 조립)
@Getter
@Builder
public class CategoryResponse {
    private Long category_id;
    private Long parent_id;   // null이면 최상위
    private String name;
}
