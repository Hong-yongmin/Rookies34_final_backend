package com.shop.backend.seller.product.repository;

import com.shop.backend.global.common.CommonFunction;
import com.shop.backend.seller.product.dto.SellerProductRequest;
import com.shop.backend.seller.product.dto.SellerProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SellerProductRepository {
    private final JdbcTemplate jdbcTemplate;
    private final CommonFunction cf;

    // 상품 등록 (S-02) — 생성된 product_id 반환
    public Long insertProduct(SellerProductRequest req, String sellerId, String thumbnailUrl) {
        int discountRate = (req.getDiscount_rate() != null) ? req.getDiscount_rate() : 0;
        String storageType = (req.getStorage_type() != null) ? req.getStorage_type().name() : "ROOM";

        String sql = ("INSERT INTO product (seller_id, category_id, name, price, discount_rate, stock, "
                + "storage_type, origin, description, thumbnail, status) "
                + "VALUES ('%s', %d, '%s', %d, %d, %d, '%s', %s, %s, '%s', 'PENDING')").formatted(
                sellerId, req.getCategory_id(), req.getName(), req.getPrice(), discountRate, req.getStock(),
                storageType, cf.q(req.getOrigin()), cf.q(req.getDescription()), thumbnailUrl);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(con -> con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), keyHolder);
        if (rows <= 0 || keyHolder.getKey() == null) {
            return null;
        }
        return keyHolder.getKey().longValue();
    }

    // 상세 이미지 저장 (S-02) — product_image 테이블
    public void insertImages(Long productId, List<String> urls) {
        for (int i = 0; i < urls.size(); i++) {
            String sql = ("INSERT INTO product_image (product_id, url, sort_order) VALUES (%d, '%s', %d)")
                    .formatted(productId, urls.get(i), i);
            jdbcTemplate.update(sql);
        }
    }

    // 상품 수정 (S-02)
    public Boolean updateProduct(Long productId, SellerProductRequest req, String thumbnailUrl) {
        int discountRate = (req.getDiscount_rate() != null) ? req.getDiscount_rate() : 0;
        String storageType = (req.getStorage_type() != null) ? req.getStorage_type().name() : "ROOM";

        String sql = ("UPDATE product SET category_id=%d, name='%s', price=%d, discount_rate=%d, stock=%d, "
                + "storage_type='%s', origin=%s, description=%s").formatted(
                req.getCategory_id(), req.getName(), req.getPrice(), discountRate, req.getStock(),
                storageType, cf.q(req.getOrigin()), cf.q(req.getDescription()));
        if (thumbnailUrl != null) {   // 새 썸네일이 있을 때만 교체
            sql += ", thumbnail='%s'".formatted(thumbnailUrl);
        }
        sql += " WHERE product_id=%d".formatted(productId);

        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    // 수정 시 기존 이미지 삭제 (S-02)
    public void deleteImages(Long productId) {
        jdbcTemplate.update("DELETE FROM product_image WHERE product_id=%d".formatted(productId));
    }

    // 내 상품 목록 (S-11)
    public List<SellerProductResponse> getMyProducts(String sellerId, String status, String keyword, int page) {
        String sql = ("SELECT product_id, name, price, discount_rate, stock, status, thumbnail, created_at "
                + "FROM product WHERE seller_id='%s' ").formatted(sellerId);
        if (status != null) {
            sql += "AND status='%s' ".formatted(status);
        }
        if (keyword != null) {
            sql += "AND name LIKE '%%%s%%' ".formatted(keyword);
        }
        sql += "ORDER BY created_at DESC LIMIT 20 OFFSET %d".formatted((page - 1) * 20);

        return jdbcTemplate.query(sql, (rs, i) ->
                SellerProductResponse.builder()
                        .product_id(rs.getLong("product_id"))
                        .name(rs.getString("name"))
                        .price(rs.getInt("price"))
                        .discount_rate(rs.getInt("discount_rate"))
                        .stock(rs.getInt("stock"))
                        .status(rs.getString("status"))
                        .thumbnail(rs.getString("thumbnail"))
                        .created_at(rs.getString("created_at"))
                        .build());
    }

    // 상품 삭제
    public Boolean deleteProduct(Integer product_id){
        return jdbcTemplate.update("DELETE FROM product WHERE product_id=%d".formatted(product_id)) > 0;
    }
}
