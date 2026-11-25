package com.shop.backend.order.repository;

import com.shop.backend.order.dto.AdminOrder;
import com.shop.backend.order.dto.AdminOrderListRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminOrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<AdminOrder> getOrders(AdminOrderListRequest req){
        /*
         * 관리자의 전체 주문 조회
         * seller_id는 주문의 첫 번째 상품 판매자
         * status, seller_id는 해당 값을 가진 주문 상품이 하나라도 있는 주문만 조회
         */
        String sql = "SELECT o.order_id, o.ordered_at, o.user_id, o.total_amount, "
                + "(SELECT oi.seller_id FROM order_item oi WHERE oi.order_id=o.order_id ORDER BY oi.order_item_id LIMIT 1) AS seller_id "
                + "FROM orders o WHERE 1=1 ";

        if (req != null){
            if (req.getDate_from() != null){
                sql += "AND DATE(o.ordered_at) >= '%s' ".formatted(req.getDate_from());
            }
            if (req.getDate_to() != null){
                sql += "AND DATE(o.ordered_at) <= '%s' ".formatted(req.getDate_to());
            }
            if (req.getStatus() != null){
                sql += "AND o.order_id IN (SELECT order_id FROM order_item WHERE status='%s') ".formatted(req.getStatus());
            }
            if (req.getSeller_id() != null){
                sql += "AND o.order_id IN (SELECT order_id FROM order_item WHERE seller_id='%s') ".formatted(req.getSeller_id());
            }
            sql += "ORDER BY o.ordered_at DESC ";
            Integer page;
            if ((page = req.getPage()) != null && page > 0){
                sql += "LIMIT 10 OFFSET %d".formatted((page - 1) * 10);
            }
        }

        return jdbcTemplate.query(sql,
                (rs, i) -> AdminOrder.builder()
                        .order_id(rs.getLong("order_id"))
                        .ordered_at(rs.getString("ordered_at"))
                        .buyer_login_id(rs.getString("user_id"))
                        .seller_id(rs.getString("seller_id"))
                        .total_amount(rs.getInt("total_amount")).build());
    }
}
