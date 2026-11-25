package com.shop.backend.order.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class OrderListRequest { // GET /api/orders
    private String period;      // 3M / 6M / 1Y
    private String keyword;     // 상품명 검색
    private Integer page;
}
