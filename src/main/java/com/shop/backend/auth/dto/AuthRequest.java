package com.shop.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String name;
    private String phone;
    private String email;
    private String login_id;
    private String verify_code;
    private String reset_token;
    private String new_password;
    private String new_password_confirm;
}
