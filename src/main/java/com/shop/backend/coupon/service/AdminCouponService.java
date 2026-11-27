package com.shop.backend.coupon.service;

import com.shop.backend.coupon.dto.*;
import com.shop.backend.coupon.repository.AdminCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminCouponService {
    private final AdminCouponRepository adminCouponRepository;

    public CouponListResponse getCoupons(Integer page, String status, String date_from, String date_to){
        List<Coupons> coupons = adminCouponRepository.getCoupons(page, status, date_from, date_to);

        return CouponListResponse.builder()
                .code("ok")
                .num(coupons.size())
                .coupons(coupons)
                .build();
    }

    public AdminCouponResponse generateCoupon(GenerateCouponRequest generateCouponRequest){
        if (adminCouponRepository.generateCoupon(generateCouponRequest)){
            return AdminCouponResponse.builder()
                    .code("ok") // 성공 시 code = "ok"
                    .build();
        }
        return AdminCouponResponse.builder()
                .code("no") // 실패 시 code = "no"
                .build();
    }

    public AdminCouponResponse grantCoupon(Long coupon_id, List<String> target_user_ids){
        if (!adminCouponRepository.isValidCoupon(coupon_id)){ // 유효하지 않은 쿠폰이면 실패
            return AdminCouponResponse.builder()
                    .code("no")
                    .build();
        }
        if(adminCouponRepository.grantCoupon(coupon_id, target_user_ids)){
            return AdminCouponResponse.builder()
                    .code("ok")
                    .build();
        }
        return AdminCouponResponse.builder()
                .code("no")
                .build();

    }
}
