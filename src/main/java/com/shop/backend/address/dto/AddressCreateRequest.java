package com.shop.backend.address.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressCreateRequest {
    private String recipient_name;
    private String recipient_phone;
    private String zipcode;
    private String address;
    private String address_detail;
    private Boolean is_default;
}
