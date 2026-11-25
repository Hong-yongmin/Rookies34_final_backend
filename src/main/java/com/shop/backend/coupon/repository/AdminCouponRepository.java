package com.shop.backend.coupon.repository;

import com.shop.backend.coupon.dto.GenerateCouponRequest;
import com.shop.backend.coupon.dto.GrantCouponRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminCouponRepository {
    private final JdbcTemplate jdbcTemplate;

    public Boolean isValidCoupon(Long coupon_id){
        /*
         * 현재 유효한 쿠폰인지 확인
         * 유효하면 true 반환
         */
        String sql = "SELECT 1 FROM coupon WHERE coupon_id=" + coupon_id
                + " AND (CURDATE() BETWEEN valid_from AND valid_to)";
        return jdbcTemplate.queryForList(sql).toArray().length > 0;
    }
    public Boolean generateCoupon(GenerateCouponRequest req){
        /*
         * 쿠폰 등록
         * 쿼리가 문제없이 실행되면 true 반환
         */
        String sql = "INSERT INTO coupon (coupon_name, discount_type, discount_value, min_order_amount, valid_from, valid_to) "
                + "VALUES ('" + req.getCoupon_name() + "',"
                + "'" + req.getDiscount_type() + "',"
                + req.getDiscount_value() + ","
                + req.getMin_order_amount() + ","
                + "'" + req.getValid_from() + "',"
                + "'" + req.getValid_to() + "')";
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public Boolean grantCoupon(Long coupon_id, List<String> target_user_ids){
        /*
         * 쿠폰 지급
         * 쿼리가 문제없이 실행되면 true 반환
         * status는 AVAILABLE로 고정 -> 쿠폰의 유효성은 호출 전에 검사
         */
        String sql;
        int rows;
        for(int i=0; i<target_user_ids.toArray().length; i++){
            sql = "INSERT INTO user_coupon (user_id, coupon_id, status) "
                    + "VALUES ('"+target_user_ids.get(i)+"', '"+coupon_id+"', 'AVAILABLE')";
            rows = jdbcTemplate.update(sql);
            if (rows <= 0){
                return false;
            }
        }
        return true;
    }

}
