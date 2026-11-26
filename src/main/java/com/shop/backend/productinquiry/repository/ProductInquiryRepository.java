package com.shop.backend.productinquiry.repository;

import com.shop.backend.productinquiry.dto.ProductInquiryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductInquiryRepository {
    private final JdbcTemplate jdbcTemplate;

    // 문의 작성 (U-25) — title/content 정화 없이 저장 (저장형 XSS 진단 포인트)
    public void insert(Long productId, String writer, String title, String content, boolean isSecret) {
        String sql = ("INSERT INTO product_inquiry (product_id, user_id, title, content, is_secret) "
                + "VALUES (%d, '%s', '%s', '%s', %d)")
                .formatted(productId, writer, title, content, isSecret ? 1 : 0);
        jdbcTemplate.update(sql);
    }

    // 특정 상품의 문의 목록 (U-26) — 마스킹/비밀글 보호 없음(진단 포인트)
    public List<ProductInquiryResponse> getByProduct(Long productId, int page) {
        String sql = ("SELECT inquiry_id, title, user_id, created_at, is_secret, content, answer "
                + "FROM product_inquiry WHERE product_id=%d ORDER BY created_at DESC LIMIT 10 OFFSET %d")
                .formatted(productId, (page - 1) * 10);

        return jdbcTemplate.query(sql, (rs, i) ->
                ProductInquiryResponse.builder()
                        .inquiry_id(rs.getLong("inquiry_id"))
                        .title(rs.getString("title"))
                        .writer(rs.getString("user_id"))        // user_id → writer (마스킹 X)
                        .created_at(rs.getString("created_at"))
                        .is_secret(rs.getBoolean("is_secret"))
                        .is_answered(rs.getString("answer") != null)
                        .content(rs.getString("content"))
                        .answer(rs.getString("answer"))
                        .build());
    }
}
