package com.shop.backend.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SellerInfo {
    private final String manager_name;
    private final String email;
    private final String phone;
    private final String business_reg_no;
    private final String business_name;
    private final String cert_file_url;
    private final String bank;
    private final String account_no;
    private final String account_holder;
    private final String status;
    private final String created_at;
}
