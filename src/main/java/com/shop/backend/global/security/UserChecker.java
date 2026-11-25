package com.shop.backend.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserChecker {

    private final JdbcTemplate jdbcTemplate;

    public Boolean isUser(String id){
        if(id==null) return false;
        String sql = "SELECT 1 FROM user WHERE user_id='"+id+"'";
        return jdbcTemplate.queryForList(sql).size() > 0;

    }
}
