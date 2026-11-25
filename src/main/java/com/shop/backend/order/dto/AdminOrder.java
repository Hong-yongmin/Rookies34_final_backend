package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminOrder {
    private Long order_id;
    private String ordered_at;
    private String buyer_login_id;
    private String seller_id;
    private int total_amount;
}
