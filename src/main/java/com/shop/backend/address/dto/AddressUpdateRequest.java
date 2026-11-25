package com.shop.backend.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateRequest {
    private String recipient_name;
    private String recipient_phone;
    private String zipcode;
    private String address;
    private String address_detail;
    private Boolean is_default;
}