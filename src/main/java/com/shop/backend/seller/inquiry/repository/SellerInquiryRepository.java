package com.shop.backend.seller.inquiry.repository;

import com.shop.backend.seller.inquiry.dto.SellerInquiryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SellerInquiryRepository {
    private final JdbcTemplate jdbcTemplate;

    // 문의 답변 (S-06) — 본인 상품 문의 확인 생략(수평권한 진단 포인트)
    public Boolean answer(Long inquiryId, String answer) {
        String sql = ("UPDATE product_inquiry SET answer='%s', answered_at=NOW() WHERE inquiry_id=%d")
                .formatted(answer, inquiryId);
        return jdbcTemplate.update(sql) > 0;
    }

    // 받은 문의 목록 (S-12) — 내 상품(seller_id)에 달린 문의, 상품명 JOIN
    public List<SellerInquiryResponse> getMyInquiries(String sellerId, int page) {
        String sql = ("SELECT i.inquiry_id, i.product_id, p.name AS product_name, i.title, i.content, "
                + "i.is_secret, i.answer, i.created_at "
                + "FROM product_inquiry i, product p "
                + "WHERE i.product_id=p.product_id AND p.seller_id='%s' "
                + "ORDER BY i.created_at DESC LIMIT 10 OFFSET %d")
                .formatted(sellerId, (page - 1) * 10);

        return jdbcTemplate.query(sql, (rs, i) ->
                SellerInquiryResponse.builder()
                        .inquiry_id(rs.getLong("inquiry_id"))
                        .product_id(rs.getLong("product_id"))
                        .product_name(rs.getString("product_name"))
                        .title(rs.getString("title"))
                        .content(rs.getString("content"))
                        .is_secret(rs.getBoolean("is_secret"))
                        .is_answered(rs.getString("answer") != null)
                        .created_at(rs.getString("created_at"))
                        .build());
    }
}
