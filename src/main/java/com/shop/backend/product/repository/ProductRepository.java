package com.shop.backend.product.repository;

import com.shop.backend.product.dto.ProductDetailResponse;
import com.shop.backend.product.dto.ProductSort;
import com.shop.backend.product.dto.ProductSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate;

    // 정렬(ProductSort) → ORDER BY 절
    private String orderBy(ProductSort sort) {
        if (sort == null) return "ORDER BY created_at DESC";
        return switch (sort) {
            case PRICE_ASC      -> "ORDER BY price ASC";
            case PRICE_DESC     -> "ORDER BY price DESC";
            case NEW, RECOMMEND -> "ORDER BY created_at DESC";
        };
    }

    // 목록/검색 공통 RowMapper
    private ProductSummaryResponse mapSummary(ResultSet rs, int i) throws SQLException {
        return ProductSummaryResponse.builder()
                .product_id(rs.getLong("product_id"))
                .name(rs.getString("name"))
                .category_id(rs.getLong("category_id"))
                .price(rs.getInt("price"))
                .discount_rate(rs.getInt("discount_rate"))
                .thumbnail(rs.getString("thumbnail_url"))
                .build();
    }

    // 카테고리별 목록 (U-20)
    public List<ProductSummaryResponse> getListByCategory(Long categoryId, ProductSort sort, int page) {
        String sql = ("SELECT product_id, name, category_id, price, discount_rate, thumbnail_url "
                + "FROM product WHERE category_id=%d %s LIMIT 20 OFFSET %d")
                .formatted(categoryId, orderBy(sort), (page - 1) * 20);
        return jdbcTemplate.query(sql, this::mapSummary);
    }

    // 큐레이션별 목록 (U-20) — curation enum
    public List<ProductSummaryResponse> getListByCuration(String curation, ProductSort sort, int page) {
        String sql = ("SELECT product_id, name, category_id, price, discount_rate, thumbnail_url "
                + "FROM product WHERE curation='%s' %s LIMIT 20 OFFSET %d")
                .formatted(curation, orderBy(sort), (page - 1) * 20);
        return jdbcTemplate.query(sql, this::mapSummary);
    }

    // 검색 (U-21) — 상품명 LIKE
    public List<ProductSummaryResponse> search(String keyword, ProductSort sort, int page) {
        String sql = ("SELECT product_id, name, category_id, price, discount_rate, thumbnail_url "
                + "FROM product WHERE name LIKE '%%%s%%' %s LIMIT 20 OFFSET %d")
                .formatted(keyword, orderBy(sort), (page - 1) * 20);
        return jdbcTemplate.query(sql, this::mapSummary);
    }

    // 상세 (U-22) — 없으면 null
    public ProductDetailResponse getDetail(Long productId) {
        String sql = ("SELECT product_id, name, category_id, price, discount_rate, stock, "
                + "storage_type, origin, description, thumbnail_url "
                + "FROM product WHERE product_id=%d").formatted(productId);
        List<ProductDetailResponse> list = jdbcTemplate.query(sql, (rs, i) ->
                ProductDetailResponse.builder()
                        .product_id(rs.getLong("product_id"))
                        .name(rs.getString("name"))
                        .category_id(rs.getLong("category_id"))
                        .price(rs.getInt("price"))
                        .discount_rate(rs.getInt("discount_rate"))
                        .stock(rs.getInt("stock"))
                        .storage_type(rs.getString("storage_type"))
                        .origin(rs.getString("origin"))
                        .description(rs.getString("description"))
                        .thumbnail(rs.getString("thumbnail_url"))
                        .build());
        return list.isEmpty() ? null : list.get(0);
    }

    // 상세 이미지 목록 (U-22) — product_image 테이블
    public List<String> getDetailImages(Long productId) {
        String sql = "SELECT url FROM product_image WHERE product_id=%d ORDER BY sort_order".formatted(productId);
        return jdbcTemplate.query(sql, (rs, i) -> rs.getString("url"));
    }

    // 상품명 조회 (S-12 등) — 없으면 ""
    public String getProductName(Long productId) {
        String sql = "SELECT name FROM product WHERE product_id=%d".formatted(productId);
        List<String> list = jdbcTemplate.query(sql, (rs, i) -> rs.getString("name"));
        return list.isEmpty() ? "" : list.get(0);
    }
}
