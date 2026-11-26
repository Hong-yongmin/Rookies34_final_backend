package com.shop.backend.review.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReviewCreateRepository {

    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> findOrderItem(Integer orderItemId) {
        String sql = """
                SELECT
                    oi.order_item_id,
                    oi.product_id,
                    oi.status,
                    oi.order_id,
                    o.user_id
                FROM order_item oi
                JOIN orders o ON oi.order_id = o.order_id
                WHERE oi.order_item_id = ?
                """;

        return jdbcTemplate.queryForMap(sql, orderItemId);
    }
}