package com.shop.backend.address.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer address_id;

    @Column(nullable = false)
    private String user_id;

    @Column(nullable = false)
    private String recipient_name;

    @Column(nullable = false)
    private String recipient_phone;

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = true)
    private String address_detail;

    @Column(nullable = false)
    private boolean is_default;
}