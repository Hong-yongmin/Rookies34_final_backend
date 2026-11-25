package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckoutCoupon {
    private Long coupon_id;
    private String coupon_name;
    private String discount_type;       // AMOUNT / PERCENT
    private int discount_value;
    private Integer min_order_amount;   // 선택
    private String valid_to;            // YYYY-MM-DD
}
