package com.shop.backend.settlement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AdminWithdrawalResponse {
    private String code;
    private Integer num;
    private List<Withdrawal> withdrawals;
}
