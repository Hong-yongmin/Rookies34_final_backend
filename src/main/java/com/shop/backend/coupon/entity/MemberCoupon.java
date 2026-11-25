package com.shop.backend.coupon.entity;

import jakarta.persistence.*;

@Entity
@Table(name="user_coupon")
public class MemberCoupon {
    // 기본키. 자동 생성
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="user_coupon_id")
    private Long mc_id;

}
