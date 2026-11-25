package com.shop.backend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerApprovalRequest {
    private String decision;        // APPROVE / REJECT
    private String reject_reason;   // REJECT일 때 필수
}
