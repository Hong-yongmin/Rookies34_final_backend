
package com.shop.backend.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminChecker {

    private final JdbcTemplate jdbcTemplate;

    public Boolean isAdmin(String id) {

        if (id == null) {
            return false;
        }

        String sql = "SELECT EXISTS(SELECT 1 FROM admin WHERE admin_id = ?)";

        Boolean result = jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                id
        );

        return Boolean.TRUE.equals(result);
    }
}
