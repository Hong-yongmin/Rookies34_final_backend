package com.shop.backend.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentRequest { // PATCH /api/seller/order-items/{orderItemId}/shipment
    private String expected_delivery_date;  // YYYY-MM-DD, 선택
    private String courier;                 // 송장 입력 시
    private String tracking_no;             // 송장 입력 시
}
