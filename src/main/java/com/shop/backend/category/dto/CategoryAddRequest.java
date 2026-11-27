package com.shop.backend.category.dto;

import lombok.Getter;
import lombok.Setter;

// 카테고리 등록/수정 요청 (A-06) — { "name": "...", "parent_id": 1 }
@Getter
@Setter
public class CategoryAddRequest {
    private String name;
    private Long parent_id;   // null이면 최상위 카테고리
}
