package com.shop.backend.coupon.entity;

import jakarta.persistence.*;

@Entity
@Table(name="coupon")
public class Coupon {
    // 기본키. 자동 생성
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="coupon_id")
    private Long coupon_id;

    @Column(name="coupone_code", nullable = true, unique = true)
    private String coupon_code;

    @Column(name="coupon_name", nullable = false)
    private String coupon_name;

    @Column(name="discount_type", nullable = false)
    private String discount_type;

    @Column(name="discount_value", nullable = false)
    private String discount_value;

    @Column(name="min_order_amount", nullable = true)
    private int min_order_amount;

    @Column(name="valid_from", nullable = false)
    private String valid_from;

    @Column(name="valid_to", nullable=false)
    private String valid_to;
}
