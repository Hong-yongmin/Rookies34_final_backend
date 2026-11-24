package com.shop.backend.member.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SellerRepository {
    private final JdbcTemplate jdbcTemplate;
}
