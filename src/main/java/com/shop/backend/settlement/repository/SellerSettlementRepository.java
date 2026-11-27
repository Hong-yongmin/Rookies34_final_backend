package com.shop.backend.settlement.repository;

import com.shop.backend.settlement.dto.Revenue;
import com.shop.backend.settlement.dto.SellerRevenueRequest;
import com.shop.backend.settlement.dto.Withdrawal;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SellerSettlementRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Revenue> getRevenues(String id, SellerRevenueRequest req){
        /*
         * 판매자 수익금 내역 조회
         */
        String sql = ("SELECT s.seller_id, s.settlement_id, s.order_item_id, s.order_amount, s.commission, s.settlement_amount, s.status, os.ordered_at "
                + "FROM order_item o, settlement s, orders os "
                + "WHERE s.order_item_id = o.order_item_id AND o.order_id = os.order_id AND s.seller_id='%s'").formatted(id);

        if(req.getStatus() != null){
            sql += " AND s.status='%s'".formatted(req.getStatus());
        }
        if(req.getDate_from() != null){
            sql += " AND os.ordered_at >= STR_TO_DATE('%s', '%%Y-%%m-%%d %%H:%%i:%%s')".formatted(req.getDate_from());
        }
        if(req.getDate_to() != null){
            sql += " AND os.ordered_at <= DATE_ADD(STR_TO_DATE('%s', '%%Y-%%m-%%d %%H:%%i:%%s'), INTERVAL 1 DAY)".formatted(req.getDate_to());
            // 지정한 날짜까지 검색하기 위해 하루 추가
        }
        if(req.getPage() != null && req.getPage() > 0){
            sql += " LIMIT 10 OFFSET %d".formatted((req.getPage()-1)*10);
        }
        return jdbcTemplate.query(sql, (rs, i)
                ->Revenue.builder()
                .seller_id(rs.getString("s.seller_id"))
                .settlement_id(rs.getInt("s.settlement_id"))
                .order_item_id(rs.getInt("s.order_item_id"))
                .order_amount(rs.getInt("s.order_amount"))
                .commission(rs.getInt("s.commission"))
                .settlement_amount(rs.getInt("s.settlement_amount"))
                .status(rs.getString("s.status"))
                .created_at(rs.getString("os.ordered_at")).build());
    }

    public List<Withdrawal> getWithdrawals(String id, Integer page){
        /*
         * 판매자 출금 내역 조회
         */
        String sql = ("SELECT seller_id, withdrawal_id, amount, status, requested_at, processed_at "
                + "FROM withdrawal "
                + "WHERE seller_id='%s'").formatted(id);

        if( page != null && page > 0){
            sql += " LIMIT 10 OFFSET %d".formatted((page-1)*10);
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

    public Boolean applyWithdrawal(String id, Integer amount){
        /*
         * 판매자 출금 신청
         * 쿼리 성공 시 true 반환
         */
        String sql = ("INSERT INTO withdrawal (seller_id, amount, status) "
                + "VALUES ('%s', %d, 'REQUESTED')").formatted(id, amount);
        return jdbcTemplate.update(sql) > 0;
    }
}
