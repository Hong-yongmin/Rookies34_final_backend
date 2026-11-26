package com.shop.backend.order.service;

import com.shop.backend.order.dto.*;
import com.shop.backend.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private boolean isBlank(String v){ return v == null || v.isBlank(); }

    private int getProductAmount(List<CheckoutItem> items){ // 상품 금액 합계 (판매가 × 수량)
        int sum = 0;
        for (CheckoutItem item : items){
            sum += item.getPrice() * item.getQuantity();
        }
        return sum;
    }

    private int getDiscountAmount(List<CheckoutItem> items){ // 상품 할인액 합계 (원 단위 절사)
        int sum = 0;
        for (CheckoutItem item : items){
            int rate = item.getDiscount_rate() == null ? 0 : item.getDiscount_rate();
            sum += item.getPrice() * item.getQuantity() * rate / 100;
        }
        return sum;
    }

    public CheckoutResponse checkout(String id, CheckoutRequest req){
        List<Long> cart_item_ids = req.getCart_item_ids();
        if (cart_item_ids == null || cart_item_ids.isEmpty()){
            return CheckoutResponse.builder()
                    .code("no")
                    .build();
        }

        List<CheckoutItem> items = orderRepository.getCheckoutItems(id, cart_item_ids);
        if (items.size() != cart_item_ids.size()){ // 본인 장바구니에 없는 항목 포함
            return CheckoutResponse.builder()
                    .code("no")
                    .build();
        }

        return CheckoutResponse.builder()
                .code("ok")
                .items(items)
                .addresses(orderRepository.getAddresses(id))
                .coupons(orderRepository.getAvailableCoupons(id))
                .points(orderRepository.getPoints(id))
                .expected_amount(getProductAmount(items) - getDiscountAmount(items)) // 쿠폰·적립금 적용 전
                .build();
    }

    private OrderCreateResponse fail(){ // 주문 처리 중 실패 시 이전 쿼리까지 롤백
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return OrderCreateResponse.builder()
                .code("no")
                .build();
    }

    @Transactional
    public OrderCreateResponse createOrder(String id, OrderCreateRequest req){
        // 필수 항목 확인
        List<Long> cart_item_ids = req.getCart_item_ids();
        if (cart_item_ids == null || cart_item_ids.isEmpty() || req.getAddress_id() == null
                || isBlank(req.getRecipient_name()) || isBlank(req.getRecipient_phone())
                || isBlank(req.getPayment_method()) || !Boolean.TRUE.equals(req.getAgree_payment())){
            return fail();
        }
        String payment_method = req.getPayment_method();
        if (!payment_method.equals("CARD") && !payment_method.equals("BANK_TRANSFER")
                && !payment_method.equals("VIRTUAL_ACCOUNT")){
            return fail();
        }

        // 주문 항목, 배송지 확인
        List<CheckoutItem> items = orderRepository.getCheckoutItems(id, cart_item_ids);
        if (items.size() != cart_item_ids.size()){
            return fail();
        }
        CheckoutAddress addr = orderRepository.getAddress(id, req.getAddress_id());
        if (addr == null){
            return fail();
        }

        // 금액 계산 (서버 계산)
        int product_amount = getProductAmount(items);
        int discount_amount = getDiscountAmount(items);
        int amount = product_amount - discount_amount;  // 쿠폰·적립금 적용 전 금액

        int coupon_discount = 0;
        if (req.getCoupon_id() != null){
            CheckoutCoupon coupon = orderRepository.getAvailableCoupon(id, req.getCoupon_id());
            if (coupon == null){ // 보유하지 않았거나 사용 불가능한 쿠폰
                return fail();
            }
            if (coupon.getMin_order_amount() != null && amount < coupon.getMin_order_amount()){ // 최소 주문 금액 미달
                return fail();
            }
            if (coupon.getDiscount_type().equals("PERCENT")){
                coupon_discount = amount * coupon.getDiscount_value() / 100;
            } else {
                coupon_discount = coupon.getDiscount_value();
            }
            coupon_discount = Math.min(coupon_discount, amount); // 할인액이 결제 금액보다 클 수 없음
        }

        int use_points = req.getUse_points() == null ? 0 : req.getUse_points();
        if (use_points < 0 || use_points > amount - coupon_discount){ // 0 이상, 결제 금액 이하 (보유액 검증 없음 - 취약점 포인트)
            return fail();
        }
        int total_amount = amount - coupon_discount - use_points;

        // 주문 생성
        Long order_id = orderRepository.insertOrder(id, req, addr,
                product_amount, discount_amount, coupon_discount, use_points, total_amount);
        if (order_id == null){
            return fail();
        }
        if (!orderRepository.insertOrderItems(order_id, cart_item_ids)){
            return fail();
        }
        if (!orderRepository.decreaseStock(cart_item_ids)){
            return fail();
        }
        if (!orderRepository.deleteCartItems(cart_item_ids)){
            return fail();
        }
        if (req.getCoupon_id() != null && !orderRepository.useCoupon(id, req.getCoupon_id(), order_id)){
            return fail();
        }
        if (use_points > 0 && !orderRepository.usePoints(id, use_points, order_id)){
            return fail();
        }

        return OrderCreateResponse.builder()
                .code("ok")
                .order_id(order_id)
                .build();
    }

    public OrderListResponse getOrders(String id, OrderListRequest req){
        List<OrderSummary> orders = orderRepository.getOrders(id, req);

        return OrderListResponse.builder()
                .code("ok")
                .num(orders.size())
                .orders(orders)
                .build();
    }

    public OrderDetailResponse getOrderDetail(String id, Long order_id){
        OrderDetailResponse detail = orderRepository.getOrderDetail(id, order_id);
        if (detail == null){ // 본인 주문이 아니거나 없는 주문
            return OrderDetailResponse.builder()
                    .code("no")
                    .build();
        }
        detail.setCode("ok");
        detail.setItems(orderRepository.getOrderItems(order_id));
        return detail;
    }
}
