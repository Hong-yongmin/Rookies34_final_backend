package com.shop.backend.settlement.repository;

import com.shop.backend.settlement.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminSettlementRepository {

    private final JdbcTemplate jdbcTemplate;

    public Boolean setCommissionRate(String id, Double rate){
        /*
         * 수수료율 설정
         * 판매자별로 적용되는 수수료율을 설정함
         * 쿼리 성공 시 true 반환
         */
        String sql = "UPDATE commission_rate SET commission_rate='%s' WHERE seller_id='%s'".formatted(rate, id);
        return !jdbcTemplate.queryForList(sql).isEmpty();
    }

    public List<Revenue> getRevenues(AdminRevenueRequest req){
        /*
         * 관리자 수익 조회
         */
        String sql = "SELECT s.seller_id, s.settlement_id, s.order_item_id, s.order_amount, s.commission, s.settlement_amount, s.status, o.ordered_at "
                + "FROM order_item o, settlement s"
                + "WHERE o.seller_id = s.seller_id";

        if(req.getSeller_id() != null){
            sql += " AND o.seller_id='%s' AND s.seller_id='%s'".formatted(req.getSeller_id(), req.getSeller_id());
        }
        if(req.getDate_from() != null){
            sql += " AND o.ordered_at >= STR_TO_DATE('%s', '%%Y-%%m-%%d %%H:%%i:%%s')".formatted(req.getDate_from());
        }
        if(req.getDate_to() != null){
            sql += " AND o.ordered_at <= DATE_ADD(STR_TO_DATE('%s', '%%Y-%%m-%%d %%H:%%i:%%s'), INTERVAL 1 DAY)".formatted(req.getDate_to());
            // 지정한 날짜까지 검색하기 위해 하루 추가
        }
        if(req.getStatus() != null){
            sql += " AND s.status='%s'".formatted(req.getStatus());
        }
        if(req.getPage() != null && req.getPage() > 0){
            sql += " LIMIT 10 OFFSET %d".formatted((req.getPage()-1)*10);
        }

        return jdbcTemplate.query(sql,
                (rs, i) -> Revenue.builder()
                        .seller_id(rs.getString("s.seller_id"))
                        .settlement_id(rs.getInt("s.settlement_id"))
                        .order_item_id(rs.getInt("s.order_item_id"))
                        .order_amount(rs.getInt("s.order_amount"))
                        .commission(rs.getInt("s.commission"))
                        .settlement_amount(rs.getInt("s.settlement_amount"))
                        .status(rs.getString("s.status"))
                        .created_at(rs.getString("o.ordered_at"))
                        .build());
    }

    public List<Withdrawal> getWithdrawals(AdminWithdrawalRequest req){
        /*
         * 관리자 출금 신청 내역 조회
         * seller_id, status, date_from, date_to는 값이 있을 경우 조건 추가
         */
        String sql = "SELECT withdrawal_id, seller_id, amount, status, requested_at, processed_at "
                + "FROM withdrawal "
                + "WHERE 1=1";

        if(req.getSeller_id() != null){
            sql += " AND seller_id='%s'".formatted(req.getSeller_id());
        }
        if(req.getDate_from() != null){
            sql += " AND requested_at >= STR_TO_DATE('%s', '%%Y-%%m-%%d %%H:%%i:%%s')".formatted(req.getDate_from());
        }
        if(req.getDate_to() != null){
            sql += " AND requested_at <= DATE_ADD(STR_TO_DATE('%s', '%%Y-%%m-%%d %%H:%%i:%%s'), INTERVAL 1 DAY)".formatted(req.getDate_to());
            // 지정한 날짜까지 검색하기 위해 하루 추가
        }
        if(req.getStatus() != null){
            sql += " AND status='%s'".formatted(req.getStatus());
        }
        sql += " ORDER BY requested_at DESC";  // 최신 신청부터
        if(req.getPage() != null && req.getPage() > 0){
            sql += " LIMIT 10 OFFSET %d".formatted((req.getPage()-1)*10);
        }

        return jdbcTemplate.query(sql,
                (rs, i) -> Withdrawal.builder()
                        .withdrawal_id(rs.getLong("withdrawal_id"))
                        .seller_id(rs.getString("seller_id"))
                        .amount(rs.getInt("amount"))
                        .status(rs.getString("status"))
                        .requested_at(rs.getString("requested_at"))
                        .processed_at(rs.getString("processed_at"))
                        .build());
    }

//    public Boolean confirmSettlement(Integer settlement_id, String decision){
//        /*
//         * 관리자 정산 확정
//         */
//    }
}
