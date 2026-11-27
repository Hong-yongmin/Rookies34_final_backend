package com.shop.backend.settlement.controller;

import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.settlement.dto.*;
import com.shop.backend.settlement.service.AdminSettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminSettlementController {

    private final AdminSettlementService adminSettlementService;
    private final AdminChecker adminChecker;

    @PatchMapping("/sellers/{sellerId}/commission-rate")
    public ResponseEntity<SettlementResponse> setCommissionRate(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("sellerId") String seller_id,
            @RequestBody CommissionRateRequest req
    ){
        if(!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.ok(adminSettlementService.setCommissionRate(seller_id, req.getCommission_rate()));
    }

    @GetMapping("/settlements")
    public ResponseEntity<AdminRevenueResponse> getRevenues(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @ModelAttribute AdminRevenueRequest req
    ){
        if(!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.ok(adminSettlementService.getRevenues(req));
    }



    @GetMapping("/withdrawals")
    public ResponseEntity<AdminWithdrawalResponse> getWithdrawals(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @ModelAttribute AdminWithdrawalRequest req
    ){
        if(!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.ok(adminSettlementService.getWithdrawals(req));
    }

    @PatchMapping("/settlements/{settlementId}")    // 정산 확정 (A-10)
    public ResponseEntity<SettlementResponse> confirmSettlement(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("settlementId") Integer settlement_id,
            @RequestBody DecisionRequest req
    ){
        if(!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.ok(adminSettlementService.confirmSettlement(settlement_id, req.getDecision()));
    }

    @PatchMapping("/withdrawals/{withdrawalId}")    // 출금 승인 (A-10)
    public ResponseEntity<SettlementResponse> confirmWithdrawal(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("withdrawalId") Integer withdrawal_id,
            @RequestBody DecisionRequest req
    ){
        if(!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.ok(adminSettlementService.confirmWithdrawals(withdrawal_id, req.getDecision()));
    }
}
