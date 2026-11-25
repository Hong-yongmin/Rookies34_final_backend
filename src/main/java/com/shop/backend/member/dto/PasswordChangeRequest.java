package com.shop.backend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {
    private String current_password;
    private String new_password;
}
