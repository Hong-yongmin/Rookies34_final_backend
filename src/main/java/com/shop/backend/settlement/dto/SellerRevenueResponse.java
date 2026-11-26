package com.shop.backend.settlement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SellerRevenueResponse {
    private String code;
    private Long expected_settlement;
    private Integer num;
    private List<Revenue> revenues;
}
