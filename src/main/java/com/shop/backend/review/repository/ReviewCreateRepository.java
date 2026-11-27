package com.shop.backend.review.repository;

import com.shop.backend.review.dto.WritableReviewItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewCreateRepository {
    private static final String WRITABLE_ITEMS = """
            FROM order_item oi
            JOIN orders o ON o.order_id = oi.order_id
            WHERE o.user_id = ? AND oi.status = 'COMPLETED' AND o.status <> 'CANCELED'
              AND NOT EXISTS (SELECT 1 FROM review r WHERE r.order_item_id = oi.order_item_id)
            """;

    private final JdbcTemplate jdbcTemplate;

    public record WrittenReviewRow(
            int reviewId, int productId, String productName, int rating, String content, String createdAt
    ) {}

    public long countWrittenReviews(String userId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM review WHERE user_id = ?", Long.class, userId);
    }

    public List<WrittenReviewRow> findWrittenReviews(String userId, int pageSize, long offset) {
        String sql = """
                SELECT r.review_id, r.product_id, COALESCE(oi.product_name, p.name, '상품') AS product_name,
                       r.rating, r.content, r.created_at
                FROM review r
                LEFT JOIN order_item oi ON oi.order_item_id = r.order_item_id
                LEFT JOIN product p ON p.product_id = r.product_id
                WHERE r.user_id = ?
                ORDER BY r.created_at DESC, r.review_id DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, (rs, row) -> new WrittenReviewRow(
                rs.getInt("review_id"), rs.getInt("product_id"), rs.getString("product_name"),
                rs.getInt("rating"), rs.getString("content"), rs.getString("created_at")
        ), userId, pageSize, offset);
    }

    public long countWritableItems(String userId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) " + WRITABLE_ITEMS, Long.class, userId);
    }

    public List<WritableReviewItem> findWritableItems(String userId, int pageSize, long offset) {
        String sql = "SELECT oi.order_item_id, o.order_id, oi.product_name, o.ordered_at "
                + WRITABLE_ITEMS + "ORDER BY o.ordered_at DESC, oi.order_item_id DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, (rs, row) -> new WritableReviewItem(
                rs.getInt("order_item_id"),
                rs.getInt("order_id"),
                rs.getString("product_name"),
                rs.getString("ordered_at")
        ), userId, pageSize, offset);
    }

    public Map<String, Object> findOrderItem(Integer orderItemId) {
        String sql = """
                SELECT
                    oi.order_item_id,
                    oi.product_id,
                    oi.status,
                    oi.order_id,
                    o.status AS order_status,
                    o.user_id
                FROM order_item oi
                JOIN orders o ON oi.order_id = o.order_id
                WHERE oi.order_item_id = ?
                """;

        return jdbcTemplate.queryForMap(sql, orderItemId);
    }
}
