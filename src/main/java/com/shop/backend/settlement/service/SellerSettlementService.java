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

    public SellerWithdrawalsResponse getWithdrawals(String id, Integer page){
        List<Withdrawal> withdrawals = sellerSettlementRepository.getWithdrawals(id, page);
        return SellerWithdrawalsResponse.builder()
                .code("ok")
                .num(withdrawals.size())
                .withdrawals(withdrawals)
                .build();
    }

    public SellerResponse applyWithdrawal(String id, Integer amount){
        if(sellerSettlementRepository.applyWithdrawal(id, amount)){
            return SellerResponse.builder()
                    .code("ok").build();
        }
        return SellerResponse.builder()
                .code("no").build();
    }
}
