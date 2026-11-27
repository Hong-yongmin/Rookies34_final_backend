package com.shop.backend.order.repository;

import com.shop.backend.global.common.CommonFunction;
import com.shop.backend.order.dto.Claim;
import com.shop.backend.order.dto.ClaimRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class UserClaimRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CommonFunction cf;

    public Integer requestClaim(ClaimRequest req, Integer order_item_id, String user_id){
        /*
         * 취소/환불/반품 신청
         * 취소, 환불은 order_item의 status가 paid일 때 가능, 반품은 COMPLETED일 때 가능
         *
         */
        String sql = ("INSERT INTO claim (order_item_id, user_id, request_type, reason_type, reason_detail, status) "
                + "VALUES (%d, '%s', '%s', '%s', %s, 'REQUESTED')").formatted(
                order_item_id, user_id, req.getRequest_type(), req.getReason_type(), cf.q(req.getReason_detail()));   // 상세 사유는 없으면 NULL

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(
                con -> con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS),  // 생성 키 반환 요청
                keyHolder);
        if (rows <= 0 || keyHolder.getKey() == null){
            return null;
        }
        return keyHolder.getKey().intValue(); //claim_id 반환
    }

    public String getStatus(Integer order_item_id){
        // 취소/환불/교환 신청 주문 상품 상태 확인
        return jdbcTemplate.queryForObject("SELECT status FROM order_item WHERE order_item_id=%d".formatted(order_item_id),
                String.class);
    }

    public String getPaymentMethod(Integer order_id){
        return jdbcTemplate.queryForObject("SELECT payment_method FROM orders WHERE order_id=%d".formatted(order_id),
                String.class);
    }

    public Boolean addBankInfo(Integer claim_id, String refund_bank, String refund_account_no, String refund_account_holder){
        /*
         * 결제 방식이 현금인 경우 계좌 정보 추가
         */
        String sql = ("UPDATE claim SET refund_bank='%s', refund_account_no='%s', refund_account_holder='%s' "
                + "WHERE claim_id=%d").formatted(refund_bank, refund_account_no, refund_account_holder, claim_id);
        return jdbcTemplate.update(sql) > 0;
    }

    public Boolean addClaimImage(Integer claim_id, String file_path){
        /*
         * claim 이미지 경로 저장
         */
        String sql = ("INSERT INTO claim_image (claim_id, url) "
                    + "VALUES (%d, '%s')").formatted(claim_id, file_path);
        return jdbcTemplate.update(sql) > 0;
    }

    public List<Claim> getClaims(String user_id, Integer page){
        /*
         * 취소/반품/교환 내역 조회
         */
        String sql = "SELECT * FROM claim WHERE user_id='%s'".formatted(user_id);
        if(page != null && page > 0){
            sql += " LIMIT 10 OFFSET %d".formatted((page-1)*10);
        }

        return jdbcTemplate.query(sql,
                (rs, i)->Claim.builder()
                        .claim_id(rs.getInt("claim_id"))
                        .order_id(rs.getInt("order_id"))
                        .request_type(rs.getString("request_type"))
                        .reason_type(rs.getString("reason_type"))
                        .status(rs.getString("status"))
                        .created_at(rs.getString("requested_at"))   // claim 테이블 컬럼명은 requested_at
                        .build());
    }

    public Boolean updateOrderItemStatus(Integer order_item_id){
        /*
         * 주문 취소/환불/교환이 접수된 order_item의 status를 WAITING으로 업데이트
         */
        return jdbcTemplate.update("UPDATE order_item SET status='WAITING' WHERE order_item_id=%d".formatted(order_item_id)) > 0;
    }
}
