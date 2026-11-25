package com.shop.backend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String current_password;
    private String name;
    private String email;
    private String phone;
    private String gender;      // MALE / FEMALE / NONE
    private String birth_date;  // YYYY-MM-DD
}
