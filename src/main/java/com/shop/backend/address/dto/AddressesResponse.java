package com.shop.backend.address.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AddressesResponse {
    private String code;
    private List<Address> addresses;
}
