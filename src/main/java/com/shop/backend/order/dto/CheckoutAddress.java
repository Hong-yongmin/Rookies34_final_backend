package com.shop.backend.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckoutAddress {
    private Long address_id;
    private String recipient_name;
    private String recipient_phone;
    private String zipcode;
    private String address;
    private String address_detail;
    private Boolean is_default;
}
