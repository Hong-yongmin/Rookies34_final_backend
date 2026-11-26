package com.shop.backend.settlement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Withdrawal {
    private Long withdrawal_id;
    private String seller_id;
    private Integer amount;
    private String status;
    private String requested_at;
    private String processed_at;
}
