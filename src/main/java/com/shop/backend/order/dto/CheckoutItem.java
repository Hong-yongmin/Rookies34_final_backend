package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckoutItem {
    private Long cart_item_id;
    private Long product_id;
    private String name;
    private int quantity;
    private int price;
    private Integer discount_rate;  // 선택
    private String thumbnail;
}
