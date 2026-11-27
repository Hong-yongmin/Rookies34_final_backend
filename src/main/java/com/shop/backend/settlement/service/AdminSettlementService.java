package com.shop.backend.settlement.service;

import com.shop.backend.settlement.dto.*;
import com.shop.backend.settlement.repository.AdminSettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSettlementService {

    private final AdminSettlementRepository adminSettlementRepository;

    public SettlementResponse setCommissionRate(String id, Double rate){
        if(adminSettlementRepository.setCommissionRate(id, rate)){
            return SettlementResponse.builder().code("ok").build();
        }
        return SettlementResponse.builder().code("no").build();
    }

    public AdminRevenueResponse getRevenues(AdminRevenueRequest req){
        List<Revenue> result = adminSettlementRepository.getRevenues(req);
        return AdminRevenueResponse.builder()
                .code("ok")
                .num(result.size())
                .revenues(result)
                .build();
    }

    public AdminWithdrawalResponse getWithdrawals(AdminWithdrawalRequest req){
        List<Withdrawal> result = adminSettlementRepository.getWithdrawals(req);
        return AdminWithdrawalResponse.builder()
                .code("ok")
                .num(result.size())
                .withdrawals(result)
                .build();
    }

    public SettlementResponse confirmSettlement(Integer settlement_id, String decision){
        if(adminSettlementRepository.confirmSettlement(settlement_id, decision)){
            return SettlementResponse.builder().code("ok").build();
        }
        return SettlementResponse.builder().code("no").build();
    }

    public SettlementResponse confirmWithdrawals(Integer withdrawal_id, String decision){
        if(adminSettlementRepository.confirmWithdrawal(withdrawal_id, decision)){
            return SettlementResponse.builder().code("ok").build();
        }
        return SettlementResponse.builder().code("no").build();
    }
}
