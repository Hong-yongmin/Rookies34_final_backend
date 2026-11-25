package com.shop.backend.order.repository;

import com.shop.backend.global.common.CommonFunction;
import com.shop.backend.order.dto.SellerOrder;
import com.shop.backend.order.dto.SellerOrderListRequest;
import com.shop.backend.order.dto.ShipmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SellerOrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private final CommonFunction cf;

    public List<SellerOrder> getSellerOrders(String id, SellerOrderListRequest req){
        /*
         * 판매자의 판매 주문(주문 상품 단위) 목록 조회
         * date_from, date_to, status는 값이 있을 경우 조건 추가
         */
        String sql = ("SELECT oi.order_item_id, oi.order_id, oi.product_name, oi.quantity, (oi.price * oi.quantity) AS amount, "
                + "oi.status, o.ordered_at, oi.tracking_no "
                + "FROM order_item oi, orders o "
                + "WHERE oi.order_id=o.order_id AND oi.seller_id='%s' ").formatted(id);

        if (req != null){
            if (req.getDate_from() != null){
                sql += "AND DATE(o.ordered_at) >= '%s' ".formatted(req.getDate_from());
            }
            if (req.getDate_to() != null){
                sql += "AND DATE(o.ordered_at) <= '%s' ".formatted(req.getDate_to());
            }
            if (req.getStatus() != null){
                sql += "AND oi.status='%s' ".formatted(req.getStatus());
            }
            sql += "ORDER BY o.ordered_at DESC ";
            Integer page;
            if ((page = req.getPage()) != null && page > 0){
                sql += "LIMIT 10 OFFSET %d".formatted((page - 1) * 10);
            }
        }

        return jdbcTemplate.query(sql,
                (rs, i) -> SellerOrder.builder()
                        .order_item_id(rs.getLong("order_item_id"))
                        .order_id(rs.getLong("order_id"))
                        .product_name(rs.getString("product_name"))
                        .quantity(rs.getInt("quantity"))
                        .amount(rs.getInt("amount"))
                        .status(rs.getString("status"))
                        .ordered_at(rs.getString("ordered_at"))
                        .tracking_no(rs.getString("tracking_no")).build());
    }

    public Boolean updateShipment(String id, Long order_item_id, ShipmentRequest req){
        /*
         * 배송 예정일, 택배사, 송장번호 입력 (본인 상품 주문만)
         * 송장번호가 입력되면 상태를 SHIPPING으로 변경
         * 쿼리 실행 성공 시 true 반환
         */
        String sql = "UPDATE order_item SET expected_delivery_date=%s, courier=%s, tracking_no=%s ".formatted(
                cf.q(req.getExpected_delivery_date()), cf.q(req.getCourier()), cf.q(req.getTracking_no())); // 없으면 NULL
        if (req.getTracking_no() != null && !req.getTracking_no().isBlank()){
            sql += ", status='SHIPPING' ";
        }
        sql += "WHERE order_item_id=%d AND seller_id='%s'".formatted(order_item_id, id);
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public Boolean cancelOrderItem(String id, Long order_item_id, String reason){
        /*
         * 판매자 주문 취소 (본인 상품 주문만, 이미 취소된 주문 제외)
         * reason : OUT_OF_STOCK / ETC
         * 쿼리 실행 성공 시 true 반환
         */
        String sql = ("UPDATE order_item SET status='CANCELED', cancel_reason='%s' "
                + "WHERE order_item_id=%d AND seller_id='%s' AND status<>'CANCELED'").formatted(reason, order_item_id, id);
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }
}
