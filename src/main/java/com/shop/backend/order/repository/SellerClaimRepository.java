package com.shop.backend.order.repository;

import com.shop.backend.order.dto.Claim;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SellerClaimRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Claim> getClaims(String seller_id, Integer page, String status){
        /*
        * 판매자의 취소/반품/교환 내역 조회
        */
        String sql = "SELECT * FROM claim WHERE order_item_id IN (SELECT order_item_id FROM order_item WHERE seller_id='%s')".formatted(seller_id);

        if(status != null){
            sql += " AND status='%s'".formatted(status);
        }
        if(page != null && page > 0){
            sql += " LIMIT 10 OFFSET %d".formatted((page-1)*10);
        }


        return jdbcTemplate.query(sql,
                (rs, i)->Claim.builder()
                        .claim_id(rs.getInt("claim_id"))
                        .order_item_id(rs.getInt("order_item_id"))
                        .request_type(rs.getString("request_type"))
                        .reason_type(rs.getString("reason_type"))
                        .status(rs.getString("status"))
                        .created_at(rs.getString("requested_at"))   // claim 테이블 컬럼명은 requested_at
                        .build());
    }

    public Boolean acceptClaim(Integer claim_id, String decision){
        /*
         * 판매자 취소/환불/교환 신청 관리
         */
        String sql = "UPDATE claim SET status='%s' WHERE claim_id=%d".formatted(decision, claim_id);
        return jdbcTemplate.update(sql) > 0;
    }

    public Integer getOrderItemId(Integer claim_id){
        return jdbcTemplate.queryForObject("SELECT order_item_id FROM claim WHERE claim_id=%d".formatted(claim_id), Integer.class);
    }

    public Boolean updateOrderItemStatus(Integer order_item_id){
        /*
         * 취소 승인 시 order_item의 status를 CANCELED로 변경
         */
        String sql = "UPDATE order_item SET status='CANCELED' WHERE order_item_id=%d".formatted(order_item_id);
        return jdbcTemplate.update(sql) > 0;
    }

    public Boolean updateSettlementStatus(Integer order_item_id){
        /*
         * 취소 승인 시 settlement의 status를 CANCELED로 변경
         */
        String sql = "UPDATE settlement SET status='CANCELED' WHERE order_item_id=%d".formatted(order_item_id);
        return jdbcTemplate.update(sql) > 0;
    }
}
