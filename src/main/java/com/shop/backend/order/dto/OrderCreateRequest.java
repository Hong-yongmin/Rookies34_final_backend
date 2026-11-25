package com.shop.backend.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequest { // POST /api/orders
    private List<Long> cart_item_ids;
    private Long address_id;
    private String recipient_name;
    private String recipient_phone;
    private String delivery_request;    // 선택
    private Long coupon_id;             // 선택
    private Integer use_points;         // 선택
    private String payment_method;      // CARD / BANK_TRANSFER / VIRTUAL_ACCOUNT
    private Boolean agree_payment;
}
