package com.shop.backend.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellerChecker {
    private final JdbcTemplate jdbcTemplate;

    public Boolean isSeller(String id){
        if(id==null) return false;
        String sql = "SELECT 1 FROM seller WHERE seller_id='"+id+"' AND status='APPROVED'";
        return jdbcTemplate.queryForList(sql).size() > 0;
    }
}
