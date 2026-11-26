package com.shop.backend.coupon.repository;

import com.shop.backend.coupon.dto.Coupons;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCouponRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Coupons> getCoupons(String id, String status){
        /*
         * 사용자 소유 쿠폰 리스트 확인
         */
        String sql = """
                SELECT c.*, uc.status AS user_coupon_status
                FROM coupon c
                JOIN user_coupon uc ON uc.coupon_id = c.coupon_id
                WHERE uc.user_id = """ + id;
        if(status != null ){
            sql += " AND uc.status='%s'".formatted(status);
        }

        return jdbcTemplate.query(sql,
                (rs, i) -> new Coupons(
                        rs.getLong("coupon_id"),
                        rs.getString("coupon_name"),
                        rs.getString("discount_type"),
                        rs.getInt("discount_value"),
                        rs.getInt("min_order_amount"),
                        rs.getString("valid_from"),
                        rs.getString("valid_to"),
                        rs.getString("user_coupon_status"))
                );
    }
}
