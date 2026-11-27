package com.shop.backend.order.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.order.dto.ClaimAcceptRequest;
import com.shop.backend.order.dto.ClaimResponse;
import com.shop.backend.order.service.SellerClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/claims")
public class SellerClaimController {

    private final SellerChecker sellerChecker;
    private final SellerClaimService sellerClaimService;

    @GetMapping
    public ClaimResponse getClaims(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String status){
        if(!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return sellerClaimService.getClaims(id, page, status);
    }

    @PatchMapping("/{claimId}")
    public Map<String, Object> acceptClaim(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("claimId") Integer claim_id,
            @ModelAttribute ClaimAcceptRequest req){
        if(!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        if(sellerClaimService.acceptClaim(claim_id, req.getDecision())){
            return Map.of("code", "ok");
        }
        return Map.of("code", "no");
    }
}
