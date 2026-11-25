package com.shop.backend.address.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Address {
    private int address_id;
    private String recipient_name;
    private String recipient_phone;
    private String zipcode;
    private String address;
    private String address_detail;
    private Boolean is_default;
}
