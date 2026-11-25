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
        String sql = "SELECT h.type, h.amount, h.reason, o.ordered_at FROM point_history p, orders o "
                + "WHERE h.member_id='"+id+"' AND h.order_id=o.order_id ";

        if( req != null) {
            String type;
            if ((type = req.getType()) != null) { // type 값이 있는 경우 조건 추가
                sql += "AND type='" + type + "' ";
            }
            Integer page;
            if ((page = req.getPage()) != null) { // page 값이 있는 경우 일부만 반환
                sql += "LIMIT 10 OFFSET " + (page - 1) * 10 + " ";
            }
        }

        return jdbcTemplate.query(sql,
                (rs, i)-> new History(
                        rs.getString("h.type"),
                        rs.getInt("h.amount"),
                        rs.getString("h.reason"),
                        rs.getDate("o.ordered_at")
                ));
    }
}
