package com.shop.backend.coupon.entity;

import jakarta.persistence.*;

@Entity
@Table(name="member_coupon")
public class MemberCoupon {
    // 기본키. 자동 생성
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="member_coupon_id")
    private Long mc_id;

}
