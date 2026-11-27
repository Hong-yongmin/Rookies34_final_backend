package com.shop.backend.order.service;

import com.shop.backend.order.dto.Claim;
import com.shop.backend.order.dto.ClaimResponse;
import com.shop.backend.order.repository.SellerClaimRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SellerClaimService {

    private final SellerClaimRepository sellerClaimRepository;

    public ClaimResponse getClaims(String seller_id, Integer page, String status){
        List<Claim> claims = sellerClaimRepository.getClaims(seller_id, page, status);

        return ClaimResponse.builder()
                .code("ok")
                .num(claims.size())
                .claims(claims)
                .build();
    }

    @Transactional
    public Boolean acceptClaim(Integer claim_id, String decision){
        if(!sellerClaimRepository.acceptClaim(claim_id, decision)) return false;
        if(Objects.equals(decision, "COMPLETED")){ // 승인인 경우 order_item과 settlement의 상태 변경
            Integer order_item_id = sellerClaimRepository.getOrderItemId(claim_id);
            if(!sellerClaimRepository.updateOrderItemStatus(order_item_id)) return false;
            if(!sellerClaimRepository.updateSettlementStatus(claim_id)) return false;
        }
        return true;
    }
}
