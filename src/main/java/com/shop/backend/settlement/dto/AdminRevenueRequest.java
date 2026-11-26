package com.shop.backend.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminRevenueRequest {
    private String seller_id;
    private String status;
    private String date_from;
    private String date_to;
    private Integer page;
}
