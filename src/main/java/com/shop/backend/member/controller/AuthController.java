package com.shop.backend.member.controller;

import com.shop.backend.member.dto.LoginRequest;
import com.shop.backend.member.dto.LoginResponse;
import com.shop.backend.member.dto.MemberResponse;
import com.shop.backend.member.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/auth/check-id")
    public ResponseEntity<MemberResponse> checkId(@RequestParam String id){
        return ResponseEntity.ok(authService.checkId(id));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(req));
    }

    @PostMapping("/admin/auth/login")
    public ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginRequest req){
        return ResponseEntity.status(HttpStatus.OK).body(authService.loginAdmin(req));
    }
}
