package com.shop.backend.order.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.UserChecker;
import com.shop.backend.order.dto.ClaimRequest;
import com.shop.backend.order.dto.ClaimResponse;
import com.shop.backend.order.dto.DefaultResponse;
import com.shop.backend.order.service.UserClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserClaimController {

    private final UserChecker userChecker;
    private final UserClaimService userClaimService;

    @PostMapping("/{orderId}/claims")
    public ResponseEntity<DefaultResponse> requestClaim(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("orderId") Integer order_id,
            @RequestBody ClaimRequest req){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.ok(userClaimService.requestClaim(req, order_id, id));
    }

    @GetMapping("/me/claims")
    public ResponseEntity<ClaimResponse> getClaims(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestParam(required = false) Integer page
    ){
        if (!userChecker.isUser(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        }
        return ResponseEntity.ok(userClaimService.getClaims(id, page));
    }
}
