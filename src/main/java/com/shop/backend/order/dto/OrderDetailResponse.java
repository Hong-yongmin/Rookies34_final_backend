package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderDetailResponse { // GET /api/orders/{orderId}
    private String code;
    private Long order_id;
    private String ordered_at;
    private String payment_method;
    private int total_amount;
    private String recipient_name;
    private String recipient_phone;
    private String zipcode;
    private String address;
    private String address_detail;
    private String delivery_request;
    private List<OrderItemInfo> items;
}
