package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SellerOrderListResponse {
    private String code;
    private int num;
    private List<SellerOrder> orders;
}
