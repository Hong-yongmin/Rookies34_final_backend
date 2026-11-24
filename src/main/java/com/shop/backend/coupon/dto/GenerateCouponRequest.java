package com.shop.backend.coupon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class GenerateCouponRequest {
    private String coupon_name;
    private String discount_type;
    private int discount_value;
    private int min_order_amount;
    private String valid_from;
    private String valid_to;
}
