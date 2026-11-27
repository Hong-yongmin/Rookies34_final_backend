package com.shop.backend.order.repository;

import com.shop.backend.global.common.CommonFunction;
import com.shop.backend.order.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private final CommonFunction cf;

    public List<CheckoutItem> getCheckoutItems(String id, List<Long> cart_item_ids){
        /*
         * 주문할 장바구니 항목 조회 (사용자 본인 장바구니의 항목만)
         */
        String sql = ("SELECT ci.cart_item_id, p.product_id, p.name, ci.quantity, p.price, p.discount_rate, p.thumbnail_url "
                + "FROM cart_item ci, cart c, product p "
                + "WHERE ci.cart_id=c.cart_id AND ci.product_id=p.product_id "
                + "AND c.user_id='%s' AND ci.cart_item_id IN (%s)").formatted(id, cf.join(cart_item_ids));

        return jdbcTemplate.query(sql,
                (rs, i) -> CheckoutItem.builder()
                        .cart_item_id(rs.getLong("cart_item_id"))
                        .product_id(rs.getLong("product_id"))
                        .name(rs.getString("name"))
                        .quantity(rs.getInt("quantity"))
                        .price(rs.getInt("price"))
                        .discount_rate(rs.getInt("discount_rate"))
                        .thumbnail(rs.getString("thumbnail_url")).build());
    }

    public List<CheckoutAddress> getAddresses(String id){
        /*
         * 사용자의 저장된 배송지 목록 조회
         */
        String sql = "SELECT * FROM address WHERE user_id='%s'".formatted(id);

        return jdbcTemplate.query(sql,
                (rs, i) -> CheckoutAddress.builder()
                        .address_id(rs.getLong("address_id"))
                        .recipient_name(rs.getString("recipient_name"))
                        .recipient_phone(rs.getString("recipient_phone"))
                        .zipcode(rs.getString("zipcode"))
                        .address(rs.getString("address"))
                        .address_detail(rs.getString("address_detail"))
                        .is_default(rs.getBoolean("is_default")).build());
    }

    public CheckoutAddress getAddress(String id, Long address_id){
        /*
         * 주문 시 선택한 배송지 조회 (주문 시점 값을 orders에 복사하기 위함)
         * 없으면 null 반환
         */
        String sql = "SELECT * FROM address WHERE address_id=%d AND user_id='%s'".formatted(address_id, id);

        List<CheckoutAddress> list = jdbcTemplate.query(sql,
                (rs, i) -> CheckoutAddress.builder()
                        .address_id(rs.getLong("address_id"))
                        .recipient_name(rs.getString("recipient_name"))
                        .recipient_phone(rs.getString("recipient_phone"))
                        .zipcode(rs.getString("zipcode"))
                        .address(rs.getString("address"))
                        .address_detail(rs.getString("address_detail"))
                        .is_default(rs.getBoolean("is_default")).build());
        return list.isEmpty() ? null : list.get(0);
    }

    public List<CheckoutCoupon> getAvailableCoupons(String id){
        /*
         * 사용자가 보유한 쿠폰 중 현재 사용 가능한 쿠폰 조회
         */
        String sql = ("SELECT c.* FROM coupon c, user_coupon uc "
                + "WHERE c.coupon_id=uc.coupon_id AND uc.user_id='%s' "
                + "AND uc.status='AVAILABLE' AND (CURDATE() BETWEEN c.valid_from AND c.valid_to)").formatted(id);

        return jdbcTemplate.query(sql,
                (rs, i) -> CheckoutCoupon.builder()
                        .coupon_id(rs.getLong("coupon_id"))
                        .coupon_name(rs.getString("coupon_name"))
                        .discount_type(rs.getString("discount_type"))
                        .discount_value(rs.getInt("discount_value"))
                        .min_order_amount(rs.getInt("min_order_amount"))
                        .valid_to(rs.getString("valid_to")).build());
    }

    public CheckoutCoupon getAvailableCoupon(String id, Long coupon_id){
        /*
         * 주문 시 선택한 쿠폰이 사용 가능한지 확인하고 쿠폰 정보 반환
         * 사용 불가능하면 null 반환
         */
        String sql = ("SELECT c.* FROM coupon c, user_coupon uc "
                + "WHERE c.coupon_id=uc.coupon_id AND uc.user_id='%s' AND c.coupon_id=%d "
                + "AND uc.status='AVAILABLE' AND (CURDATE() BETWEEN c.valid_from AND c.valid_to)").formatted(id, coupon_id);

        List<CheckoutCoupon> list = jdbcTemplate.query(sql,
                (rs, i) -> CheckoutCoupon.builder()
                        .coupon_id(rs.getLong("coupon_id"))
                        .coupon_name(rs.getString("coupon_name"))
                        .discount_type(rs.getString("discount_type"))
                        .discount_value(rs.getInt("discount_value"))
                        .min_order_amount(rs.getInt("min_order_amount"))
                        .valid_to(rs.getString("valid_to")).build());
        return list.isEmpty() ? null : list.get(0);
    }

    public int getPoints(String id){
        /*
         * 사용자 보유 적립금 조회 (user.point)
         */
        String sql = "SELECT point FROM user WHERE user_id='%s'".formatted(id);
        Integer point = jdbcTemplate.queryForObject(sql, Integer.class);
        return point == null ? 0 : point;
    }

    public Long insertOrder(String id, OrderCreateRequest req, CheckoutAddress addr,
                            int product_amount, int discount_amount, int coupon_discount, int use_points, int total_amount){
        /*
         * 주문 생성
         * 배송지는 주문 시점 값을 복사해 저장
         * 금액은 서비스에서 계산한 값 사용
         * 생성된 order_id 반환, 실패 시 null
         */
        String sql = ("INSERT INTO orders (user_id, status, payment_method, product_amount, discount_amount, coupon_id, coupon_discount, "
                + "use_points, total_amount, recipient_name, recipient_phone, zipcode, address, address_detail, delivery_request, ordered_at) "
                + "VALUES ('%s', 'PAID', '%s', %d, %d, %s, %d, %d, %d, '%s', '%s', '%s', '%s', %s, %s, NOW())").formatted(   // ordered_at = 주문 시각
                id, req.getPayment_method(), product_amount, discount_amount,
                req.getCoupon_id() == null ? "NULL" : req.getCoupon_id(), coupon_discount, use_points, total_amount,
                req.getRecipient_name(), req.getRecipient_phone(), addr.getZipcode(), addr.getAddress(),
                cf.q(addr.getAddress_detail()), cf.q(req.getDelivery_request()));    // 선택 항목은 없으면 NULL

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(con -> con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), keyHolder);
        if (rows <= 0 || keyHolder.getKey() == null){
            return null;
        }
        return keyHolder.getKey().longValue();
    }

    public Boolean insertOrderItems(Long order_id, List<Long> cart_item_ids){
        /*
         * 장바구니 항목을 주문 상품으로 저장
         * 상품명, 판매가는 주문 시점 값 복사, 상태는 PAID
         * 쿼리 실행 성공 시 true 반환
         */
        String sql = ("INSERT INTO order_item (order_id, product_id, seller_id, product_name, price, quantity, status) "
                + "SELECT %d, p.product_id, p.seller_id, p.name, p.price, ci.quantity, 'PAID' "
                + "FROM cart_item ci, product p "
                + "WHERE ci.product_id=p.product_id AND ci.cart_item_id IN (%s)").formatted(order_id, cf.join(cart_item_ids));
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public Boolean updateSettlement(Long order_id){
        /*
         * 정산 추가
         * 주문 금액 = 판매가 × 수량
         * commission_rate 테이블은 0~1 비율, settlement.commission_rate는 0~100(%) → ×100 해서 저장
         * 수수료는 원 단위 절사, 정산액 = 주문 금액 - 수수료 (settlement CHECK 조건과 일치)
         */
        String sql = ("INSERT INTO settlement (seller_id, order_item_id, order_amount, commission_rate, commission, settlement_amount, status) "
                + "SELECT o.seller_id, o.order_item_id, o.price * o.quantity, c.commission_rate * 100, "
                + "FLOOR(o.price * o.quantity * c.commission_rate), "
                + "o.price * o.quantity - FLOOR(o.price * o.quantity * c.commission_rate), 'PENDING' "
                + "FROM order_item o, commission_rate c WHERE o.order_id=%d AND c.seller_id=o.seller_id").formatted(order_id);

        return jdbcTemplate.update(sql) > 0;
    }

    public Boolean decreaseStock(List<Long> cart_item_ids){
        /*
         * 주문 수량만큼 상품 재고 차감
         */
        String sql = ("UPDATE product p, cart_item ci SET p.stock = IF( (p.stock - ci.quantity)<0, 0, p.stock-ci.quantity) "
                + "WHERE p.product_id=ci.product_id AND ci.cart_item_id IN (%s)").formatted(cf.join(cart_item_ids));
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public Boolean deleteCartItems(List<Long> cart_item_ids){
        /*
         * 주문 완료된 장바구니 항목 삭제
         */
        String sql = "DELETE FROM cart_item WHERE cart_item_id IN (%s)".formatted(cf.join(cart_item_ids));
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public Boolean useCoupon(String id, Long coupon_id, Long order_id){
        /*
         * 쿠폰 사용 처리 (AVAILABLE -> USED)
         */
        String sql = ("UPDATE user_coupon SET status='USED', used_order_id='%d' "
                + "WHERE user_id='%s' AND coupon_id=%d AND status='AVAILABLE' LIMIT 1").formatted(order_id, id, coupon_id);
        int rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public Boolean usePoints(String id, int use_points, Long order_id){
        /*
         * 적립금 사용 처리
         * user.point 차감 후 point_history에 사용 내역 기록
         */
        String sql = "UPDATE user SET point = point - %d WHERE user_id='%s'".formatted(use_points, id);
        int rows = jdbcTemplate.update(sql);
        if (rows <= 0){
            return false;
        }
        sql = ("INSERT INTO point_history (user_id, type, amount, reason, order_id, created_at) "
                + "VALUES ('%s', 'POINT', %d, '주문 사용', '%d', NOW())").formatted(id, -use_points, order_id);
        rows = jdbcTemplate.update(sql);
        return rows > 0;
    }

    public List<OrderSummary> getOrders(String id, OrderListRequest req){
        /*
         * 사용자 주문 내역 조회
         * period(3M/6M/1Y), keyword(상품명)는 값이 있을 경우 조건 추가
         */
        String sql = ("SELECT o.order_id, o.ordered_at, o.total_amount, "
                + "(SELECT oi.product_name FROM order_item oi WHERE oi.order_id=o.order_id ORDER BY oi.order_item_id LIMIT 1) AS product_name, "
                + "(SELECT oi.status FROM order_item oi WHERE oi.order_id=o.order_id ORDER BY oi.order_item_id LIMIT 1) AS status,"
                + "(SELECT COUNT(*) FROM order_item oi WHERE oi.order_id=o.order_id) AS item_count "
                + "FROM orders o WHERE o.user_id='%s' ").formatted(id);

        if (req != null){
            String period;
            if ((period = req.getPeriod()) != null){ // 조회 기간
                String interval = switch (period){
                    case "3M" -> "3 MONTH";
                    case "6M" -> "6 MONTH";
                    case "1Y" -> "1 YEAR";
                    default -> null;
                };
                if (interval != null){
                    sql += "AND o.ordered_at >= DATE_SUB(NOW(), INTERVAL %s) ".formatted(interval);
                }
            }
            String keyword;
            if ((keyword = req.getKeyword()) != null){ // 상품명 검색
                sql += "AND o.order_id IN (SELECT order_id FROM order_item WHERE product_name LIKE '%%%s%%') ".formatted(keyword);
            }
            sql += "ORDER BY o.ordered_at DESC ";
            Integer page;
            if ((page = req.getPage()) != null && page > 0){
                sql += "LIMIT 10 OFFSET %d".formatted((page - 1) * 10);
            }
        }

        return jdbcTemplate.query(sql,
                (rs, i) -> OrderSummary.builder()
                        .order_id(rs.getLong("order_id"))
                        .ordered_at(rs.getString("ordered_at"))
                        .product_name(rs.getString("product_name"))
                        .status(rs.getString("status"))
                        .item_count(rs.getInt("item_count"))
                        .total_amount(rs.getInt("total_amount")).build());
    }

    public OrderDetailResponse getOrderDetail(String id, Long order_id){
        /*
         * 주문 상세 조회 (주문 상품 목록은 getOrderItems로 별도 조회)
         * 본인 주문이 아니거나 없으면 null 반환
         */
        String sql = "SELECT * FROM orders WHERE order_id=%d AND user_id='%s'".formatted(order_id, id);

        List<OrderDetailResponse> list = jdbcTemplate.query(sql,
                (rs, i) -> OrderDetailResponse.builder()
                        .order_id(rs.getLong("order_id"))
                        .status(rs.getString("status"))
                        .ordered_at(rs.getString("ordered_at"))
                        .payment_method(rs.getString("payment_method"))
                        .total_amount(rs.getInt("total_amount"))
                        .recipient_name(rs.getString("recipient_name"))
                        .recipient_phone(rs.getString("recipient_phone"))
                        .zipcode(rs.getString("zipcode"))
                        .address(rs.getString("address"))
                        .address_detail(rs.getString("address_detail"))
                        .delivery_request(rs.getString("delivery_request")).build());
        return list.isEmpty() ? null : list.get(0);
    }

    public List<OrderItemInfo> getOrderItems(Long order_id){
        /*
         * 주문에 포함된 주문 상품 목록 조회
         */
        String sql = "SELECT * FROM order_item WHERE order_id=%d ORDER BY order_item_id".formatted(order_id);

        return jdbcTemplate.query(sql,
                (rs, i) -> OrderItemInfo.builder()
                        .order_item_id(rs.getLong("order_item_id"))
                        .product_id(rs.getLong("product_id"))
                        .name(rs.getString("product_name"))
                        .quantity(rs.getInt("quantity"))
                        .price(rs.getInt("price"))
                        .status(rs.getString("status")).build());
    }
    public Boolean isOrderOwnedByUser(String userId, Integer orderId) {
        String sql = "SELECT * FROM orders WHERE order_id=%d AND user_id='%s'"
                .formatted(orderId, userId);

        List<Integer> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getInt("order_id")
        );

        return !result.isEmpty();
    }
}
