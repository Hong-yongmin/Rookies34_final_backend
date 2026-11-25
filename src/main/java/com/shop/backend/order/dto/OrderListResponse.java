package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderListResponse {
    private String code;
    private int num;
    private List<OrderSummary> orders;
}
