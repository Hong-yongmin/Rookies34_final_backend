package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderCreateResponse {
    private String code;
    private Long order_id;      // 주문 성공 시 전달
}
