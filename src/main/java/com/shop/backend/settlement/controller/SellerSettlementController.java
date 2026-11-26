package com.shop.backend.settlement.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.settlement.dto.SettlementResponse;
import com.shop.backend.settlement.dto.SellerRevenueRequest;
import com.shop.backend.settlement.dto.SellerRevenueResponse;
import com.shop.backend.settlement.dto.SellerWithdrawalResponse;
import com.shop.backend.settlement.service.SellerSettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
public class SellerSettlementController {

    private final SellerChecker sellerChecker;
    private final SellerSettlementService sellerSettlementService;

    @GetMapping("/revenues")
    public ResponseEntity<SellerRevenueResponse> getRevenues(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @ModelAttribute SellerRevenueRequest req
            ){
        if(!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.ok(sellerSettlementService.getRevenues(id, req));
    }

    @GetMapping("/withdrawals")
    public ResponseEntity<SellerWithdrawalResponse> getWithdrawals(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestParam Integer page
    ){
        if(!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.ok(sellerSettlementService.getWithdrawals(id, page));
    }

    @PostMapping("/withdrawals")
    public ResponseEntity<SettlementResponse> applyWithdrawals(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestBody Integer amount
    ){
        if(!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.ok(sellerSettlementService.applyWithdrawal(id, amount));
    }
}
