package com.shop.backend.member.controller;

import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.global.security.SellerChecker;
import com.shop.backend.member.dto.*;
import com.shop.backend.member.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SellerController {

    private final SellerService sellerService;
    private final SellerChecker sellerChecker;

    @PostMapping("/sellers") // multipart/form-data로 수신, business_cert_file은 파일
    public ResponseEntity<MemberResponse> signup(@ModelAttribute SellerSignupRequest req){
        return ResponseEntity.status(HttpStatus.OK).body(sellerService.signupSeller(req));
    }

    @GetMapping("/seller/profile") // API 명세에 없음 (판매자 정보 조회)
    public ResponseEntity<SellerInfoResponse> myInfo(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id){
        if (!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sellerService.getSellerInfo(id));
    }

    @PutMapping("/seller/profile")
    public ResponseEntity<MemberResponse> changeMyInfo(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestBody SellerProfileRequest req){
        if (!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sellerService.changeSellerInfo(id, req));
    }

    @PutMapping("/seller/password") //(판매자 비밀번호 변경)
    public ResponseEntity<MemberResponse> changeMyPassword(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestBody PasswordChangeRequest req){
        if (!sellerChecker.isSeller(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "판매자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sellerService.changeSellerPassword(id, req));
    }
}
