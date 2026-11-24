package com.shop.backend.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SellerSignupRequest {
    private String login_id;
    private String password;
    private String manager_name;
    private String email;
    private String phone;
    private String business_reg_no;
    private String business_name;
    private String cert_file_path;
}
