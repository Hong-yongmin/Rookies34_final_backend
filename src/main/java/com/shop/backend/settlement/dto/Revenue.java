package com.shop.backend.settlement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Revenue {
    private Integer order_item_id;
    private Integer order_amount;
    private Integer commission;
    private Integer settlement_amount;
    private String status;
    private String created_at;
}
