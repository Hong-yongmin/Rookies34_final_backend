package com.shop.backend.coupon.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class GrantCouponRequest {
    private List<String> target_user_ids;
}
