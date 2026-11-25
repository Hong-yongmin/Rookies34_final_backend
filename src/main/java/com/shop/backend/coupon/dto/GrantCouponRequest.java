package com.shop.backend.coupon.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GrantCouponRequest {
    private List<String> target_user_ids;
}
