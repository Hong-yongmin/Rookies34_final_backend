package com.shop.backend.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminMemberInfo {
    private String user_id;     // DB의 user_id / seller_id
    private String login_id;    // 로그인 아이디 (user_id와 동일)
    private String name;
    private String role;        // BUYER / SELLER
    private String status;
    private String created_at;
}
