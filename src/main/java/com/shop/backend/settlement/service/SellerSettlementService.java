package com.shop.backend.settlement.service;

import com.shop.backend.settlement.dto.*;
import com.shop.backend.settlement.repository.SellerSettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerSettlementService {

    private final SellerSettlementRepository sellerSettlementRepository;

    public SellerRevenueResponse getRevenues(String id, SellerRevenueRequest req){
        List<Revenue> revenues = sellerSettlementRepository.getRevenues(id, req);
        Long sum = 0L;

        for(Revenue r : revenues){
            sum += r.getSettlement_amount(); // 총액 계산
        }
        return SellerRevenueResponse.builder()
                .code("ok")
                .expected_settlement(sum)
                .num(revenues.size())
                .revenues(revenues).build();
    }

    public SellerWithdrawalResponse getWithdrawals(String id, Integer page){
        List<Withdrawal> withdrawals = sellerSettlementRepository.getWithdrawals(id, page);
        return SellerWithdrawalResponse.builder()
                .code("ok")
                .num(withdrawals.size())
                .withdrawals(withdrawals)
                .build();
    }

    public SettlementResponse applyWithdrawal(String id, Integer amount){
        if(amount != null && sellerSettlementRepository.applyWithdrawal(id, amount)){ // amount 누락 시 실패
            return SettlementResponse.builder()
                    .code("ok").build();
        }
        return SettlementResponse.builder()
                .code("no").build();
    }
}
