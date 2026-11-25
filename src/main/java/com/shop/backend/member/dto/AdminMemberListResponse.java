package com.shop.backend.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AdminMemberListResponse {
    private String code;
    private int num;
    private List<AdminMemberInfo> users;
}
