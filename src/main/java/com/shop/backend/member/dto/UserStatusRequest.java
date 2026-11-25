package com.shop.backend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusRequest {
    private String status;  // ACTIVE / SUSPENDED
    private String memo;    // 사유, 선택
}
