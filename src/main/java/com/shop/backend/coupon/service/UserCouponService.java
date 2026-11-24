package com.shop.backend.coupon.service;

import com.shop.backend.coupon.dto.CouponListResponse;
import com.shop.backend.coupon.dto.Coupons;
import com.shop.backend.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCouponService {
    private final UserCouponRepository userCouponRepository;

    public CouponListResponse getUserCoupons(String id, String status){
        List<Coupons> coupons = userCouponRepository.getCoupons(id, status);

        return CouponListResponse.builder()
                .code("ok")
                .num(coupons.toArray().length)
                .coupons(coupons)
                .build();
    }
}
