package com.shop.backend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerProfileRequest {
    private String business_name;
    private String phone;
    private String bank;
    private String account_no;
    private String account_holder;
}
