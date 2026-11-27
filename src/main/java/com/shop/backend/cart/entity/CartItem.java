package com.shop.backend.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cart_item_id;

    @Column(nullable = false)
    private Integer cart_id;

    @Column(nullable = false)
    private Integer product_id;

    @Column(nullable = false)
    private Integer quantity;
}
