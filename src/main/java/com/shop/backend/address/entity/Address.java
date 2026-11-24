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

//    @ManyToOne
//    @JoinColumn(name = "login_id")
//    private User user;

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

    public Address(String recipient_name, String recipient_phone, String zipcode, String address, String address_detail, boolean is_default) {
        update(recipient_name, recipient_phone, zipcode, address, address_detail, is_default);
    }

    public void update(String recipient_name, String recipient_phone, String zipcode, String address, String address_detail, boolean is_default) {
        this.recipient_name = recipient_name;
        this.recipient_phone = recipient_phone;
        this.zipcode = zipcode;
        this.address = address;
        this.address_detail = address_detail;
        this.is_default = is_default;
    }
}