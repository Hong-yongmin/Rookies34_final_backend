package com.shop.backend.coupon.controller;

import com.shop.backend.coupon.dto.AdminCouponResponse;
import com.shop.backend.coupon.dto.GenerateCouponRequest;
import com.shop.backend.coupon.dto.GrantCouponRequest;
import com.shop.backend.coupon.service.AdminCouponService;
import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {
    private final AdminCouponService adminCouponService;
    private final AdminChecker adminChecker;

    @PostMapping
    public ResponseEntity<AdminCouponResponse> generateCoupon(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestBody GenerateCouponRequest req){
        if(!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(adminCouponService.generateCoupon(req));
    }

    @PostMapping("/{coupon_id}")
    public ResponseEntity<AdminCouponResponse> grantCoupon(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("coupon_id") Long coupon_id,
            @RequestBody GrantCouponRequest req){
        if(!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(adminCouponService.grantCoupon(coupon_id, req.getTarget_user_ids()));
    }
}
