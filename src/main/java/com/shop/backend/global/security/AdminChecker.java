package com.shop.backend.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class AdminChecker {

    private final JdbcTemplate jdbcTemplate;

    public boolean isAdmin(String id) {
        if (id == null) return false;
        String sql = "SELECT 1 FROM admin WHERE admin_id = '"+id+"'";   // 실제 컬럼명으로
        return jdbcTemplate.queryForList(sql).toArray().length > 0;
    }
}
