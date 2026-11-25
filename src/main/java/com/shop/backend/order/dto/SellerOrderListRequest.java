package com.shop.backend.order.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class SellerOrderListRequest { // GET /api/seller/orders
    private String date_from;   // YYYY-MM-DD
    private String date_to;     // YYYY-MM-DD
    private String status;      // PAID / SHIPPING / COMPLETED / CANCELED
    private Integer page;
}
