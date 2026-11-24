package com.shop.backend.member.entity;

import jakarta.persistence.*;

@Entity
@Table(name="user")
public class User {

    @Id
    private String login_id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = true)
    private String address_detail;

    @Column(nullable = true)
    private String gender;

    @Column(nullable = true)
    private String birth_date;
}
