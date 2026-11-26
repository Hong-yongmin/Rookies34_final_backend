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

    @PatchMapping("/{settlementId}")
    public ResponseEntity<SettlementResponse> confirmSettlement(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("settlementId") Integer settlement_id,
            @RequestBody String decision
    ){
        if(!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.ok(adminSettlementService.confirmSettlement(settlement_id, decision));
    }

    @PatchMapping("/{withdrawalId}")
    public ResponseEntity<SettlementResponse> confirmWithdrawal(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("withdrawalId") Integer withdrawal_id,
            @RequestBody String decision
    ){
        if(!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.ok(adminSettlementService.confirmSettlement(withdrawal_id, decision));
    }
}
