package com.shop.backend.member.controller;

import com.shop.backend.global.security.AdminChecker;
import com.shop.backend.global.security.JwtAuthenticationFilter;
import com.shop.backend.member.dto.*;
import com.shop.backend.member.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminChecker adminChecker;

    @GetMapping("/users")
    public ResponseEntity<AdminMemberListResponse> members(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page){
        if (!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getMembers(keyword, role, status, page));
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<MemberResponse> changeUserStatus(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("userId") String user_id,
            @RequestBody UserStatusRequest req){
        if (!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(adminService.changeUserStatus(user_id, req));
    }

    @PatchMapping("/sellers/{sellerId}/approval")
    public ResponseEntity<MemberResponse> changeSellerStatus(
            @RequestAttribute(value= JwtAuthenticationFilter.MEMBER_ID, required=false) String id,
            @PathVariable("sellerId") String seller_id,
            @RequestBody SellerApprovalRequest req){
        if (!adminChecker.isAdmin(id)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 권한 필요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(adminService.changeSellerStatus(seller_id, req));
    }
}
