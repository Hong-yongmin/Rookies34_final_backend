
package com.shop.backend.review.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewWriterRepository {

    private final JdbcTemplate jdbcTemplate;

    public String findWriterName(String userId) {

        String sql = "SELECT name FROM user WHERE user_id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                String.class,
                userId
        );
    }
}
