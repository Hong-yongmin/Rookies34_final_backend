package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderItemInfo {
    private Long order_item_id;
    private Long product_id;
    private String name;
    private int quantity;
    private int price;  // 주문 당시 판매가
    private String status;  // 주문 상품 상태 PAID / SHIPPING / COMPLETED / CANCELED
}
