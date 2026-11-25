package com.shop.backend.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserInfo {
    private final String name;
    private final String email;
    private final String phone;
    private final String gender;
    private final String birth_date;
    private final String created_at;
}
