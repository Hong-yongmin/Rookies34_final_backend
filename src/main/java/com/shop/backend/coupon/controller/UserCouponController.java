package com.shop.backend.coupon.controller;

import com.shop.backend.coupon.dto.CouponListResponse;
import com.shop.backend.coupon.service.UserCouponService;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.UserChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class UserCouponController {

    private final UserCouponService userCouponService;
    private final UserChecker userChecker;

    @GetMapping("/coupons")
    public ResponseEntity<CouponListResponse> myCoupons(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id, @RequestParam String status){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userCouponService.getUserCoupons(id, status));
    }
}
