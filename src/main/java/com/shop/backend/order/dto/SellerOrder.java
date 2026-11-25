package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SellerOrder {
    private Long order_item_id;
    private Long order_id;
    private String product_name;
    private int quantity;
    private int amount;         // 판매가 × 수량
    private String status;
    private String ordered_at;
    private String tracking_no; // 입력 전이면 null
}
