package com.shop.backend.point.repository;

import com.shop.backend.point.dto.History;
import com.shop.backend.point.dto.PointRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<History> getHistories(String id, PointRequest req){
        /*
         * 사용자의 적립금 내역 조회
         */
        String sql = "SELECT type, amount, reason, created_at FROM point_history "
                + "WHERE user_id='"+id+"' ";

        if( req != null) {
            String type;
            if ((type = req.getType()) != null) { // type 값이 있는 경우 조건 추가
                sql += "AND type='" + type + "' ";
            }
            sql += "ORDER BY created_at DESC "; // 최신 내역부터
            Integer page;
            if ((page = req.getPage()) != null && page > 0) { // page 값이 있는 경우 일부만 반환
                sql += "LIMIT 10 OFFSET " + (page - 1) * 10 + " ";
            }
        }

        return jdbcTemplate.query(sql,
                (rs, i)-> History.builder()
                        .type(rs.getString("type"))
                        .amount(rs.getInt("amount"))
                        .reason(rs.getString("reason"))
                        .created_at(rs.getTimestamp("created_at")).build());
    }

    public int getBalance(String id){
        /*
         * 사용자의 현재 적립금 잔액 조회
         * 잔액은 user 테이블의 point 컬럼에 저장
         */
        String sql = "SELECT point FROM user WHERE user_id='"+id+"'";
        Integer point = jdbcTemplate.queryForObject(sql, Integer.class);
        return point == null ? 0 : point;
    }
}
