package com.shop.backend.coupon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class CouponListResponse {
    private String code;
    private int num;
    List<Coupons> coupons;
}
