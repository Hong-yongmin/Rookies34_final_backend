package com.shop.backend.productinquiry.repository;

import com.shop.backend.productinquiry.dto.MyInquiryDetailResponse;
import com.shop.backend.productinquiry.dto.MyInquiryResponse;
import com.shop.backend.productinquiry.dto.ProductInquiryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductInquiryRepository {
    private final JdbcTemplate jdbcTemplate;

    // 문의 작성 (U-25)
    public void insert(Long productId, String writer, String title, String content, boolean isSecret) {
        String sql = "INSERT INTO product_inquiry (product_id, user_id, title, content, is_secret) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, productId, writer, title, content, isSecret);
    }

    // 특정 상품의 문의 목록 (U-26)
    public List<ProductInquiryResponse> getByProduct(Long productId, int page, String userId) {
        String sql = "SELECT inquiry_id, title, user_id, created_at, is_secret, content, answer "
                + "FROM product_inquiry WHERE product_id=? ORDER BY created_at DESC LIMIT 10 OFFSET ?";

        return jdbcTemplate.query(sql, (rs, i) -> {
            String writer = rs.getString("user_id");
            boolean secret = rs.getBoolean("is_secret");
            boolean visible = !secret || (userId != null && userId.equals(writer));
            String answer = rs.getString("answer");
            return ProductInquiryResponse.builder()
                        .inquiry_id(rs.getLong("inquiry_id"))
                        .title(visible ? rs.getString("title") : "비밀글입니다.")
                        .writer(visible ? writer : "-")
                        .created_at(rs.getString("created_at"))
                        .is_secret(secret)
                        .is_answered(answer != null)
                        .content(visible ? rs.getString("content") : "접근 권한이 없습니다.")
                        .answer(visible ? answer : null)
                        .build();
        }, productId, (page - 1L) * 10);
    }

    // 내 문의 내역 (U-53) — 내가 쓴 문의 + 상품명 JOIN
    public List<MyInquiryResponse> getMyInquiries(String userId, int page) {
        String sql = "SELECT i.inquiry_id, i.product_id, p.name AS product_name, "
                + "i.title, i.is_secret, i.answer, i.created_at "
                + "FROM product_inquiry i, product p "
                + "WHERE i.product_id = p.product_id AND i.user_id = ? "
                + "ORDER BY i.created_at DESC LIMIT 10 OFFSET ?";

        return jdbcTemplate.query(sql, (rs, i) ->
                MyInquiryResponse.builder()
                        .inquiry_id(rs.getLong("inquiry_id"))
                        .product_id(rs.getLong("product_id"))
                        .product_name(rs.getString("product_name"))
                        .title(rs.getString("title"))
                        .is_secret(rs.getBoolean("is_secret"))
                        .is_answered(rs.getString("answer") != null)
                        .created_at(rs.getString("created_at"))
                        .build(), userId, (page - 1L) * 10);
    }

    // 내 문의 상세 (목록에서 클릭)
    public MyInquiryDetailResponse getMyInquiryDetail(Long inquiryId, String userId) {
        String sql = "SELECT i.inquiry_id, i.product_id, p.name AS product_name, "
                + "i.title, i.content, i.is_secret, i.answer, i.answered_at, i.created_at "
                + "FROM product_inquiry i, product p "
                + "WHERE i.product_id = p.product_id AND i.inquiry_id = ? AND i.user_id = ?";

        List<MyInquiryDetailResponse> list = jdbcTemplate.query(sql, (rs, i) ->
                MyInquiryDetailResponse.builder()
                        .inquiry_id(rs.getLong("inquiry_id"))
                        .product_id(rs.getLong("product_id"))
                        .product_name(rs.getString("product_name"))
                        .title(rs.getString("title"))
                        .content(rs.getString("content"))
                        .is_secret(rs.getBoolean("is_secret"))
                        .is_answered(rs.getString("answer") != null)
                        .answer(rs.getString("answer"))
                        .answered_at(rs.getString("answered_at"))
                        .created_at(rs.getString("created_at"))
                        .build(), inquiryId, userId);

        return list.isEmpty() ? null : list.get(0);
    }
}
