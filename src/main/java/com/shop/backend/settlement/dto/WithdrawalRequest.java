package com.shop.backend.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequest {   // 판매자 출금 신청 {"amount": 50000}
    private Integer amount;
}
