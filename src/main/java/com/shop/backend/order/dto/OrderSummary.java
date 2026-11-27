package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderSummary {
    private Long order_id;
    private String ordered_at;      // YYYY-MM-DD HH:mm:ss
    private String product_name;    // 주문의 첫 번째 상품명
    private String status;
    private int item_count;
    private int total_amount;
}
