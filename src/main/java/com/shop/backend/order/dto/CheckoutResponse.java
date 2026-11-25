package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CheckoutResponse {
    private String code;
    private List<CheckoutItem> items;
    private List<CheckoutAddress> addresses;
    private List<CheckoutCoupon> coupons;
    private int points;             // 보유 적립금
    private int expected_amount;    // 상품 금액 - 상품 할인 (쿠폰·적립금 적용 전)
}
