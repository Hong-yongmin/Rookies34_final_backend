package com.shop.backend.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminWithdrawalRequest {
    private String seller_id;
    private String status;      // REQUESTED / APPROVED / REJECTED
    private String date_from;   // 신청일 기준
    private String date_to;
    private Integer page;
}
