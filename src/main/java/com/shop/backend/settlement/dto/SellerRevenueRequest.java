package com.shop.backend.settlement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerRevenueRequest {
    private String status;
    private String date_from;
    private String date_to;
    private Integer page;
}
