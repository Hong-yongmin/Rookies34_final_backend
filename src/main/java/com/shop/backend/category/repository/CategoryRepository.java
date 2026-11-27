package com.shop.backend.category.repository;

import com.shop.backend.category.dto.CategoryResponse;
import com.shop.backend.global.common.CommonFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CommonFunction cf;

    // 전체 카테고리 조회 — flat 목록 (프론트가 트리로 조립)
    public List<CategoryResponse> getAll() {
        String sql = "SELECT category_id, parent_id, name FROM category ORDER BY parent_id, category_id";
        return jdbcTemplate.query(sql, (rs, i) -> {
            // parent_id는 NULL 가능 → getObject로 받아 null 보존 (getLong은 NULL을 0으로 바꿔버림)
            Number parent = (Number) rs.getObject("parent_id");
            return CategoryResponse.builder()
                    .category_id(rs.getLong("category_id"))
                    .parent_id(parent == null ? null : parent.longValue())
                    .name(rs.getString("name"))
                    .build();
        });
    }

    // 카테고리 등록 (A-06) — parent_id null이면 최상위
    public void insert(String name, Long parentId) {
        String parent = (parentId == null) ? "NULL" : parentId.toString();
        String sql = "INSERT INTO category (name, parent_id) VALUES (%s, %s)"
                .formatted(cf.q(name), parent);   // cf.q: 'name' 또는 NULL (정화 없음 = SQLi 진단 포인트)
        jdbcTemplate.update(sql);
    }

    // 카테고리 수정 (A-06) — 영향 행 수 반환(0이면 해당 카테고리 없음)
    public int update(Long categoryId, String name, Long parentId) {
        String parent = (parentId == null) ? "NULL" : parentId.toString();
        String sql = "UPDATE category SET name=%s, parent_id=%s WHERE category_id=%d"
                .formatted(cf.q(name), parent, categoryId);
        return jdbcTemplate.update(sql);
    }
}
