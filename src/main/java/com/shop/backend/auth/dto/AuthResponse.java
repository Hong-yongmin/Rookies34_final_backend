package com.shop.backend.auth.dto;

import lombok.*;


@Getter
@Setter
@Builder
public class AuthResponse {
    private String code;
    private String verify_code;
}
