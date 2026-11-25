package com.shop.backend.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Coupons {
    private Long coupon_id;
    private String coupon_name;
    private String discount_type;
    private int discount_value;
    private int min_order_amount;
    private String valid_from;
    private String valid_to;
    private String status;
}
